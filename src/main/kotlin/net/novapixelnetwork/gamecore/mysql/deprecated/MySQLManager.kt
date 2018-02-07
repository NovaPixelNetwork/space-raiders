package net.novapixelnetwork.gamecore.mysql.deprecated

import net.novapixelnetwork.spaceraiders.SpaceRaiders
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.*
import java.util.logging.Level
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMembers
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

/**
 * Created by owner on 1/5/2018.
 */
object MySQLManager {

    private var connectionPool: ArrayList<Connection> = ArrayList()
    internal var types: ArrayList<KClass<*>> = ArrayList()
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
        if(entityType.findAnnotation<Cacheable>() == null)
            throw Exception("Entity type missing Cacheable annotation!")
        types.add(entityType)
    }
    fun getPrimaryKeyColumn(clazz: KClass<*>): String? {
        for(member in clazz.declaredMembers){
            if(member.findAnnotation<Column>() != null && member.findAnnotation<PrimaryKey>() != null){
                return member.findAnnotation<Column>()!!.name
            }
        }
        return null
    }

    fun getColumnValue(obj: CacheableEntity, column: String): Any? {
        for(field in obj::class.memberProperties){
            if(field.findAnnotation<Column>() != null && field.findAnnotation<Column>()!!.name == column){
                return field.getter.call(obj)
            }
        }
        return null
    }

    fun init() {
        connectionPool.clear()
        val mysqlInfo = SpaceRaiders.getPlugin().config.getConfigurationSection("mysql")
        val host = mysqlInfo.getString("host")
        val schema = mysqlInfo.getString("schema")
        val user = mysqlInfo.getString("user")
        val password = mysqlInfo.getString("password")
        for(x in 0..poolSize){
            connectionPool.add(DriverManager.getConnection("jdbc:mysql://${host}:3306/${schema}", user, password))
        }
        var con = grabConnection()
        for(clazz in types){
            val classColumns = TreeMap<String, String>()
            for(field in clazz.declaredMembers){
                var column = field.findAnnotation<Column>()
                if(column != null){
                    var type = column.type
                    if(field.findAnnotation<PrimaryKey>() != null)
                        type += " PRIMARY KEY"
                    classColumns.put(column.name, type)
                }
            }
            val name = clazz.findAnnotation<Cacheable>()!!.table

            var ps = con.prepareStatement("SHOW TABLES LIKE ?")
            ps.setString(1, name)
            var result = ps.executeQuery()
            if(result.next()){
                ps = con.prepareStatement("SHOW COLUMNS FROM $name")
                result = ps.executeQuery()
                val columns = HashMap<String, String>()
                while(result.next()){
                    columns.put(result.getString("Field"), result.getString("Type"))
                }

                for((column, type) in classColumns){
                    if(!columns.containsKey(column)){
                        ps = con.prepareStatement("ALTER TABLE $name ADD $column $type")
                        try {
                            ps.executeUpdate()
                            SpaceRaiders.getLogger().log(Level.INFO, "Updated table $name for class ${clazz.simpleName}, added column $column of type $type.")
                        } catch (e: SQLException){
                            SpaceRaiders.getLogger().log(Level.SEVERE, "Could not add the column $column of type $type to table $name. SQL Exception:")
                            SpaceRaiders.getLogger().log(Level.SEVERE, e.message)
                        }
                    }
                }


            }else {
                SpaceRaiders.getLogger().log(Level.INFO, "Creating MySQL table for new entity type ${clazz.simpleName}")
                var query = "CREATE TABLE $name ("
                for((column, type) in classColumns.descendingMap()){
                    query = "$query$column $type, "
                }
                query = query.substring(0, query.length - 2) + ")"
                try{
                    con.prepareStatement(query).executeUpdate()
                    SpaceRaiders.getLogger().log(Level.INFO, "Table created successfully.")
                } catch(e: SQLException){
                    SpaceRaiders.getLogger().log(Level.SEVERE, "An SQL error occurred creating the table:")
                    SpaceRaiders.getLogger().log(Level.SEVERE, query)
                    SpaceRaiders.getLogger().log(Level.SEVERE, "Error: ${e.message}")
                }
            }

        }
        returnConnection(con)

        ObjectCacheManager.init()

    }

    class PoolEmptyException: Exception()

}