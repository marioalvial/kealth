package io.github.marioalvial.kealth.testing

import io.github.marioalvial.kealth.HealthComponent
import io.github.marioalvial.kealth.HealthStatus
import kotlinx.coroutines.delay

class HealthComponentB : HealthComponent {

    override val name = "component B"

    override suspend fun isHealth(): HealthStatus {
        println("Starting isHealth of component $name")
        delay(2000)
        println("Finish isHealth of component $name - 2s")
        return HealthStatus.UNHEALTHY
    }

    override suspend fun handleFailure(throwable: Throwable?) {
        println("Starting handleFailure of component $name")
        delay(6000)
        println("Finish handle failure of component $name - 6s")
    }
}