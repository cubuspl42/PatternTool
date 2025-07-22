package dev.toolkt.geometry.curves

import dev.toolkt.core.ReprObject
import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.NumericTolerance
import dev.toolkt.geometry.Direction
import dev.toolkt.geometry.LineSegment
import dev.toolkt.geometry.Point
import dev.toolkt.geometry.SpatialObject
import dev.toolkt.geometry.math.ParametricPolynomial
import dev.toolkt.geometry.math.parametric_curve_functions.ParametricCurveFunction
import dev.toolkt.geometry.math.parametric_curve_functions.ParametricCurveFunction.Companion.primaryTRange
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
                tRange = primaryTRange,
                tolerance = NumericTolerance.Absolute.Default,
            )

            // Filter out intersections outside either curve
            return tValues.mapNotNull { tSimple ->
                // Technically, it's an extraneous check as we solved the equation for the 0..1 range,
                // but lets' double protect from numerical anomalies
                val coordSimple = Coord.of(t = tSimple) ?: return@mapNotNull null

                val potentialIntersectionPoint = simpleSubjectCurve.evaluate(coord = coordSimple)

                val tComplex = complexObjectCurve.basisFunction.locatePoint(
                    point = potentialIntersectionPoint.pointVector,
                    tRange = primaryTRange,
                    tolerance = tolerance,
                ) ?: return@mapNotNull null

                val coordComplex = Coord.of(t = tComplex) ?: return@mapNotNull null

                object : Intersection() {
                    override val point = potentialIntersectionPoint

                    override val subjectCoord: Coord = coordSimple

                    override val objectCoord: Coord = coordComplex
                }
            }.toSet()
        }
    }

    // TODO: Make this final
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
     * Locate the point on the curve
     *
     * @return If the [point] is on the curve, coordinate of that point. If the
     * point is not on the curve, `null`
     *
     * TODO: Nuke in favor of the newer API
     */
    abstract fun locatePoint(
        point: Point,
    ): Coord?

    /**
     * Locate the point on the curve
     *
     * @return If the [point] lies on the curve (within the given [tolerance]), coordinate of that point. If the point
     * does not lie on the curve or is extremely close to the self-intersection, `null`.
     */
    fun locatePointByInversion(
        point: Point,
        tolerance: SpatialObject.SpatialTolerance,
    ): Coord? {
        val inversionResult = invertedBasisFunction.apply(
            point.pointVector,
        ) as? InversionResult.Specific ?: run {
            // If a point lies on the self-intersection, there are actually multiple
            // coordinates that are a possibly acceptable answer. For now, let's give up.
            return null
        }

        // If the inversion returned a result outside the primary t-value range,
        // it means that the point is not on the curve
        val invertedCoord = Coord.of(
            t = inversionResult.t,
            tolerance = NumericTolerance.Absolute.Default,
        ) ?: return null

        // If the inversion gave a specific t-value, it's not a guarantee that
        // the point is on the curve, as the inversion function is defined for
        // all (non-self-intersection) points

        // Let's see what point lies on the inverted t-value
        val actualPoint = evaluate(coord = invertedCoord)

        return when {
            // If the point we try to locate and the point at the inverted t-value
            // are effectively the same point, the point location was successful
            point.equalsWithSpatialTolerance(
                other = actualPoint,
                tolerance = tolerance,
            ) -> invertedCoord

            else -> null
        }
    }
}
