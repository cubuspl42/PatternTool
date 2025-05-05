package diy.lingerie.geometry.curves.bezier

import diy.lingerie.geometry.BoundingBox
import diy.lingerie.math.algebra.NumericObject
import diy.lingerie.math.algebra.NumericObject.Tolerance
import diy.lingerie.geometry.Direction
import diy.lingerie.geometry.Point
import diy.lingerie.geometry.curves.PrimitiveCurve
import diy.lingerie.geometry.transformations.PrimitiveTransformation
import diy.lingerie.geometry.transformations.Transformation
import diy.lingerie.geometry.x
import diy.lingerie.geometry.y
import diy.lingerie.math.geometry.ParametricPolynomial
import diy.lingerie.math.geometry.parametric_curve_functions.ParametricCurveFunction
import diy.lingerie.math.geometry.parametric_curve_functions.bezier_binomials.CubicBezierBinomial

data class BezierCurve(
    override val start: Point,
    val firstControl: Point,
    val secondControl: Point,
    override val end: Point,
) : PrimitiveCurve() {
    data class Edge(
        val firstControl: Point,
        val secondControl: Point,
    ) : PrimitiveCurve.Edge() {
        val lastControl: Point
            get() = secondControl

        override fun bind(
            start: Point,
            end: Point,
        ): BezierCurve = BezierCurve(
            start = start,
            firstControl = firstControl,
            secondControl = secondControl,
            end = end,
        )

        override fun transformBy(
            transformation: Transformation,
        ): PrimitiveCurve.Edge = Edge(
            firstControl = firstControl.transformBy(transformation = transformation),
            secondControl = secondControl.transformBy(transformation = transformation),
        )

        override fun equalsWithTolerance(
            other: NumericObject,
            tolerance: Tolerance,
        ): Boolean = when {
            other !is Edge -> false
            !firstControl.equalsWithTolerance(other.firstControl, tolerance) -> false
            !secondControl.equalsWithTolerance(other.secondControl, tolerance) -> false
            else -> true
        }

        override fun toReprString(): String {
            return """
                |BezierCurve.Edge(
                |  firstControl = ${firstControl.toReprString()},
                |  secondControl = ${secondControl.toReprString()},
                |)
            """.trimMargin()
        }
    }

    override val basisFunction: ParametricCurveFunction = CubicBezierBinomial(
        point0 = start.pointVector,
        point1 = firstControl.pointVector,
        point2 = secondControl.pointVector,
        point3 = end.pointVector,
    )

    val lastControl: Point
        get() = secondControl

    override val edge: Edge
        get() = Edge(
            firstControl = firstControl,
            secondControl = secondControl,
        )

    override val subCurves: List<BezierCurve>
        get() = listOf(this)

    override fun transformBy(
        transformation: Transformation,
    ): BezierCurve = BezierCurve(
        start = start.transformBy(transformation = transformation),
        firstControl = firstControl.transformBy(transformation = transformation),
        secondControl = secondControl.transformBy(transformation = transformation),
        end = end.transformBy(transformation = transformation),
    )

    // TODO: This might not work for degenerate curves
    override val startTangent: Direction?
        get() = start.directionTo(firstControl)

    // TODO: This might not work for degenerate curves
    override val endTangent: Direction?
        get() = secondControl.directionTo(end)

    override fun splitAt(
        coord: Coord,
    ): Pair<BezierCurve, BezierCurve> {
        TODO("Not yet implemented")
    }

    override fun evaluate(coord: Coord): Point {
        TODO("Not yet implemented")
    }

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: Tolerance,
    ): Boolean = when {
        other !is BezierCurve -> false
        !start.equalsWithTolerance(other.start, tolerance = tolerance) -> false
        !firstControl.equalsWithTolerance(other.firstControl, tolerance = tolerance) -> false
        !secondControl.equalsWithTolerance(other.secondControl, tolerance = tolerance) -> false
        !end.equalsWithTolerance(other.end, tolerance = tolerance) -> false
        else -> true
    }

    override fun findOffsetCurve(
        offset: Double,
    ): BezierCurve = this.transformBy(
        transformation = PrimitiveTransformation.Translation(
            tx = offset,
            ty = offset,
        ),
    )

    override fun findBoundingBox(): BoundingBox {
        val startPoint = path.start
        val endPoint = path.end

        val criticalPointSet = basisFunction.findCriticalPoints()

        val criticalXValues = criticalPointSet.xRoots.map { t -> basisFunction.apply(t).x }
        val potentialXExtrema = criticalXValues + startPoint.x + endPoint.x
        val xMin = potentialXExtrema.min()
        val xMax = potentialXExtrema.max()

        val criticalYValues = criticalPointSet.yRoots.map { t -> basisFunction.apply(t).y }
        val potentialYExtrema = criticalYValues + startPoint.y + endPoint.y
        val yMin = potentialYExtrema.min()
        val yMax = potentialYExtrema.max()

        return BoundingBox.of(
            xMin = xMin,
            xMax = xMax,
            yMin = yMin,
            yMax = yMax,
        )
    }

    override fun toReprString(): String {
        return """
            |BezierCurve(
            |  start = ${start.toReprString()},
            |  firstControl = ${firstControl.toReprString()},
            |  secondControl = ${secondControl.toReprString()},
            |  end = ${end.toReprString()},
            |)
        """.trimMargin()
    }
}
