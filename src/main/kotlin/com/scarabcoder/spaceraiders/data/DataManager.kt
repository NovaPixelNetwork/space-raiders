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

package com.scarabcoder.spaceraiders.data

import com.scarabcoder.gamecore.sql.Connections
import com.scarabcoder.spaceraiders.SpaceRaiders
import com.scarabcoder.spaceraiders.player.SRPlayer
import com.scarabcoder.spaceraiders.player.Squad
import com.scarabcoder.spaceraiders.ship.Engine
import com.scarabcoder.spaceraiders.ship.Hangar
import com.scarabcoder.spaceraiders.ship.Hull
import com.scarabcoder.spaceraiders.ship.Ship
import com.scarabcoder.spaceraiders.world.Planet
import com.scarabcoder.spaceraiders.world.SpaceLocation
import com.scarabcoder.spaceraiders.world.generation.Generator
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.scheduler.BukkitRunnable
import java.io.File
import java.sql.SQLException
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.logging.Level
import kotlin.collections.ArrayList

object DataManager: Listener{

    private val cachedObjRemove = TimeUnit.MINUTES.toMillis(15) //15 minutes

    private val players: HashMap<UUID, SRPlayer> = HashMap()
    private val squads: HashMap<Int, Squad> = HashMap()
    private val ships: HashMap<Int, Ship> = HashMap()
    private val hangars: HashMap<Int, Hangar> = HashMap()
    private val planets: HashMap<Int, Planet> = HashMap()

    init {
        Bukkit.getPluginManager().registerEvents(this, SpaceRaiders.getPlugin())
    }

    fun createTables() {
        val c = Connections.grabConnection()
        try {
            c.prepareStatement(SRPlayer.createTable()).executeUpdate()
            c.prepareStatement(Hangar.createTable()).executeUpdate()
            c.prepareStatement(Planet.createTable()).executeUpdate()
            c.prepareStatement(Squad.createTable()).executeUpdate()
            c.prepareStatement(Ship.createTable()).executeUpdate()
        } finally {
            c.close()
        }
    }

    fun load() {

        val c = Connections.grabConnection()
        try {
            var ps = c.prepareStatement("SELECT * FROM planets")
            var rs = ps.executeQuery()
            while(rs.next()){
                planets.put(rs.getInt("id"), Planet(rs.getInt("id"), SpaceLocation(rs.getInt("x"), rs.getInt("z")), rs.getLong("expire_date"), Generator.valueOf(rs.getString("generator"))))
            }
            ps = c.prepareStatement("SELECT * FROM squads")
            rs = ps.executeQuery()
            while(rs.next()){
                ps = c.prepareStatement("SELECT uuid FROM players WHERE squad=?")
                ps.setInt(1, rs.getInt("id"))
                val squadPlayers = ps.executeQuery()
                val members: MutableList<UUID> = ArrayList()
                while(squadPlayers.next()){
                    members.add(UUID.fromString(squadPlayers.getString("uuid")))
                }
                squads.put(rs.getInt("id"), Squad(rs.getInt("id"), UUID.fromString(rs.getString("owner")), members, rs.getString("name"), rs.getInt("planet")))
            }
        } finally {
            c.close()
        }

        //Load directories
        val parts = File(SpaceRaiders.getPlugin().dataFolder, "parts")
        val hulls = File(parts, "hulls")
        val engines = File(parts, "engines")
        val ships = File(SpaceRaiders.getPlugin().dataFolder, "ships")
        ships.mkdir()
        hulls.mkdirs()
        engines.mkdirs()

        Hull.loadAll(hulls)
        Engine.loadAll(engines)

        object: BukkitRunnable() {
            override fun run() {
                save(true)
            }

        }.runTaskTimer(SpaceRaiders.getPlugin(), TimeUnit.MINUTES.toMillis(5), TimeUnit.MINUTES.toMillis(5))

    }

    fun getHangar(hangarID: Int): Hangar? {
        if(hangars.containsKey(hangarID)) return hangars[hangarID]

        val c = Connections.grabConnection()
        try {
            val ps = c.prepareStatement("SELECT * FROM hangars WHERE id=?")
            ps.setInt(1, hangarID)
            val rs = ps.executeQuery()
            if(rs.next()){
                val planet = planets[rs.getInt("planet")]!!
                if(!planet.isLoaded())
                    planet.loadWorld()
                val center = Location(planet.world!!, rs.getInt("x").toDouble(),
                        rs.getInt("y").toDouble(),
                        rs.getInt("z").toDouble())
                val shipID = ships.filter { it.value.hangar.id == hangarID }.toList().first().second
                val hangar = Hangar(hangarID, center, Hangar.Size.valueOf(rs.getString("size")),
                        UUID.fromString(rs.getString("owner")), shipID.id,
                        rs.getBoolean("auto_generated"),
                        planet)
                hangars.put(hangar.id, hangar)
                return hangar
            }
        } finally {
            c.close()
        }
        return null
    }

