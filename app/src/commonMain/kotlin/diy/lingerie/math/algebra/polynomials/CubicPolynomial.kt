package diy.lingerie.math.algebra.polynomials

import diy.lingerie.math.algebra.NumericObject
import diy.lingerie.math.algebra.RealFunction
import diy.lingerie.math.algebra.equalsWithTolerance
import diy.lingerie.math.algebra.linear.vectors.Vector2
import diy.lingerie.math.algebra.linear.vectors.Vector4
import kotlin.math.acos
import kotlin.math.cbrt
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class CubicPolynomial internal constructor(
    override val a0: Double,
    override val a1: Double,
    override val a2: Double,
    override val a3: Double,
) : LowPolynomial(), SuperLinearPolynomial {
    sealed class ShiftedForm : RealFunction<Double> {
        abstract val origin: Vector2

        final override fun apply(
            a: Double,
        ): Double = applyShifted(a - origin.a0) + origin.a1

        abstract fun applyShifted(x: Double): Double
    }

    /**
     * The "anchored form", i.e. the polynomial a' x^3 + c' x anchored at the given
     * [origin]
     */
    data class AnchoredForm(
        /**
         * The point of symmetry
         */
        override val origin: Vector2,
        /**
         * The scale factor (a')
         */
        val scale: Double,
        /**
         * The initial slope (c')
         */
        val initialSlope: Double,
    ) : ShiftedForm() {
        init {
            require(scale != 0.0)
        }

        fun toStandardForm(): CubicPolynomial = CubicPolynomial(
            a3 = scale,
            a2 = 0.0,
            a1 = initialSlope,
            a0 = 0.0,
        ).translate(
            t = origin,
        )

        /**
         * Convert to the normalized tense form, i.e. with the tension vector
         * pointing in the +X direction
         */
        fun toTenseForm(): TenseForm {
            val denominator = sqrt(1 + initialSlope * initialSlope)

            val tension = Vector2(
                scale / denominator,
                (scale * initialSlope) / denominator,
            )

            return TenseForm(
                origin = origin,
                tension = tension,
            )
        }

        override fun applyShifted(
            x: Double,
        ): Double = scale * (x * x * x) + initialSlope * x
    }

    data class TenseForm(
        /**
         * The point of symmetry
         */
        override val origin: Vector2,
        /**
         * The tension vector
         */
        val tension: Vector2,
    ) : ShiftedForm(), OriginForm {
        init {
            require(tension != Vector2.Zero)
        }

        fun toStandardForm(): CubicPolynomial = toAnchoredForm().toStandardForm()

        fun toAnchoredForm(): AnchoredForm = AnchoredForm(
            origin = origin,
            scale = tension.magnitude,
            initialSlope = tension.a1 / tension.a0,
        )

        override fun applyShifted(
            x: Double,
        ): Double = tension.magnitude * (x * x * x) + tension.a1 / tension.a0 * x
    }

    companion object {
        fun normalized(
            a0: Double,
            a1: Double,
            a2: Double,
            a3: Double,
        ): LowPolynomial = when (a3) {
            0.0 -> QuadraticPolynomial.normalized(
                a0 = a0,
                a1 = a1,
                a2 = a2,
            )

            else -> CubicPolynomial(
                a0 = a0,
                a1 = a1,
                a2 = a2,
                a3 = a3,
            )
        }

        fun monomialVector(
            x: Double,
        ) = Vector4(
            x * x * x,
            x * x,
            x,
            1.0,
        )
    }

    fun translate(
        t: Vector2,
    ): CubicPolynomial {
        // f(x - t.x) + t.y
        return substitute(
            // (x - t.x)
            linearPolynomial = LinearPolynomial(
                a0 = -t.a0,
                a1 = 1.0,
            ),
        ) + t.a1 // + t.y
    }

    operator fun plus(other: SubCubicPolynomial): CubicPolynomial = CubicPolynomial(
        a0 = this.a0 + other.a0,
        a1 = this.a1 + (other.a1 ?: 0.0),
        a2 = this.a2 + (other.a2 ?: 0.0),
        a3 = this.a3,
    )

    operator fun plus(constant: Double): CubicPolynomial = copy(
        a0 = this.a0 + constant,
    )

    override val coefficients: List<Double>
        get() = listOf(
            a0,
            a1,
            a2,
            a3,
        )

    override fun findRootsAnalytically(): List<Double> {
        val a = a3
        val b = a2
        val c = a1
        val d = a0

        val f = (3.0 * a * c - b * b) / (3.0 * a * a)
        val g = (2.0 * b * b * b - 9.0 * a * b * c + 27.0 * a * a * d) / (27.0 * a * a * a)
        val h = g * g / 4.0 + f * f * f / 27.0

        return when {
            h > 0 -> {
                // One real root

                val r = -g / 2.0
                val s = sqrt(h)
                val u = cbrt(r + s)
                val v = cbrt(r - s)

                val x0 = u + v - (b / (3.0 * a))

                listOf(x0)
            }

            h == 0.0 -> {
                // All roots real, at least two equal

                val u = cbrt(-g / 2.0)

                val x0 = 2.0 * u - (b / (3.0 * a))
                val x1 = -u - (b / (3.0 * a))

                listOf(x0, x1)
            }

            else -> {
                // Three distinct real roots

                val i = sqrt(g * g / 4.0 - h)
                val j = cbrt(i)
                val k = acos(-g / (2.0 * i))
                val m = cos(k / 3.0)
                val n = sqrt(3.0) * sin(k / 3.0)
                val p = -b / (3.0 * a)

                val x0 = 2.0 * j * m + p
                val x1 = j * (-m + n) + p
                val x2 = j * (-m - n) + p

                listOf(x0, x1, x2)
            }
        }
    }

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericObject.Tolerance,
    ): Boolean = when {
        other !is CubicPolynomial -> false
        !a0.equalsWithTolerance(other.a0, tolerance = tolerance) -> false
        !a1.equalsWithTolerance(other.a1, tolerance = tolerance) -> false
        !a2.equalsWithTolerance(other.a2, tolerance = tolerance) -> false
        !a3.equalsWithTolerance(other.a3, tolerance = tolerance) -> false
        else -> true
    }

    override fun apply(
        x: Double,
    ): Double = a0 + a1 * x + a2 * x * x + a3 * x * x * x

    fun substitute(
        linearPolynomial: LinearPolynomial,
    ): CubicPolynomial = this.substitute(
        p = linearPolynomial,
    ) as CubicPolynomial

    fun substitute(
        p: Polynomial,
    ) = a0 + a1 * p + a2 * p * p + a3 * p * p * p

    val derivativeQuadratic: QuadraticPolynomial
        get() = derivative as QuadraticPolynomial

    val symmetryAxis: Double
        get() = derivativeQuadratic.symmetryAxis

    val symmetryPoint: Vector2
        get() = Vector2(
            symmetryAxis,
            apply(symmetryAxis),
        )

    fun toAnchoredForm(): AnchoredForm {
        val symmetryPoint = this.symmetryPoint

        val shiftedPolynomial = this.translate(
            t = -symmetryPoint,
        )

        if (!shiftedPolynomial.a0.equalsWithTolerance(0.0)) {
            throw AssertionError()
        }

        if (!shiftedPolynomial.a2.equalsWithTolerance(0.0)) {
            throw AssertionError()
        }

        return AnchoredForm(
            origin = symmetryPoint,
            scale = shiftedPolynomial.a3,
            initialSlope = shiftedPolynomial.a1,
        )
    }

    fun toTenseForm(): TenseForm = toAnchoredForm().toTenseForm()
}
