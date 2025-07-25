package dev.toolkt.geometry.math

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.NumericTolerance
import dev.toolkt.geometry.Vector2
import dev.toolkt.geometry.math.parametric_curve_functions.bezier_binomials.CubicBezierBinomial
import dev.toolkt.geometry.x
import dev.toolkt.geometry.y
import dev.toolkt.math.algebra.RealFunction
import dev.toolkt.math.algebra.linear.vectors.Vector2
import dev.toolkt.math.algebra.linear.vectors.times
import dev.toolkt.math.algebra.polynomials.ConstantPolynomial
import dev.toolkt.math.algebra.polynomials.CubicPolynomial
import dev.toolkt.math.algebra.polynomials.LowPolynomial
import dev.toolkt.math.algebra.polynomials.Polynomial
import dev.toolkt.math.algebra.polynomials.SubCubicPolynomial
import dev.toolkt.math.algebra.polynomials.SubQuadraticPolynomial
import dev.toolkt.math.algebra.polynomials.derivativeSubCubic
import dev.toolkt.math.algebra.polynomials.modulate
import dev.toolkt.math.algebra.polynomials.transform
import kotlin.jvm.JvmName

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
            xPolynomial = Polynomial.Companion.cubic(
                a3 = a3.x,
                a2 = a2.x,
                a1 = a1.x,
                a0 = a0.x,
            ),
            yPolynomial = Polynomial.Companion.cubic(
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
            xPolynomial = Polynomial.Companion.quadratic(
                a2 = a.x,
                a1 = b.x,
                a0 = c.x,
            ),
            yPolynomial = Polynomial.Companion.quadratic(
                a2 = a.y,
                a1 = b.y,
                a0 = c.y,
            ),
        )

        fun linear(
            a1: Vector2,
            a0: Vector2,
        ): ParametricPolynomial<SubQuadraticPolynomial> = ParametricPolynomial(
            xPolynomial = Polynomial.Companion.linear(
                a1 = a1.x,
                a0 = a0.x,
            ),
            yPolynomial = Polynomial.Companion.linear(
                a1 = a1.y,
                a0 = a0.y,
            ),
        )

        fun constant(
            a: Vector2,
        ): ParametricPolynomial<ConstantPolynomial> = ParametricPolynomial(
            xPolynomial = Polynomial.Companion.constant(
                a0 = a.x,
            ),
            yPolynomial = Polynomial.Companion.constant(
                a0 = a.y,
            ),
        )
    }

    fun findRoots(
        range: ClosedFloatingPointRange<Double>,
        tolerance: NumericTolerance.Absolute,
    ): RootSet = RootSet(
        xRoots = xPolynomial.findRoots(
            range = range,
            tolerance = tolerance,
        ).toSet(),
        yRoots = yPolynomial.findRoots(
            range = range,
            tolerance = tolerance,
        ).toSet(),
    )

    override fun apply(x: Double): Vector2 = Vector2(
        x = xPolynomial.apply(x),
        y = yPolynomial.apply(x),
    )

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericTolerance,
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
        // dealing with a degenerate line-alike curve. If neither X nor Y
        // can be normalized (both are constant), this curve degenerates to a
        // point, and a point is already normalized
        return normalizeByX() ?: normalizeByY() ?: this
    }

    fun transform(
        transformation: LowPolynomial.Transformation,
    ): ParametricPolynomial<P> = ParametricPolynomial(
        xPolynomial = xPolynomial.transform(transformation = transformation),
        yPolynomial = yPolynomial.transform(transformation = transformation),
    )

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

typealias CubicParametricPolynomial = ParametricPolynomial<CubicPolynomial>

val LowParametricPolynomial.a0: Vector2
    get() = Vector2(
        x = xPolynomial.a0,
        y = yPolynomial.a0,
    )

val LowParametricPolynomial.a1: Vector2
    get() = Vector2(
        x = xPolynomial.a1 ?: 0.0,
        y = yPolynomial.a1 ?: 0.0,
    )

val LowParametricPolynomial.a2: Vector2
    get() = Vector2(
        x = xPolynomial.a2 ?: 0.0,
        y = yPolynomial.a2 ?: 0.0,
    )

val LowParametricPolynomial.a3: Vector2
    get() = Vector2(
        x = xPolynomial.a3 ?: 0.0,
        y = yPolynomial.a3 ?: 0.0,
    )


val CubicParametricPolynomial.a0: Vector2
    @JvmName("a0Cubic")
    get() = Vector2(
        x = xPolynomial.a0,
        y = yPolynomial.a0,
    )

val CubicParametricPolynomial.a1: Vector2
    @JvmName("a1Cubic")
    get() = Vector2(
        x = xPolynomial.a1,
        y = yPolynomial.a1,
    )

val CubicParametricPolynomial.a2: Vector2
    @JvmName("a2Cubic")
    get() = Vector2(
        x = xPolynomial.a2,
        y = yPolynomial.a2,
    )

val CubicParametricPolynomial.a3: Vector2
    @JvmName("a3Cubic")
    get() = Vector2(
        x = xPolynomial.a3,
        y = yPolynomial.a3,
    )

fun CubicParametricPolynomial.toBezierBinomial(): CubicBezierBinomial = CubicBezierBinomial(
    point0 = a0,
    point1 = a0 + a1 / 3.0,
    point2 = a0 + (2.0 * a1 + a2) / 3.0,
    point3 = a0 + a1 + a2 + a3,
)
