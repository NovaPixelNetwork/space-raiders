package net.novapixelnetwork.spaceraiders.data

import net.novapixelnetwork.spaceraiders.SpaceRaiders
import java.io.File

object DataFolders {

    val root = SpaceRaiders.getPlugin().dataFolder
    val engines = File(root, "parts/engine")
    val hulls = File(root, "parts/hull")
    val ships = File(root, "ships")

    init {

        engines.mkdirs()
        hulls.mkdirs()
        hulls.mkdirs()
        ships.mkdirs()

    }


}