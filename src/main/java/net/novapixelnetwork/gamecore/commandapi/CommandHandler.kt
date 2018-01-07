package net.novapixelnetwork.gamecore.commandapi

import net.novapixelnetwork.sendMessage
import net.novapixelnetwork.spaceraiders.enum.MessageType
import org.apache.commons.lang.StringUtils
import org.bukkit.entity.Player
import java.lang.reflect.Parameter
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KType

class CommandHandler {


    internal fun execute(sender: Player, cmd: String, args: List<String>) {
        var section = CommandRegistry.INSTANCE.getSection(cmd)!!
        for (set in CommandRegistry.INSTANCE.getTree(section)) {
            if (StringUtils.join(args, " ").startsWith(set.key)) {
                var modifiedArgs: MutableList<String> = args.subList(if (StringUtils.split(set.key, ' ').isNotEmpty()) StringUtils.split(set.key, ' ').size else 0, args.size).toMutableList()
                if (modifiedArgs.size > 0) {
                    for (method in set.value::class.members) {
                        var hasAn = false
                        for (an in method.annotations) {
                            if (an.annotationClass == Command::class) {
                                hasAn = true
                            }
                        }
                        if (hasAn) {

                        }
                        if (hasAn && method.name == modifiedArgs[0]) {
                            modifiedArgs.removeAt(0)
                            var toPass: MutableList<Any> = ArrayList()
                            toPass.addAll(modifiedArgs)
                            toPass.add(0, sender)
                            var x = 0
                            var newParams: MutableList<KParameter> = method.parameters.toMutableList()
                            newParams.removeAt(0)

                            for (param in newParams) {
                                if (toPass.size == x)
                                    break
                                if (x == 0) {
                                    x++
                                    continue
                                }
                                if (param.type != toPass[x]::class) {
                                    try {
                                        toPass[x] = ArgumentParser.INSTANCE.parse(toPass[x] as String, param.type.classifier as KClass<*>) as Any

                                    } catch(exc: ArgumentParseException) {
                                        sender.sendMessage("Error with argument <${getParamName(param)}>: ${exc.name}", MessageType.ERROR)
                                        return
                                    } catch(exc: CommandException) {
                                        sender.sendMessage(exc.name)
                                        exc.printStackTrace()
                                        return
                                    }

                                }
                                x++
                            }
                            newParams.add(0, method.parameters[0])
                            if (method.parameters.size - 2 == modifiedArgs.size) {
                                method.call(set.value, *toPass.toTypedArray())
                                return
                            } else {
                                var params: String = ""
                                var requiredParams = method.parameters.toMutableList()
                                requiredParams.removeAt(0)
                                requiredParams.removeAt(0)
                                for (param in requiredParams) {
                                    params += "<${getParamName(param)}> "
                                }
                                sender.sendMessage("Invalid arguments. Usage: /${set.key} ${method.name} $params", MessageType.ERROR)
                                return
                            }

                        }
                    }
                } else {
                    section.onCommand(sender)
                }
            }
        }
    }

    private fun getParamName(p: KParameter): String {
        for (a in p.annotations) {
            println(a::class)
            println(Argument::class)
            if (a::class == Argument::class) {
                val arg = a as Argument
                return arg.name
            }
        }
        return p.name!!
    }

    companion object {
        val INSTANCE = CommandHandler()
    }

}