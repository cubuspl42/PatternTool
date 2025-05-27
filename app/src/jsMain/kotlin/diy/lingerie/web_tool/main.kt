package diy.lingerie.web_tool

import dev.toolkt.reactive_html.ReactiveButtonElement
import dev.toolkt.reactive_html.ReactiveDivElement
import dev.toolkt.reactive_html.ReactiveHtmlText
import dev.toolkt.reactive_html.ReactiveWrapperElement
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.reactive_list.ReactiveList
import dev.toolkt.reactive.event_stream.hold
import kotlinx.browser.document

fun main() {
    val button = ReactiveButtonElement(
        children = ReactiveList.of(
            ReactiveHtmlText(
                data = Cell.of("Click me!"),
            ),
        ),
    )

    val position = button.onClick.map {
        it.position
    }.hold(initialValue = null)

    val root = ReactiveDivElement(
        children = ReactiveList.of(
            ReactiveHtmlText(
                data = position.map { positionNow ->
                    when (positionNow) {
                        null -> "(none)"
                        else -> "$positionNow"
                    }
                },
            ),
            button,
            ReactiveWrapperElement(
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
