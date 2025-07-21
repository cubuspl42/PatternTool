package dev.toolkt.core.numeric

import dev.toolkt.core.numeric.NumericObject.Tolerance
import kotlin.jvm.JvmName
import kotlin.math.abs

interface NumericObject {
    sealed class Tolerance {
        /**
         * Zero tolerance
         *
         * The only value that will be considered equal to the reference value
         * is the reference value itself.
         *
         * Useful in cases where an algorithm was designed with numeric tolerance
         * in mind, but in a specific case it's actually preferable to use exact
         * equality.
         */
        data object Zero : Tolerance() {
            override fun equalsApproximately(
                value: Double,
                reference: Double,
            ): Boolean = value == reference
        }

        /**
         * Threshold tolerance: |v - v_ref| ≤ t(v_ref)
         */
        sealed class Threshold : Tolerance() {
            companion object {
                fun hybrid(
                    relative: Relative,
                    absolute: Absolute,
                ): Hybrid = Hybrid(
                    relative = relative,
                    absolute = absolute,
                )
            }

            final override fun equalsApproximately(
                value: Double,
                reference: Double,
            ): Boolean = abs(value - reference) <= threshold(reference = reference)

            abstract fun threshold(reference: Double): Double
        }

        /**
         * Hybrid tolerance
         *
         * Hybrid tolerance uses the higher of two thresholds determined by
         * the [relative] and the [absolute] tolerance.
         *
         * Useful for cases where the compared values are expected to be
         * potentially both very small and very large, but it's also possible
         * that the reference value can be exactly zero. In such cases, relative
         * tolerance degrades to zero tolerance, which might be unacceptable.
         */
        data class Hybrid(
            /**
             * Relative tolerance, effective in the general case
             */
            val relative: Relative,
            /**
             * Absolute tolerance, effective for reference values close to zero
             */
            val absolute: Absolute,
        ) : Threshold() {
            override fun threshold(
                reference: Double,
            ): Double = maxOf(
                relative.threshold(reference),
                absolute.threshold(reference),
            )
        }

        /**
         * Absolute tolerance: |v - v_ref| ≤ a
         *
         * Useful for cases where the order of magnitude of the compared values
         * is known up front.
         */
        data class Absolute(
            /**
             * The absolute tolerance threshold
             */
            val absoluteTolerance: Double,
        ) : Threshold() {
            operator fun times(factor: Double) = Absolute(
                absoluteTolerance = absoluteTolerance * factor,
            )

            override fun threshold(
                reference: Double,
            ): Double = absoluteTolerance
        }

        /**
         * Asymmetric relative tolerance: |v - v_ref| ≤ r * |v_ref|
         *
         * Useful for cases where the compared values are expected to be
         * potentially both very small and very large. If the reference value
         * is zero, it should be noted that _only_ ±0.0 will be considered
         * equal to the reference value (within tolerance), no matter what
         * relative tolerance factor we pick.
         */
        data class Relative(
            /**
             * The relative tolerance factor
             */
            val relativeTolerance: Double,
        ) : Threshold() {
            override fun threshold(
                reference: Double,
            ): Double = relativeTolerance * abs(reference)

            init {
                require(relativeTolerance > 0.0 && relativeTolerance < 0.25)
            }
        }

        companion object {
            val Default = Absolute(
                absoluteTolerance = 1e-6,
            )
        }

        /**
         * Check if the [value] is approximately equal to the [reference] within
         * the tolerance defined by this object. This operation might be asymmetric.
         */
        abstract fun equalsApproximately(
            value: Double,
            reference: Double,
        ): Boolean
    }

    fun equalsWithTolerance(
        other: NumericObject,
        tolerance: Tolerance = Tolerance.Default,
    ): Boolean
}

fun <T : NumericObject> T?.equalsWithToleranceOrNull(
    other: NumericObject?,
    tolerance: Tolerance = Tolerance.Default,
): Boolean = when {
    this != null && other != null -> equalsWithTolerance(
        other = other,
        tolerance = tolerance,
    )

    this == null && other == null -> true

    else -> false
}

fun Double.equalsZeroWithTolerance(
    tolerance: Tolerance.Absolute = Tolerance.Default,
): Boolean = tolerance.equalsApproximately(this, 0.0)

fun Double.equalsWithTolerance(
    other: Double,
    tolerance: Tolerance = Tolerance.Default,
): Boolean = tolerance.equalsApproximately(this, other)

fun Double.divideWithTolerance(
    divisor: Double,
    tolerance: Tolerance = Tolerance.Default,
): Double? = when {
    divisor.equalsWithTolerance(
        0.0,
        tolerance = tolerance,
    ) -> null

    else -> this / divisor
}

@JvmName("equalsWithToleranceListDouble")
fun List<Double>.equalsWithTolerance(
    other: List<Double>,
    tolerance: Tolerance = Tolerance.Default,
): Boolean {
    if (this.size != other.size) return false

    return zip(other).all { (a, b) ->
        a.equalsWithTolerance(b, tolerance)
    }
}

@JvmName("equalsWithToleranceOrNullListDouble")
fun List<Double>?.equalsWithToleranceOrNull(
    other: List<Double>?,
    tolerance: Tolerance = Tolerance.Default,
): Boolean = when {
    this != null && other != null -> equalsWithTolerance(
        other = other,
        tolerance = tolerance,
    )

    this == null && other == null -> true

    else -> false
}

@JvmName("equalsWithToleranceListNumericObject")
fun List<NumericObject>.equalsWithTolerance(
    other: List<NumericObject>,
    tolerance: Tolerance = Tolerance.Default,
): Boolean {
    if (this.size != other.size) return false

    return zip(other).all { (a, b) ->
        a.equalsWithTolerance(b, tolerance)
    }
}
