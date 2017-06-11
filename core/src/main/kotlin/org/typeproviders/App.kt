package org.typeproviders

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

typealias JacksonObjectNode = com.fasterxml.jackson.databind.node.ObjectNode
typealias JacksonObjectPair = Pair<String, JsonNode>

data class SimpleNode(val name: String, val level: Int, val node: JsonNode, var value: Any? = null)

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
    //    val map = mapper.readValue<Map<String, Any?>>(jsonUrl)
    val tree = mapper.readTree(jsonUrl)
    // tree.get(0).fields().asSequence().toList()[2].node.nodeType
    makeObjectNode("", tree)
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

fun collectElements(jsonNode: JsonNode, level: Int): List<SimpleNode> {
    val result = mutableListOf<SimpleNode>()
    val fields = jsonNode.fields()
    while (fields.hasNext()) {
        val node = fields.next()
        result.add(SimpleNode(node.key, level, node.value))
    }
    return result.toList()
}


/*
list nodes_to_visit = {root};
while( nodes_to_visit isn't empty ) {
  currentnode = nodes_to_visit.take_first();
  nodes_to_visit.append( currentnode.children );
  //do something
}
 */

fun getElements(jsonNode: JsonNode): List<JacksonObjectPair> {
    val awaitingNodes = mutableListOf<SimpleNode>()
    var trees = listOf<Tree>()
    awaitingNodes.addAll(collectElements(jsonNode, 0))

    var current : SimpleNode? = null
    var prev : SimpleNode?
    var lastLeaf : Tree? = null

    while (!awaitingNodes.isEmpty()) {
        prev = current
        current = awaitingNodes.removeAt(0)
        awaitingNodes.addAll(0, collectElements(current.node, current.level + 1))

        if (!current.node.isObject) {
            current.value = current.node.toString()
        } else {

        }

//        if (prev == null || prev.level == current.level) {
//            lastLeaf = Tree.Leaf(current.name, current.node)
//            trees += listOf(lastLeaf)
//        } else {
//            val node = Tree.Node(name = (lastLeaf as Tree.Leaf<*>).name)
//            trees = trees.take(trees.size - 1) + listOf(node)
//        }

    }
    return mutableListOf()
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
    val elements = getElements(jsonNode)

    return ObjectNode("", "", listOf<ContainerNode>().asSequence())
}

open class Tree private constructor() {
    class Leaf<out T>(val name: String, val value: T) : Tree()
    class Node(val name: String, var subTrees: List<Tree> = listOf()) : Tree()
}
