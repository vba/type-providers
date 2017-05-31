package org.typeproviders

import java.io.File
import net.bytebuddy.ByteBuddy
import net.bytebuddy.implementation.FixedValue
import net.bytebuddy.matcher.ElementMatchers.named


fun main(args: Array<String>) {
    val buildFolder = "/Users/victor/projects/type-providers/core/build/typeproviders"

    ByteBuddy()
        .subclass(Any::class.java)
        .name("org.typeproviders.Class1")
        .method(named("toString"))
        .intercept(FixedValue.value("Hello World!"))
        .make()
        .saveIn(File(buildFolder))
}
