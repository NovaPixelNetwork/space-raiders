package net.novapixelnetwork.gamecore.mysql.deprecated

import kotlin.reflect.KClass
import java.util.function.Function

class TypeCaster(val from: KClass<*>, val to: KClass<*>, val func: Function<Any, Any?>) {

}