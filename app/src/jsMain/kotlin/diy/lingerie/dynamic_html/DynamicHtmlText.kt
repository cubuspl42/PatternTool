package diy.lingerie.dynamic_html

import diy.lingerie.frp.cell.Cell
import kotlinx.browser.document
import org.w3c.dom.Text

class DynamicHtmlText(
    val data: Cell<String>,
) : DynamicHtmlNode() {
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
