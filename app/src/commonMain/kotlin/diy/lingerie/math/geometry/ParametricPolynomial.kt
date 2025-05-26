package diy.lingerie.math.geometry

import diy.lingerie.geometry.Vector2
import diy.lingerie.geometry.x
import diy.lingerie.geometry.y
import dev.toolkt.core.numeric.NumericObject
import diy.lingerie.math.algebra.RealFunction
import diy.lingerie.math.algebra.linear.vectors.Vector2
import diy.lingerie.math.algebra.polynomials.ConstantPolynomial
import diy.lingerie.math.algebra.polynomials.LowPolynomial
import diy.lingerie.math.algebra.polynomials.Polynomial
import diy.lingerie.math.algebra.polynomials.SubCubicPolynomial
import diy.lingerie.math.algebra.polynomials.SubQuadraticPolynomial
import diy.lingerie.math.algebra.polynomials.derivativeSubCubic
import diy.lingerie.math.algebra.polynomials.modulate

data class ParametricPolynomial<P : LowPolynomial>(
    val xPolynomial: P,
    val yPolynomial: P,
) : RealFunction<Vector2>, NumericObject {
    data class RootSet(
        val xRoots: Set<Double>,
        val yRoots: Set<Double>,
    ) {
        fun filter(
            predicate: (Double) -> Boolean,
        ): RootSet = RootSet(
            xRoots = xRoots.filter(predicate).toSet(),
            yRoots = yRoots.filter(predicate).toSet(),
        )

        val allRoots: Set<Double>
            get() = xRoots + yRoots
    }

    companion object {
        fun cubic(
            a3: Vector2,
            a2: Vector2,
            a1: Vector2,
            a0: Vector2,
        ): ParametricPolynomial<LowPolynomial> = ParametricPolynomial(
            xPolynomial = Polynomial.cubic(
                a3 = a3.x,
                a2 = a2.x,
                a1 = a1.x,
                a0 = a0.x,
            ),
            yPolynomial = Polynomial.cubic(
                a3 = a3.y,
                a2 = a2.y,
                a1 = a1.y,
                a0 = a0.y,
            ),
        )

        fun quadratic(
            a: Vector2,
            b: Vector2,
            c: Vector2,
        ): ParametricPolynomial<SubCubicPolynomial> = ParametricPolynomial(
            xPolynomial = Polynomial.quadratic(
                a2 = a.x,
                a1 = b.x,
                a0 = c.x,
            ),
            yPolynomial = Polynomial.quadratic(
                a2 = a.y,
                a1 = b.y,
                a0 = c.y,
            ),
        )

        fun linear(
            a1: Vector2,
            a0: Vector2,
        ): ParametricPolynomial<SubQuadraticPolynomial> = ParametricPolynomial(
            xPolynomial = Polynomial.linear(
                a1 = a1.x,
                a0 = a0.x,
            ),
            yPolynomial = Polynomial.linear(
                a1 = a1.y,
                a0 = a0.y,
            ),
        )

        fun constant(
            a: Vector2,
        ): ParametricPolynomial<ConstantPolynomial> = ParametricPolynomial(
            xPolynomial = Polynomial.constant(
                a0 = a.x,
            ),
            yPolynomial = Polynomial.constant(
                a0 = a.y,
            ),
        )
    }

    fun findRoots(): RootSet = RootSet(
        xRoots = xPolynomial.findRoots().toSet(),
        yRoots = yPolynomial.findRoots().toSet(),
    )

    override fun apply(x: Double): Vector2 = Vector2(
        x = xPolynomial.apply(x),
        y = yPolynomial.apply(x),
    )

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericObject.Tolerance,
    ): Boolean = when {
        other !is ParametricPolynomial<*> -> false
        !xPolynomial.equalsWithTolerance(other.xPolynomial) -> false
        !yPolynomial.equalsWithTolerance(other.yPolynomial) -> false
        else -> true
    }

    fun findDerivative(): SubCubicParametricPolynomial = ParametricPolynomial(
        xPolynomial = xPolynomial.derivativeSubCubic,
        yPolynomial = yPolynomial.derivativeSubCubic,
    )

    fun normalize(): ParametricPolynomial<*> {
        // If the X function can't be normalized (it's constant), we're
        // dealing with a denormalized line-alike curve. If neither X nor Y
        // can be normalized (both are constant), this curve degenerates to a
        // point, and a point is already normalized
        return normalizeByX() ?: normalizeByY() ?: this
    }

    private fun normalizeByX(): ParametricPolynomial<*>? {
        val (xFunctionNormalized, normalProjection) = xPolynomial.normalize() ?: return null
        val yFunctionNormalized = yPolynomial.modulate(normalProjection.invert())

        return ParametricPolynomial(
            xPolynomial = xFunctionNormalized,
            yPolynomial = yFunctionNormalized,
        )
    }

    private fun normalizeByY(): ParametricPolynomial<*>? {
        val (yFunctionNormalized, normalProjection) = yPolynomial.normalize() ?: return null
        val xFunctionNormalized = xPolynomial.modulate(normalProjection.invert())

        return ParametricPolynomial(
            xPolynomial = xFunctionNormalized,
            yPolynomial = yFunctionNormalized,
        )
    }
}

typealias LowParametricPolynomial = ParametricPolynomial<LowPolynomial>

typealias SubCubicParametricPolynomial = ParametricPolynomial<SubCubicPolynomial>
