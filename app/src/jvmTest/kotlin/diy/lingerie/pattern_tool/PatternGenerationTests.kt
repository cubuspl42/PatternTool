package diy.lingerie.pattern_tool

import diy.lingerie.simple_dom.svg.SvgRoot
import diy.lingerie.test_utils.getResourceAsReader
import kotlin.io.path.Path
import kotlin.io.path.outputStream
import kotlin.test.Test

class PatternGenerationTests {
    @Test
    fun generateSimplePatternTest() {
        val pieceSvgRoot = SvgRoot.parse(
            reader = PatternGenerationTests::class.java.getResourceAsReader("pattern.svg")!!,
        )

        val outline = Outline.loadSvg(svgRoot = pieceSvgRoot)

        val patternDocument = PatternDocument(
            outlines = listOf(outline),
        )

        Path("../output/pattern.pdf").outputStream().use { fileOutputStream ->
            patternDocument.dumpPdf(outputStream = fileOutputStream)
        }
    }
}
