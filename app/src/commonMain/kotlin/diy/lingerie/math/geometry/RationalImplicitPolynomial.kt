package diy.lingerie.math.geometry

import diy.lingerie.math.geometry.implicit_curve_functions.ImplicitCurveFunction
import diy.lingerie.math.Ratio
import diy.lingerie.math.algebra.Function
import diy.lingerie.math.algebra.NumericObject
import diy.lingerie.math.algebra.linear.vectors.Vector2

data class RationalImplicitPolynomial(
    val nominatorFunction: ImplicitCurveFunction,
    val denominatorFunction: ImplicitCurveFunction,
) : Function<Vector2, Ratio>, NumericObject {
    override fun apply(v: Vector2): Ratio = Ratio(
        nominator = nominatorFunction.apply(v),
        denominator = denominatorFunction.apply(v),
    )

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericObject.Tolerance,
    ): Boolean = when {
        other !is RationalImplicitPolynomial -> false
        !nominatorFunction.equalsWithTolerance(other.nominatorFunction, tolerance = tolerance) -> false
        !denominatorFunction.equalsWithTolerance(other.denominatorFunction, tolerance = tolerance) -> false
        else -> true
    }
}
