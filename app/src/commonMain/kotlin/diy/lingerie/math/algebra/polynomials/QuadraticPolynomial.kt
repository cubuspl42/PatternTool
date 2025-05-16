package diy.lingerie.math.algebra.polynomials

import diy.lingerie.math.algebra.NumericObject
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
        override val origin: Vector2,
        /**
         * The vertical scale factor
         */
        val verticalScale: Double,
    ) : OriginForm {
        companion object {
            fun normal(
                origin: Vector2,
            ): VertexForm = VertexForm(
                origin = origin,
                verticalScale = 1.0,
            )

            fun of(
                origin: Vector2,
                horizontalScale: Double,
            ): VertexForm = VertexForm(
                origin = origin,
                verticalScale = 1 / horizontalScale.sq,
            )
        }

        init {
            require(verticalScale != 0.0)
        }

        override val horizontalScale: Double
            get() = sqrt(1 / verticalScale)

        override fun apply(
            x: Double,
        ): Double = verticalScale * (x - origin.a0).sq + origin.a1

        override fun toStandardForm(): QuadraticPolynomial = QuadraticPolynomial(
            a0 = verticalScale * origin.a0.sq + origin.a1,
            a1 = -2 * verticalScale * origin.a0,
            a2 = verticalScale,
        )

        override fun normalize(): Pair<OriginForm, Double> {
            val dilation = sqrt(1 / verticalScale)

            return Pair(
                normal(origin = origin),
                dilation,
            )
        }
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

    override val isNormalized: Boolean
        get() = when {
            !a1.equalsWithTolerance(0.0) -> false
            !a2.equalsWithTolerance(1.0) -> false
            else -> true
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

    override fun substituteDirectly(
        p: LinearPolynomial,
    ): QuadraticPolynomial {
        val result = a0 + a1 * p + a2 * p * p
        return result as QuadraticPolynomial
    }

    override fun toOriginForm(): OriginForm = toVertexForm()

    /**
     * The parameter x0 of the vertical line x = x0 that's the axis of symmetry
     * of the parabola
     */
    override val symmetryAxis: Double
        get() = -a1 / (2 * a2)

    override fun normalizeSymmetric(): Pair<LowPolynomial, Dilation> {
        val normalDilation = Dilation(
            dilation = sqrt(1 / a2),
        )

        return Pair(
            QuadraticPolynomial(
                a2 = 1.0,
                a1 = 0.0,
                a0 = a0,
            ),
            normalDilation,
        )
    }

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
