package org.typeproviders.json

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.typeproviders.NodeWrapper
import org.typeproviders.TPField
import org.typeproviders.TPType
import java.net.URI
import java.net.URL

class JsonProvider {

    val mapper = ObjectMapper()
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .enable(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY)


    fun mapSourceToTree(jsonUrl: URL): TPType {
        return getTType(mapper.readTree(jsonUrl))
    }
    fun mapSourceToTree(json: String): TPType {
        return getTType(mapper.readTree(json))
    }

    private fun getTType(tree: JsonNode): TPType {
        val map = mapper.convertValue<Map<String, *>>(tree, object : TypeReference<Map<String, *>>(){})
        return getTType(map)
    }

    private fun getTType(map: Map<String, *>): TPType {
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
}