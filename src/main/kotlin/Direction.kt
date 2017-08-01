enum class Direction {
    TOP, RIGHT, BOTTOM, LEFT;

    fun next() = when (this) {
        Direction.LEFT -> BOTTOM
        Direction.BOTTOM -> RIGHT
        Direction.RIGHT -> TOP
        Direction.TOP -> LEFT
    }
}