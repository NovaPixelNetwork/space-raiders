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

package com.scarabcoder.spaceraiders.world

import com.scarabcoder.gamecore.sql.Connections
import com.scarabcoder.spaceraiders.SpaceRaiders
import com.scarabcoder.spaceraiders.data.DataManager
import com.scarabcoder.spaceraiders.world.generation.Generator
import com.scarabcoder.spaceraiders.world.generation.VolcanicWorldGenerator
import org.bukkit.World
import org.bukkit.WorldCreator
import java.sql.SQLException
import java.sql.Statement
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.TimeUnit

/**
 * Created by owner on 1/5/2018.
 */
class Planet(val id: Int, val location: SpaceLocation, val expireTime: Long, val worldGenerator: Generator) {


    var world: World? = null

    fun isLoaded(): Boolean{
        return world != null
    }

    fun loadWorld(){
        val wc = WorldCreator("planet$id")
        wc.generator(worldGenerator.getGenerator())
        this.world = wc.createWorld()
        DataManager.loadHangarsAndShips(this)
    }


    companion object {

        fun generatePlanet(location: SpaceLocation): Planet {
            val c = Connections.grabConnection()
            try {
                val ps = c.prepareStatement("INSERT INTO planets (x, z, expire_date, generator) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)
                ps.setInt(1, location.x)
                ps.setInt(2, location.z)
                val fc = SpaceRaiders.getPlugin().config
                val expireTime = System.currentTimeMillis() + ThreadLocalRandom.current().nextLong(TimeUnit.DAYS.toMillis(fc.getLong("planet-fade-min")), TimeUnit.DAYS.toMillis(fc.getLong("planet-fade-max")))
                ps.setLong(3, expireTime)
                val gen = Generator.values()[ThreadLocalRandom.current().nextInt(Generator.values().size)]
                ps.setString(4, gen.name)
                ps.executeUpdate()
                val rs = ps.generatedKeys
                val planet = Planet(rs.getInt(1), location, expireTime, gen)
                DataManager.addToCache(planet)
                return planet
            } finally {
                c.close()
            }
            throw SQLException("There was an error inserting a new planet into the database.")
        }

        fun createTable(): String {
            return "CREATE TABLE planets (" +
                    "id INTEGER NOT NULL," +
                    "x INT NOT NULL," +
                    "z INT NOT NULL," +
                    "expire_date BIGINT NOT NULL, " +
                    "generator VARCHAR(32) NOT NULL, " +
                    "PRIMARY KEY (`id`));"
        }
    }

}