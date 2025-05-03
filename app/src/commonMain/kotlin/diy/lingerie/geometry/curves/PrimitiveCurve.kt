package diy.lingerie.geometry.curves

import diy.lingerie.algebra.NumericObject
import diy.lingerie.geometry.Point
import diy.lingerie.geometry.curves.bezier.BezierCurve
import diy.lingerie.geometry.splines.Spline
import diy.lingerie.geometry.transformations.Transformation

abstract class PrimitiveCurve : OpenCurve() {
    abstract class Edge : NumericObject {
        abstract fun bind(
            start: Point,
            end: Point,
        ): PrimitiveCurve

        fun semiBind(
            end: Point,
        ): Spline.Link {
            return Spline.Link(
                edge = this,
                end = end,
            )
        }

        abstract fun transformBy(
            transformation: Transformation,
        ): Edge
    }

    // TODO: Make this final
    override val subCurves: List<PrimitiveCurve>
        get() = listOf(this)

    abstract val edge: Edge

    abstract override fun splitAt(
        coord: Coord,
    ): Pair<PrimitiveCurve, PrimitiveCurve>

    abstract fun evaluate(coord: Coord): Point

    abstract override fun transformBy(
        transformation: Transformation,
    ): PrimitiveCurve

    abstract fun toBezier(): BezierCurve
}
