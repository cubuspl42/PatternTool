package diy.lingerie.geometry.curves

import diy.lingerie.geometry.BoundingBox
import diy.lingerie.geometry.LineSegment
import diy.lingerie.geometry.Point
import diy.lingerie.geometry.SpatialObject
import diy.lingerie.geometry.transformations.PrimitiveTransformation
import diy.lingerie.geometry.transformations.Transformation
import diy.lingerie.math.algebra.NumericObject
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
            tolerance: NumericObject.Tolerance,
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

    companion object {
        /**
         * Finds intersections between two Bézier curves using the default
         * strategy
         */
        fun findIntersections(
            subjectBezierCurve: BezierCurve,
            objectBezierCurve: BezierCurve,
        ): Set<Intersection> {
            // For two Bézier curves, let's use the intersection equation solving
            // strategy
            return BezierCurve.findIntersectionsByEquationSolving(
                subjectBezierCurve = subjectBezierCurve,
                objectBezierCurve = objectBezierCurve,
                tolerance = NumericObject.Tolerance.Default,
            )
        }

        /**
         * Finds intersections between two Bézier curves by solving their
         * intersection equation
         */
        fun findIntersectionsByEquationSolving(
            subjectBezierCurve: BezierCurve,
            objectBezierCurve: BezierCurve,
            tolerance: NumericObject.Tolerance,
        ): Set<Intersection> = findIntersectionsByEquationSolving(
            simpleSubjectCurve = subjectBezierCurve,
            complexObjectCurve = objectBezierCurve,
            tolerance = tolerance,
        )

        /**
         * Finds intersections between two Bézier curves by subdividing them
         * recursively and checking for overlaps
         */
        fun findIntersectionsBySubdivision(
            firstCurve: BezierCurve,
            secondCurve: BezierCurve,
            tolerance: SpatialObject.SpatialTolerance,
        ): Set<Point> {
            val firstBoundingBox = firstCurve.findBoundingBox()
            val secondBoundingBox = secondCurve.findBoundingBox()

            if (!firstBoundingBox.overlaps(secondBoundingBox)) {
                return emptySet()
            }

            val isFirstBoundingBoxSmallEnough = firstBoundingBox.area < tolerance.spanTolerance.valueSquared
            val isSecondBoundingBoxSmallEnough = secondBoundingBox.area < tolerance.spanTolerance.valueSquared

            if (isFirstBoundingBoxSmallEnough && isSecondBoundingBoxSmallEnough) {
                return setOf(
                    Point.Companion.midPoint(
                        firstBoundingBox.center,
                        secondBoundingBox.center,
                    ),
                )
            }

            val (firstCurveLeft, firstCurveRight) = firstCurve.splitAt(coord = Coord.half)
            val (secondCurveLeft, secondCurveRight) = secondCurve.splitAt(coord = Coord.half)

            return findIntersectionsBySubdivision(
                firstCurve = firstCurveLeft,
                secondCurve = secondCurveLeft,
                tolerance = tolerance,
            ) + findIntersectionsBySubdivision(
                firstCurve = firstCurveLeft,
                secondCurve = secondCurveRight,
                tolerance = tolerance,
            ) + findIntersectionsBySubdivision(
                firstCurve = firstCurveRight,
                secondCurve = secondCurveLeft,
                tolerance = tolerance,
            ) + findIntersectionsBySubdivision(
                firstCurve = firstCurveRight,
                secondCurve = secondCurveRight,
                tolerance = tolerance,
            )
        }

        /**
         * Finds intersections between a Bézier curve and a line segment
         */
        fun findIntersections(
            subjectLineSegment: LineSegment,
            objectBezierCurve: BezierCurve,
        ): Set<Intersection> = LineSegment.Companion.findIntersections(
            subjectLineSegment = subjectLineSegment,
            objectPrimitiveCurve = objectBezierCurve,
        )
    }

    override val basisFunction = CubicBezierBinomial(
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

    override fun transformBy(
        transformation: Transformation,
    ): BezierCurve = BezierCurve(
        start = start.transformBy(transformation = transformation),
        firstControl = firstControl.transformBy(transformation = transformation),
        secondControl = secondControl.transformBy(transformation = transformation),
        end = end.transformBy(transformation = transformation),
    )

    override fun splitAt(
        coord: Coord,
    ): Pair<BezierCurve, BezierCurve> {
        val quadraticBezierBinomial = basisFunction.evaluatePartially(t = coord.t)
        val lineFunction = quadraticBezierBinomial.evaluatePartially(t = coord.t)

        val midPoint = Point(
            pointVector = lineFunction.apply(coord.t),
        )

        return Pair(
            BezierCurve(
                start = start,
                firstControl = Point(
                    pointVector = quadraticBezierBinomial.point0,
                ),
                secondControl = Point(
                    pointVector = lineFunction.point0,
                ),
                end = midPoint,
            ),
            BezierCurve(
                start = midPoint,
                firstControl = Point(
                    pointVector = lineFunction.point1,
                ),
                secondControl = Point(
                    pointVector = quadraticBezierBinomial.point2,
                ),
                end = end,
            ),
        )
    }

    override fun evaluate(
        coord: Coord,
    ): Point = Point(
        pointVector = basisFunction.apply(coord.t),
    )

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericObject.Tolerance,
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
        val startPoint = pathFunction.start
        val endPoint = pathFunction.end

        val criticalPointSet = basisFunction.findCriticalPoints()

        val criticalXValues = criticalPointSet.xRoots.mapNotNull { t ->
            Coord.of(t = t)?.let { evaluate(it).x }
        }

        val potentialXExtrema = criticalXValues + startPoint.x + endPoint.x
        val xMin = potentialXExtrema.min()
        val xMax = potentialXExtrema.max()

        val criticalYValues = criticalPointSet.yRoots.mapNotNull { t ->
            Coord.of(t = t)?.let { evaluate(it).y }
        }

        val potentialYExtrema = criticalYValues + startPoint.y + endPoint.y
        val yMin = potentialYExtrema.min()
        val yMax = potentialYExtrema.max()

        return BoundingBox.Companion.of(
            xMin = xMin,
            xMax = xMax,
            yMin = yMin,
            yMax = yMax,
        )
    }

    override fun findIntersections(objectCurve: OpenCurve): Set<Intersection> {
        return objectCurve.findIntersectionsBezierCurve(
            subjectBezierCurve = this,
        )
    }

    override fun findIntersectionsBezierCurve(
        subjectBezierCurve: BezierCurve,
    ): Set<Intersection> = BezierCurve.findIntersections(
        subjectBezierCurve = subjectBezierCurve,
        objectBezierCurve = this,
    )

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