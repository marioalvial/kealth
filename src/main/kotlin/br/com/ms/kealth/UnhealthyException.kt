package br.com.ms.kealth

import java.lang.RuntimeException

class UnhealthyException(
    private val unhealthyIndicators: List<HealthIndicator>
) : RuntimeException() {

    fun response() = mapOf(
        "message" to "Health Check considered this service unhealthy",
        "unhealthy_components" to unhealthyIndicators
            .map { UnhealthyComponentResponse(it.componentName, it.criticalLevel.name) }
    )
}