    fun loadHangarsAndShips(planet: Planet) {
        val c = Connections.grabConnection()
        try {
            val ps = c.prepareStatement("SELECT * FROM hangars WHERE planet=?")
            ps.setInt(1, planet.id)
            val rs = ps.executeQuery()
            while(rs.next()){
                val loc = Location(Bukkit.getWorld("planet${rs.getInt("planet")}"), rs.getInt("x").toDouble(), rs.getInt("y").toDouble(), rs.getInt("z").toDouble())
                val hangar = Hangar(rs.getInt("id"), loc, Hangar.Size.valueOf(rs.getString("size")), UUID.fromString(rs.getString("owner")), rs.getInt("ship"), rs.getBoolean("auto_generated"), planet)
                hangar.ship
                hangars.put(hangar.id, hangar)
            }
        } finally {
            c.close()
        }
    }

    fun addToCache(planet: Planet) {
        planets.put(planet.id, planet)
    }

    fun addToCache(hangar: Hangar) {
        hangars.put(hangar.id, hangar)
    }

    fun addToCache(ship: Ship) {
        ships.put(ship.id, ship)
    }

    fun getPlanet(planetID: Int): Planet? {
        return planets[planetID]
    }

    fun getShip(shipID: Int): Ship? {
        if(ships.containsKey(shipID)) return ships[shipID]

        val c = Connections.grabConnection()
        try {
            val ps = c.prepareStatement("SELECT * FROM ships WHERE id=?")
            ps.setInt(1, shipID)
            val rs = ps.executeQuery()
            if(rs.next()){
                val hangar = DataManager.getHangar(rs.getInt("hangar"))!!
                val ship = Ship(shipID, hangar, hangar.size, UUID.fromString(rs.getString("owner")), rs.getString("name"),
                        Hull.get(rs.getString("hull"))!!, Engine.get(rs.getString("engine"))!!)
                ships.put(shipID, ship)
                return ship
            }
        } finally {
            c.close()
        }
        return null
    }

    fun getSquad(squadID: Int): Squad? {
        if(squads.containsKey(squadID)) return squads[squadID]

        val c = Connections.grabConnection()
        try {
            var ps = c.prepareStatement("SELECT * FROM squads WHERE id=?")
            ps.setInt(1, squadID)
            var rs = ps.executeQuery()
            if(rs.next()){
                val playersInSquad: MutableList<UUID> = ArrayList()
                ps = c.prepareStatement("SELECT * FROM players WHERE squad=?")
                ps.setInt(1, squadID)
                rs = ps.executeQuery()
                while(rs.next()){
                    val uuid = UUID.fromString(rs.getString("uuid"))
                    if(players.containsKey(uuid)){
                        playersInSquad.add(uuid)
                        continue
                    }
                    val player = SRPlayer(uuid, rs.getString("username"), squadID)
                    players.put(player.uuid, player)
                    playersInSquad.add(player.uuid)
                }
                val squad = Squad(squadID, UUID.fromString(rs.getString("owner")), playersInSquad, rs.getString("name"), rs.getInt("planet"))
                squads.put(squadID, squad)
                return squad
            }
        } finally {
            c.close()
        }
        return null
    }

    private fun createPlayer(player: OfflinePlayer): SRPlayer {
        val c = Connections.grabConnection()
        try {
            val ps = c.prepareStatement("INSERT INTO players (uuid, username, squad) VALUES (?, ?, NULL);")
            ps.setString(1, player.uniqueId.toString())
            ps.setString(2, player.name)
            ps.executeUpdate()
        } finally {
            c.close()
        }
        val pl = SRPlayer(player.uniqueId, player.name, null)
        players.put(pl.uuid, pl)
        return pl
    }

