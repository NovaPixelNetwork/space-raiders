package net.novapixelnetwork.spaceraiders

import net.novapixelnetwork.gamecore.CommandRegistry
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin

class SpaceRaiders : JavaPlugin(){



    override fun onEnable() {
        var test: String = ""
    }

    companion object {
        fun getPlugin(): Plugin {
            return Bukkit.getPluginManager().getPlugin("SpaceRaiders")
        }
    }

}