package net.novapixelnetwork

import net.novapixelnetwork.spaceraiders.enum.MessageType
import org.bukkit.entity.Player

/**
 * Created by owner on 12/27/2017.
 */
private val chat_prefix = ""
fun Player.sendMessage(s: String, type: MessageType) {
    this.sendMessage(chat_prefix + type.color + s)
}
