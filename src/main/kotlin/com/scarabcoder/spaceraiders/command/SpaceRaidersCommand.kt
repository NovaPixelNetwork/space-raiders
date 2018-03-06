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

package com.scarabcoder.spaceraiders.command

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import com.scarabcoder.gamecore.commandapi.Command
import com.scarabcoder.gamecore.commandapi.CommandSection
import org.bukkit.entity.Player
import net.minecraft.server.v1_12_R1.PacketPlayOutSpawnEntityLiving
import net.minecraft.server.v1_12_R1.EntityArmorStand
import com.scarabcoder.gamecore.sql.Connections
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld



/**
 * Created by owner on 1/3/2018.
 */
class SpaceRaidersCommand(name: String): CommandSection(name) {

    override fun onCommand(player: Player) {

    }

    @Command
    fun mytest(sender: Player){
        Connections.grabConnection().close()
    }

    @Command
    fun test(sender: Player){
        var loc = sender.location
        val s = (loc.world as CraftWorld).handle
        val stand = EntityArmorStand(s)

        stand.setLocation(loc.x, loc.y, loc.z, 0f, 0f)
        var container = PacketContainer(PacketType.Play.Server.SPAWN_ENTITY_LIVING, PacketPlayOutSpawnEntityLiving(stand))
        ProtocolLibrary.getProtocolManager().sendServerPacket(sender, container)

    }

}