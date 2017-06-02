package org.typeproviders

abstract class DtoMember (open val name: String)

data class DtoSequence(override val name: String, val type: String, val values: Sequence<DtoMember>) : DtoMember(name)

data class DtoObject(override val name: String, val values: Sequence<DtoMember>) : DtoMember(name)

fun f1 () {
    DtoObject("dsfdsf", listOf<DtoMember>().asSequence()).name
}