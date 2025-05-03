package diy.lingerie.geometry.curves.bezier

import diy.lingerie.geometry.Point
import diy.lingerie.geometry.curves.PrimitiveCurve

abstract class BezierCurve : PrimitiveCurve() {
    abstract class Edge : PrimitiveCurve.Edge() {
        abstract val firstControl: Point

        abstract val lastControl: Point

        abstract override fun bind(
            start: Point,
            end: Point,
        ): BezierCurve
    }

    final override fun toBezier(): BezierCurve = this

    fun connectsSmoothly(
        nextCurve: BezierCurve,
    ): Boolean {
        require(end == nextCurve.start)

        return false // TODO
    }

    abstract override val edge: BezierCurve.Edge

    abstract val firstControl: Point

    abstract val lastControl: Point

    abstract override val subCurves: List<MonoBezierCurve>

    abstract override fun splitAt(
        coord: Coord,
    ): Pair<BezierCurve, BezierCurve>

    abstract override fun findOffsetCurve(
        offset: Double,
    ): BezierCurve
}
