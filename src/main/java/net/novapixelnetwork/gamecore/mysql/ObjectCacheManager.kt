package net.novapixelnetwork.gamecore.mysql

import net.novapixelnetwork.spaceraiders.SpaceRaiders
import org.bukkit.scheduler.BukkitRunnable
import java.sql.PreparedStatement
import kotlin.reflect.KClass
import kotlin.jvm.javaClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

class ObjectCacheManager {

    var cache: HashMap<KClass<*>, List<CacheableEntity>> = HashMap()

    fun init(){
        for(type in MySQLManager.INSTANCE.types){
            cache.put(type, ArrayList())
        }
        CacheGarbageCollector().runTaskTimerAsynchronously(SpaceRaiders.getPlugin(), 0, 20 * 40)
    }


    companion object {
        internal val INSTANCE = ObjectCacheManager()
    }



    inline fun <reified T : CacheableEntity> getFromCache(key: Any): T? {
        val primary = MySQLManager.INSTANCE.getPrimaryKeyColumn(T::class)
        if(primary != null){
            for(obj in cache[T::class]!!){
                val objKey = MySQLManager.INSTANCE.getColumnValue(obj, primary)!!
                if(objKey == key){
                    return obj as T
                }
            }
            val con = MySQLManager.INSTANCE.grabConnection()

            val ps: PreparedStatement = con.prepareStatement("SELECT * FROM " + T::class.findAnnotation<Cacheable>()!!.table + " WHERE $primary=?")
            ps.setString(1, key.toString())
            println(ps.toString())
            val result = ps.executeQuery()

            MySQLManager.INSTANCE.returnConnection(con)
            if(result.next()){
                println(T::class.java.constructors[0].parameterCount)
                var inst = T::class.java.constructors[0].newInstance(key) as T
                for(member in inst::class.memberProperties){
                    if(member.findAnnotation<Column>() != null && member.findAnnotation<PrimaryKey>() == null && member is KMutableProperty<*>){
                        println(result.getObject(member.findAnnotation<Column>()!!.name))
                        member.setter.call(inst, result.getObject(member.findAnnotation<Column>()!!.name))
                    }
                }
                return inst
            }else{
                return null
            }
        }
        return null
    }

    internal class CacheGarbageCollector: BukkitRunnable() {

        override fun run() {

            val cache = ObjectCacheManager.INSTANCE.cache

            for((type, value) in cache){
                val objs = value.toMutableList()
                for(entity in value){
                    if(entity.shouldExpire()){
                        val c = MySQLManager.INSTANCE.grabConnection()
                        objs.remove(entity)
                        val table = type.findAnnotation<Cacheable>()!!.table
                        var query = "UPDATE $table SET "
                        val params: MutableList<Any?> = ArrayList()
                        var primary = ""
                        var primaryVal: Any? = null
                        var x = 0
                        for(field in type.memberProperties){
                            val column = field.findAnnotation<Column>()
                            if(column != null){
                                if(x == 0) {
                                    primary = column.name
                                    primaryVal = field.getter.call(entity)

                                }
                                x++
                                if(field.findAnnotation<PrimaryKey>() != null) {
                                    primary = column.name
                                    primaryVal = field.getter.call(entity)
                                    continue
                                }
                                val fieldVal = field.getter.call(entity)

                                query = "$query + ${column.name}=?, "
                                params.add(fieldVal)

                            }

                        }
                        params.add(primaryVal)
                        query = query.substring(0, query.length - 2) + " WHERE $primary=?"
                        val ps = c.prepareStatement(query)
                        params.forEachIndexed { index, any ->  run {
                            ps.setObject(index, any)
                        }}
                        ps.executeUpdate()
                        MySQLManager.INSTANCE.returnConnection(c)

                    }

                }
                ObjectCacheManager.INSTANCE.cache.put(type, objs)
            }

        }

    }

}