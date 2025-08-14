package diy.lingerie.cup_layout_tool.application_state

import dev.toolkt.core.iterable.withNextCyclic
import dev.toolkt.geometry.Point3D

/**
 * A special kind of cone-shaped net of Bézier patches that all share a single point (the apex)
 * and also share the sets of three control points for each consecutive wall pair.
 */
class BezierConeSurface(
    /**
     * The position of the apex of the Bezier cone
     */
    val apex: Point3D,
    /**
     * The walls of the Bezier cone (cyclic, in the CCW order)
     */
    val walls: List<Wall>,
) {
    data class PartialBezierCurve(
        val base: Point3D,
        val baseControl: Point3D,
        val apexControl: Point3D,
    ) {
        fun toBezierCurve(
            apex: Point3D,
        ): BezierCurve3D = BezierCurve3D(
            start = base,
            firstControl = baseControl,
            secondControl = apexControl,
            end = apex,
        )
    }

    /**
     * A Bezier cone wall is a quad cubic Bezier surface with the four "top" control points coinciding with the apex of
     * the cone, effectively resulting in thirteen control points per wall. It's similar to a cubic Bézier triangle, but
     * cubic Bézier triangles have ten control points.
     */
    data class Wall(
        /**
         * The (partial) vertical curve that defines the start point of a horizontal curve (and the end point of the respective
         * horizontal curve from the previous wall)
         */
        val partialVerticalStartCurve: PartialBezierCurve,
        /**
         * The (partial) vertical curve that defines the first control point of a horizontal curve
         */
        val partialVerticalFirstControlCurve: PartialBezierCurve,
        /**
         * The (partial) vertical curve that defines the second control point of a horizontal curve
         */
        val partialVerticalSecondControlCurve: PartialBezierCurve,
    ) {
        fun toVerticalWires(
            apex: Point3D,
            nextWall: Wall,
            verticalSegmentCount: Int,
            radialSegmentCount: Int,
        ): List<DistortedConeSurfaceMesh.VerticalWire> {
            // Vertical curve for the start point
            val startCurve = partialVerticalStartCurve.toBezierCurve(apex = apex)

            // Vertical curve for the first control point
            val firstControlCurve = partialVerticalFirstControlCurve.toBezierCurve(apex = apex)

            // Vertical curve for the second control point
            val secondControlCurve = partialVerticalSecondControlCurve.toBezierCurve(apex = apex)

            // Vertical curve for the end point
            val endCurve = nextWall.partialVerticalStartCurve.toBezierCurve(apex = apex)

            return List(size = radialSegmentCount) { horizontalIndex ->
                // The horizontal time parameter
                val tH = horizontalIndex.toDouble() / radialSegmentCount

                DistortedConeSurfaceMesh.VerticalWire(
                    vertices = List(size = verticalSegmentCount) { verticalIndex ->
                        // The vertical time parameter
                        val tV = verticalIndex.toDouble() / verticalSegmentCount

                        val horizontalCurve = BezierCurve3D(
                            start = startCurve.evaluateAt(t = tV),
                            firstControl = firstControlCurve.evaluateAt(t = tV),
                            secondControl = secondControlCurve.evaluateAt(t = tV),
                            end = endCurve.evaluateAt(t = tV),
                        )

                        horizontalCurve.evaluateAt(t = tH)
                    },
                )
            }
        }
    }

    fun toDistortedConeMesh(
        verticalSegmentCount: Int,
        radialSegmentPerWallCount: Int,
    ): DistortedConeSurfaceMesh = DistortedConeSurfaceMesh(
        apex = apex,
        verticalWires = walls.withNextCyclic().flatMap { (wall, nextWall) ->
            wall.toVerticalWires(
                apex = apex,
                nextWall = nextWall,
                verticalSegmentCount = verticalSegmentCount,
                radialSegmentCount = radialSegmentPerWallCount,
            )
        },
    )
}
