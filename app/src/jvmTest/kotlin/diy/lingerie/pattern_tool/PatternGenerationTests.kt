package diy.lingerie.pattern_tool

import diy.lingerie.geometry.Angle
import diy.lingerie.geometry.Point
import diy.lingerie.simple_dom.svg.SvgRoot
import diy.lingerie.test_utils.getResourceAsReader
import kotlin.io.path.Path
import kotlin.io.path.outputStream
import kotlin.test.Test

class PatternGenerationTests {
    @Test
    fun generateSimplePatternTest() {
        val patternPieceUpperCupSvgRoot = SvgRoot.parse(
            reader = PatternGenerationTests::class.java.getResourceAsReader("patternPieceUpperCup.svg")!!,
        )

        val patternPieceUpperCup = PatternPiece(
            position = Point(x = 100.0, y = 20.0),
            rotationAngle = Angle.ofDegrees(90.0),
            outline = Outline.loadSvg(svgRoot = patternPieceUpperCupSvgRoot),
        )

        val patternDocument = PatternDocument(
            patternPieces = listOf(patternPieceUpperCup),
        )

        Path("../output/pattern.pdf").outputStream().use { fileOutputStream ->
            patternDocument.dumpPdf(outputStream = fileOutputStream)
        }
    }
}
