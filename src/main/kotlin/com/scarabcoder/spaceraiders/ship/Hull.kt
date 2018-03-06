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
import com.scarabcoder.gamecore.commandapi.Logger
import com.scarabcoder.spaceraiders.SpaceRaiders
import com.scarabcoder.spaceraiders.data.DataFolders
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.util.Vector
import java.io.File
import java.io.FileNotFoundException
import java.util.logging.Level
import java.util.stream.Collectors

data class Hull(val nameID: String, val displayName: String, val turretLocations: List<Vector>, val engineOne: Vector, val engineTwo: Vector){

    val schematic: Schematic = ClipboardFormat.SCHEMATIC.load(File(DataFolders.hulls, "${nameID}.schematic"))

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

        fun get(nameID: String): Hull?{
            return hulls[nameID]
        }

        fun loadFromFile(file: File): Hull {
            if(!file.exists()) throw FileNotFoundException("File ${file.absolutePath} does not exist!")
            val fc = YamlConfiguration.loadConfiguration(file)
            val turretLocations: MutableList<Vector> = ArrayList()
            fc.getStringList("turret-locations")
                    .map { it.split(",") }
                    .mapTo(turretLocations) { Vector(it[0].toInt(), it[1].toInt(), it[2].toInt()) }
            return Hull(file.nameWithoutExtension, fc.getString("display-name"), turretLocations, fc.getVector("engine-one"), fc.getVector("engine-two"))
        }

        fun getDefault(size: Hangar.Size): Hull{
            return get(SpaceRaiders.getPlugin().config.getString("default-parts." + size.name.toLowerCase() + "-hull"))!!
        }

        fun generateHullData(location: File, hull: Hull): File {
            val genFile = File(location, hull.nameID)
            if(genFile.exists()) throw IllegalArgumentException("Hull data file ${genFile.absolutePath} already exists!")
            genFile.createNewFile()
            val config = YamlConfiguration.loadConfiguration(genFile)
            config.set("turrets-unlocked", 1)
            config.set("unlocked", false)
            config.save(genFile)
            return genFile
        }

        fun reload() {

        }
    }

}