package net.novapixelnetwork.spaceraiders.entity

import net.novapixelnetwork.gamecore.mysql.Cacheable
import net.novapixelnetwork.gamecore.mysql.CacheableEntity
import net.novapixelnetwork.gamecore.mysql.Column
import net.novapixelnetwork.gamecore.mysql.PrimaryKey
import java.util.*

/**
 * Created by owner on 1/5/2018.
 */
@Cacheable("players") class SRPlayer(uuid: String): CacheableEntity() {

    @PrimaryKey
    @Column("uuid", "VARCHAR(36)")
    val uuid = UUID.fromString(uuid)

    @Column("credits", "INTEGER")
    var credits: Int? = null

    @Column("squad", "VARCHAR(16)")
    var squad: UUID? = null
}