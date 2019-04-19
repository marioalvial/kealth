package br.com.ms.kealth

data class HealthResponse(
    val name: String,
    val status: HealthStatus
)