package net.novapixelnetwork.spaceraiders.entity

class ShipPart(val name: String, val section: Section) {


    enum class Section(val readableName: String) {
        HULL("Hull"), ENGINE("Engine")
    }

}