package net.novapixelnetwork.gamecore.commandapi

import net.novapixelnetwork.spaceraiders.SpaceRaiders

class Logger {

    private val prefix = "SpaceRaiders";

    fun log(msg:String, level: Level){
        if(!(level == Level.DEBUG && SpaceRaiders.getPlugin().config.getBoolean("debug"))){
            System.out.println("[$prefix] [$level] $msg")
        }
    }


    companion object {
        var logger: Logger = Logger();
    }

    enum class Level {
        INFO, WARNING, ERROR, DEBUG
    }
}