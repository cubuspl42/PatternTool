package diy.lingerie.math.geometry

import diy.lingerie.geometry.Vector2
import diy.lingerie.geometry.x
import diy.lingerie.geometry.y
import diy.lingerie.math.algebra.NumericObject
import diy.lingerie.math.algebra.RealFunction
import diy.lingerie.math.algebra.linear.vectors.Vector2
import diy.lingerie.math.algebra.polynomials.ConstantPolynomial
import diy.lingerie.math.algebra.polynomials.LowPolynomial
import diy.lingerie.math.algebra.polynomials.Polynomial
import diy.lingerie.math.algebra.polynomials.SubCubicPolynomial
import diy.lingerie.math.algebra.polynomials.SubQuadraticPolynomial
import diy.lingerie.math.algebra.polynomials.derivativeSubCubic

data class ParametricPolynomial<P : LowPolynomial>(
    val xFunction: P,
    val yFunction: P,
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
            xFunction = Polynomial.cubic(
                a3 = a3.x,
                a2 = a2.x,
                a1 = a1.x,
                a0 = a0.x,
            ),
            yFunction = Polynomial.cubic(
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
            xFunction = Polynomial.quadratic(
                a2 = a.x,
                a1 = b.x,
                a0 = c.x,
            ),
            yFunction = Polynomial.quadratic(
                a2 = a.y,
                a1 = b.y,
                a0 = c.y,
            ),
        )

        fun linear(
            a1: Vector2,
            a0: Vector2,
        ): ParametricPolynomial<SubQuadraticPolynomial> = ParametricPolynomial(
            xFunction = Polynomial.linear(
                a1 = a1.x,
                a0 = a0.x,
            ),
            yFunction = Polynomial.linear(
                a1 = a1.y,
                a0 = a0.y,
            ),
        )

        fun constant(
            a: Vector2,
        ): ParametricPolynomial<ConstantPolynomial> = ParametricPolynomial(
            xFunction = Polynomial.constant(
                a0 = a.x,
            ),
            yFunction = Polynomial.constant(
                a0 = a.y,
            ),
        )
    }

    fun findRoots(): RootSet = RootSet(
        xRoots = xFunction.findRoots().toSet(),
        yRoots = yFunction.findRoots().toSet(),
    )

    override fun apply(x: Double): Vector2 = Vector2(
        x = xFunction.apply(x),
        y = yFunction.apply(x),
    )

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericObject.Tolerance,
    ): Boolean = when {
        other !is ParametricPolynomial<*> -> false
        xFunction != other.xFunction -> false
        yFunction != other.yFunction -> false
        else -> true
    }

    fun findDerivative(): SubCubicParametricPolynomial = ParametricPolynomial(
        xFunction = xFunction.derivativeSubCubic,
        yFunction = yFunction.derivativeSubCubic,
    )

    fun normalize(): ParametricPolynomial<*> {
        // If the X function can't be normalized (it's constant), we're
        // dealing with a denormalized line-alike curve. If neither X nor Y
        // can be normalized (both are constant), this curve degenerates to a
        // point, and a point is already normalized
        return normalizeByX() ?: normalizeByY() ?: this
    }

    private fun normalizeByX(): ParametricPolynomial<*>? {
        val (xFunctionNormalized, normalProjection) = xFunction.normalize() ?: return null
        val yFunctionNormalized = yFunction.project(normalProjection.invert())

        return ParametricPolynomial(
            xFunction = xFunctionNormalized,
            yFunction = yFunctionNormalized,
        )
    }

    private fun normalizeByY(): ParametricPolynomial<*>? {
        val (yFunctionNormalized, normalProjection) = yFunction.normalize() ?: return null
        val xFunctionNormalized = xFunction.project(normalProjection.invert())

        return ParametricPolynomial(
            xFunction = xFunctionNormalized,
            yFunction = yFunctionNormalized,
        )
    }
}

typealias LowParametricPolynomial = ParametricPolynomial<LowPolynomial>

typealias SubCubicParametricPolynomial = ParametricPolynomial<SubCubicPolynomial>
