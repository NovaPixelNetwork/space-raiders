package net.novapixelnetwork.gamecore.commandapi

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandMap
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.lang.reflect.Field
import java.util.*

class CommandRegistry {

    var cmds: HashMap<String, CommandSection> = HashMap();

    fun registerCommand(section: CommandSection){
        cmds.put(section.name, section)

        val cmdMap: Field = Bukkit.getServer().javaClass.getDeclaredField("commandMap")
        cmdMap.isAccessible = true

        val serverCmds: CommandMap = cmdMap.get(Bukkit.getServer()) as CommandMap
        serverCmds.register(section.name, BukkitCommand(section))

        Logger.logger.log("Registered command " + section.name, Logger.Level.INFO)
    }

    companion object {

        var INSTANCE = CommandRegistry()
            private set(instance) {}
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