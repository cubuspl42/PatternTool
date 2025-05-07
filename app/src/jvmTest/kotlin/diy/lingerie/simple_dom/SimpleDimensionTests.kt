package diy.lingerie.simple_dom

import diy.lingerie.test_utils.assertEqualsWithTolerance
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertIs

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
            expected = 123.2.px,
            actual = SimpleDimension.parse("123.2px"),
        )

        assertEquals(
            expected = SimpleDimension(
                value = 123.0,
                unit = SimpleUnit.Percent,
            ),
            actual = SimpleDimension.parse("123%"),
        )
    }

    @Test
    fun testMmInUnit() {
        assertEquals(
            expected = 1.mm,
            actual = 1.mm.inUnit(SimpleUnit.Mm)
        )

        assertEqualsWithTolerance(
            expected = 0.039370.inch,
            actual = 1.mm.inUnit(SimpleUnit.Inch)
        )

        assertEqualsWithTolerance(
            expected = 2.834645.pt,
            actual = 1.mm.inUnit(SimpleUnit.Pt)
        )

        assertEqualsWithTolerance(
            expected = 3.779527.px,
            actual = 1.mm.inUnit(SimpleUnit.Px)
        )

        assertEqualsWithTolerance(
            expected = 4.860236.inch,
            actual = 123.45.mm.inUnit(SimpleUnit.Inch)
        )
    }

    @Test
    fun testInchInUnit() {
        assertEquals(
            expected = 1.inch,
            actual = 1.inch.inUnit(SimpleUnit.Inch)
        )

        assertEquals(
            expected = 25.4.mm,
            actual = 1.inch.inUnit(SimpleUnit.Mm)
        )

        assertEquals(
            expected = 72.0.pt,
            actual = 1.inch.inUnit(SimpleUnit.Pt)
        )

        assertEquals(
            expected = 96.0.px,
            actual = 1.inch.inUnit(SimpleUnit.Px)
        )

        assertEquals(
            expected = 3135.63.mm,
            actual = 123.45.inch.inUnit(SimpleUnit.Mm)
        )
    }

    @Test
    fun testPtInUnit() {
        assertEquals(
            expected = 1.pt,
            actual = 1.pt.inUnit(SimpleUnit.Pt)
        )

        assertEqualsWithTolerance(
            expected = 0.013888.inch,
            actual = 1.pt.inUnit(SimpleUnit.Inch)
        )

        assertEqualsWithTolerance(
            expected = 0.352778.mm,
            actual = 1.pt.inUnit(SimpleUnit.Mm)
        )

        assertEqualsWithTolerance(
            expected = 1.333333.px,
            actual = 1.pt.inUnit(SimpleUnit.Px)
        )

        assertEqualsWithTolerance(
            expected = 43.550416.mm,
            actual = 123.45.pt.inUnit(SimpleUnit.Mm)
        )
    }

    @Test
    fun testPxInUnit() {
        assertEquals(
            expected = 1.px,
            actual = 1.px.inUnit(SimpleUnit.Px)
        )

        assertEqualsWithTolerance(
            expected = 0.010417.inch,
            actual = 1.px.inUnit(SimpleUnit.Inch)
        )

        assertEqualsWithTolerance(
            expected = 0.264583.mm,
            actual = 1.px.inUnit(SimpleUnit.Mm)
        )

        assertEqualsWithTolerance(
            expected = 0.75.pt,
            actual = 1.px.inUnit(SimpleUnit.Pt)
        )

        assertEqualsWithTolerance(
            expected = 32.662812.mm,
            actual = 123.45.px.inUnit(SimpleUnit.Mm)
        )
    }

    @Test
    fun testToDimensionString() {
        assertEquals("123.0mm", 123.0.mm.toDimensionString())
        assertEquals("72.0pt", 72.0.pt.toDimensionString())
        assertEquals("1.0in", 1.0.inch.toDimensionString())
        assertEquals("50.0%", 50.0.percent.toDimensionString())
    }

    @Test
    fun testParseInvalidFormat() {
        assertIs<IllegalArgumentException>(
            assertFails {
                SimpleDimension.parse("invalid")
            },
        )
    }
}
