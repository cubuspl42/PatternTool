package diy.lingerie.algebra

import diy.lingerie.algebra.NumericObject.Tolerance
import kotlin.jvm.JvmName
import kotlin.math.abs

interface NumericObject {
    sealed class Tolerance {
        data object Zero : Tolerance() {
            override fun equalsApproximately(
                value: Double, reference: Double
            ): Boolean = value == reference
        }

        data class Absolute(
            val absoluteTolerance: Double,
        ) : Tolerance() {
            override fun equalsApproximately(
                value: Double, reference: Double
            ): Boolean = abs(value - reference) <= absoluteTolerance
        }

        data class Relative(
            val relativeTolerance: Double,
        ) : Tolerance() {
            init {
                require(relativeTolerance > 0.0 && relativeTolerance < 0.25)
            }

            override fun equalsApproximately(
                value: Double,
                reference: Double,
            ): Boolean = abs(value - reference) <= relativeTolerance * abs(reference)
        }

        companion object {
            val Default = Absolute(
                absoluteTolerance = 10e-6,
            )
        }

        abstract fun equalsApproximately(
            value: Double,
            reference: Double,
        ): Boolean
    }

    fun equalsWithTolerance(
        other: NumericObject,
        tolerance: Tolerance,
    ): Boolean
}

fun NumericObject.equalsWithAbsoluteTolerance(
    other: NumericObject,
    absoluteTolerance: Double,
): Boolean = equalsWithTolerance(
    other = other,
    tolerance = Tolerance.Absolute(
        absoluteTolerance = absoluteTolerance,
    ),
)

fun NumericObject.equalsWithRelativeTolerance(
    other: NumericObject,
    relativeTolerance: Double,
): Boolean = equalsWithTolerance(
    other = other,
    tolerance = Tolerance.Relative(
        relativeTolerance = relativeTolerance,
    ),
)

fun <T : NumericObject> T?.equalsWithToleranceOrNull(
    other: NumericObject?,
    tolerance: Tolerance,
): Boolean = when {
    this != null && other != null -> equalsWithTolerance(
        other = other,
        tolerance = tolerance,
    )

    this == null && other == null -> true

    else -> false
}

fun NumericObject.equalsWithNoTolerance(
    other: NumericObject,
): Boolean = equalsWithTolerance(
    other = other,
    tolerance = Tolerance.Zero,
)

fun Double.equalsWithTolerance(
    other: Double,
    tolerance: Tolerance,
): Boolean = tolerance.equalsApproximately(this, other)

@JvmName("equalsWithToleranceListDouble")
fun List<Double>.equalsWithTolerance(
    other: List<Double>,
    tolerance: Tolerance,
): Boolean {
    if (this.size != other.size) return false

    return zip(other).all { (a, b) ->
        a.equalsWithTolerance(b, tolerance)
    }
}

@JvmName("equalsWithToleranceListNumericObject")
fun List<NumericObject>.equalsWithTolerance(
    other: List<NumericObject>,
    tolerance: Tolerance,
): Boolean {
    if (this.size != other.size) return false

    return zip(other).all { (a, b) ->
        a.equalsWithTolerance(b, tolerance)
    }
}
