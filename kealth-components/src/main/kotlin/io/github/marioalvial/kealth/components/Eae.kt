package io.github.marioalvial.kealth.components

import io.github.marioalvial.kealth.core.HealthComponent

class Eae(jdbcHealthComponent: JdbcHealthComponent) : HealthComponent by jdbcHealthComponent {
}