package diy.lingerie.geometry

import kotlin.test.Test
import kotlin.test.assertEquals

class RelativeAngleTests {
    @Test
    fun testZeroMinus() {
        assertEquals(
            expected = RelativeAngle.Zero,
            actual = RelativeAngle.Zero - RelativeAngle.Zero,
        )

        assertEquals(
            expected = RelativeAngle.NegativeStraight,
            actual = RelativeAngle.Zero - RelativeAngle.Straight,
        )
    }
}
