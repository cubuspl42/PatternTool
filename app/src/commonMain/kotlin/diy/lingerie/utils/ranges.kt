package diy.lingerie.utils

/**
 * Normalizes the value to the range [start, end].
 */
fun ClosedFloatingPointRange<Double>.normalize(x: Double): Double {
    val x0 = start
    val x1 = endInclusive

    require(x0 != x1) { "x0 and x1 must be different to avoid division by zero." }

    return (x - x0) / (x1 - x0)
}

/**
 * @param t The value 0..1
 * @return The interpolated value in the range [[x0], [x1]].
 */
fun linearlyInterpolate(
    t: Double,
    x0: Double,
    x1: Double,
): Double {
    require(x0 != x1) { "x0 and x1 must be different to avoid division by zero." }

    return x0 + (t * (x1 - x0))
}

/**
 * @param t The value 0..1
 * @return The interpolated value in the range [start, endInclusive].
 */
fun ClosedFloatingPointRange<Double>.linearlyInterpolate(t: Double): Double = linearlyInterpolate(
    t = t,
    x0 = start,
    x1 = endInclusive,
)

fun Double.rescale(
    sourceRange: ClosedFloatingPointRange<Double>,
    targetRange: ClosedFloatingPointRange<Double>,
): Double {
    val normalized = sourceRange.normalize(this)
    return targetRange.linearlyInterpolate(normalized)
}
