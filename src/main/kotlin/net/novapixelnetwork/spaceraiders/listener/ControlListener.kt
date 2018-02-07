package net.novapixelnetwork.spaceraiders.listener

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import org.bukkit.plugin.Plugin
import com.comphenix.protocol.events.PacketEvent



/**
 * Created by owner on 1/3/2018.
 */
class ControlListener(plugin: Plugin, vararg packetTypes: PacketType): PacketAdapter(plugin, *packetTypes) {

    override fun onPacketReceiving(event: PacketEvent?) {
        if (event!!.packetType === PacketType.Play.Client.STEER_VEHICLE) {
            var packet = event!!.packet
            println(packet.float.read(0))
            println(packet.float.read(1))
        }
    }

}