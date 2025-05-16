package diy.lingerie.utils

import diy.lingerie.test_utils.assertEqualsWithTolerance
import kotlin.test.Test

class RangesTests {
    @Test
    fun testNormalize() {
        val range = -2.0..3.14

        assertEqualsWithTolerance(
            expected = -0.019455252918287952,
            actual = range.normalize(-2.1),
        )

        assertEqualsWithTolerance(
            expected = 0.0,
            actual = range.normalize(-2.0),
        )

        assertEqualsWithTolerance(
            expected = 0.6291828793774318,
            actual = range.normalize(1.234),
        )

        assertEqualsWithTolerance(
            expected = 1.0,
            actual = range.normalize(3.14),
        )

        assertEqualsWithTolerance(
            expected = 1.1673151750972761,
            actual = range.normalize(4.0),
        )
    }
}
