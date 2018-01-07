package net.novapixelnetwork.spaceraiders

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import net.novapixelnetwork.gamecore.commandapi.CommandRegistry
import net.novapixelnetwork.gamecore.mysql.MySQLManager
import net.novapixelnetwork.spaceraiders.command.SpaceRaidersCommand
import net.novapixelnetwork.spaceraiders.entity.Hangar
import net.novapixelnetwork.spaceraiders.entity.SRPlayer
import net.novapixelnetwork.spaceraiders.listener.ControlListener
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Logger

class SpaceRaiders : JavaPlugin(){

    private val chat_prefix = ""

    init {

    }

    override fun onEnable() {

        config.options().copyDefaults(true)
        saveDefaultConfig()

        SpaceRaiders.log = logger

        val protocolManager = ProtocolLibrary.getProtocolManager()
        protocolManager.addPacketListener(ControlListener(this, PacketType.Play.Client.STEER_VEHICLE))

        CommandRegistry.INSTANCE.registerCommand(SpaceRaidersCommand("spaceraiders"))

        MySQLManager.INSTANCE.registerEntityType(SRPlayer::class)
        MySQLManager.INSTANCE.init()

    }

    companion object {
        fun getPlugin(): Plugin {
            return Bukkit.getPluginManager().getPlugin("SpaceRaiders")
        }

        private var log: Logger? = null

        fun getLogger():Logger {
            return log!!
        }
    }

}