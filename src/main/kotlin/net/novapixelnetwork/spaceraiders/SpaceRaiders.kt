package net.novapixelnetwork.spaceraiders

import com.comphenix.protocol.ProtocolLibrary
import net.novapixelnetwork.gamecore.commandapi.CommandRegistry
import net.novapixelnetwork.gamecore.mysql.Connections
import net.novapixelnetwork.spaceraiders.command.SpaceRaidersCommand
import net.novapixelnetwork.spaceraiders.data.DataManager
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Logger

class SpaceRaiders : JavaPlugin(){

    override fun onEnable() {

        config.options().copyDefaults(true)
        saveDefaultConfig()

        SpaceRaiders.log = logger

        val protocolManager = ProtocolLibrary.getProtocolManager()

        CommandRegistry.registerCommand(SpaceRaidersCommand("spaceraiders"))

        Connections.grabConnection().close()
        DataManager.createTables()

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