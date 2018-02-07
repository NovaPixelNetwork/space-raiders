package net.novapixelnetwork.gamecore.mysql

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import net.novapixelnetwork.spaceraiders.SpaceRaiders
import java.sql.Connection


object Connections {

    val dataSource: HikariDataSource

    init {
        val cf = SpaceRaiders.getPlugin().config.getConfigurationSection("mysql")
        val hcf = HikariConfig()
        hcf.jdbcUrl = "jdbc:mysql://${cf.getString("host")}:3306/${cf.getString("schema")}?useSSL=false"
        hcf.username = cf.getString("user")
        hcf.password = cf.getString("password")
        hcf.maximumPoolSize = 40
        hcf.minimumIdle = 3
        dataSource = HikariDataSource(hcf)

    }

    fun grabConnection(): Connection {
        return dataSource.connection
    }

}