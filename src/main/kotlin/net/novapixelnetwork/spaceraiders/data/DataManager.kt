package net.novapixelnetwork.spaceraiders.data

import net.novapixelnetwork.gamecore.sql.Connections
import net.novapixelnetwork.spaceraiders.SpaceRaiders
import net.novapixelnetwork.spaceraiders.player.SRPlayer
import net.novapixelnetwork.spaceraiders.player.Squad
import net.novapixelnetwork.spaceraiders.ship.Engine
import net.novapixelnetwork.spaceraiders.ship.Hangar
import net.novapixelnetwork.spaceraiders.ship.Hull
import net.novapixelnetwork.spaceraiders.ship.Ship
import net.novapixelnetwork.spaceraiders.world.Planet
import net.novapixelnetwork.spaceraiders.world.SpaceLocation
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

object DataManager: Listener{

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
                planets.put(rs.getInt("id"), Planet(rs.getInt("id"), SpaceLocation(rs.getInt("x"), rs.getInt("z"))))
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
                val hangar = Hangar(hangarID, center, Hangar.Size.valueOf(rs.getString("size")),
                        UUID.fromString(rs.getString("owner")), rs.getInt("ship"),
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
                val hangar = getHangar(rs.getInt("hangar"))!!
                val ship = Ship(shipID, hangar, hangar.size, UUID.fromString(rs.getString("owner")), rs.getString("name"))
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

    fun savePlayerData(player: SRPlayer){
        val c = Connections.grabConnection()
        try {
            val ps = c.prepareStatement("UPDATE players SET username=?, squad=?")
            ps.setString(1, player.username)
            ps.setObject(2, player.squad)
            ps.executeUpdate()
        } finally {
            c.close()
        }
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

}