package diy.lingerie.pattern_tool

import org.apache.fop.apps.FopFactory
import org.apache.fop.apps.MimeConstants
import java.io.File
import java.io.Reader
import java.nio.file.Path
import javax.xml.transform.TransformerFactory
import javax.xml.transform.sax.SAXResult
import javax.xml.transform.stream.StreamSource
import kotlin.io.path.outputStream

fun Outline.dumpPdf(
    foReader: Reader,
    outputPath: Path,
) {
    val fopFactory = FopFactory.newInstance(File(".").toURI())
    val transformerFactory = TransformerFactory.newInstance()
    val foUserAgent = fopFactory.newFOUserAgent()


    outputPath.outputStream().use { outputStream ->
        val transformer = transformerFactory.newTransformer()
        val fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, outputStream)

        transformer.transform(
            StreamSource(foReader),
            SAXResult(fop.defaultHandler),
        )
    }
}
