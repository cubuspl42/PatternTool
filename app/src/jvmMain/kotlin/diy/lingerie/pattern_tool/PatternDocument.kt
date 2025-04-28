package diy.lingerie.pattern_tool

import diy.lingerie.simple_dom.fo.FoRoot
import org.apache.fop.apps.FopFactory
import org.apache.fop.apps.MimeConstants
import java.io.File
import java.nio.file.Path
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.sax.SAXResult
import kotlin.io.path.outputStream

enum class PatternGenerationDirection {
    Forward, Backward
}

enum class StrictnessLevel {
    Forgiving, Strict
}

data class PatternDocument(
    val pages: List<PatternPage>,
) {
    fun toFoRoot(): FoRoot = FoRoot(
        pageWidth = PaperSizeConstants.A4.width,
        pageHeight = PaperSizeConstants.A4.height,
        blocks = pages.map { page ->
            page.toPageFoBlock()
        },
    )

    fun dumpPdf(
        outputPath: Path,
    ) {
        val foRoot = toFoRoot()

        val fopFactory = FopFactory.newInstance(File(".").toURI())
        val transformerFactory = TransformerFactory.newInstance()
        val foUserAgent = fopFactory.newFOUserAgent()

        val transformer = transformerFactory.newTransformer()

        outputPath.resolve("pattern.pdf").outputStream().use { outputStream ->
            val fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, outputStream)

            transformer.transform(
                DOMSource(foRoot.toDocument()),
                SAXResult(fop.defaultHandler),
            )
        }
    }
}
