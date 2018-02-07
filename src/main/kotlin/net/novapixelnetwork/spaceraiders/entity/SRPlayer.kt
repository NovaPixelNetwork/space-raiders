package net.novapixelnetwork.spaceraiders.entity


import org.bukkit.entity.Player
import java.util.*

/**
 * Created by owner on 1/5/2018.
 */
class SRPlayer(val uuid: UUID, var username: String, var gang: Int?)  {

    var cached = false
    var cachedTime = System.currentTimeMillis()

    companion object {

        fun from(player: Player): SRPlayer{
            return DataManager.getPlayer(player.uniqueId)
        }

        fun createTable(): String {
            return "CREATE TABLE IF NOT EXISTS players (" +
                    "uuid VARCHAR(36) NOT NULL PRIMARY KEY, " +
                    "username VARCHAR(16) NOT NULL, " +
                    "squad INT);"
        }
    }

}