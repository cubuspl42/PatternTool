package diy.lingerie.pattern_tool

import diy.lingerie.simple_dom.fo.FoRoot
import diy.lingerie.simple_dom.fo.FoSvgBlock
import diy.lingerie.simple_dom.svg.SvgPath
import diy.lingerie.simple_dom.svg.SvgGroup
import diy.lingerie.geometry.Point
import diy.lingerie.simple_dom.SimpleUnit
import diy.lingerie.simple_dom.svg.SvgRoot
import org.apache.fop.apps.FopFactory
import org.apache.fop.apps.MimeConstants
import java.io.File
import java.nio.file.Path
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.sax.SAXResult
import kotlin.io.path.outputStream

val foRoot = FoRoot(
    pageWidth = PaperSizeConstants.A4.width,
    pageHeight = PaperSizeConstants.A4.height,
    pageUnit = SimpleUnit.mm,
    blocks = listOf(
        FoSvgBlock(
            svgElement = SvgRoot(
                width = PaperSizeConstants.A4.width,
                height = PaperSizeConstants.A4.height,
                unit = SimpleUnit.mm,
                children = listOf(
                    SvgGroup(
                        children = listOf(
                            SvgPath(
                                segments = listOf(
                                    SvgPath.Segment.MoveTo(Point(0.0, 0.0)),
                                    SvgPath.Segment.LineTo(Point(100.0, 50.0)),
                                    SvgPath.Segment.LineTo(Point(0.0, 50.0)),
                                    SvgPath.Segment.LineTo(Point(100.0, 0.0))
                                ),
                            ),
                        ),
                    ),
                ),
            ),
        ),
    ),
)

fun Outline.dumpPdf(
    outputPath: Path,
) {
    val fopFactory = FopFactory.newInstance(File(".").toURI())
    val transformerFactory = TransformerFactory.newInstance()
    val foUserAgent = fopFactory.newFOUserAgent()

    outputPath.outputStream().use { outputStream ->
        val transformer = transformerFactory.newTransformer()
        val fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, outputStream)

        transformer.transform(
            DOMSource(foRoot.toDocument()),
            SAXResult(fop.defaultHandler),
        )
    }
}
