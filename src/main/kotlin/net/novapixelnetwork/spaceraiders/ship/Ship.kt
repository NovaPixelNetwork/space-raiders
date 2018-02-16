package net.novapixelnetwork.spaceraiders.ship

import net.novapixelnetwork.spaceraiders.data.DataFolders
import java.io.File
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by owner on 1/5/2018.
 */

class Ship(val id: Int, val hangar: Hangar, val size: Hangar.Size, val owner: UUID, var name: String, val hull: Hull, val engine: Engine) {

    val partData = File(DataFolders.ships, id.toString())

    init {
        if(!partData.exists()){
            partData.mkdir()
            val engines = File(partData, "engine")
            val hulls = File(partData, "hull")
            Engine.generateEngineData(engines, Engine.getDefault(size))
            Hull.generateHullData(hulls, Hull.getDefault(size))
        }
    }

    val engineData: HashMap<Engine, EngineData> = HashMap()
    val hullData: HashMap<Hull, HullData> = HashMap()

    var status = ShipStatus.HANGAR

    companion object {
        fun createTable(): String {
            return "CREATE TABLE IF NOT EXISTS ships (" +
                    "id INT NOT NULL AUTO_INCREMENT, " +
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

}