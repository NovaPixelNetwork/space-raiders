package net.novapixelnetwork.spaceraiders.entity

import net.novapixelnetwork.spaceraiders.ship.ShipStatus
import java.util.*

/**
 * Created by owner on 1/5/2018.
 */

class Ship(val id: Int, val hangar: Hangar, val size: Hangar.Size, val owner: UUID, var name: String) {

    var status = ShipStatus.HANGAR



}