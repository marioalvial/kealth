package io.github.marioalvial.kealth.testing

import io.github.marioalvial.kealth.HealthComponent
import io.github.marioalvial.kealth.HealthStatus
import kotlinx.coroutines.asContextElement
import kotlin.coroutines.CoroutineContext

class HealthComponentB : HealthComponent() {

    override val name = "component B"
    private val threadLocal = ThreadLocal<String>().apply { set("Thread Local $name") }

    override fun doHealthCheck(): HealthStatus {
        println("Starting isHealth of component $name")

        Thread.sleep(200)

        println("Finish isHealth of component $name - 200ms")

        throw RuntimeException("$name throws exception")
    }

    override fun handleFailure(throwable: Throwable) {
        val componentName = threadLocal.get() ?: throw RuntimeException()

        println("Starting handleFailure of $componentName")

        Thread.sleep(200)

        println("Finish handle failure of component $name - 200ms")
    }

    override fun context(): CoroutineContext = threadLocal.asContextElement()
}