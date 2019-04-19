package br.com.ms.kealth

data class UnhealthyComponentResponse(
    val name: String,
    val criticality: CriticalLevel
)