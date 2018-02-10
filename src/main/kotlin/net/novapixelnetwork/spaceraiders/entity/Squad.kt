package net.novapixelnetwork.spaceraiders.entity

import net.novapixelnetwork.spaceraiders.data.DataManager
import org.bukkit.Bukkit
import java.util.*

/**
 * Created by owner on 1/5/2018.
 */
class Squad(val id: Int, val owner: UUID, var members: MutableList<UUID>, var name: String, private val planetID: Int) {

    val planet: Planet by lazy {
        DataManager.getPlanet(planetID)!!
    }

    fun getPlayers(): List<SRPlayer> {
        return members.map { t -> DataManager.getPlayer(t)}
    }

    fun getOnlinePlayers(): List<SRPlayer> {
        return members.filter {  t -> Bukkit.getPlayer(t) != null }.map { t -> DataManager.getPlayer(t) }
    }

    fun addMember(player: SRPlayer) {
        members.add(player.uuid)
        player.squad = this.id
    }

    companion object {
        fun createTable(): String {
            return "CREATE TABLE IF NOT EXISTS squads (" +
                    "id NOT NULL AUTO_INCREMENT, " +
                    "owner VARCHAR(16) NOT NULL, " +
                    "name VARCHAR(16) NOT NULL, " +
                    "planet INT NOT NULL, " +
                    "PRIMARY KEY (`id`));"
        }
    }

}