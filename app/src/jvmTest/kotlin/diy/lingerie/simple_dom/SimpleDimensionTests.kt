package diy.lingerie.simple_dom

import kotlin.test.Test
import kotlin.test.assertEquals

class SimpleDimensionTests {
    @Test
    fun testParse() {
        assertEquals(
            expected = SimpleDimension(
                value = 123.0,
                unit = SimpleUnit.Pt,
            ),
            actual = SimpleDimension.parse("123pt"),
        )

        assertEquals(
            expected = SimpleDimension(
                value = 123.0,
                unit = SimpleUnit.Mm,
            ),
            actual = SimpleDimension.parse("123mm"),
        )

        assertEquals(
            expected = SimpleDimension(
                value = 123.0,
                unit = SimpleUnit.Percent,
            ),
            actual = SimpleDimension.parse("123%"),
        )
    }
}
