package net.novapixelnetwork.gamecore.mysql.deprecated

import net.novapixelnetwork.spaceraiders.SpaceRaiders
import org.bukkit.scheduler.BukkitRunnable
import java.sql.PreparedStatement
import java.util.*
import kotlin.reflect.KClass
import java.util.function.Function
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

object ObjectCacheManager {

    var cache: HashMap<KClass<*>, MutableList<CacheableEntity>> = HashMap()
    var typeCasters: MutableList<TypeCaster> = ArrayList()

    fun init(){
        for(type in MySQLManager.types){
            cache.put(type, ArrayList())
        }
        typeCasters.add(TypeCaster(String::class, UUID::class, Function { t: Any ->
            {
                try {
                    val str = t as String
                    UUID.fromString(str)
                } catch (e: IllegalArgumentException) {
                    null
                }
            }.invoke()
        }))
        println("Starting garbage collector")
        CacheGarbageCollector().runTaskTimerAsynchronously(SpaceRaiders.getPlugin(), 20, 20)
    }

    inline fun<reified T: CacheableEntity> get(key: Any): T?{
        val obj = getFromCache(T::class, key) ?: return null
        return obj as T
    }

    private fun findCaster(from: KClass<*>, to: KClass<*>): TypeCaster? {
        for(caster in typeCasters){
            if(caster.from == from && caster.to == to){
                return caster
            }
        }
        return null

    }

    fun getFromCache(type: KClass<*>, key: Any): CacheableEntity? {
        val primary = MySQLManager.getPrimaryKeyColumn(type)
        if(primary != null){
            for(obj in cache[type]!!){
                val objKey = MySQLManager.getColumnValue(obj, primary)!!
                if(objKey == key){
                    return obj
                }
            }
            val con = MySQLManager.grabConnection()

            val ps: PreparedStatement = con.prepareStatement("SELECT * FROM " + type.findAnnotation<Cacheable>()!!.table + " WHERE $primary=?")
            ps.setString(1, key.toString())
            println(ps.toString())
            val result = ps.executeQuery()

            MySQLManager.returnConnection(con)
            if(result.next()){
                println(type.java.constructors[0].parameterCount)
                var inst = type.java.constructors[0].newInstance(key) as CacheableEntity

                for(member in inst::class.memberProperties){
                    if(member.findAnnotation<Column>() != null && member.findAnnotation<PrimaryKey>() == null && member is KMutableProperty<*>){
                        println(result.getObject(member.findAnnotation<Column>()!!.name)::class)
                        var obj: Any = result.getObject(member.findAnnotation<Column>()!!.name) ?: continue

                        var clazz = member.returnType.classifier as KClass<*>
                        if(clazz == obj::class){
                            member.setter.call(inst, obj)

                        }else{
                            var caster = findCaster(obj::class, clazz)
                            if(caster != null){
                                member.setter.call(inst, caster.func.apply(obj))
                            }else
                                continue
                        }
                    }
                }
                cache[type]!!.add(inst)
                return inst
            }else{
                return null
            }
        }
        return null
    }

    internal class CacheGarbageCollector: BukkitRunnable() {

        override fun run() {

            val cache = cache

            for((type, value) in cache){
                val objs = value.toMutableList()
                for(entity in value){
                    println(entity.shouldExpire())
                    if(entity.shouldExpire()){
                        val c = MySQLManager.grabConnection()
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
                            ps.setObject(index + 1, any)
                        }}
                        ps.executeUpdate()
                        MySQLManager.returnConnection(c)

                    }

                }
                ObjectCacheManager.cache.put(type, objs)
            }

        }

    }

}