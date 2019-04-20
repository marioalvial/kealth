package io.github.marioalvial.kealth.testing

import io.github.marioalvial.kealth.HealthComponent
import io.github.marioalvial.kealth.HealthStatus
import kotlinx.coroutines.delay

class HealthComponentC : HealthComponent {

    override val name = "component C"

    override suspend fun isHealth(): HealthStatus {
        println("Starting isHealth of component $name")
        delay(500)
        println("Finish isHealth of component $name - 500ms")
        throw RuntimeException("$name throws exception")
    }

    override suspend fun handleFailure(throwable: Throwable?) {
        println("Starting handleFailure of component $name")
        delay(3200)
        println("Finish handle failure of component $name - 3.2s")
    }
}