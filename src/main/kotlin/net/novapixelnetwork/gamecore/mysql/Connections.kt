package net.novapixelnetwork.gamecore.mysql

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import net.novapixelnetwork.spaceraiders.SpaceRaiders
import java.io.File
import java.sql.Connection
import java.sql.DriverManager


object Connections {

    val connectionType = ConnectionType.SQLITE

    private lateinit var dataSource: HikariDataSource
    private lateinit var sqliteConnection: Connection

    init {
        if(connectionType == ConnectionType.MYSQL){
            val cf = SpaceRaiders.getPlugin().config.getConfigurationSection("mysql")
            val hcf = HikariConfig()
            hcf.jdbcUrl = "jdbc:mysql://${cf.getString("host")}:3306/${cf.getString("schema")}?useSSL=false"
            hcf.username = cf.getString("user")
            hcf.password = cf.getString("password")
            hcf.maximumPoolSize = 40
            hcf.minimumIdle = 3
            dataSource = HikariDataSource(hcf)
        }


    }

    fun grabConnection(): Connection {
        return when(connectionType) {
            ConnectionType.SQLITE -> {
                val dbFile = File(SpaceRaiders.getPlugin().dataFolder, "spaceraiders.db")
                DriverManager.getConnection("jdbc:sqlite:${dbFile.absolutePath}")
            }
            ConnectionType.MYSQL -> {
                dataSource.connection
            }
        }
    }

    enum class ConnectionType {
        MYSQL, SQLITE
    }

}