package org.typeproviders

abstract class ContainerNode(open val name: String)

data class SequenceNode(override val name: String, val type: String, val content: Sequence<ContainerNode>) : ContainerNode(name)

data class ObjectNode(override val name: String, val type: String, val values: Sequence<ContainerNode>) : ContainerNode(name)

data class ValueNode(override val name: String, val value: Any, val type: String): ContainerNode(name)



