package diy.lingerie.pattern_tool

import diy.lingerie.geometry.Angle
import diy.lingerie.geometry.Point
import diy.lingerie.simple_dom.svg.SvgRoot
import diy.lingerie.utils.getResourceAsReader
import kotlin.io.path.Path
import kotlin.io.path.outputStream
import kotlin.test.Test

class PatternGenerationTests {
    @Test
    fun generateSimplePatternTest() {
        val upperCupPatternPiece = PatternPiece(
            position = Point(x = 100.0, y = 20.0),
            rotationAngle = Angle.ofDegrees(90.0),
            outline = Outline.loadSvg(
                svgRoot = SvgRoot.parse(
                    reader = PatternGenerationTests::class.java.getResourceAsReader("patternPieceUpperCup.svg")!!,
                ),
            ),
        )

        val innerLowerCupPatternPiece = PatternPiece(
            position = Point(x = 0.0, y = 20.0),
            rotationAngle = Angle.ofDegrees(0.0),
            outline = Outline.loadSvg(
                svgRoot = SvgRoot.parse(
                    reader = PatternGenerationTests::class.java.getResourceAsReader("patternPieceInnerLowerCup.svg")!!,
                ),
            ),
        )

        val patternDocument = PatternDocument(
            pages = listOf(
                PatternPage(
                    patternPieces = listOf(
                        upperCupPatternPiece,
                        innerLowerCupPatternPiece,
                    ),
                ),
                PatternPage(
                    patternPieces = listOf(
                        upperCupPatternPiece,
                        innerLowerCupPatternPiece,
                    ),
                ),
            )
        )

        patternDocument.dumpPdf(outputPath = Path("../output"))
    }
}
