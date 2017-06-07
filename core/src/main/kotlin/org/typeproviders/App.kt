package org.typeproviders

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

typealias JacksonObjectNode = com.fasterxml.jackson.databind.node.ObjectNode
typealias JacksonObjectPair = Pair<String, JsonNode>

fun main(args: Array<String>) {
    //    val buildFolder = "/Users/victor/projects/type-providers/core/build/typeproviders"
    //    val jsonUrl = URL("https://api.github.com/users/vba/repos")
    val jsonUrl = Any().javaClass.getResource("/json/sample1.json")

    //    ByteBuddy()
    //        .subclass(Any::class.java)
    //        .name("org.typeproviders.Class1")
    //        .method(named("toString"))
    //        .intercept(FixedValue.value("Hello World!"))
    //        .make()
    //        .saveIn(File(buildFolder))

    val mapper = ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .enable(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY)
    //    val map = mapper.readValue<Map<String, Any?>>(jsonUrl)
    val tree = mapper.readTree(jsonUrl)
    // tree.get(0).fields().asSequence().toList()[2].value.nodeType
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

fun collectElements(jsonNode: JsonNode): List<JacksonObjectPair> {
    val result = mutableListOf<JacksonObjectPair>()
    val fields = jsonNode.fields()
    while (fields.hasNext()) {
        val node = fields.next()
        result.add(Pair(node.key, node.value))
    }
    return result.toList()
}


fun getElements(jsonNode: JsonNode): Tree {
    return collectElements(jsonNode).map {
        if(!it.second.isObject && !it.second.isPojo) {
            Tree.Leaf(it.first, it.second.asText())
        } else {
            Tree.Node(it.first, getElements(it.second))
        }
    }.fold()
}

fun makeObjectNode(name: String = "", jsonNode: JsonNode): ObjectNode {
    //return ObjectNode("", "", listOf<ContainerNode>().asSequence())

    //val elements = collectElements(jsonNode)
    val elements = getElements(jsonNode)

    return ObjectNode("", "", listOf<ContainerNode>().asSequence())
}

open class Tree private constructor() {
    class Leaf<out T>(val name: String, val value: T) : Tree()
    class Node(val name: String, val subTrees: Sequence<Tree>) : Tree()
}
