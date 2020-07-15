package io.github.marioalvial.kealth.testing

import io.github.marioalvial.kealth.core.CriticalLevel
import io.github.marioalvial.kealth.core.HealthComponent
import io.github.marioalvial.kealth.core.HealthStatus
import kotlin.coroutines.CoroutineContext

class HealthComponentC : HealthComponent() {

    override val name = "component C"
    override val criticalLevel = CriticalLevel.LOW

    override fun doHealthCheck(): HealthStatus {
        println("Starting isHealth of component $name in thread ${Thread.currentThread().name}")

        Thread.sleep(300)

        println("Finish isHealth of component $name - 300ms")

        throw RuntimeException("$name throws exception")
    }

    override fun handleFailure(throwable: Throwable) {
        println("Starting handleException of $name in thread ${Thread.currentThread().name}")

        Thread.sleep(300)

        println("Finish handle failure of component $name - 300ms")
    }
}
