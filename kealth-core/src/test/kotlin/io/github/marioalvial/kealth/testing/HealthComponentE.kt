package io.github.marioalvial.kealth.testing

import io.github.marioalvial.kealth.core.CriticalLevel
import io.github.marioalvial.kealth.core.HealthComponent
import io.github.marioalvial.kealth.core.HealthStatus

class HealthComponentE : HealthComponent {

    override val name = "component E"
    override val criticalLevel = CriticalLevel.HIGH
    private val threadLocal = ThreadLocal<String>().apply { set("Thread Local $name") }

    override fun doHealthCheck(): HealthStatus {
        println("Starting isHealth of component $name in thread ${Thread.currentThread().name}")

        Thread.sleep(500)
        threadLocal.get() ?: throw IllegalAccessException("You can't access thread local")

        println("Finish isHealth of component $name - 500ms")

        return HealthStatus.HEALTHY
    }

    override fun handleFailure(throwable: Throwable) {
        println("Starting handleFailure of $name")

        Thread.sleep(500)

        println("Finish handle failure of component $name - 500ms")
    }
}