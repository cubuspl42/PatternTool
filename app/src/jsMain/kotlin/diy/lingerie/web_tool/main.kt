package diy.lingerie.web_tool

import diy.lingerie.dynamic_html.DynamicButtonElement
import diy.lingerie.dynamic_html.DynamicDivElement
import diy.lingerie.dynamic_html.DynamicHtmlText
import diy.lingerie.dynamic_html.DynamicWrapperElement
import diy.lingerie.reactive.cell.Cell
import diy.lingerie.reactive.reactive_list.ReactiveList
import diy.lingerie.reactive.event_stream.hold
import kotlinx.browser.document

fun main() {
    val button = DynamicButtonElement(
        children = ReactiveList.of(
            DynamicHtmlText(
                data = Cell.of("Click me!"),
            ),
        ),
    )

    val position = button.onClick.map {
        it.position
    }.hold(initialValue = null)

    val root = DynamicDivElement(
        children = ReactiveList.of(
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
