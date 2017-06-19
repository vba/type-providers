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
// OOOOOOOLLLLLLLLLLDDDDDDDDDDD

fun getType(wrapper: NodeWrapper) : Type {
    return when(wrapper.node.nodeType) {
        JsonNodeType.BINARY -> emptyArray<Byte>().javaClass
        JsonNodeType.BOOLEAN -> false.javaClass
        JsonNodeType.NUMBER -> 1L.javaClass
        JsonNodeType.STRING -> "".javaClass
        JsonNodeType.ARRAY -> emptyArray<Any>().javaClass
        else -> {
            object{}.javaClass
        }
    }
}

// jsonschema2pojo
fun makeClass (wrappers: Iterable<NodeWrapper>) : TPClass {
   /* val grupped = wrappers.groupBy { it.level }
    val associativeMap = (0 ..(wrappers.map { it.level }.max() ?: 0))
        .fold(emptyList<Pair<NodeWrapper?, TPClass>>(), { acc, i ->
            val parents = grupped.get(i)?.groupBy { it.parent } ?: emptyMap()
            val classes = parents.keys.map {
                val fields = parents.get(it)?.map {
                    TPField(it.name, TPType.Primitive(getType(it), it.isArray))
                }?.toList() ?: emptyList()
                Pair(it, TPClass(fields))
            }
            acc + classes
        }).map { it.first to it.second }.toMap()*/
    return TPClass(emptyList<TPField>())
}

fun collectElements(jsonNode: JsonNode, level: Int, parent: NodeWrapper?): List<NodeWrapper> {
    val getWrappers = { x :JsonNode ->
        val fields = x.fields()
        var wrappers = emptyList<NodeWrapper>()
        while (fields.hasNext()) {
            val node = fields.next()
            wrappers += (NodeWrapper(node.key, level, node.value, parent = parent, isArray = node.value.isArray))
        }
        wrappers
    }
    if (!jsonNode.isArray()) {
        return getWrappers(jsonNode)
    }
    val arrayNode = jsonNode as ArrayNode
    return arrayNode.toList().map{ NodeWrapper("", level, it, parent = parent, isArray = it.isArray) }
}

/*
list nodes_to_visit = {root};
while( nodes_to_visit isn't empty ) {
  currentnode = nodes_to_visit.take_first();
  nodes_to_visit.append( currentnode.children );
  //do something
}
 */

fun getElements(jsonNode: JsonNode): List<NodeWrapper> {
    var nodes = emptyList<NodeWrapper>()
    val awaitingNodes = mutableListOf<NodeWrapper>()
    awaitingNodes.addAll(collectElements(jsonNode, 0, null))

    var current : NodeWrapper? = null
    var prev : NodeWrapper?

    while (!awaitingNodes.isEmpty()) {
        prev = current
        current = awaitingNodes.removeAt(0)

        awaitingNodes.addAll(0, collectElements(current.node, current.level + 1, parent = current))
        nodes += current
//        if (!current.node.isObject) {
//            current.value = current.node.toString()
//        } else {
//        }
//        if (prev == null || prev.level == current.level) {
//            lastLeaf = Tree.Leaf(current.name, current.node)
//            trees += listOf(lastLeaf)
//        } else {
//            val node = Tree.Node(name = (lastLeaf as Tree.Leaf<*>).name)
//            trees = trees.take(trees.size - 1) + listOf(node)
//        }
    }
    return nodes
}

//fun getElements(jsonNode: JsonNode): Tree {
//    var current = collectElements(jsonNode)
//    var next: List<JacksonObjectPair>? = null
//
//    while (true) {
//        if (current.isEmpty()) break
//            next = current.
//        }
//    }
//    return collectElements(jsonNode).map {
//        if(!it.second.isObject && !it.second.isPojo) {
//            Tree.Leaf(it.first, it.second.asText())
//        } else {
//            Tree.Node(it.first, getElements(it.second))
//        }
//    }.fold()
//}

fun makeObjectNode(name: String = "", jsonNode: JsonNode): ObjectNode {
    //return ObjectNode("", "", listOf<ContainerNode>().asSequence())

    //val elements = collectElements(jsonNode)
    val elements = makeClass(getElements(jsonNode))

    return ObjectNode("", "", listOf<ContainerNode>().asSequence())
}

open class Tree private constructor() {
    class Leaf<out T>(val name: String, val value: T) : Tree()
    class Node(val name: String, var subTrees: List<Tree> = listOf()) : Tree()
}
