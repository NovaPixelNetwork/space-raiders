package net.novapixelnetwork.spaceraiders.ship

import com.boydti.fawe.`object`.schematic.Schematic
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat
import net.novapixelnetwork.spaceraiders.data.DataFolders
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.util.Vector
import java.io.File
import java.io.FileNotFoundException

class Engine(val nameID: String, val displayName: String, val speedModifier: Double, val hullLink: Vector) {

    val schematic: Schematic = ClipboardFormat.SCHEMATIC.load(File(DataFolders.engines, "${nameID}.schematic"))

    companion object {

        private val engines: HashMap<String, Engine> = HashMap()

        fun loadAll(directory: File){
            if(!directory.isDirectory) throw IllegalArgumentException("${directory.name} must be a directory, not a file!")
            directory.listFiles()
                    .filter { f -> f.extension == "yml" }
                    .forEach { f ->
                        run {
                            val engine = loadFromFile(f)
                            engines.put(engine.nameID, engine)
                        }
                    }
        }

        fun get(nameID: String): Engine? {
            return engines[nameID]
        }


        fun loadFromFile(file: File): Engine{
            if(!file.exists()) throw FileNotFoundException("Engine data file not found: ${file.absolutePath}")
            if(file.isDirectory) throw IllegalArgumentException("Expected a file, but got a directory!")
            val fc = YamlConfiguration.loadConfiguration(file)
            if(!fc.contains("display-name")) throw IllegalArgumentException("Missing entry 'display-name' in ${file.absolutePath}")
            if(!fc.contains("speed-modifier")) throw IllegalArgumentException("Missing entry 'speed-modifier' in ${file.absolutePath}")
            if(!fc.contains("hull-link")) throw IllegalArgumentException("Missing entry 'hull-link' in ${file.absolutePath}")
            return Engine(file.nameWithoutExtension, fc.getString("display-name"), fc.getDouble("speed-modifier"), fc.getVector("hull-link"))
        }

    }

}