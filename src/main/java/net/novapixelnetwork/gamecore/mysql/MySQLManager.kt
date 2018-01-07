package net.novapixelnetwork.gamecore.mysql

import java.sql.Connection
import kotlin.reflect.KClass

/**
 * Created by owner on 1/5/2018.
 */
class MySQLManager {

    private var connectionPool: ArrayList<Connection> = ArrayList()
    private var types: ArrayList<KClass<*>> = ArrayList()
    private val poolSize = 5

    fun grabConnection(): Connection {
        if(connectionPool.size == 0)
            throw PoolEmptyException()
        val c = connectionPool[0]
        connectionPool.removeAt(0)
        return c
    }

    fun returnConnection(connection: Connection){
        connectionPool.add(connection)
    }

    fun registerEntityType(entityType: KClass<*>) {
        types.add(entityType)
    }

    init {
        for(x in 0..poolSize){

        }
    }


    companion object {
        val INSTANCE = MySQLManager()
    }

    class PoolEmptyException: Exception()

}