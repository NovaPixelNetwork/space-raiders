package net.novapixelnetwork.gamecore.mysql.deprecated

/**
 * Created by owner on 1/8/2018.
 */
open class CacheableEntity {

    internal val cacheTime = System.currentTimeMillis()
    open val expireTime = 5 * 60 * 100 //5 minutes

    fun shouldExpire(): Boolean {
        return System.currentTimeMillis() - cacheTime >= expireTime
    }

}