package net.novapixelnetwork.spaceraiders.ship

import org.bukkit.util.Vector

interface Hull {

    val name: String
    val maxTurrets: Int
    val startTurrets: Int
    val turretLocations: List<Vector>

}