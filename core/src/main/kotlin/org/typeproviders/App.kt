package org.typeproviders

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeType
import java.lang.reflect.Type
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

typealias JacksonObjectNode = com.fasterxml.jackson.databind.node.ObjectNode
typealias JacksonObjectPair = Pair<String, JsonNode>

data class NodeWrapper(val name: String,
                       val level: Int,
                       val node: JsonNode,
                       val isArray: Boolean = false,
                       var parent: NodeWrapper? = null)


open class TPType private constructor() {
    data class Primitive(val type: Class<Any>, val isArray : Boolean = false) : TPType()
    data class Reference(val type: TPClass, val isArray : Boolean = false) : TPType()
}
data class TPClass(val fields: Iterable<TPField>)
data class TPField(val name: String, val type: TPType)

fun main(args: Array<String>) {
    //    val buildFolder = "/Users/victor/projects/type-providers/core/build/typeproviders"
    //    val jsonUrl = URL("https://api.github.com/users/vba/repos")
    val jsonUrl = Any().javaClass.getResource("/json/sample1.json")

    //    ByteBuddy()
    //        .subclass(Any::class.java)
    //        .name("org.typeproviders.Class1")
    //        .method(named("toString"))
    //        .intercept(FixedValue.node("Hello World!"))
    //        .make()
    //        .saveIn(File(buildFolder))

    val mapper = ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .enable(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY)
    val tree = mapper.readTree(jsonUrl)
    val map = mapper.convertValue<Map<String, *>>(tree, object : TypeReference<Map<String, *>>(){})
    //val f5 = map["field5"]?.javaClass?.canonicalName
    //val f6 = map["field6"]

    // tree.get(0).fields().asSequence().toList()[2].node.nodeType
    getTType(map)
    //makeObjectNode("", tree)
    println(tree)

    /*
    *  Structured sources
    *  download / load a structure
    *  convert it to java representation
    *  compile it to the disk
    *
    *
    * */
}
fun getTType(map: Map<String, *>): TPType {
    map.entries
    var nodes = emptyList<NodeWrapper>()
    val awaitingNodes = mutableListOf<Pair<String, *>>()
    awaitingNodes.addAll(map.entries.map {Pair(it.key, it.value)})

    var current : Pair<String, *>? = null
    var prev : Pair<String, *>?

    while (!awaitingNodes.isEmpty()) {
        prev = current
        current = awaitingNodes.removeAt(0)

        if(current.second is Map<*, *>) {
            val subMap = current.second as Map<*, *>
            awaitingNodes.addAll(0, subMap.entries.map { Pair(it.key.toString(), it.value) })
        } else {
            val type = (current.second?.javaClass ?: Any::class.java)
            TPField(current.first, TPType.Primitive(type))
        }
    }

    return TPType.Primitive(object : Any() {}.javaClass)
}

open class Tree private constructor() {
    class Leaf<out T>(val name: String, val value: T) : Tree()
    class Node(val name: String, var subTrees: List<Tree> = listOf()) : Tree()
}
