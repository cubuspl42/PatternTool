package dev.toolkt.geometry.curves

import dev.toolkt.core.ReprObject
import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.NumericTolerance
import dev.toolkt.geometry.Direction
import dev.toolkt.geometry.LineSegment
import dev.toolkt.geometry.Point
import dev.toolkt.geometry.math.ParametricPolynomial
import dev.toolkt.geometry.math.parametric_curve_functions.ParametricCurveFunction
import dev.toolkt.geometry.math.parametric_curve_functions.ParametricCurveFunction.InvertedCurveFunction.InversionResult
import dev.toolkt.geometry.splines.OpenSpline
import dev.toolkt.geometry.splines.Spline
import dev.toolkt.geometry.transformations.Transformation

abstract class PrimitiveCurve : OpenCurve() {
    abstract class Edge : NumericObject, ReprObject {
        companion object;

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

    /**
     * The result of point inversion (mapping of a point on the curve to the
     * respective coordinates)
     */
    sealed class PointInversionResult() : NumericObject {
        abstract val representativeCoord: Coord

        /**
         * A single point
         *
         * One (and only one) coordinate corresponds to the given point.
         */
        data class Single(
            val coord: Coord,
        ) : PointInversionResult() {
            override val representativeCoord: Coord
                get() = coord

            override fun equalsWithTolerance(
                other: NumericObject,
                tolerance: NumericTolerance,
            ): Boolean = when {
                other !is Single -> false
                !coord.equalsWithTolerance(other.coord, tolerance) -> false
                else -> true
            }
        }

        /**
         * A double point
         *
         * Two points on the curve correspond to the given point. This is
         * possible in the case of a self-intersection.
         */
        data class Double(
            val firstCoord: Coord,
            val secondCoord: Coord,
        ) : PointInversionResult() {
            override val representativeCoord: Coord
                get() = firstCoord

            override fun equalsWithTolerance(
                other: NumericObject,
                tolerance: NumericTolerance,
            ): Boolean = when {
                other !is Double -> false
                !firstCoord.equalsWithTolerance(other.firstCoord, tolerance) -> false
                !secondCoord.equalsWithTolerance(other.secondCoord, tolerance) -> false
                else -> true
            }
        }
    }

    companion object {
        /**
         * Finds intersections between two curves by solving the intersection
         * equation
         *
         * @param simpleSubjectCurve a curve that's not more complex than [complexObjectCurve]
         * @param complexObjectCurve a curve that's not simpler than [simpleSubjectCurve]
         */
        fun findIntersectionsByEquationSolving(
            simpleSubjectCurve: PrimitiveCurve,
            complexObjectCurve: PrimitiveCurve,
            tolerance: NumericTolerance.Absolute,
        ): Set<Intersection> {
            // Solve the intersection equation for the curves (for t ∈ 0..1)
            val tValues = simpleSubjectCurve.basisFunction.solveIntersectionEquation(
                other = complexObjectCurve.basisFunction,
                tolerance = NumericTolerance.Absolute.Default,
            )

            // We now know the set of intersections in the extended range, but some of them might lie outside the
            // curves' primary 0..1 range. We have to filter those intersections out.
            return tValues.mapNotNull { tSimple ->
                // Technically, it's an extraneous check as we solved the equation for the simple curve's 0..1 range,
                // but lets' double protect from numerical anomalies
                val coordSimple = Coord.of(t = tSimple) ?: return@mapNotNull null

                // We now know that the intersection lies on the simple curve (in the proper range), but it might
                // still lie outside the complex curve's primary range

                // Let's find the actual intersection point (until now, we knew only its t-value)
                val intersectionPoint = simpleSubjectCurve.evaluate(coord = coordSimple)

                // As we know that the intersection point lies on the complex curve (in the full range), we can safely
                // use the complex curve's inversion mechanism
                val pointInversionResult = complexObjectCurve.invertPoint(intersectionPoint) ?: return@mapNotNull null

                object : Intersection() {
                    override val point: Point = intersectionPoint

                    override val subjectCoord: Coord = coordSimple

                    override val objectCoord: Coord = pointInversionResult.representativeCoord
                }
            }.toSet()
        }
    }

