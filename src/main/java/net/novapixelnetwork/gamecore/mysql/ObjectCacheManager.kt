package net.novapixelnetwork.gamecore.mysql

import net.novapixelnetwork.spaceraiders.SpaceRaiders
import org.bukkit.scheduler.BukkitRunnable

class ObjectCacheManager {

    fun startTask(){
        CacheGarbageCollector().runTaskTimerAsynchronously(SpaceRaiders.getPlugin(), 0, 20 * 40)
    }


    companion object {
        internal val INSTANCE = ObjectCacheManager()
    }




    internal class CacheGarbageCollector(): BukkitRunnable() {
        override fun run() {


        }

    }

}