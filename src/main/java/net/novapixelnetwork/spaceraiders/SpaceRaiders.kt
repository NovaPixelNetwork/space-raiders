package net.novapixelnetwork.spaceraiders

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import net.novapixelnetwork.gamecore.commandapi.CommandRegistry
import net.novapixelnetwork.spaceraiders.command.SpaceRaidersCommand
import net.novapixelnetwork.spaceraiders.listener.ControlListener
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin

class SpaceRaiders : JavaPlugin(){

    private val chat_prefix = ""

    init {

    }

    override fun onEnable() {
        val protocolManager = ProtocolLibrary.getProtocolManager()
        protocolManager.addPacketListener(ControlListener(this, PacketType.Play.Client.STEER_VEHICLE))

        CommandRegistry.INSTANCE.registerCommand(SpaceRaidersCommand("spaceraiders"))

    }

    companion object {
        fun getPlugin(): Plugin {
            return Bukkit.getPluginManager().getPlugin("SpaceRaiders")
        }
    }

}