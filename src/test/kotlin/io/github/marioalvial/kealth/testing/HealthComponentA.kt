package io.github.marioalvial.kealth.testing

import io.github.marioalvial.kealth.HealthComponent
import io.github.marioalvial.kealth.HealthStatus
import kotlinx.coroutines.delay

class HealthComponentA : HealthComponent() {

    override val name = "component A"

    override suspend fun doHealthCheck(): HealthStatus {
        println("Starting isHealth of component $name")
        delay(4000)
        println("Finish isHealth of component $name - 4s")
        return HealthStatus.HEALTHY
    }

    override suspend fun handleFailure(throwable: Throwable?) {
        println("Starting handleFailure of component $name")
        delay(2000)
        println("Finish handle failure of component $name - 2s")
    }
}