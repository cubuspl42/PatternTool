package diy.lingerie.web_tool_3d.application_state

import dev.toolkt.geometry.Direction
import dev.toolkt.geometry.Point
import dev.toolkt.geometry.Span
import dev.toolkt.geometry.transformations.PrimitiveTransformation

/**
 * A flat piece of fabric in rest.
 */
data class FlatFabricPiece(
    /**
     * The distance between two adjacent threads.
     */
    val threadGap: Double,
    /**
     * The coordinates of the thread intersections that are contained within
     * the fabric piece.
     */
    val innerCoords: Set<ThreadCoord>,
    /**
     * The edges of the fabric piece in the clockwise order, cyclic.
     */
    val edges: List<Edge>,
) {
    /**
     * The direction in the thread coordinate space.
     */
    sealed class ThreadDirection {
        data object IPlus : ThreadDirection() {
            override val direction: Direction = Direction.Companion.YAxisPlus
        }

        data object IMinus : ThreadDirection() {
            override val direction: Direction = Direction.Companion.YAxisMinus
        }

        data object JPlus : ThreadDirection() {
            override val direction: Direction = Direction.Companion.XAxisPlus
        }

        data object JMinus : ThreadDirection() {
            override val direction: Direction = Direction.Companion.XAxisMinus
        }

        /**
         * The corresponding direction in the standard geometric space.
         */
        abstract val direction: Direction
    }

    /**
     * Thread intersection coordinate.
     */
    data class ThreadCoord(
        /**
         * Vertical thread index (along the Y axis).
         */
        val i: Int,
        /**
         * Horizontal thread index (along the X axis).
         */
        val j: Int,
    ) {
        /**
         * Calculates the position of the thread intersection in the standard
         * geometric space.
         */
        fun calculatePosition(
            containingFabricPiece: FlatFabricPiece,
        ): Point = Point(
            x = j * containingFabricPiece.threadGap,
            y = i * containingFabricPiece.threadGap,
        )
    }

    /**
     * A link between an inner thread intersection and the fabric piece edge.
     */
    data class Tassel(
        /**
         * The coordinate of the thread intersection that the tassel is attached to.
         */
        val baseCoord: ThreadCoord,
        /**
         * The direction of the tassel relative to the base thread intersection.
         */
        val threadDirection: ThreadDirection,
        /**
         * The length of the tassel
         */
        val length: Span,
    ) {
        /**
         * Calculates the end position of the tassel (lying on the edge) in the standard geometric space.
         */
        fun calculateEdgeParticlePosition(
            containingFabricPiece: FlatFabricPiece,
        ): Point {
            val basePosition = baseCoord.calculatePosition(
                containingFabricPiece = containingFabricPiece,
            )

            return translation.transform(point = basePosition)
        }

        val translation: PrimitiveTransformation.Translation
            get() = PrimitiveTransformation.Translation.inDirection(
                direction = threadDirection.direction,
                distance = length,
            )
    }

    /**
     * The fabric piece edge.
     */
    data class Edge(
        /**
         * The position of the edge's start point (one of the fabric piece's corners). The edge's start point is
         * connected to the first tassel's end point.
         */
        val start: Point,
        /**
         * A sequence of tassels that define the consecutive points of the edge. Each tassel's end point is connected
         * to the next tassel's start point. The last tassel's end point is connected to the next edge's start point.
         */
        val tassels: List<Tassel>,
    ) {
        fun calculateParticlePositions(
            containingFabricPiece: FlatFabricPiece,
        ): Iterable<Point> = listOf(
            start,
        ) + tassels.map { tassel ->
            tassel.calculateEdgeParticlePosition(containingFabricPiece = containingFabricPiece)
        }
    }

    /**
     * Checks if the fabric piece contains the specified thread coordinate.
     */
    fun contains(
        threadCoord: ThreadCoord,
    ): Boolean = innerCoords.contains(threadCoord)

    val innerParticlePositions: Iterable<Point>
        get() = innerCoords.map {
            it.calculatePosition(containingFabricPiece = this)
        }

    val edgeParticlePositions: Iterable<Point>
        get() = edges.flatMap { edge ->
            edge.calculateParticlePositions(
                containingFabricPiece = this,
            )
        }
}
