package diy.lingerie.geometry.curves.bezier

import diy.lingerie.geometry.Point
import diy.lingerie.geometry.curves.SegmentCurve
import diy.lingerie.geometry.transformations.Transformation

/**
 * A C1-continuous cubic poly-Bézier curve
 */
data class PolyBezierCurve(
    override val start: Point,
    val firstControl: Point,
    val joints: List<Joint>,
    val lastControl: Point,
    override val end: Point,
) : BezierCurve() {
    data class Edge(
        override val firstControl: Point,
        override val joints: List<Joint>,
        override val lastControl: Point,
    ) : BezierCurve.Edge() {
        override fun bind(
            start: Point,
            end: Point,
        ): BezierCurve = when {
            joints.isEmpty() -> MonoBezierCurve(
                start = start,
                firstControl = firstControl,
                secondControl = lastControl,
                end = end,
            )

            else -> PolyBezierCurve(
                start = start,
                firstControl = firstControl,
                joints = joints,
                lastControl = lastControl,
                end = end,
            )
        }

        override fun transformBy(
            transformation: Transformation,
        ): PolyBezierCurve.Edge = PolyBezierCurve.Edge(
            firstControl = firstControl.transformBy(transformation = transformation),
            joints = joints.map { joint ->
                joint.transformBy(transformation = transformation)
            },
            lastControl = lastControl.transformBy(transformation = transformation),
        )
    }

    init {
        require(joints.isNotEmpty())
    }

    override val edge: PolyBezierCurve.Edge
        get() = Edge(
            firstControl = firstControl,
            joints = joints,
            lastControl = lastControl,
        )

    override val subCurves: List<MonoBezierCurve>
        get() {
            val firstJoint = joints.first()
            val lastJoint = joints.last()

            return listOf(
                MonoBezierCurve(
                    start = start,
                    firstControl = firstControl,
                    secondControl = firstJoint.rearControl,
                    end = firstJoint.position
                ),
            ) + joints.zipWithNext { joint, nextJoint ->
                MonoBezierCurve(
                    start = joint.position,
                    firstControl = joint.frontControl,
                    secondControl = nextJoint.rearControl,
                    end = nextJoint.position,
                )
            } + listOf(
                MonoBezierCurve(
                    start = lastJoint.position,
                    firstControl = lastJoint.frontControl,
                    secondControl = lastControl,
                    end = end,
                ),
            )
        }

    override fun splitAt(
        coord: Coord,
    ): Pair<PolyBezierCurve, PolyBezierCurve> {
        TODO("Not yet implemented")
    }

    override fun evaluate(coord: Coord): Point {
        TODO("Not yet implemented")
    }
}