    final override val subCurves: List<PrimitiveCurve>
        get() = listOf(this)

    val invertedBasisFunction by lazy {
        basisFunction.buildInvertedFunction(
            tolerance = NumericTolerance.Absolute.Default,
        )
    }

    final override val pathFunction: FeatureFunction<Point> by lazy {
        FeatureFunction.wrap(basisFunction).map { vector ->
            Point(pointVector = vector)
        }
    }

    private val basisFunctionDerivative: ParametricPolynomial<*> by lazy {
        basisFunction.findDerivative()
    }

    override val tangentDirectionFunction: FeatureFunction<Direction?> by lazy {
        FeatureFunction.wrap(basisFunctionDerivative).map { vector ->
            Direction.normalize(vector)
        }
    }

    fun connectsSmoothly(
        nextCurve: PrimitiveCurve,
    ): Boolean {
        require(end == nextCurve.start)

        val endTangent =
            this.endTangent ?: throw IllegalStateException("Cannot check smoothness of a curve with no end tangent")

        val nextStartTangent = nextCurve.startTangent
            ?: throw IllegalStateException("Cannot check smoothness of a curve with no start tangent")

        return endTangent.equalsWithRadialTolerance(nextStartTangent)
    }

    abstract override fun transformBy(
        transformation: Transformation,
    ): PrimitiveCurve

    final override fun findIntersectionsOpenSpline(
        subjectSpline: OpenSpline,
    ): Set<Intersection> = Intersection.swap(
        OpenSpline.findIntersections(
            subjectPrimitiveCurve = this,
            objectOpenSpline = subjectSpline,
        ),
    )

    final override fun findIntersectionsLineSegment(
        subjectLineSegment: LineSegment,
    ): Set<Intersection> = PrimitiveCurve.findIntersectionsByEquationSolving(
        // Line segment is never more complex than other primitive curves
        simpleSubjectCurve = subjectLineSegment,
        complexObjectCurve = this,
        tolerance = NumericTolerance.Absolute.Default,
    )

    abstract val basisFunction: ParametricCurveFunction

    abstract val edge: Edge

    abstract override fun splitAt(
        coord: Coord,
    ): Pair<PrimitiveCurve, PrimitiveCurve>

    abstract fun evaluate(coord: Coord): Point

    /**
     * Invert the point lying on the curve (including its extension)
     *
     * @return The point inversion result, or `null` if the point doesn't lie
     * in the curve's primary range.
     */
    fun invertPoint(
        point: Point,
    ): PointInversionResult? {
        val inversionResult = invertedBasisFunction.apply(
            point.pointVector,
        )

        when (inversionResult) {
            InversionResult.SelfIntersection -> {
                val selfIntersectionResult = basisFunction.findSelfIntersection(
                    tolerance = NumericTolerance.Absolute.Default,
                ) ?: return null // If we couldn't find the self-intersection, this is a numerical anomaly or a bug

                val firstCoord = Coord.of(selfIntersectionResult.t0)
                val secondCoord = Coord.of(selfIntersectionResult.t1)

                when {
                    firstCoord != null && secondCoord != null -> return PointInversionResult.Double(
                        firstCoord = firstCoord,
                        secondCoord = secondCoord,
                    )

                    else -> {
                        val singleCoord = firstCoord ?: secondCoord ?: return null

                        return PointInversionResult.Single(
                            coord = singleCoord,
                        )
                    }
                }
            }

            is InversionResult.Specific -> {
                val singleCoord = Coord.of(inversionResult.t) ?: return null

                return PointInversionResult.Single(
                    coord = singleCoord,
                )
            }
        }
    }
}
