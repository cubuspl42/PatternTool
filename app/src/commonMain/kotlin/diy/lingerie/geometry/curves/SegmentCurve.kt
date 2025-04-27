package diy.lingerie.geometry.curves

import diy.lingerie.algebra.NumericObject
import diy.lingerie.algebra.equalsWithTolerance
import diy.lingerie.geometry.Point
import diy.lingerie.geometry.Line
import diy.lingerie.geometry.transformations.Transformation
import kotlin.jvm.JvmInline

abstract class SegmentCurve : NumericObject {
    @JvmInline
    value class Coord(
        /**
         * The t-value for the basis function
         */
        val t: Double,
    ) : NumericObject {
        val complement: Coord
            get() = Coord(
                t = 1.0 - t,
            )

        init {
            require(t in 0.0..1.0)
        }

        override fun equalsWithTolerance(
            other: NumericObject,
            tolerance: NumericObject.Tolerance,
        ): Boolean = when {
            other !is Coord -> false
            !t.equalsWithTolerance(other.t, tolerance) -> false
            else -> true
        }
    }

    /**
     * The intersection of two curves
     */
    abstract class Intersection {
        /**
         * The point of intersection
         */
        abstract val point: Point

        /**
         * The coordinate of the intersection point on the subject curve
         */
        abstract val coord: Coord

        /**
         * The coordinate of the intersection point on the other curve
         */
        abstract val otherCoord: Coord
    }

    /**
     * Find the intersections of this curve (also referred to as the "subject
     * curve") with the [other] curve.
     */
    fun findIntersections(
        other: SegmentCurve,
    ): Set<Intersection> {
        TODO()
    }

    abstract class Edge : NumericObject {
        abstract fun bind(
            start: Point,
            end: Point,
        ): SegmentCurve

        abstract fun transformBy(
            transformation: Transformation,
        ): Edge
    }

    abstract val start: Point

    abstract val edge: Edge

    abstract val end: Point

    abstract fun splitAt(
        coord: Coord,
    ): Pair<SegmentCurve, SegmentCurve>

    abstract fun evaluate(coord: Coord): Point
}
