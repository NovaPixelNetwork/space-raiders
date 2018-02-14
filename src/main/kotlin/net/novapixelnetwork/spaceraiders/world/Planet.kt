package net.novapixelnetwork.spaceraiders.world

import net.novapixelnetwork.spaceraiders.world.SpaceLocation
import org.bukkit.World

/**
 * Created by owner on 1/5/2018.
 */
class Planet(val id: Int, val location: SpaceLocation) {


    var world: World? = null

    fun isLoaded(): Boolean{
        return world != null
    }

    fun loadWorld(){

    }


    companion object {
        fun createTable(): String {
            return "CREATE TABLE planets (" +
                    "id INT NOT NULL AUTO_INCREMENT," +
                    "x INT NOT NULL," +
                    "z INT NOT NULL," +
                    "PRIMARY KEY (`id`));"
        }
    }

}