package diy.lingerie.math.algebra.polynomials

import diy.lingerie.math.algebra.NumericObject
import diy.lingerie.math.algebra.RealFunction
import diy.lingerie.math.algebra.equalsWithTolerance
import diy.lingerie.math.algebra.linear.vectors.Vector2
import diy.lingerie.utils.sq
import kotlin.math.sqrt

data class QuadraticPolynomial internal constructor(
    override val a0: Double,
    override val a1: Double,
    override val a2: Double,
) : SubCubicPolynomial(), SuperLinearPolynomial {
    /**
     * The vertex form of the quadratic function, i.e. the polynomial a' x^2
     * anchored at the given [origin]
     */
    data class VertexForm(
        /**
         * The position of the vertex
         */
        val origin: Vector2,
        /**
         * The vertical scale factor
         */
        val verticalScale: Double,
    ) : RealFunction<Double> {
        init {
            require(verticalScale != 0.0)
        }

        val horizontalScale: Double
            get() = sqrt(1 / verticalScale)

        override fun apply(
            x: Double,
        ): Double = verticalScale * (x - origin.a0).sq + origin.a1

        fun toStandardForm(): QuadraticPolynomial = QuadraticPolynomial(
            a0 = verticalScale * origin.a0.sq + origin.a1,
            a1 = -2 * verticalScale * origin.a0,
            a2 = verticalScale,
        )
    }

    companion object {
        fun normalized(
            a0: Double,
            a1: Double,
            a2: Double,
        ): SubCubicPolynomial = when (a2) {
            0.0 -> LinearPolynomial.normalized(
                a0 = a0,
                a1 = a1,
            )

            else -> QuadraticPolynomial(
                a0 = a0,
                a1 = a1,
                a2 = a2,
            )
        }
    }

    operator fun plus(
        other: SubQuadraticPolynomial,
    ): QuadraticPolynomial = QuadraticPolynomial(
        a0 = this.a0 + other.a0,
        a1 = this.a1 + (other.a1 ?: 0.0),
        a2 = this.a2,
    )

    override val coefficients: List<Double>
        get() = listOf(
            a0,
            a1,
            a2,
        )

    override fun findRootsAnalytically(): List<Double> = findRoots()?.toList() ?: emptyList()

    /**
     * The parameter x0 of the vertical line x = x0 that's the axis of symmetry
     * of the parabola
     */
    val symmetryAxis: Double
        get() = -a1 / (2 * a2)

    val vertex: Vector2
        get() = Vector2(
            symmetryAxis,
            apply(symmetryAxis),
        )

    fun toVertexForm(): VertexForm = VertexForm(
        origin = vertex,
        verticalScale = a2,
    )

    fun findRoots(): Pair<Double, Double>? {
        val a = a2
        val b = a1
        val c = a0

        val discriminant: Double = b * b - 4 * a * c

        fun buildRoot(
            sign: Double,
        ): Double = (-b + sign * sqrt(discriminant)) / (2 * a)

        return when {
            discriminant >= 0 -> Pair(
                buildRoot(sign = -1.0),
                buildRoot(sign = 1.0),
            )

            else -> null
        }
    }

    override fun apply(x: Double): Double = a0 + a1 * x + a2 * x * x

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericObject.Tolerance,
    ): Boolean = when {
        other !is QuadraticPolynomial -> false
        !a0.equalsWithTolerance(other.a0, tolerance = tolerance) -> false
        !a1.equalsWithTolerance(other.a1, tolerance = tolerance) -> false
        !a2.equalsWithTolerance(other.a2, tolerance = tolerance) -> false
        else -> true
    }
}
