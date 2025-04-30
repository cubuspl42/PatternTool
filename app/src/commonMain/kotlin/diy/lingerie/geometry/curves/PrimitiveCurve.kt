package diy.lingerie.geometry.curves

import diy.lingerie.algebra.NumericObject
import diy.lingerie.geometry.Point
import diy.lingerie.geometry.transformations.Transformation

abstract class PrimitiveCurve : OpenCurve() {
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

    // TODO: Make this final
    override val subCurves: List<PrimitiveCurve>
        get() = listOf(this)

    abstract val edge: Edge

    abstract fun splitAt(
        coord: Coord,
    ): Pair<PrimitiveCurve, PrimitiveCurve>

    abstract fun evaluate(coord: Coord): Point
}
