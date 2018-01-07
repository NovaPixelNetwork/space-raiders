package net.novapixelnetwork.spaceraiders.entity

import net.novapixelnetwork.gamecore.mysql.Cacheable
import net.novapixelnetwork.gamecore.mysql.Column
import java.util.*

/**
 * Created by owner on 1/5/2018.
 */
class SRPlayer(val uuid: UUID): Cacheable {

    @Column("credits", "INTEGER")
    var credits: Int? = null

    @Column("squad", "STRING")
    var squad: UUID? = null

    override fun table(): String {
        return "players"
    }
}