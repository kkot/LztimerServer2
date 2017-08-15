package com.lztimer.server.webapi

import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.stereotype.Controller

@Controller
open class A() {
    init {
        println("A is being created")
    }

    constructor(secondary: String) : this() {
        println("A secondary")

    }
}

@Component
class AProvider() {

    @Bean
    fun getA(): A {
        return A("x");
    }

}

//@Component
//class NeedsA(a : A) {
//
//    init {
//        println(a)
//    }
//}

//@Component
//class NeedsB(b : B) {
//
//    init {
//        println(b)
//    }
//}
