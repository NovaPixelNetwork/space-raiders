package net.novapixelnetwork.spaceraiders.world.generation

import org.bukkit.generator.ChunkGenerator

enum class Generator {
    VOLCANIC;

    fun getGenerator(): ChunkGenerator {
        when(this) {
            VOLCANIC -> return VolcanicWorldGenerator()
        }
    }
}