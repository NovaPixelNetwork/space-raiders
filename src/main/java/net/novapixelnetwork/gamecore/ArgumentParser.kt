package net.novapixelnetwork.gamecore

import org.bukkit.entity.Player
import java.util.function.Function
import kotlin.reflect.KClass

class ArgumentParser {

    val arguments = HashMap<KClass<*>, java.util.function.Function<String, Any>>()

    init {

        arguments.put(Int::class, Function<String, Any> {
            t: String ->  {
                t.toInt()
            }
        })
        arguments.put(Double::class, Function<String, Any> {
            t: String ->  {
                t.toDouble()
            }
        })
        arguments.put(Player::class, Function<String, Any> {
            t: String ->  {
                t.toDouble()
            }
        })

    }

    fun registerArgument(type:KClass<*>, func:java.util.function.Function<String, Any>){
        arguments.put(type, func)
    }

    fun parse(arg:String, clazz:KClass<Any>): Any? {
        if(arguments.containsKey(clazz)){
            return arguments[clazz]!!.apply(arg)
        }
        return null
    }

    companion object {
        val INSTANCE = ArgumentParser()
    }

}