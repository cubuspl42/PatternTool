package diy.lingerie.math.algebra.polynomials

import diy.lingerie.math.algebra.NumericObject
import diy.lingerie.math.algebra.equalsWithTolerance
import diy.lingerie.math.algebra.linear.vectors.Vector2
import diy.lingerie.math.algebra.polynomials.QuadraticPolynomial.VertexForm

data class LinearPolynomial internal constructor(
    override val a0: Double,
    override val a1: Double,
) : SubQuadraticPolynomial(), SuperConstantPolynomial {
    data class FactoredForm(
        val root: Double,
        val slope: Double,
    ) : OriginForm {
        companion object {
            fun normal(
                root: Double,
            ) = FactoredForm(
                slope = 1.0,
                root = root,
            )

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
            get() = TODO("Nuke")

        override fun normalize(): Pair<OriginForm, Double> {
            val dilation = 1 / slope

            return Pair(
                normal(root = root),
                dilation,
            )
        }

        override fun toStandardForm(): LinearPolynomial = LinearPolynomial(
            a0 = -slope * root,
            a1 = slope,
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

    override val isNormalized: Boolean
        get() = when {
            !a1.equalsWithTolerance(1.0) -> false
            else -> true
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

    override fun substituteDirectly(p: LinearPolynomial): LowPolynomial {
        val result = a0 + a1 * p
        return result as LinearPolynomial
    }

    fun findRoot(): Double = -a0 / a1

    override val symmetryAxis: Double
        get() = 0.0

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

    override fun toOriginForm(): OriginForm = toFactoredForm()
}
