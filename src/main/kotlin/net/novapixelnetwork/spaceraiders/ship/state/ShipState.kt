package net.novapixelnetwork.spaceraiders.ship.state

import net.novapixelnetwork.spaceraiders.player.SRPlayer
import net.novapixelnetwork.spaceraiders.ship.Ship

open class ShipState(val ship: Ship) {

    val members: MutableList<SRPlayer> = ArrayList()

}