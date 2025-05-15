package diy.lingerie.math.algebra.polynomials

import diy.lingerie.math.algebra.NumericObject
import diy.lingerie.math.algebra.equalsWithTolerance
import diy.lingerie.math.algebra.linear.vectors.Vector2
import diy.lingerie.math.algebra.polynomials.CubicPolynomial.TenseForm
import diy.lingerie.math.algebra.polynomials.QuadraticPolynomial.VertexForm
import diy.lingerie.utils.sq

data class LinearPolynomial internal constructor(
    override val a0: Double,
    override val a1: Double,
) : SubQuadraticPolynomial(), SuperConstantPolynomial {
    data class FactoredForm(
        val root: Double,
        val slope: Double,
    ) : OriginForm {
        companion object {
            fun of(
                origin: Vector2,
                horizontalScale: Double,
            ): VertexForm = VertexForm(
                origin = origin,
                verticalScale = 1 / horizontalScale,
            )
        }

        override fun apply(
            a: Double,
        ): Double = slope * (a - root)

        override val origin: Vector2
            get() = Vector2(root, 0.0)

        override val horizontalScale: Double
            get() = 1 / slope

        override fun normalizeHorizontally(): VertexForm = of(
            origin = origin,
            horizontalScale = 1.0,
        )
    }

    companion object {
        fun normalized(
            a0: Double,
            a1: Double,
        ): SubQuadraticPolynomial = when (a1) {
            0.0 -> ConstantPolynomial(
                a0 = a0,
            )

            else -> LinearPolynomial(
                a0 = a0,
                a1 = a1,
            )
        }
    }

    fun toFactoredForm(): FactoredForm = FactoredForm(
        root = findRoot(),
        slope = a1,
    )

    override val coefficients: List<Double>
        get() = listOf(
            a0,
            a1,
        )

    override fun findRootsAnalytically(): List<Double> = listOf(findRoot())

    fun findRoot(): Double = -a0 / a1

    override fun apply(
        x: Double,
    ): Double = a0 + a1 * x

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericObject.Tolerance,
    ): Boolean = when {
        other !is LinearPolynomial -> false
        !a0.equalsWithTolerance(other.a0, tolerance = tolerance) -> false
        !a1.equalsWithTolerance(other.a1, tolerance = tolerance) -> false
        else -> true
    }
}
