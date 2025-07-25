package dev.toolkt.geometry.math.parametric_curve_functions.bezier_binomials

import dev.toolkt.core.iterable.LinSpace
import dev.toolkt.core.iterable.partitionAtCenter
import dev.toolkt.core.iterable.withNeighboursOrNull
import dev.toolkt.core.math.sq
import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.NumericTolerance
import dev.toolkt.core.numeric.equalsWithTolerance
import dev.toolkt.core.numeric.equalsZeroWithTolerance
import dev.toolkt.geometry.math.LowParametricPolynomial
import dev.toolkt.geometry.math.ParametricPolynomial
import dev.toolkt.geometry.math.RationalImplicitPolynomial
import dev.toolkt.geometry.math.a1
import dev.toolkt.geometry.math.a2
import dev.toolkt.geometry.math.a3
import dev.toolkt.geometry.math.implicit_curve_functions.ImplicitCubicCurveFunction
import dev.toolkt.geometry.math.implicit_curve_functions.ImplicitLineFunction
import dev.toolkt.geometry.math.implicit_curve_functions.times
import dev.toolkt.geometry.math.parametric_curve_functions.ParametricLineFunction
import dev.toolkt.geometry.x
import dev.toolkt.geometry.y
import dev.toolkt.math.algebra.linear.matrices.matrix2.Matrix2x2
import dev.toolkt.math.algebra.linear.matrices.matrix2.Matrix4x2
import dev.toolkt.math.algebra.linear.matrices.matrix2.MatrixNx2
import dev.toolkt.math.algebra.linear.matrices.matrix3.Matrix3x3
import dev.toolkt.math.algebra.linear.matrices.matrix4.Matrix3x4
import dev.toolkt.math.algebra.linear.matrices.matrix4.Matrix4x3
import dev.toolkt.math.algebra.linear.matrices.matrix4.Matrix4x4
import dev.toolkt.math.algebra.linear.matrices.matrix4.MatrixNx4
import dev.toolkt.math.algebra.linear.vectors.Vector2
import dev.toolkt.math.algebra.linear.vectors.Vector3
import dev.toolkt.math.algebra.linear.vectors.Vector4
import dev.toolkt.math.algebra.linear.vectors.times
import dev.toolkt.math.algebra.polynomials.CubicPolynomial
import dev.toolkt.math.algebra.polynomials.Polynomial
import dev.toolkt.math.algebra.polynomials.QuadraticPolynomial
import dev.toolkt.math.minByUnimodalWithSelectee
import dev.toolkt.math.minByWithSelecteeOrNull

