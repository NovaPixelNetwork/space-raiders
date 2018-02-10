package net.novapixelnetwork.spaceraiders.data

import net.novapixelnetwork.gamecore.mysql.Connections
import net.novapixelnetwork.spaceraiders.SpaceRaiders
import net.novapixelnetwork.spaceraiders.entity.*
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*

object DataManager: Listener{

    private val cachedPlayers: HashMap<UUID, SRPlayer> = HashMap()
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
        } finally {
            c.close()
        }
    }

    fun getHangar(hangarID: Int): Hangar {

    }

    fun getPlanet(planetID: Int): Planet {

    }

    fun getShip(shipID: Int): Ship {

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
        cachedPlayers.put(pl.uuid, pl)
        return pl
    }

    fun getPlayer(uuid: UUID): SRPlayer {
        if(cachedPlayers.containsKey(uuid)) return cachedPlayers[uuid]!!

        val c = Connections.grabConnection()
        try {
            val ps = c.prepareStatement("SELECT * FROM players WHERE uuid=?")
            ps.setString(1, uuid.toString())
            val rs = ps.executeQuery()
            if(rs.next()){
                val player = SRPlayer(uuid, Bukkit.getOfflinePlayer(uuid).name, rs.getInt("squad"))
                cachedPlayers.put(uuid, player)
                return player
            }else {
                return createPlayer(Bukkit.getOfflinePlayer(uuid))
            }
        } finally {
            c.close()
        }
    }

    fun getPlayer(username: String): SRPlayer?{
        for((_,v) in cachedPlayers){
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
                cachedPlayers.put(player.uuid, player)
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
        savePlayerData(cachedPlayers[e.player.uniqueId]!!)
        cachedPlayers.remove(e.player.uniqueId)
    }

}