    fun getPlayer(uuid: UUID): SRPlayer {
        if(players.containsKey(uuid)) return players[uuid]!!

        val c = Connections.grabConnection()
        try {
            val ps = c.prepareStatement("SELECT * FROM players WHERE uuid=?")
            ps.setString(1, uuid.toString())
            val rs = ps.executeQuery()
            if(rs.next()){
                val player = SRPlayer(uuid, Bukkit.getOfflinePlayer(uuid).name, rs.getInt("squad"))
                players.put(uuid, player)
                return player
            }else {
                return createPlayer(Bukkit.getOfflinePlayer(uuid))
            }
        } finally {
            c.close()
        }
    }

    fun getPlayer(username: String): SRPlayer?{
        for((_,v) in players){
            if(v.username.equals(username, true))
                return v
        }
        val c = Connections.grabConnection()
        try {
            val ps = c.prepareStatement("SELECT * FROM players WHERE username=?")
            ps.setString(1, username)
            val rs = ps.executeQuery()
            if(rs.next()){
                val player = SRPlayer(UUID.fromString(rs.getString("uuid")), rs.getString("username"), rs.getInt("squad"))
                player.cached = true
                players.put(player.uuid, player)
                return player
            }else return null
        } finally {
            c.close()
        }
    }

    fun saveShipData(ship: Ship){
        val c = Connections.grabConnection()
        try {
            val ps = c.prepareStatement("UPDATE ships SET name=?, engine=?, hull=?")
            ps.setString(1, ship.name)
            ps.setString(2, ship.engine.nameID)
            ps.setString(3, ship.hull.nameID)
            ps.executeUpdate()
        } finally {
            c.close()
        }
    }

    fun savePlayerData(player: SRPlayer){
        val c = Connections.grabConnection()
        try {
            val ps = c.prepareStatement("UPDATE players SET username=?, squad=? WHERE uuid=?")
            ps.setString(1, player.username)
            ps.setObject(2, player.squad)
            ps.setString(3, player.uuid.toString())
            ps.executeUpdate()
        } finally {
            c.close()
        }
    }

    fun saveSquadData(squad: Squad) {
        val c = Connections.grabConnection()
        try {
            val ps = c.prepareStatement("UPDATE squads SET name=? WHERE id=?")
            ps.setString(1, squad.name)
            ps.setInt(2, squad.id)
            ps.executeUpdate()
        } finally {
            c.close()
        }
    }

    fun save(async: Boolean){
        SpaceRaiders.getPlugin().logger.log(Level.FINE, "Saving data")
        val br = object: BukkitRunnable() {
            override fun run() {
                //Player Data
                for((uuid, player) in players){
                    //If the object is old enough to get save and removed, or if save() isn't being called async (for server shutdown)
                    if(!((player.cached && System.currentTimeMillis() - player.cachedTime >= cachedObjRemove) || !async)) continue
                    savePlayerData(player)
                    players.remove(uuid)
                }
                //Ship data
                for((_, ship) in ships){

                    ship.savePartsData()
                    saveShipData(ship)
                }

                //Squad data
                for((_, squad) in squads) {
                    saveSquadData(squad)
                }
                SpaceRaiders.getPlugin().logger.log(Level.FINE, "Data saved successfully. ")
            }

        }
        if(!async) br.run()
        else br.runTaskAsynchronously(SpaceRaiders.getPlugin())


    }


    @EventHandler
    private fun onPlayerJoin(e: PlayerJoinEvent){
        getPlayer(e.player.uniqueId)
    }

    @EventHandler
    private fun onPlayerQuit(e: PlayerQuitEvent){
        savePlayerData(players[e.player.uniqueId]!!)
        players.remove(e.player.uniqueId)
    }

    object IDGen {

        var genHangarID: Int
            get() = ++field
        var genShipID: Int
            get() = ++field
        var genSquadID: Int
            get() = ++field
        var genPlanetID: Int
            get() = ++field

        init {
            val c = Connections.grabConnection()
            try {
                val statement = "SELECT id FROM %table% ORDER BY id DESC LIMIT 1"
                var rs = c.prepareStatement(statement.replace("%table%", "hangars")).executeQuery()
                genHangarID = if(rs.next()) rs.getInt("id") else 0
                rs = c.prepareStatement(statement.replace("%table%", "ships")).executeQuery()
                genShipID = if(rs.next()) rs.getInt("id") else 0
                rs = c.prepareStatement(statement.replace("%table", "squads")).executeQuery()
                genSquadID = if(rs.next()) rs.getInt("id") else 0
                rs = c.prepareStatement(statement.replace("%table%", "planets")).executeQuery()
                genPlanetID = if(rs.next()) rs.getInt("id") else 0
            } finally {
                c.close()
            }
        }


    }

}