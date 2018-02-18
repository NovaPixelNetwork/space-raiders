package net.novapixelnetwork.spaceraiders.world

import net.novapixelnetwork.spaceraiders.data.DataManager
import net.novapixelnetwork.spaceraiders.world.generation.Generator
import net.novapixelnetwork.spaceraiders.world.generation.VolcanicWorldGenerator
import org.bukkit.World
import org.bukkit.WorldCreator

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