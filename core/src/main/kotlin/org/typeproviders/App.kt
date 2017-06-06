package org.typeproviders

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.JsonNodeType

typealias JacksonObjectNode = com.fasterxml.jackson.databind.node.ObjectNode


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

fun getElements(jsonNode: JsonNode): List<Pair<String, JsonNode>> {
    val result = mutableListOf<Pair<String, JsonNode>>()
    val fields = jsonNode.fields()
    while (fields.hasNext()) {
        val node = fields.next()
        result.add(Pair(node.key, node.value))
    }
    return result.toList()
}

fun makeObjectNode(name: String = "", jsonNode: JsonNode): ObjectNode {
    //return ObjectNode("", "", listOf<ContainerNode>().asSequence())

    when(jsonNode.nodeType) {
        JsonNodeType.OBJECT -> {
            val result = getElements(jsonNode)

            //makeObjectNode(name, (jsonNode as JacksonObjectNode).)
        }
        else -> {

        }
    }

    return ObjectNode("", "", listOf<ContainerNode>().asSequence())
}

