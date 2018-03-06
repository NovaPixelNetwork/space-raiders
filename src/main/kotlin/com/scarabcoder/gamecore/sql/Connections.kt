/*
 * Copyright 2018 Nicholas Harris
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 *  OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.scarabcoder.gamecore.sql

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import com.scarabcoder.spaceraiders.SpaceRaiders
import java.io.File
import java.sql.Connection
import java.sql.DriverManager


object Connections {

    val connectionType = ConnectionType.SQLITE

    private lateinit var dataSource: HikariDataSource
    private lateinit var sqliteConnection: Connection

    init {
        if(connectionType == ConnectionType.MYSQL){
            val cf = SpaceRaiders.getPlugin().config.getConfigurationSection("sql")
            val hcf = HikariConfig()
            hcf.jdbcUrl = "jdbc:sql://${cf.getString("host")}:3306/${cf.getString("schema")}?useSSL=false"
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