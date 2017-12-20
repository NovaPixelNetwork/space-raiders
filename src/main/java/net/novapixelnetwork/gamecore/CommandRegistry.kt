package net.novapixelnetwork.gamecore

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandMap
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.lang.reflect.Field

class CommandRegistry {

    var cmds: MutableList<CommandSection> = ArrayList()

    fun registerCommand(section: CommandSection){
        cmds.add(section)

        val cmdMap: Field = Bukkit.getServer().javaClass.getDeclaredField("commandMap")
        cmdMap.isAccessible = true

        val serverCmds:CommandMap = cmdMap.get(Bukkit.getServer()) as CommandMap
        serverCmds.register(section.name, BukkitCommand(section))

        Logger.logger.log("Registered command " + section.name, Logger.Level.INFO)
    }

    companion object {

        var INSTANCE = CommandRegistry()
            private set(instance) {}
    }

    private fun findSection(name:String): CommandSection? {
        return cmds.firstOrNull { it.name == name.toLowerCase() || it.aliases().contains(name.toLowerCase()) }
    }

    class BukkitCommand(val section: CommandSection) : Command(section.name, section.description(), section.usage(), section.aliases()) {

        override fun execute(sender: CommandSender?, p1: String?, argsArr: Array<out String>?): Boolean {

            val args = argsArr!!.toList()

            if(sender is Player){
                if(args.isEmpty()){
                    CommandRegistry.INSTANCE.findSection(section.name)!!.onCommand(sender)
                }else{

                }
            }
            return true
        }

    }

}