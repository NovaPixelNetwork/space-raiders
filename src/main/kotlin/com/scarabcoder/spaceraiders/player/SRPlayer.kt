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
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.util.*

/**
 * Created by owner on 1/5/2018.
 */
class SRPlayer(val uuid: UUID, var username: String, var squad: Int?)  {

    var cached = false
    var cachedTime = System.currentTimeMillis()



    fun isOnline(): Boolean{
        return getPlayer() != null
    }

    fun getOfflinePlayer(): OfflinePlayer {
        return Bukkit.getOfflinePlayer(uuid)
    }

    fun getPlayer(): Player {
        return Bukkit.getPlayer(uuid)
    }

    fun getSquad(): Squad? {
        return if(squad == null) null else DataManager.getSquad(squad!!)
    }

    companion object {

        fun from(username: String): SRPlayer? {
            return DataManager.getPlayer(username)
        }

        fun from(player: Player): SRPlayer {
            return DataManager.getPlayer(player.uniqueId)
        }

        fun createTable(): String {
            return "CREATE TABLE IF NOT EXISTS players (" +
                    "uuid VARCHAR(36) NOT NULL PRIMARY KEY, " +
                    "username VARCHAR(16) NOT NULL, " +
                    "squad INT);"
        }
    }

}