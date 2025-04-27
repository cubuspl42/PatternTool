package diy.lingerie.geometry.curves.bezier

import diy.lingerie.geometry.Point
import diy.lingerie.geometry.curves.SegmentCurve
import diy.lingerie.geometry.transformations.Transformation

abstract class BezierCurve : SegmentCurve() {
    data class Joint(
        val rearControl: Point,
        val coord: Coord,
        val frontControl: Point,
    ) {
        val position: Point
            get() = TODO()

        fun transformBy(
            transformation: Transformation,
        ): Joint = Joint(
            frontControl = frontControl.transformBy(transformation),
            coord = coord,
            rearControl = rearControl.transformBy(transformation),
        )

    }

    abstract class Edge : SegmentCurve.Edge() {
        abstract val firstControl: Point

        abstract val joints: List<Joint>

        abstract val lastControl: Point

        abstract override fun bind(
            start: Point,
            end: Point,
        ): BezierCurve
    }

    abstract override val edge: BezierCurve.Edge

    abstract val subCurves: List<MonoBezierCurve>

    abstract override fun splitAt(
        coord: Coord,
    ): Pair<BezierCurve, BezierCurve>
}
