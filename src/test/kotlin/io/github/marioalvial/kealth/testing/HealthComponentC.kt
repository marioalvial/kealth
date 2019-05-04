package io.github.marioalvial.kealth.testing

import io.github.marioalvial.kealth.HealthComponent
import io.github.marioalvial.kealth.HealthStatus
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HealthComponentC : HealthComponent() {

    override val name = "component C"

    override fun doHealthCheck(): HealthStatus {
        println("Starting isHealth of component $name")

        Thread.sleep(300)

        println("Finish isHealth of component $name - 300ms")

        throw RuntimeException("$name throws exception")
    }

    override fun handleFailure(throwable: Throwable) {
        println("Starting handleFailure of $name")

        Thread.sleep(300)

        println("Finish handle failure of component $name - 300ms")
    }
}