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
import com.scarabcoder.spaceraiders.data.DataFolders
import com.scarabcoder.spaceraiders.data.DataManager
import com.scarabcoder.spaceraiders.ship.state.*
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by owner on 1/5/2018.
 */

class Ship(val id: Int, val hangar: Hangar, val size: Hangar.Size, val owner: UUID, var name: String?, val hull: Hull, val engine: Engine) {

    val partData = File(DataFolders.ships, id.toString())
    val engineFolder = File(partData, "engine")
    val hullFolder = File(partData, "hull")

    val engineData: HashMap<Engine, EngineData> = HashMap()
    val hullData: HashMap<Hull, HullData> = HashMap()

    var state: ShipState = HangarState(this)

    val stateType: State
    get() {
        if(state is HangarState) return State.HANGAR
        if(state is BattleState) return State.BATTLE
        if(state is TravelState) return State.TRAVEL
        if(state is PlanetState) return State.PLANET
        throw IllegalStateException("The state was not set correctly!")
    }


    init {
        if(!partData.exists()){
            partData.mkdir()
            engineFolder.mkdir()
            hullFolder.mkdir()
            Engine.generateEngineData(engineFolder, Engine.getDefault(size))
            Hull.generateHullData(hullFolder, Hull.getDefault(size))
            engineData.put(Engine.getDefault(size), EngineData(Engine.getDefault(size), true))
            hullData.put(Hull.getDefault(size), HullData(Hull.getDefault(size), true, 1))
        }else{
            engineFolder.listFiles()
                    .filter { !it.isDirectory }
                    .filter { it.extension == "yml" }
                    .filter { Engine.get(it.nameWithoutExtension) != null }
                    .forEach { run {
                        val fc = YamlConfiguration.loadConfiguration(it)
                        val engine = Engine.get(it.nameWithoutExtension)!!
                        engineData.put(engine, EngineData(engine, fc.getBoolean("unlocked")))
                    } }
            hullFolder.listFiles()
                    .filter { !it.isDirectory }
                    .filter { it.extension == "yml" }
                    .filter { Hull.get(it.nameWithoutExtension) != null }
                    .forEach { run {
                        val fc = YamlConfiguration.loadConfiguration(it)
                        val engine = Hull.get(it.nameWithoutExtension)!!
                        hullData.put(hull, HullData(hull, fc.getBoolean("unlocked"), fc.getInt("turrets-unlocked")))
                    } }
        }
    }


    fun buildSchematic(): Schematic? {
        //TODO: Will build the hull along with both engines, using the connection points defined as vectors for each point
        return null
    }

    fun savePartsData(){
        for((_, hull) in hullData){
            val hullFile = File(hullFolder, hull.hull.nameID + ".yml")
            if(!hullFile.exists()) hullFile.createNewFile()
            val fc = YamlConfiguration.loadConfiguration(hullFile)
            fc.set("unlocked", hull.unlocked)
            fc.set("turrets-unlocked", hull.turretsUnlocked)
            fc.save(hullFile)
        }
        for((_, engine) in engineData){
            val engineFile = File(engineFolder, engine.engine.nameID + ".yml")
            if(!engineFile.exists()) engineFile.createNewFile()
            val fc = YamlConfiguration.loadConfiguration(engineFile)
            fc.set("unlocked", engine.unlocked)
            fc.save(engineFile)
        }
    }

    fun getEngineData(engine: Engine): EngineData {
        if(engineData.containsKey(engine)) return engineData[engine]!!
        return EngineData(engine, false)
    }

    fun getHullData(hull: Hull): HullData {
        if(hullData.containsKey(hull)) return hullData[hull]!!
        return HullData(hull, false, 0)
    }

    fun setEngineData(engine: Engine, data: EngineData) {
        engineData.put(engine, data)
    }

    fun setHullData(hull: Hull, data: HullData) {
        hullData.put(hull, data)
    }

    companion object {

        fun from(id: Int): Ship? {
            return DataManager.getShip(id)
        }

        fun createTable(): String {
            return "CREATE TABLE IF NOT EXISTS ships (" +
                    "id INTEGER NOT NULL, " +
                    "owner VARCHAR(36) NOT NULL," +
                    "name VARCHAR(16) NULL," +
                    "hangar INT NOT NULL," +
                    "engine VARCHAR(32) NOT NULL, " +
                    "hull VARCHAR(32) NOT NULL, " +
                    "PRIMARY KEY (`id`));"
        }
    }

    data class EngineData(val engine: Engine, var unlocked: Boolean)

    data class HullData(val hull: Hull, var unlocked: Boolean, var turretsUnlocked: Int)

    enum class State {
        HANGAR, TRAVEL, BATTLE, PLANET
    }

}