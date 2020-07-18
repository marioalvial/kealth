package io.github.marioalvial.kealth.testing

import io.github.marioalvial.kealth.core.CriticalLevel
import io.github.marioalvial.kealth.core.HealthComponent
import io.github.marioalvial.kealth.core.HealthStatus

class FourthHealthComponent(private val stub: Stub) : HealthComponent() {

    override val name = "fourth-component"
    override val criticalLevel = CriticalLevel.MEDIUM

    override fun healthCheck(): HealthStatus {
        println("Starting isHealth of $name")

        Thread.sleep(500)

        throw RuntimeException("$name throws exception")
    }

    override fun handleUnhealthy() {
        stub.nothing()
    }

    override fun handleFailure(throwable: Throwable) {
        stub.nothing()

        println("Handling failure of $name")

        Thread.sleep(500)

        println("Finish failure logic of $name - 500ms")
    }
}
