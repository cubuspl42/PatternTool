package diy.lingerie.geometry

import diy.lingerie.algebra.Vector2
import kotlin.jvm.JvmInline

sealed interface Direction {
    /**
     * A direction without associated orientation
     */
    @JvmInline
    value class Vague(
        /**
         * One of two precise directions
         */
        val representativeDirection: Precise,
    ) : Direction {
        override val directionVector: Vector2
            get() = representativeDirection.directionVector
    }

    /**
     * A direction with associated orientation
     */
    @JvmInline
    value class Precise(
        /**
         * Normalized direction vector
         */
        val directionVector: Vector2,
    )

    val directionVector: Vector2
}


data class Line(
    val representativePoint: Point,
    val vagueDirection: Direction.Vague,
) {
    companion object {
        fun throughPoints(
            point0: Point,
            point1: Point,
        ): Line? {
            TODO("Not yet implemented")
        }
    }
}
