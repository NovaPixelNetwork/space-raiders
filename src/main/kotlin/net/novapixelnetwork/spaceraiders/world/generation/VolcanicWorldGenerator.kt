package net.novapixelnetwork.spaceraiders.world.generation

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.generator.BlockPopulator
import org.bukkit.generator.ChunkGenerator
import org.bukkit.util.noise.SimplexOctaveGenerator
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class VolcanicWorldGenerator: ChunkGenerator() {

    private val baseHeight = 32
    private val hillAmplifier = 12
    private val amplitude = 0.5
    private val frequency = 0.5
    private val octaves = 8
    private val scale = 1 / 64.0

    override fun getFixedSpawnLocation(world: World?, random: Random?) = Location(world, 4.0, 4.0, 4.0)

    override fun generateChunkData(world: World?, random: Random?, chunkX: Int, chunkZ: Int, biome: ChunkGenerator.BiomeGrid?): ChunkGenerator.ChunkData {
        val chunkData = createChunkData(world)

        val simplex = SimplexOctaveGenerator(world, octaves)
        simplex.setScale(scale)

        for (x in 0..16) {
            for (z in 0..16) {
                chunkData.setBlock(x, 0, z, Material.BEDROCK)

                val height: Int = (baseHeight + simplex.noise(x + chunkX * 16.0, z + chunkZ * 16.0, frequency, amplitude) * hillAmplifier).roundToInt()
                chunkData.setRegion(x, 1, z, x + 1, height, z + 1, Material.STONE)
                chunkData.setRegion(x, height, z, x + 1, height + 1, z + 1, Material.GRASS)
            }
        }

        return chunkData
    }

    override fun canSpawn(world: World, x: Int, z: Int) = true

    override fun getDefaultPopulators(world: World?) = ArrayList<BlockPopulator>()

}