data class CubicBezierBinomial(
    val point0: Vector2,
    val point1: Vector2,
    val point2: Vector2,
    val point3: Vector2,
) : BezierBinomial() {
    companion object {
        const val n = 3

        /**
         * The characteristic matrix of the cubic Bézier curve.
         */
        val characteristicMatrix = Matrix4x4.rowMajor(
            row0 = Vector4(-1.0, 3.0, -3.0, 1.0),
            row1 = Vector4(3.0, -6.0, 3.0, 0.0),
            row2 = Vector4(-3.0, 3.0, 0.0, 0.0),
            row3 = Vector4(1.0, 0.0, 0.0, 0.0),
        )

        /**
         * A matrix for raising a quadratic curve to a cubic curve
         */
        val raiseMatrix = Matrix4x3.rowMajor(
            row0 = Vector3(1.0, 0.0, 0.0),
            row1 = Vector3(1.0 / 3.0, 2.0 / 3.0, 0.0),
            row2 = Vector3(0.0, 2.0 / 3.0, 1.0 / 3.0),
            row3 = Vector3(0.0, 0.0, 1.0),
        )

        val raiseMatrixPseudoInverse: Matrix3x4 = raiseMatrix.pseudoInverse()

        val characteristicMatrixInverted =
            characteristicMatrix.invert() ?: throw AssertionError("The characteristic matrix is not invertible")

        fun bestFit(
            samples: List<Sample>,
        ): CubicBezierBinomial {
            val pMatrix = MatrixNx2(
                rows = samples.map { it.point },
            )

            // T
            val tMatrix = MatrixNx4(
                rows = samples.map { it ->
                    CubicPolynomial.monomialVector(it.t)
                },
            )

            // (M^-1) * (T^t * T)^-1 * T^t
            val dMatrix = characteristicMatrixInverted * tMatrix.pseudoInverse()

            // P (control points)
            val pointMatrix = dMatrix * pMatrix

            return CubicBezierBinomial(
                pointMatrix = pointMatrix,
            )
        }
    }

    constructor(
        pointMatrix: Matrix4x2,
    ) : this(
        point0 = pointMatrix.row0,
        point1 = pointMatrix.row1,
        point2 = pointMatrix.row2,
        point3 = pointMatrix.row3,
    )

    val pointMatrix: Matrix4x2
        get() = Matrix4x2(
            row0 = point0,
            row1 = point1,
            row2 = point2,
            row3 = point3,
        )

    private val delta0: Vector2
        get() = point1 - point0

    private val delta1: Vector2
        get() = point2 - point1

    private val delta2: Vector2
        get() = point3 - point2

    private val x0: Double
        get() = point0.x

    private val y0: Double
        get() = point0.y

    private val x1: Double
        get() = point1.x

    private val y1: Double
        get() = point1.y

    private val x2: Double
        get() = point2.x

    private val y2: Double
        get() = point2.y

    private val x3: Double
        get() = point3.x

    private val y3: Double
        get() = point3.y


    private val l10: ImplicitLineFunction
        get() = ImplicitLineFunction(
            a = 3 * y1 - 3 * y0,
            b = 3 * x0 - 3 * x1,
            c = 3 * x1 * y0 - 3 * x0 * y1,
        )

    private val l20: ImplicitLineFunction
        get() = ImplicitLineFunction(
            a = 3 * y2 - 3 * y0,
            b = 3 * x0 - 3 * x2,
            c = 3 * x2 * y0 - 3 * x0 * y2,
        )

    private val l21: ImplicitLineFunction
        get() = ImplicitLineFunction(
            a = 9 * y2 - 9 * y1,
            b = 9 * x1 - 9 * x2,
            c = 9 * x2 * y1 - 9 * x1 * y2,
        )

    private val l30: ImplicitLineFunction
        get() = ImplicitLineFunction(
            a = y3 - y0,
            b = x0 - x3,
            c = x3 * y0 - x0 * y3,
        )

    private val l31: ImplicitLineFunction
        get() = ImplicitLineFunction(
            a = 3 * y3 - 3 * y1,
            b = 3 * x1 - 3 * x3,
            c = 3 * x3 * y1 - 3 * x1 * y3,
        )

    private val l32: ImplicitLineFunction
        get() = ImplicitLineFunction(
            a = 3 * y3 - 3 * y2,
            b = 3 * x2 - 3 * x3,
            c = 3 * x3 * y2 - 3 * x2 * y3,
        )

    sealed class SelfIntersectionResult {
        /**
         * The self-intersection exists and occurs at the given t-values.
         */
        data class Existing(
            val t0: Double,
            val t1: Double,
        ) : SelfIntersectionResult()

        /**
         * The curve does not have a self-intersection
         */
        data object NonExisting : SelfIntersectionResult()
    }

    /**
     * @return The self-intersection result, or null which implies that the
     * curve is degenerate.
     */
    fun findSelfIntersection(
        tolerance: NumericTolerance.Absolute,
    ): SelfIntersectionResult? {
        // Sánchez-Reyes, J. Self-Intersections of Cubic Bézier Curves Revisited. Mathematics 2024, 12, 2463. https://doi.org/10.3390/math12162463
        // 4. Finding the Parameter Values for the Double Point via Factorization

        val parametricPolynomial = toParametricPolynomial()

        val a1: Vector2 = parametricPolynomial.a1
        val a2: Vector2 = parametricPolynomial.a2
        val a3: Vector2 = parametricPolynomial.a3

        val detA1: Double = Matrix2x2.columnMajor(
            column0 = a2,
            column1 = a3,
        ).determinant

        if (detA1.equalsZeroWithTolerance(tolerance = tolerance)) {
            // This implies a degenerate straight line case
            // FIXME: While this conclusion is taken straight from the article, it seems to be incorrect. |A₁| seems
            //  to imply only that the solution definitely can't be found, the curve might be a proper cubic curve
            //  with some constraints. It might be the case that |A₁| = 0 implies that _either_ the X or the Y component
            //  of the parametric cubic curve degenerates (to a quadratic/linear curve or point?).
            return null
        }

        val detA2: Double = Matrix2x2.columnMajor(
            column0 = a1,
            column1 = a3,
        ).determinant

        val detA3: Double = Matrix2x2.columnMajor(
            column0 = a1,
            column1 = a2,
        ).determinant

        // Substitute...
        // α = uv
        // β = u + v
        // γ = (u + v)² - uv = β² - α

        // Cramer’s rule furnishes the unique solution as a quotient of determinants A₁, A₂, A₃ as follows:
        val beta: Double = -detA2 / detA1
        val gamma: Double = detA3 / detA1

        // α = β² - γ
        val alpha: Double = beta.sq - gamma

        // Solve t² - βt + αt = 0
        val (u, v) = QuadraticPolynomial.checked(
            a0 = alpha,
            a1 = -beta,
            a2 = 1.0,
        ).findRoots() ?: return SelfIntersectionResult.NonExisting

        return SelfIntersectionResult.Existing(
            t0 = u,
            t1 = v,
        )
    }

    /**
     * Find the polynomial B(t) . B'(t)
     */
    fun findPointProjectionPolynomial(
        g: Vector2,
    ): Polynomial {
        val p0 = point0 - g
        val p1 = point1 - g
        val p2 = point2 - g
        val p3 = point3 - g

        val a = p3 - 3.0 * p2 + 3.0 * p1 - p0
        val b = 3.0 * p2 - 6.0 * p1 + 3.0 * p0
        val c = 3.0 * (p1 - p0)
        val d = p0

        return Polynomial.normalized(
            c.dot(d),
            c.dot(c) + 2.0 * b.dot(d),
            3.0 * b.dot(c) + 3.0 * a.dot(d),
            4.0 * a.dot(c) + 2.0 * b.dot(b),
            5.0 * a.dot(b),
            3.0 * a.dot(a),
        )
    }

    fun evaluatePartially(t: Double): QuadraticBezierBinomial {
        val subPoint0 = point0 + delta0 * t
        val subPoint1 = point1 + delta1 * t
        val subPoint2 = point2 + delta2 * t

        return QuadraticBezierBinomial(
            point0 = subPoint0,
            point1 = subPoint1,
            point2 = subPoint2,
        )
    }

    fun isFullyOverlapping(
        other: CubicBezierBinomial,
    ): Boolean = normalize().equalsWithTolerance(other.normalize())

    fun normalize(): ParametricPolynomial<*> = toParametricPolynomial().normalize()

    override fun toParametricPolynomial(): LowParametricPolynomial = ParametricPolynomial.cubic(
        a3 = -point0 + 3.0 * point1 - 3.0 * point2 + point3,
        a2 = 3.0 * point0 - 6.0 * point1 + 3.0 * point2,
        a1 = -3.0 * point0 + 3.0 * point1,
        a0 = point0,
    )

    override fun apply(a: Double): Vector2 {
        val t = a

        val u = 1.0 - t
        val c1 = u * u * u * point0
        val c2 = 3.0 * u * u * t * point1
        val c3 = 3.0 * u * t * t * point2
        val c4 = t * t * t * point3
        return c1 + c2 + c3 + c4
    }

    fun applyFast(t: Double): Vector2 {
        val quadraticBezierBinomial = evaluatePartially(t = t)
        val lineFunction = quadraticBezierBinomial.evaluatePartially(t = t)
        return lineFunction.apply(t)
    }

    override fun locatePoint(
        point: Vector2,
        tRange: ClosedFloatingPointRange<Double>,
        tolerance: NumericTolerance.Absolute,
    ): Double? {
        // TODO: Move the responsibility of hybrid approaches to a higher layer
        return locatePointByInversionWithControlCheck(
            point = point,
            tolerance = tolerance,
        ) ?: locatePointByProjection(
            point = point,
            tRange = tRange,
            tolerance = tolerance,
        )
    }

    // TODO: Nuke the control check, simplify the verification, move to a higher
    //  layer
    private fun locatePointByInversionWithControlCheck(
        point: Vector2,
        tolerance: NumericTolerance,
    ): Double? {
        val eps = 10e-8

        val locatedTValue = locatePointByInversion(
            point = point,
        )

        // A control check with an extremely close point
        val locatedControlTValue = locatePointByInversion(
            point = point + Vector2(eps, eps),
        )

        return when {
            locatedTValue == null || locatedControlTValue == null -> null

            locatedTValue.equalsWithTolerance(
                locatedControlTValue,
                tolerance = tolerance,
            ) -> locatedTValue

            // If the t-value for the control point is not remotely close to the located t-value, we're near the
            // self-intersection and the results cannot be trusted
            else -> null
        }
    }

    /**
     * Solve the equation p(t) = p0
     *
     * @return either...
     * - a t-value (t-value of the [point] if it lies on the curve or a t-value
     * of a point lying on the curve close to [point], but not an actual projection,
     * or a t-value of a quasi-random point if [point] is close to the self-intersection)
     * - `null` if the t-value ćouldn't be found, because the curve degenerates
     * to a line or a point, or if [point] is very close to the self-intersection
     */
    internal fun locatePointByInversion(
        point: Vector2,
    ): Double? = inverted?.applyOrNull(point)

    /**
     * @return the t-value (or one of t-values) for [point] if it lies on the
     * curve or `null` if the point doesn't seem to lie on the curve (within
     * the given [tRange])
     */
    internal fun locatePointByProjection(
        point: Vector2,
        tRange: ClosedFloatingPointRange<Double>,
        tolerance: NumericTolerance.Absolute,
    ): Double? {
        // Find the t-values of all points on curve orthogonal to the given point
        // TODO: Some valid points aren't found because the polynomial root
        //       finding doesn't enter the complex domain
        val projectedTValues = projectPointAll(
            point = point,
            tRange = tRange,
            tolerance = tolerance,
        )

        // Sample those t-values, so we know both the t-values and their respective
        // points
        val projectedPointSamples = projectedTValues.map { t ->
            Sample(
                t = t,
                point = apply(t),
            )
        }

        // We pick only the points which are the same point we're looking for,
        // typically it should be one point if the given point lies on the
        // curve at all
        val acceptableSamples = projectedPointSamples.filter { sample ->
            sample.point.equalsWithTolerance(
                other = point,
                tolerance = tolerance,
            )
        }

        // We take the representative t-value, the smallest one
        return acceptableSamples.minOfOrNull { it.t }
    }

    data class PointProjection(
        val t: Double,
        val distance: Double,
    )

    /**
     * Project the [point] onto the curve within [range]
     *
     * @return the t-value of the point on the curve closest to [point] or
     * the range start/end value if the true closest t-value is outside of [range]
     */
    fun projectPointIteratively(
        range: ClosedFloatingPointRange<Double>,
        point: Vector2,
        tolerance: NumericTolerance.Absolute,
    ): PointProjection? {
        val (tStart, tMid, tEnd) = LinSpace.generate(
            range = range,
            sampleCount = 12,
        ).withNeighboursOrNull().minByOrNull { (_, tMid, _) ->
            Vector2.distanceSquared(
                apply(tMid),
                point,
            )
        }!!

        val (tFound, foundDistance) = when {
            tStart == null || tEnd == null -> {
                val tStartEffective = tStart ?: tMid
                val tEndEffective = tEnd ?: tMid

                (tStartEffective..tEndEffective).minByWithSelecteeOrNull { t ->
                    Vector2.distanceSquared(
                        apply(t),
                        point,
                    )
                } ?: return null
            }

            else -> (tStart..tEnd).minByUnimodalWithSelectee(
                tolerance = tolerance,
            ) { t ->
                Vector2.distance(
                    apply(t),
                    point,
                )
            }
        }

        return PointProjection(
            t = tFound,
            distance = foundDistance,
        )
    }

    private fun projectPointAll(
        point: Vector2,
        tRange: ClosedFloatingPointRange<Double>,
        tolerance: NumericTolerance.Absolute,
    ): List<Double> {
        val projectionPolynomial = findPointProjectionPolynomial(point)

        val roots = projectionPolynomial.findTValueRoots(
            tRange = tRange,
            tolerance = tolerance,
        )

        return roots
    }

    fun projectPointClosest(
        point: Vector2,
        tRange: ClosedFloatingPointRange<Double>,
        tolerance: NumericTolerance.Absolute = NumericTolerance.Absolute.Default,
    ): Double? {
        val tValues = projectPointAll(
            point = point,
            tRange = tRange,
            tolerance = tolerance,
        )

        return tValues.minByOrNull {
            Vector2.distance(
                apply(it),
                point,
            )
        }
    }

    fun splitAt(
        t: Double,
    ): Pair<CubicBezierBinomial, CubicBezierBinomial> {
        val quadraticBezierBinomial = evaluatePartially(t = t)
        val lineFunction = quadraticBezierBinomial.evaluatePartially(t = t)

        val midPoint = lineFunction.apply(t)

        return Pair(
            CubicBezierBinomial(
                point0 = point0,
                point1 = quadraticBezierBinomial.point0,
                point2 = lineFunction.point0,
                point3 = midPoint,
            ),
            CubicBezierBinomial(
                point0 = midPoint,
                point1 = lineFunction.point1,
                point2 = quadraticBezierBinomial.point2,
                point3 = point3,
            ),
        )
    }

    /**
     * @param tValuesSorted - a sorted list of t-values to split at
     */
    fun splitAtMultipleSorted(
        tValuesSorted: List<Double>,
    ): List<CubicBezierBinomial> {
        val partitioningResult =
            tValuesSorted.partitionAtCenter() ?: return listOf(this) // We're done, no more places to split

        val leftTValues = partitioningResult.previousElements
        val medianTValue = partitioningResult.innerElement
        val rightTValues = partitioningResult.nextElements

        val (leftSplitCurve, rightSplitCurve) = splitAt(
            t = medianTValue,
        )

        val leftCorrectedTValues = leftTValues.map { it / medianTValue }
        val rightCorrectedTValues = rightTValues.map { (it - medianTValue) / (1.0 - medianTValue) }

        val leftSubSplitCurves = leftSplitCurve.splitAtMultipleSorted(
            tValuesSorted = leftCorrectedTValues,
        )

        val rightSubSplitCurves = rightSplitCurve.splitAtMultipleSorted(
            tValuesSorted = rightCorrectedTValues,
        )

        val subCurves = leftSubSplitCurves + rightSubSplitCurves

        return subCurves
    }

    // TODO: Implement a hybrid algorithm (equation / iteration) on a given range
    //  + return distance
    // FIXME: This function doesn't even stick to the contract, it's terrible
    override fun projectPoint(
        point: Vector2,
        tolerance: NumericTolerance.Absolute,
    ): Double? = projectPointAll(
        point = point,
        tRange = primaryTRange, // ?
        tolerance = tolerance,
    ).singleOrNull()

    /**
     * Find the inverse of the cubic Bézier curve, i.e. the function that maps
     * the point on the curve to the t-value.
     *
     * @return The inverted polynomial, or null if the curve is degenerate
     */
    private fun invertRational(): RationalImplicitPolynomial? {
        val denominator = 3.0 * Matrix3x3.rowMajor(
            row0 = point1.toVector3(),
            row1 = point2.toVector3(),
            row2 = point3.toVector3(),
        ).determinant

        if (denominator == 0.0) {
            return null
        }

        val nominator1 = Matrix3x3.rowMajor(
            row0 = point0.toVector3(),
            row1 = point1.toVector3(),
            row2 = point3.toVector3(),
        ).determinant

        val nominator2 = Matrix3x3.rowMajor(
            row0 = point0.toVector3(),
            row1 = point2.toVector3(),
            row2 = point3.toVector3(),
        ).determinant

        val c1 = nominator1 / denominator
        val c2 = -(nominator2 / denominator)

        val l10 = this.l10
        val l20 = this.l20
        val l21 = this.l21
        val l30 = this.l30
        val l31 = this.l31

        val la = c1 * l31 + c2 * (l30 + l21) + l20
        val lb = c1 * l30 + c2 * l20 + l10

        return RationalImplicitPolynomial(
            nominatorFunction = lb,
            denominatorFunction = lb - la,
        )
    }

    fun findSecondDerivativeCurve(): ParametricLineFunction = findDerivativeCurve().findDerivativeCurve()

    override fun findDerivativeCurve(): QuadraticBezierBinomial = QuadraticBezierBinomial(
        point0 = 3.0 * delta0,
        point1 = 3.0 * delta1,
        point2 = 3.0 * delta2,
    )

    override fun buildInvertedFunction(
        tolerance: NumericTolerance.Absolute,
    ): InvertedBezierBinomial {
        // Curves degenerating to a line need some special handling
        val implicitPolynomial = invertRational()
            ?: throw UnsupportedOperationException("Inverting degenerate curves is not yet implemented")

        return InvertedBezierBinomial(
            implicitPolynomial = implicitPolynomial,
        )
    }

    val inverted: RationalImplicitPolynomial? by lazy { invertRational() }

    fun lower(): QuadraticBezierBinomial = QuadraticBezierBinomial(
        pointMatrix = raiseMatrixPseudoInverse * pointMatrix,
    )

    override fun implicitize(): ImplicitCubicCurveFunction {
        val l32 = this.l32
        val l31 = this.l31
        val l30 = this.l30
        val l21 = this.l21
        val l20 = this.l20
        val l10 = this.l10

        return calculateDeterminant(
            a = l32, b = l31, c = l30,
            d = l31, e = l30 + l21, f = l20,
            g = l30, h = l20, i = l10,
        )
    }

    /**
     * Find the image of this curve on the [target] curve.
     *
     * If this curve and the target curve are two segments of the same curve
     * (within reasonable tolerance), the resulting image modulation will transform
     * this curve to the target curve (within reasonable tolerance). Otherwise,
     * it will transform this curve into some other, unspecified curve.
     *
     * If this curve and the target curve are geometrically equivalent (within
     * reasonable tolerance), the returned image should be the identity image.
     *
     * This operation might not behave correctly for improperly parametrized
     * curves (curves degenerating to a quadratic or linear curve).
     */
    fun findPossibleImage(
        target: CubicBezierBinomial,
    ): Image {
        val secondDerivativeCurve = findSecondDerivativeCurve()
        val targetSecondDerivativeCurve = target.findSecondDerivativeCurve()

        // Target curve, b(u)
        val b0 = targetSecondDerivativeCurve.point0
        val deltaB = targetSecondDerivativeCurve.d

        // Source curve, c(v)
        val c0 = secondDerivativeCurve.point0
        val c1 = secondDerivativeCurve.point1

        // Image of v=0, v=1 in b's timeline (u₀, u₁)
        val u0 = c0.cross(b0) / deltaB.cross(c0)
        val u1 = c1.cross(b0) / deltaB.cross(c1)

        return Image(
            t0 = u0,
            t1 = u1,
        )
    }

    fun findImage(
        target: CubicBezierBinomial,
        tolerance: NumericTolerance,
    ): Image? {
        val image = findPossibleImage(target = target)

        return image.takeIf {
            it.overlap(
                source = this,
                target = target,
                tolerance = tolerance,
            )
        }
    }

    override fun toReprString(): String {
        return """
            |CubicBezierBinomial(
            |  point0 = ${point0.toReprString()},
            |  point1 = ${point1.toReprString()},
            |  point2 = ${point2.toReprString()},
            |  point3 = ${point3.toReprString()},
            |)
        """.trimMargin()
    }

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericTolerance,
    ): Boolean = when {
        other !is CubicBezierBinomial -> false
        !point0.equalsWithTolerance(other.point0, tolerance = tolerance) -> false
        !point1.equalsWithTolerance(other.point1, tolerance = tolerance) -> false
        !point2.equalsWithTolerance(other.point2, tolerance = tolerance) -> false
        !point3.equalsWithTolerance(other.point3, tolerance = tolerance) -> false
        else -> true
    }
}

/**
 * Calculate the determinant of a 3x3 polynomial matrix in the form:
 * | a b c |
 * | d e f |
 * | g h i |
 *
 * @return The determinant of the described matrix (a polynomial!)
 */
private fun calculateDeterminant(
    a: ImplicitLineFunction, b: ImplicitLineFunction, c: ImplicitLineFunction,
    d: ImplicitLineFunction, e: ImplicitLineFunction, f: ImplicitLineFunction,
    g: ImplicitLineFunction, h: ImplicitLineFunction, i: ImplicitLineFunction,
): ImplicitCubicCurveFunction = a * (e * i - f * h) - b * (d * i - f * g) + c * (d * h - e * g)

