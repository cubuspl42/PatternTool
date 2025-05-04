package diy.lingerie.geometry.curves

import diy.lingerie.math.algebra.NumericObject
import diy.lingerie.math.algebra.equalsWithTolerance
import diy.lingerie.geometry.Point
import diy.lingerie.geometry.splines.OpenSpline
import diy.lingerie.geometry.transformations.Transformation
import kotlin.jvm.JvmInline

/**
 * A curve defined in range t [0, 1] that is open (having a specified start and
 * end point), assumed to be tangent-continuous (G1).
 */
abstract class OpenCurve : NumericObject {
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

    abstract val start: Point

    abstract val end: Point

    abstract val subCurves: List<PrimitiveCurve>

    abstract fun transformBy(
        transformation: Transformation,
    ): OpenCurve

    abstract fun splitAt(
        coord: Coord,
    ): Pair<OpenCurve, OpenCurve>

    /**
     * Find the offset curve (or its close approximation) of this curve
     */
    abstract fun findOffsetCurve(
        offset: Double,
    ): OpenCurve

    /**
     * Find the intersections of this curve (also referred to as the "subject
     * curve") with the [other] curve.
     */
    fun findIntersections(
        other: OpenCurve,
    ): Set<Intersection> {
        TODO()
    }
}
