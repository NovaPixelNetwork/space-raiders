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

package com.scarabcoder.spaceraiders.player

import com.scarabcoder.spaceraiders.data.DataManager
import com.scarabcoder.spaceraiders.world.Planet
import org.bukkit.Bukkit
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by owner on 1/5/2018.
 */
class Squad(val id: Int, private val ownerID: UUID, val members: MutableList<UUID>, var name: String, private val planetID: Int) {

    private val invites: MutableList<UUID> = ArrayList()

    fun isInvited(player: SRPlayer): Boolean {
        return invites.contains(player.uuid)
    }

    fun invite(player: SRPlayer): Boolean {
        return invites.add(player.uuid)
    }

    fun removeInvite(player: SRPlayer) {
        invites.remove(player.uuid)
    }

    val planet: Planet by lazy {
        DataManager.getPlanet(planetID)!!
    }
    val owner by lazy {
        DataManager.getPlayer(ownerID)
    }

    fun getPlayers(): List<SRPlayer> {
        return members.map { t -> DataManager.getPlayer(t)}
    }

    fun getOnlinePlayers(): List<SRPlayer> {
        return members.filter {  t -> Bukkit.getPlayer(t) != null }.map { t -> DataManager.getPlayer(t) }
    }

    fun addMember(player: SRPlayer) {
        members.add(player.uuid)
        player.squad = this.id
    }

    fun removeMember(player: SRPlayer) {
        members.remove(player.uuid)
        player.squad = null
    }

    companion object {

        fun from(id: Int): Squad? {
            return DataManager.getSquad(id)
        }

        fun createTable(): String {
            return "CREATE TABLE IF NOT EXISTS squads (" +
                    "id INTEGER NOT NULL, " +
                    "owner VARCHAR(16) NOT NULL, " +
                    "name VARCHAR(16) NOT NULL, " +
                    "planet INT NOT NULL, " +
                    "PRIMARY KEY (`id`));"
        }
    }

}