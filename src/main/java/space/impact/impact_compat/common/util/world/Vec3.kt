package space.impact.impact_compat.common.util.world

data class Vec3(
    var x: Int,
    var y: Int,
    var z: Int
) {
    companion object {
        fun create() = Vec3(0, 0, 0)
    }
}
