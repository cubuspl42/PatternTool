package diy.lingerie.geometry.curves

import diy.lingerie.ReprObject
import diy.lingerie.geometry.Direction
import diy.lingerie.geometry.Point
import diy.lingerie.geometry.splines.Spline
import diy.lingerie.geometry.transformations.Transformation
import diy.lingerie.math.algebra.NumericObject
import diy.lingerie.math.geometry.ParametricPolynomial
import diy.lingerie.math.geometry.parametric_curve_functions.ParametricCurveFunction

abstract class PrimitiveCurve : OpenCurve() {
    abstract class Edge : NumericObject, ReprObject {
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

    // TODO: Make this final
    override val subCurves: List<PrimitiveCurve>
        get() = listOf(this)

    final override val path: FeatureFunction<Point> by lazy {
        FeatureFunction.wrap(basisFunction).map { vector ->
            Point(pointVector = vector)
        }
    }

    private val basisFunctionDerivative: ParametricPolynomial by lazy {
        basisFunction.findDerivative()
    }

    override val tangentDirection: FeatureFunction<Direction?> by lazy {
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

    abstract val basisFunction: ParametricCurveFunction

    abstract val edge: Edge

    abstract override fun splitAt(
        coord: Coord,
    ): Pair<PrimitiveCurve, PrimitiveCurve>

    abstract fun evaluate(coord: Coord): Point

    abstract override fun transformBy(
        transformation: Transformation,
    ): PrimitiveCurve

    abstract val startTangent: Direction?

    abstract val endTangent: Direction?
}
