package net.novapixelnetwork.spaceraiders.enum

import net.md_5.bungee.api.ChatColor

/**
 * Created by owner on 12/27/2017.
 */
enum class MessageType(val color: String) {

    ERROR(ChatColor.RED.toString()),
    INFO(ChatColor.GRAY.toString()),
    SUCCESS(ChatColor.DARK_AQUA.toString())

}