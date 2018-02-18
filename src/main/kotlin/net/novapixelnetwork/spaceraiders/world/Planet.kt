package net.novapixelnetwork.spaceraiders.world

import net.novapixelnetwork.gamecore.sql.Connections
import net.novapixelnetwork.spaceraiders.SpaceRaiders
import net.novapixelnetwork.spaceraiders.data.DataManager
import net.novapixelnetwork.spaceraiders.world.generation.Generator
import net.novapixelnetwork.spaceraiders.world.generation.VolcanicWorldGenerator
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
                    "id INT NOT NULL AUTO_INCREMENT," +
                    "x INT NOT NULL," +
                    "z INT NOT NULL," +
                    "expire_date BIGINT NOT NULL, " +
                    "generator VARCHAR(32) NOT NULL, " +
                    "PRIMARY KEY (`id`));"
        }
    }

}