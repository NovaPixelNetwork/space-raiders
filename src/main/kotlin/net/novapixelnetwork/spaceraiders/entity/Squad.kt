package net.novapixelnetwork.spaceraiders.entity

import net.novapixelnetwork.spaceraiders.data.DataManager
import org.bukkit.Bukkit
import java.util.*

/**
 * Created by owner on 1/5/2018.
 */
class Squad(val id: Int, val owner: UUID, var members: MutableList<UUID>, var name: String, val planet: Planet) {



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

}