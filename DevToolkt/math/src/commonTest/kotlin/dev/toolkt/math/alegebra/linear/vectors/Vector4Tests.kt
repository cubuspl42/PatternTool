package dev.toolkt.math.alegebra.linear.vectors

import dev.toolkt.core.numeric.NumericTolerance
import dev.toolkt.math.algebra.linear.vectors.Vector4
import dev.toolkt.core.numeric.assertEqualsWithTolerance
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class Vector4Tests {
    @Test
    fun testMagnitude() {
        val vector = Vector4(1.0, 2.0, 2.0, 1.0)

        assertEqualsWithTolerance(
            expected = 3.16228,
            actual = vector.magnitude,
            tolerance = NumericTolerance.Absolute(
                absoluteTolerance = 1e-4,
            ),
        )
    }

    @Test
    fun testIsNormalized() {
        val normalizedVector = Vector4(0.5, 0.5, 0.5, 0.5)

        assertTrue(
            actual = normalizedVector.isNormalized(),
        )

        val nonNormalizedVector = Vector4(1.0, 2.0, 2.0, 1.0)

        assertFalse(
            actual = nonNormalizedVector.isNormalized(),
        )
    }

    @Test
    fun testNormalize() {
        val vector = Vector4(1.0, 2.0, 2.0, 1.0)

        val normalized = vector.normalize()

        assertTrue(
            actual = normalized.isNormalized(),
        )
    }

    @Test
    fun testNormalizeOrNull() {
        val zeroVector = Vector4(0.0, 0.0, 0.0, 0.0)

        assertNull(
            actual = zeroVector.normalizeOrNull(),
        )

        val vector = Vector4(1.0, 2.0, 2.0, 1.0)

        val normalized = vector.normalizeOrNull()

        assertNotNull(
            actual = normalized,
        )

        assertTrue(
            actual = normalized.isNormalized(),
        )
    }

    @Test
    fun testDotProduct() {
        val vector1 = Vector4(1.0, 2.0, 3.0, 4.0)
        val vector2 = Vector4(4.0, 3.0, 2.0, 1.0)

        assertEquals(
            expected = 20.0,
            actual = vector1.dot(vector2),
            absoluteTolerance = 1e-9,
        )
    }

    @Test
    fun testAddition() {
        val vector = Vector4(1.0, 2.0, 3.0, 4.0)

        val result = vector + Vector4(1.0, 1.0, 1.0, 1.0)

        assertEquals(
            expected = Vector4(2.0, 3.0, 4.0, 5.0),
            actual = result,
        )
    }

    @Test
    fun testSubtraction() {
        val vector1 = Vector4(5.0, 7.0, 9.0, 11.0)
        val vector2 = Vector4(1.0, 2.0, 3.0, 4.0)

        val result = vector1 - vector2

        assertEquals(
            expected = Vector4(4.0, 5.0, 6.0, 7.0),
            actual = result,
        )
    }

    @Test
    fun testMultiplicationWithScalar() {
        val vector = Vector4(1.0, 2.0, 3.0, 4.0)

        val result = vector * 2.0

        assertEquals(
            expected = Vector4(2.0, 4.0, 6.0, 8.0),
            actual = result,
        )
    }

    @Test
    fun testDivisionByScalar() {
        val vector = Vector4(2.0, 4.0, 6.0, 8.0)

        val result = vector / 2.0

        assertEquals(
            expected = Vector4(1.0, 2.0, 3.0, 4.0),
            actual = result,
        )
    }

    @Test
    fun testUnaryMinus() {
        val vector = Vector4(1.0, -2.0, 3.0, -4.0)

        val result = -vector

        assertEquals(
            expected = Vector4(-1.0, 2.0, -3.0, 4.0),
            actual = result,
        )
    }
}
