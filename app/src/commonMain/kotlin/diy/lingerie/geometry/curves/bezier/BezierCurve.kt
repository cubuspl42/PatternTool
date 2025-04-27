package diy.lingerie.geometry.curves.bezier

import diy.lingerie.geometry.Line
import diy.lingerie.geometry.Point
import diy.lingerie.geometry.curves.SegmentCurve
import diy.lingerie.geometry.transformations.Transformation

abstract class BezierCurve : SegmentCurve() {
    data class Joint(
        val rearControl: Point,
        val t: Double,
        val frontControl: Point,
    ) {
        val position: Point
            get() = TODO()

        fun transformBy(
            transformation: Transformation,
        ): Joint = Joint(
            frontControl = frontControl.transformBy(transformation),
            t = t,
            rearControl = rearControl.transformBy(transformation),
        )

        init {
            require(t in 0.0..1.0)
        }
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
}
