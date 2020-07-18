package io.github.marioalvial.kealth.testing

import io.github.marioalvial.kealth.core.CriticalLevel
import io.github.marioalvial.kealth.core.HealthComponent
import io.github.marioalvial.kealth.core.HealthStatus
import kotlinx.coroutines.asContextElement
import kotlin.coroutines.CoroutineContext

class SecondComponent : HealthComponent() {

    override val name = "second-component"
    override val criticalLevel = CriticalLevel.HIGH
    private val threadLocal = ThreadLocal<String>().apply { set("Thread Local $name") }
    override var componentContext: CoroutineContext = threadLocal.asContextElement()

    override fun healthCheck(): HealthStatus {
        println("Starting isHealth of $name in thread ${Thread.currentThread().name}")

        Thread.sleep(200)

        threadLocal.get() ?: throw RuntimeException("$name context does not have thread local element")

        println("Finish isHealth of $name - 200ms")

        return HealthStatus.HEALTHY
    }

    override fun handleFailure(throwable: Throwable) {
        println(throwable.message)
    }
}
