package diy.lingerie.utils

import dev.toolkt.core.math.split
import diy.lingerie.test_utils.assertEqualsWithTolerance
import kotlin.test.Test
import kotlin.test.assertEquals

class DoubleSplitTests {
    @Test
    fun testSplitZero() {
        assertEquals(
            expected = Pair(
                0,
                0.0,
            ),
            actual = 0.0.split(),
        )
    }

    @Test
    fun testSplitOne() {
        assertEquals(
            expected = Pair(
                1,
                0.0,
            ),
            actual = 1.0.split(),
        )
    }

    @Test
    fun testSplitNegativeOne() {
        assertEquals(
            expected = Pair(
                -1,
                0.0,
            ),
            actual = (-1.0).split(),
        )
    }

    @Test
    fun testSplitPositive() {
        val (i, f) = 1.234.split()

        assertEquals(
            expected = 1,
            actual = i,
        )

        assertEqualsWithTolerance(
            expected = 0.234,
            actual = f,
        )
    }
}
