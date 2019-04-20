package io.github.marioalvial.kealth.testing

import io.github.marioalvial.kealth.HealthComponent
import io.github.marioalvial.kealth.HealthStatus
import kotlinx.coroutines.delay

class HealthComponentD : HealthComponent() {

    override val name = "component D"

    override suspend fun doHealthCheck(): HealthStatus {
        println("Starting isHealth of component $name")
        delay(2800)
        println("Finish isHealth of component $name - 2.8s")
        return HealthStatus.HEALTHY
    }

    override suspend fun handleFailure(throwable: Throwable?) {
        println("Starting handleFailure of component $name")
        delay(1000)
        println("Finish handle failure of component $name - 1s")
    }
}