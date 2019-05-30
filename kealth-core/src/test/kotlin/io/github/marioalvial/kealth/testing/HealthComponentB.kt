package io.github.marioalvial.kealth.testing

import io.github.marioalvial.kealth.core.CriticalLevel
import io.github.marioalvial.kealth.core.HealthComponent
import io.github.marioalvial.kealth.core.HealthStatus
import kotlinx.coroutines.asContextElement
import kotlin.coroutines.CoroutineContext

class HealthComponentB : HealthComponent {

    override val name = "component B"
    override val criticalLevel = CriticalLevel.HIGH
    private val threadLocal = ThreadLocal<String>().apply { set("Thread Local $name") }

    override fun doHealthCheck(): HealthStatus {
        println("Starting isHealth of component $name in thread ${Thread.currentThread().name}")

        Thread.sleep(200)

        println("Finish isHealth of component $name - 200ms")

        throw RuntimeException("$name throws exception")
    }

    override fun handleFailure(throwable: Throwable) {
        println("Starting handleFailure of $name in thread ${Thread.currentThread().name}")

        threadLocal.get() ?: throw RuntimeException()
        Thread.sleep(200)

        println("Finish handle failure of component $name - 200ms")
    }

    override fun context(): CoroutineContext = threadLocal.asContextElement()
}