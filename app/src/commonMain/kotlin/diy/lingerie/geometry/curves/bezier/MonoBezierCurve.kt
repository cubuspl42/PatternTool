package diy.lingerie.geometry.curves.bezier

import diy.lingerie.geometry.Point
import diy.lingerie.geometry.curves.SegmentCurve
import diy.lingerie.geometry.transformations.Transformation

data class MonoBezierCurve(
    override val start: Point,
    val firstControl: Point,
    val secondControl: Point,
    override val end: Point,
) : BezierCurve() {
    data class Edge(
        override val firstControl: Point,
        val secondControl: Point,
    ) : BezierCurve.Edge() {
        override val joints: List<Joint> = emptyList()

        override val lastControl: Point
            get() = secondControl

        override fun bind(
            start: Point,
            end: Point,
        ): MonoBezierCurve = MonoBezierCurve(
            start = start,
            firstControl = firstControl,
            secondControl = secondControl,
            end = end,
        )

        override fun transformBy(transformation: Transformation): SegmentCurve.Edge {
            TODO("Not yet implemented")
        }
    }


    override val edge: Edge
        get() = MonoBezierCurve.Edge(
            firstControl = firstControl,
            secondControl = secondControl,
        )

    override val subCurves: List<MonoBezierCurve>
        get() = listOf(this)

    override fun splitAt(
        coord: Coord,
    ): Pair<MonoBezierCurve, MonoBezierCurve> {
        TODO("Not yet implemented")
    }

    override fun evaluate(coord: Coord): Point {
        TODO("Not yet implemented")
    }
}
