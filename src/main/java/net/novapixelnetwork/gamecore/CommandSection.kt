package net.novapixelnetwork.gamecore

import org.bukkit.entity.Player

class CommandSection(val name:String) {

    val sections: MutableList<CommandSection> = ArrayList()

    fun section(section:CommandSection): CommandSection {
        sections.add(section)
        Logger.logger.log("$name registered command section " + section.name, Logger.Level.DEBUG)
        return this
    }

    fun onCommand(player: Player) {

    }

    fun aliases(): MutableList<String> {
        return ArrayList();
    }

    fun description(): String {
        return ""
    }

    fun usage(): String {
        return ""
    }

}