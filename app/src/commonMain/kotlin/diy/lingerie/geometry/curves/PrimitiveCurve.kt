package diy.lingerie.geometry.curves

import diy.lingerie.algebra.NumericObject
import diy.lingerie.geometry.Point
import diy.lingerie.geometry.transformations.Transformation

abstract class PrimitiveCurve : Curve() {
    /**
     * Find the intersections of this curve (also referred to as the "subject
     * curve") with the [other] curve.
     */
    fun findIntersections(
        other: PrimitiveCurve,
    ): Set<Intersection> {
        TODO()
    }

    abstract class Edge : NumericObject {
        abstract fun bind(
            start: Point,
            end: Point,
        ): PrimitiveCurve

        abstract fun transformBy(
            transformation: Transformation,
        ): Edge
    }

    abstract val start: Point

    abstract val edge: Edge

    abstract val end: Point

    abstract fun splitAt(
        coord: Coord,
    ): Pair<PrimitiveCurve, PrimitiveCurve>

    abstract fun evaluate(coord: Coord): Point
}
