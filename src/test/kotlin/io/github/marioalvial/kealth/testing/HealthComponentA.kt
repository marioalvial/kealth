package io.github.marioalvial.kealth.testing

import io.github.marioalvial.kealth.core.HealthComponent
import io.github.marioalvial.kealth.core.HealthStatus

class HealthComponentA : HealthComponent {

    override val name = "component A"

    override fun doHealthCheck(): HealthStatus {
        println("Starting isHealth of component $name in thread ${Thread.currentThread().name}")

        Thread.sleep(100)

        println("Finish isHealth of component $name - 100ms")

        return HealthStatus.HEALTHY
    }

    override fun handleFailure(throwable: Throwable) {
            println("Starting handleFailure of component $name in thread ${Thread.currentThread().name}")

            Thread.sleep(100)

            println("Finish handle failure of component $name - 100ms")
    }
}