package net.novapixelnetwork.spaceraiders

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import net.novapixelnetwork.gamecore.commandapi.CommandRegistry
import net.novapixelnetwork.gamecore.mysql.deprecated.MySQLManager
import net.novapixelnetwork.gamecore.mysql.deprecated.ObjectCacheManager
import net.novapixelnetwork.spaceraiders.command.SpaceRaidersCommand
import net.novapixelnetwork.spaceraiders.entity.SRPlayer
import net.novapixelnetwork.spaceraiders.listener.ControlListener
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.squash.statements.insertInto
import org.jetbrains.squash.statements.values
import java.util.logging.Logger

class SpaceRaiders : JavaPlugin(){

    override fun onEnable() {

        config.options().copyDefaults(true)
        saveDefaultConfig()

        SpaceRaiders.log = logger

        val protocolManager = ProtocolLibrary.getProtocolManager()
        //protocolManager.addPacketListener(ControlListener(this, PacketType.Play.Client.STEER_VEHICLE))

        CommandRegistry.registerCommand(SpaceRaidersCommand("spaceraiders"))

        org.jetbrains.squash.statements.

        insertInto(net.novapixelnetwork.spaceraiders.mysqltables.SRPlayer()).values {

        }

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