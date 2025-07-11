package dev.toolkt.math.algebra.polynomials

import dev.toolkt.core.math.haveDifferentSigns
import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.equalsZeroWithTolerance
import dev.toolkt.core.range.isEmptyWithTolerance
import dev.toolkt.core.range.midpoint
import dev.toolkt.core.range.split
import dev.toolkt.core.range.subdivide
import dev.toolkt.core.range.withEndExcluded
import dev.toolkt.math.algebra.RealFunction

/**
 * Finds roots of the given function in the specified range using numerical methods. Assumes that roots aren't densely
 * packed and that the function is reasonably well-behaved in the range.
 *
 * @return A list of roots found in the range, or an empty list if no roots are found.
 */
fun RealFunction<Double>.findRootsNumericallyInRange(
    /**
     * The range in which to search for roots.
     */
    range: ClosedFloatingPointRange<Double>,
    /**
     * The tolerance for determining if either...
     * - a given sign-changing range's width is close enough to zero to consider its center a root
     * - f(x0) is close enough to zero to consider x0 a root
     */
    tolerance: NumericObject.Tolerance.Absolute,
): List<Double> {
    // We assume that the number of segments is large enough that a single
    // segment contains at most one root.
    return range.withEndExcluded().subdivide(segmentCount = 128).mapNotNull {
        when {
            changesSignDefinitely(range = it) -> findRootByBisection(
                subRange = it,
                tolerance = tolerance,
            )

            // As the subrange is assumed to be reasonably small, we'll assume that
            // if it does not change sign, it does not contain a root.
            else -> null
        }
    }.toList() + listOfNotNull(
        // As we exclude the end of the range in the subdivision, we need to check
        // the end point separately.
        range.endInclusive.takeIf {
            apply(it).equalsZeroWithTolerance(tolerance = tolerance)
        },
    )
}

/**
 * Checks if the range definitely changes sign, meaning that the start and end points
 * have different signs. It doesn't guarantee that there is no sign change within the
 * range.
 */
private fun RealFunction<Double>.changesSignDefinitely(
    range: OpenEndRange<Double>,
): Boolean = Double.haveDifferentSigns(
    apply(range.start),
    apply(range.endExclusive),
)

/**
 * Precondition: the function changes the sign in the subrange.
 *
 * Returns a single root within the subrange using the bisection method.
 */
private fun RealFunction<Double>.findRootByBisection(
    subRange: OpenEndRange<Double>,
    tolerance: NumericObject.Tolerance.Absolute,
    maxDepth: Int = 128,
): Double {
    tailrec fun RealFunction<Double>.findRootByBisectionRecursively(
        subRange: OpenEndRange<Double>,
        depth: Int,
    ): Double {
        val midpoint = subRange.midpoint
        val midpointValue = apply(midpoint)

        if (midpointValue.equalsZeroWithTolerance(tolerance) || subRange.isEmptyWithTolerance(tolerance = tolerance) || depth >= maxDepth) {
            // Returning the midpoint if the algorithm failed to converge is only one of the options
            return midpoint
        }

        val (leftSubRange, rightSubRange) = subRange.split()

        return when {
            changesSignDefinitely(range = leftSubRange) -> findRootByBisectionRecursively(
                subRange = leftSubRange,
                depth = depth + 1,
            )

            else -> findRootByBisectionRecursively(
                subRange = rightSubRange,
                depth = depth + 1,
            )
        }
    }

    return findRootByBisectionRecursively(
        subRange = subRange,
        depth = 0,
    )
}
