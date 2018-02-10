package net.novapixelnetwork.spaceraiders.entity


import net.novapixelnetwork.spaceraiders.data.DataManager
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.util.*

/**
 * Created by owner on 1/5/2018.
 */
class SRPlayer(val uuid: UUID, var username: String, var squad: Int?)  {

    var cached = false
    var cachedTime = System.currentTimeMillis()



    fun isOnline(): Boolean{
        return getPlayer() != null
    }

    fun getOfflinePlayer(): OfflinePlayer {
        return Bukkit.getOfflinePlayer(uuid)
    }

    fun getPlayer(): Player {
        return Bukkit.getPlayer(uuid)
    }

    fun getSquad(): Squad? {
        return if(squad == null) null else DataManager.getSquad(squad)
    }

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