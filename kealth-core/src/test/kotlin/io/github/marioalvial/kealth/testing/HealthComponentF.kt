package io.github.marioalvial.kealth.testing

import io.github.marioalvial.kealth.core.CriticalLevel
import io.github.marioalvial.kealth.core.HealthComponent
import io.github.marioalvial.kealth.core.HealthStatus
import kotlin.coroutines.CoroutineContext

class HealthComponentF : HealthComponent() {

    override val name = "component F"
    override val criticalLevel = CriticalLevel.HIGH

    override fun doHealthCheck(): HealthStatus {
        println("Starting isHealth of component $name in thread ${Thread.currentThread().name}")

        Thread.sleep(350)

        addParameter("info", "Birds can fly")

        println("Finish isHealth of component $name - 350ms")

        return HealthStatus.UNHEALTHY
    }

    override fun handleException(throwable: Throwable) {
        println("Starting handleException of $name")

        Thread.sleep(350)

        println("Finish handle failure of component $name - 350ms")
    }

    override fun handleUnhealthyStatus() {
        println("Starting handleUnhealthyStatus of $name")

        val coreInformation = parameters()["info"] ?: throw IllegalArgumentException("Key does not exist")

        println("Printing core information for debugging: $coreInformation")

        Thread.sleep(350)

        println("Finish handleUnhealthyStatus of component $name - 350ms")
    }

    override fun handleCoroutineException(coroutineContext: CoroutineContext, exception: Throwable) {
        println("Coroutine throws exception with context $coroutineContext and error ${exception.printStackTrace()}")
    }
}