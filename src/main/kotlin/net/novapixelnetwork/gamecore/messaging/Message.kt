package net.novapixelnetwork.gamecore.messaging

import org.bukkit.ChatColor

enum class Message(val msg: String, vararg val placeholders: String) {

    UNKNOWN_COMMAND("${ChatColor.RED}Command not found!"),
    TYPE_ARGUMENT_CAST_ERROR("${ChatColor.RED}Error with argument %a: %e", "a", "p"),
    INVALID_USAGE("${ChatColor.RED}Invalid command usage. Usage: %p", "p");

    fun getConfigPath(): String {
        return this.name.toLowerCase().replace("_", "-")
    }

}