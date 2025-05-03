package diy.lingerie.geometry.splines

import diy.lingerie.algebra.NumericObject
import diy.lingerie.algebra.equalsWithTolerance
import diy.lingerie.geometry.Point
import diy.lingerie.geometry.curves.OpenCurve
import diy.lingerie.geometry.curves.PrimitiveCurve
import diy.lingerie.geometry.transformations.Transformation
import diy.lingerie.utils.iterable.mapCarrying
import diy.lingerie.utils.iterable.uncons
import diy.lingerie.utils.iterable.withPrevious

/**
 * A composite open curve assumed to be tangent-continuous (G1).
 */
data class OpenSpline(
    /**
     * The curve that starts this spline
     */
    val firstCurve: PrimitiveCurve,
    /**
     * A list of links, where each link starts at the end of the previous one,
     * and ends at the start of the next one. The first link starts at the end of
     * the first curve.
     */
    val trailingSequentialLinks: List<Spline.Link>,
) : OpenCurve(), Spline {
    companion object {
        fun OpenSpline(
            origin: Point,
            sequentialLinks: List<Spline.Link>,
        ): OpenSpline {
            val (firstLink, trailingLinks) = sequentialLinks.uncons()
                ?: throw IllegalArgumentException("The list of sequential links must not be empty")

            val firstCurve = firstLink.bind(start = origin)

            return OpenSpline(
                firstCurve = firstCurve,
                trailingSequentialLinks = trailingLinks,
            )
        }

        /**
         * @param sequentialCurves a list of curves, where each curves starts at
         * the end of the previous one, and ends at the start of the next one.
         *
         * @return an open spline in the shape of the given sequential curves
         */
        fun connect(
            sequentialCurves: List<OpenCurve>,
        ): OpenSpline {
            val primitiveSequentialCurves = sequentialCurves.flatMap { it.subCurves }

            val (firstCurve, trailingCurves) = primitiveSequentialCurves.uncons() ?: throw IllegalArgumentException(
                "The list of sequential curves must not be empty"
            )

            return OpenSpline(
                firstCurve = firstCurve,
                trailingSequentialLinks = trailingCurves.withPrevious(
                    outerLeft = firstCurve,
                ).map { (prevCurve, curve) ->
                    if (prevCurve.end != curve.start) {
                        throw IllegalArgumentException("The curves are not sequential: ${prevCurve.end} != ${curve.start}")
                    }

                    Spline.Link(
                        edge = curve.edge,
                        end = curve.end,
                    )
                },
            )
        }
    }

    val origin: Point
        get() = firstCurve.start

    val sequentialCurves: List<PrimitiveCurve>
        get() = listOf(firstCurve) + trailingSequentialCurves

    private val trailingSequentialCurves: List<PrimitiveCurve>
        get() {
            val (trailingCurves, _) = trailingSequentialLinks.mapCarrying(
                initialCarry = firstCurve.end,
            ) { start, link ->
                Pair(
                    link.bind(start = start),
                    link.end,
                )
            }

            return trailingCurves
        }

    override fun transformBy(
        transformation: Transformation,
    ): OpenSpline = OpenSpline(
        firstCurve = firstCurve.transformBy(
            transformation = transformation,
        ),
        trailingSequentialLinks = trailingSequentialLinks.map {
            it.transformBy(transformation = transformation)
        },
    )

    override fun splitAt(
        coord: Coord,
    ): Pair<OpenCurve, OpenCurve> {
        TODO()
    }

    override fun findOffsetCurve(
        offset: Double,
    ): OpenCurve = connect(
        sequentialCurves.map {
            it.findOffsetCurve(offset = offset)
        },
    )

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericObject.Tolerance,
    ): Boolean = when {
        other !is OpenSpline -> false
        !trailingSequentialLinks.equalsWithTolerance(other.links, tolerance) -> false
        else -> true
    }

    override val links: List<Spline.Link>
        get() = trailingSequentialLinks

    override val start: Point
        get() = TODO("Not yet implemented")

    override val end: Point
        get() = TODO("Not yet implemented")

    override val subCurves: List<PrimitiveCurve>
        get() = sequentialCurves

    override val segmentCurves: List<PrimitiveCurve>
        get() = sequentialCurves
}

