package diy.lingerie.utils

import diy.lingerie.math.algebra.NumericObject
import diy.lingerie.math.algebra.equalsWithTolerance
import kotlin.math.sqrt

private val invPhi = (sqrt(5.0) - 1.0) / 2.0  //  1 / phi
private val invPhi2 = (3 - sqrt(5.0)) / 2.0  // 1 / phi^2

/**
 * Finds the minimum value of a function within a specified range using the
 * golden section search method.
 *
 * @param function A function that is unimodal in this range
 */
fun <T : Comparable<T>> ClosedFloatingPointRange<Double>.minBy(
    tolerance: NumericObject.Tolerance.Absolute = NumericObject.Tolerance.Default,
    function: (Double) -> T,
): Double = minByRecursive(
    tolerance = tolerance,
    function = function,
    searchRange = this,
)

private tailrec fun <T : Comparable<T>> minByRecursive(
    tolerance: NumericObject.Tolerance.Absolute,
    function: (Double) -> T,
    searchRange: ClosedFloatingPointRange<Double>,
    lowerPoint: Sample<Double, T> = function.invokeSampling(searchRange.linearlyInterpolate(t = invPhi2)),
    upperPoint: Sample<Double, T> = function.invokeSampling(searchRange.linearlyInterpolate(t = invPhi)),
): Double {
    if (searchRange.width.equalsWithTolerance(0.0, tolerance = tolerance)) {
        return searchRange.mid
    }

    return when {
        lowerPoint.value < upperPoint.value -> minByRecursive(
            tolerance = tolerance,
            function = function,
            searchRange = searchRange.copy(endInclusive = upperPoint.argument),
            upperPoint = lowerPoint,
        )

        else -> minByRecursive(
            tolerance = tolerance,
            function = function,
            searchRange = searchRange.copy(start = lowerPoint.argument),
            lowerPoint = upperPoint,
        )
    }
}
