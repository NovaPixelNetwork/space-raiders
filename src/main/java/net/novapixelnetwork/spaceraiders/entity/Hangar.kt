package net.novapixelnetwork.spaceraiders.entity

import net.novapixelnetwork.gamecore.mysql.Cacheable
import net.novapixelnetwork.gamecore.mysql.Column

/**
 * Created by owner on 1/5/2018.
 */
class Hangar: Cacheable {


    override fun table(): String {
        return "hangars"
    }
}