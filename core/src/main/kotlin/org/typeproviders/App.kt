package org.typeproviders

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File
import net.bytebuddy.ByteBuddy
import net.bytebuddy.implementation.FixedValue
import net.bytebuddy.matcher.ElementMatchers.named
import java.net.URL


fun main(args: Array<String>) {
//    val buildFolder = "/Users/victor/projects/type-providers/core/build/typeproviders"
    val jsonUrl = URL("https://api.github.com/users/vba/repos")

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
    println (tree)

    /*
    *  Structured sources
    *  download / load a structure
    *  convert it to java representation
    *  compile it to the disk
    *
    *
    * */
}
