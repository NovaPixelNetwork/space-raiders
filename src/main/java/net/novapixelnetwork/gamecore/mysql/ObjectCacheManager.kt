package net.novapixelnetwork.gamecore.mysql

import net.novapixelnetwork.spaceraiders.SpaceRaiders
import org.bukkit.scheduler.BukkitRunnable
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMembers
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

class ObjectCacheManager {

    private var cache: HashMap<KClass<*>, List<CacheableEntity>> = HashMap()

    fun init(){
        for(type in MySQLManager.INSTANCE.types){
            cache.put(type, ArrayList())
        }
        CacheGarbageCollector().runTaskTimerAsynchronously(SpaceRaiders.getPlugin(), 0, 20 * 40)
    }


    companion object {
        internal val INSTANCE = ObjectCacheManager()
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
                                if(column.type.contains("PRIMARY KEY")) {
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