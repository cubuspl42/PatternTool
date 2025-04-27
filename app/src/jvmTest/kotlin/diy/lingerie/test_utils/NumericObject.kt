package diy.lingerie.test_utils

import diy.lingerie.algebra.NumericObject
import diy.lingerie.algebra.NumericObject.Tolerance
import diy.lingerie.algebra.equalsWithTolerance

fun assertEqualsWithTolerance(
    expected: Double,
    actual: Double,
    tolerance: Tolerance = TestTolerances.defaultTolerance,
) {
    assert(expected.equalsWithTolerance(actual, tolerance = tolerance)) {
        "Expected $expected, but got $actual (tolerance: $tolerance)"
    }
}

fun <T : NumericObject> assertEqualsWithTolerance(
    expected: T,
    actual: T,
    tolerance: Tolerance = TestTolerances.defaultTolerance,
) {
    assert(expected.equalsWithTolerance(actual, tolerance = tolerance)) {
        "Expected $expected, but got $actual (tolerance: $tolerance)"
    }
}

@JvmName("assertEqualsWithToleranceNumericObject")
fun <T : NumericObject> assertEqualsWithTolerance(
    expected: List<T>,
    actual: List<T>,
    tolerance: Tolerance = TestTolerances.defaultTolerance,
) {
    assert(expected.size == actual.size) {
        "Expected list size ${expected.size}, but got ${actual.size}"
    }

    for (i in expected.indices) {
        assertEqualsWithTolerance(
            expected = expected[i],
            actual = actual[i],
            tolerance = tolerance,
        )
    }
}

@JvmName("assertEqualsWithToleranceDouble")
fun assertEqualsWithTolerance(
    expected: List<Double>,
    actual: List<Double>,
    tolerance: Tolerance = TestTolerances.defaultTolerance,
) {
    assert(expected.size == actual.size) {
        "Expected list size ${expected.size}, but got ${actual.size}"
    }

    for (i in expected.indices) {
        assertEqualsWithTolerance(
            expected = expected[i],
            actual = actual[i],
            tolerance = tolerance,
        )
    }
}
