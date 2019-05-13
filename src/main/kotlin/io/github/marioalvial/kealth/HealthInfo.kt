package io.github.marioalvial.kealth

/**
 * Class that aggregates information about the execution of component's health check
 */
class HealthInfo(
    val status: HealthStatus,
    val duration: Long
)