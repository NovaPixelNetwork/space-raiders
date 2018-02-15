package net.novapixelnetwork.spaceraiders.ship

import net.novapixelnetwork.gamecore.commandapi.Logger
import net.novapixelnetwork.spaceraiders.SpaceRaiders
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.util.Vector
import java.io.File
import java.io.FileNotFoundException
import java.util.logging.Level
import java.util.stream.Collectors

data class Hull(val nameID: String, val displayName: String, val turretLocations: List<Vector>, val engineOne: Vector, val engineTwo: Vector){

    companion object {

        private val hulls: HashMap<String, Hull> = HashMap()

        fun loadAll(directory: File) {
            if(!directory.isDirectory) throw IllegalArgumentException("Expected a directory, but got a file!")
            directory.listFiles()
                    .filter { f -> !f.isDirectory && f.extension == "yml" }
                    .forEach( {
                        try {
                            val hull = loadFromFile(it)
                            hulls.put(hull.nameID, hull)
                        } catch(e: InvalidConfigurationException){
                            SpaceRaiders.getPlugin().logger.log(Level.WARNING, "Could not load ship hull file ${it.name}")
                        }
                    })
        }

        private fun loadFromFile(file: File): Hull {
            if(!file.exists()) throw FileNotFoundException("File ${file.absolutePath} does not exist!")
            val fc = YamlConfiguration.loadConfiguration(file)
            val turretLocations: MutableList<Vector> = ArrayList()
            fc.getStringList("turret-locations")
                    .map { it.split(",") }
                    .mapTo(turretLocations) { Vector(it[0].toInt(), it[1].toInt(), it[2].toInt()) }
            return Hull(file.nameWithoutExtension, fc.getString("display-name"), turretLocations, fc.getVector("engine-one"), fc.getVector("engine-two"))
        }

        fun reload() {

        }
    }

}