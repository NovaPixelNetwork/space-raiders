package net.novapixelnetwork.spaceraiders.command

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import net.novapixelnetwork.gamecore.commandapi.Command
import net.novapixelnetwork.gamecore.commandapi.CommandSection
import org.bukkit.entity.Player
import net.minecraft.server.v1_12_R1.PacketPlayOutSpawnEntityLiving
import net.minecraft.server.v1_12_R1.EntityArmorStand
import net.novapixelnetwork.gamecore.mysql.Connections
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld



/**
 * Created by owner on 1/3/2018.
 */
class SpaceRaidersCommand(name: String): CommandSection(name) {

    override fun onCommand(player: Player) {

    }

    @Command
    fun mytest(sender: Player){
        Connections.grabConnection().close()
    }

    @Command
    fun test(sender: Player){
        var loc = sender.location
        val s = (loc.world as CraftWorld).handle
        val stand = EntityArmorStand(s)

        stand.setLocation(loc.x, loc.y, loc.z, 0f, 0f)
        var container = PacketContainer(PacketType.Play.Server.SPAWN_ENTITY_LIVING, PacketPlayOutSpawnEntityLiving(stand))
        ProtocolLibrary.getProtocolManager().sendServerPacket(sender, container)

    }

}