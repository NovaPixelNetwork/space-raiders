package net.novapixelnetwork.spaceraiders.player

import net.novapixelnetwork.spaceraiders.data.DataManager
import net.novapixelnetwork.spaceraiders.world.Planet
import org.bukkit.Bukkit
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by owner on 1/5/2018.
 */
class Squad(val id: Int, private val ownerID: UUID, val members: MutableList<UUID>, var name: String, private val planetID: Int) {

    private val invites: MutableList<UUID> = ArrayList()

    fun isInvited(player: SRPlayer): Boolean {
        return invites.contains(player.uuid)
    }

    fun invite(player: SRPlayer): Boolean {
        return invites.add(player.uuid)
    }

    fun removeInvite(player: SRPlayer) {
        invites.remove(player.uuid)
    }

    val planet: Planet by lazy {
        DataManager.getPlanet(planetID)!!
    }
    val owner by lazy {
        DataManager.getPlayer(ownerID)
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

    fun removeMember(player: SRPlayer) {
        members.remove(player.uuid)
        player.squad = null
    }

    companion object {

        fun from(id: Int): Squad? {
            return DataManager.getSquad(id)
        }

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