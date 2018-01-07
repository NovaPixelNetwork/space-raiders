package net.novapixelnetwork.gamecore.commandapi

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.function.Function
import kotlin.reflect.KClass

class ArgumentParser {

    val arguments = HashMap<KClass<*>, Function<String, Any?>>()

    init {

        arguments.put(Int::class, Function<String, Any?> {
            t: String ->  {
            try {
                t.toInt()
            } catch(e: NumberFormatException) {
                throw ArgumentParseException("$t is not a number!")
            }
            }.invoke()
        })
        arguments.put(Double::class, Function<String, Any?> {
            t: String ->  {
                try {
                    t.toDouble()
                } catch(e: NumberFormatException) {
                    throw ArgumentParseException("$t is not a number!")
                }
            }.invoke()
        })
        arguments.put(Player::class, Function<String, Any?> {
            t: String ->  {
                val p: Player? = Bukkit.getPlayer(t) ?: throw ArgumentParseException("Player \"$t\" not found!")
                p

            }.invoke()
        })



    }

    fun registerArgument(type: KClass<*>, func: Function<String, Any?>){
        arguments.put(type, func)
    }

    @Throws(ArgumentParseException::class)
    fun parse(arg:String, clazz: KClass<*>): Any? {
        if(arguments.containsKey(clazz)){
            return arguments[clazz]!!.apply(arg)
        }
        throw CommandException("Could not find a casting function for String -> " + clazz.simpleName + " (contact a developer)")
    }

    companion object {
        val INSTANCE = ArgumentParser()
    }

}