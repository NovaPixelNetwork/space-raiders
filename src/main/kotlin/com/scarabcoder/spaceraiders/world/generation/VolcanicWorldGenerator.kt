/*
 * Copyright 2018 Nicholas Harris
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 *  OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.scarabcoder.spaceraiders.world.generation

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