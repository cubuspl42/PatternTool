package diy.lingerie.utils.iterable

import diy.lingerie.test_utils.assertEqualsWithTolerance
import diy.lingerie.utils.minBy
import kotlin.math.PI
import kotlin.math.sin
import kotlin.test.Test

class GoldenSectionSearchTests {
    @Test
    fun testMinBy_minimumWithinRange() {
        val xMin = 3.0 / 8.0 * PI
        val xMax = 7.0 / 8.0 * PI

        val xFound = (xMin..xMax).minBy { x -> -sin(x) }

        assertEqualsWithTolerance(
            expected = PI / 2.0,
            actual = xFound,
        )
    }

    @Test
    fun testMinBy_minimumNotWithinRange() {
        val xMin = 3.0 / 8.0 * PI
        val xMax = 7.0 / 8.0 * PI

        val xFound = (xMin..xMax).minBy { x -> sin(x) }

        assertEqualsWithTolerance(
            expected = xMax,
            actual = xFound,
        )
    }
}
