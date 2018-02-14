package net.novapixelnetwork.gamecore.messaging

import net.novapixelnetwork.spaceraiders.SpaceRaiders
import org.bukkit.ChatColor
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

object Messages {

    private val msgFile = "messages.yml"
    private val msgCfg: FileConfiguration

    init {
        val f = File(SpaceRaiders.getPlugin().dataFolder, msgFile)
        if(!f.exists())
            f.createNewFile()

        msgCfg = YamlConfiguration.loadConfiguration(f)
        Message.values()
                .filterNot { msgCfg.contains(it.getConfigPath()) }
                .forEach { msgCfg.set(it.getConfigPath(), it.msg) }
    }

    fun msg(msg: Message, vararg placeholderReplacements: String): String{
        val cfgMsg = ChatColor.translateAlternateColorCodes('&', msgCfg.getString(msg.getConfigPath()))
        for((x, placeholder) in msg.placeholders.withIndex()){
            if(placeholderReplacements.size - 1 >= x){
                cfgMsg.replace("%" + placeholder, placeholderReplacements[x])
            }
        }
        return cfgMsg
    }


}