package space.impact.impact_compat.core

@Suppress("unused")
object WorldTick {
    const val ZERO = 0L

    const val SECOND_HALF = 10L
    const val SECOND = 20L
    const val SECOND_3 = 60L
    const val SECOND_10 = 200L
    const val SECOND_30 = 600L

    const val MINUTE = 1200L


    infix fun Long.of(value: Long): Boolean {
        return this % value == 0L
    }
}