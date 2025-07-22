package dev.toolkt.core.numeric

import kotlin.math.abs

/**
 * Numeric tolerance is a way to compare two floating-point numbers
 * with a certain degree of tolerance, rather than requiring exact equality.
 */
sealed class NumericTolerance {
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
    data object Zero : NumericTolerance() {
        override fun equalsApproximately(
            value: Double,
            reference: Double,
        ): Boolean = value == reference
    }

    /**
     * Threshold tolerance: |v - v_ref| ≤ t(v_ref)
     *
     * Threshold tolerance is a base class for different types of tolerance
     * that use a threshold function to determine if two values are
     * approximately equal.
     */
    sealed class Threshold : NumericTolerance() {
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
