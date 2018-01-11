package net.novapixelnetwork.spaceraiders.mysqltables

import org.jetbrains.squash.definition.*

class SRPlayer: TableDefinition("playerstest") {

    val id = uuid("uuid").primaryKey()
    val squad = varchar("squad", length=50).nullable()
    val credits = integer("credits")


}