package net.novapixelnetwork.gamecore.commandapi

import org.bukkit.entity.Player
import java.util.*
import kotlin.collections.HashMap

open class CommandSection(val name:String) {

    val sections: MutableList<CommandSection> = ArrayList()

    fun section(section: CommandSection): CommandSection {
        sections.add(section)
        Logger.logger.log("$name registered command section " + section.name, Logger.Level.INFO)
        return this
    }

    internal fun getChildren(): HashMap<String, CommandSection> {
        var children: HashMap<String, CommandSection> = HashMap();
        for(sec in sections){
            children.put(sec.name, sec)
            for((key, v) in sec.getChildren()){
                children.put("$key ${sec.name}", v)
            }
        }
        return children
    }

    open fun onCommand(player: Player) {

    }

    open fun aliases(): MutableList<String> {
        return ArrayList();
    }

    open fun description(): String {
        return ""
    }

    open fun usage(): String {
        return ""
    }

}