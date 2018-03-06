/*
 * Copyright 2018 Nicholas Harris
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 *  OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.scarabcoder.spaceraiders

import com.comphenix.protocol.ProtocolLibrary
import com.scarabcoder.gamecore.commandapi.CommandRegistry
import com.scarabcoder.gamecore.sql.Connections
import com.scarabcoder.spaceraiders.command.SpaceRaidersCommand
import com.scarabcoder.spaceraiders.data.DataManager
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
        DataManager.load()

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