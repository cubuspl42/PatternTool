package diy.lingerie.web_tool

import diy.lingerie.dynamic_html.DynamicButtonElement
import diy.lingerie.dynamic_html.DynamicDivElement
import diy.lingerie.dynamic_html.DynamicHtmlText
import diy.lingerie.dynamic_html.DynamicWrapperElement
import diy.lingerie.frp.cell.Cell
import diy.lingerie.frp.dynamic_list.DynamicList
import diy.lingerie.frp.event_stream.hold
import kotlinx.browser.document

fun main() {
    val button = DynamicButtonElement(
        children = DynamicList.of(
            DynamicHtmlText(
                data = Cell.of("Click me!"),
            ),
        ),
    )

    val position = button.onClick.map {
        it.position
    }.hold(initialValue = null)

    val root = DynamicDivElement(
        children = DynamicList.of(
            DynamicHtmlText(
                data = position.map { positionNow ->
                    when (positionNow) {
                        null -> "(none)"
                        else -> "$positionNow"
                    }
                },
            ),
            button,
            DynamicWrapperElement(
                document.createElement("h1").apply {
                    textContent = "Hello, world!"
                },
            ),
        ),
    )

    document.body!!.appendChild(
        root.rawNode,
    )
}
