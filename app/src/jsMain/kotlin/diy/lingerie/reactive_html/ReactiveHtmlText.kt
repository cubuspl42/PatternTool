package diy.lingerie.reactive_html

import diy.lingerie.reactive.cell.Cell
import kotlinx.browser.document
import org.w3c.dom.Text

class ReactiveHtmlText(
    val data: Cell<String>,
) : ReactiveHtmlNode() {
    override val rawNode: Text = data.form(
        create = { initialValue: String ->
            document.createTextNode(
                data = initialValue,
            )
        },
        update = { textNode: Text, newValue: String ->
            textNode.data = newValue
        },
    )
}
