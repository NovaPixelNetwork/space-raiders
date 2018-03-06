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

package com.scarabcoder.gamecore.commandapi

import com.scarabcoder.spaceraiders.SpaceRaiders
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandMap
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.lang.reflect.Field
import java.util.*
import java.util.logging.Level

object CommandRegistry {

    var cmds: HashMap<String, CommandSection> = HashMap()

    fun registerCommand(section: CommandSection){
        cmds.put(section.name, section)

        val cmdMap: Field = Bukkit.getServer().javaClass.getDeclaredField("commandMap")
        cmdMap.isAccessible = true

        val serverCmds: CommandMap = cmdMap.get(Bukkit.getServer()) as CommandMap
        serverCmds.register(section.name, BukkitCommand(section))

        SpaceRaiders.getLogger().log(Level.INFO, "Registered command " + section.name)
    }

    internal fun getTree(root: CommandSection): HashMap<String, CommandSection> {
        var children = HashMap<String, CommandSection>()
        for((key, section) in root.getChildren()){
            children.put("${root.name} $key", section)
        }
        children.put(root.name, root)
        return children
    }


    internal fun getSection(section: String): CommandSection? {
        return cmds[section]
    }

    class BukkitCommand(val section: CommandSection) : Command(section.name, section.description(), section.usage(), section.aliases()) {

        override fun execute(sender: CommandSender?, p1: String?, argsArr: Array<out String>?): Boolean {

            val args : MutableList<String> = argsArr!!.toMutableList()
            args.add(0, section.name)

            if(sender is Player){
                CommandHandler.INSTANCE.execute(sender, section.name, args)
            }
            return true
        }

    }

}