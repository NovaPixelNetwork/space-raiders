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

package com.scarabcoder.spaceraiders.ship

import com.boydti.fawe.`object`.schematic.Schematic
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat
import com.scarabcoder.spaceraiders.SpaceRaiders
import com.scarabcoder.spaceraiders.data.DataFolders
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

        fun getDefault(size: Hangar.Size): Engine{
            return get(SpaceRaiders.getPlugin().config.getString("default-parts." + size.name.toLowerCase() + "-engine"))!!
        }

        fun generateEngineData(location: File, engine: Engine): File {
            val genFile = File(location, engine.nameID)
            if(genFile.exists()) throw IllegalArgumentException("Engine data file ${genFile.absolutePath} already exists!")
            genFile.createNewFile()
            val config = YamlConfiguration.loadConfiguration(genFile)
            config.set("unlocked", false)
            config.save(genFile)
            return genFile
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