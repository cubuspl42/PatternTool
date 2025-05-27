package diy.lingerie.web_tool

import dev.toolkt.dom.pure.style.PureFlexAlignItems
import dev.toolkt.dom.pure.style.PureFlexDirection
import dev.toolkt.dom.reactive.ReactiveButtonElement
import dev.toolkt.dom.reactive.ReactiveCheckboxElement
import dev.toolkt.dom.reactive.ReactiveDivElement
import dev.toolkt.dom.reactive.ReactiveFlexStyle
import dev.toolkt.dom.reactive.ReactiveStyle
import dev.toolkt.dom.reactive.ReactiveTextNode
import dev.toolkt.dom.reactive.ReactiveWrapperElement
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.reactive_list.ReactiveList
import dev.toolkt.reactive.event_stream.hold
import kotlinx.browser.document

fun main() {
    val button = ReactiveButtonElement(
        children = ReactiveList.of(
            ReactiveTextNode(
                data = Cell.of("Click me!"),
            ),
        ),
    )

    val checkbox = ReactiveCheckboxElement()

    val position = button.onClick.map {
        it.position
    }.hold(initialValue = null)

    val root = ReactiveDivElement(
        style = ReactiveStyle(
            displayStyle = Cell.of(
                ReactiveFlexStyle(
                    direction = PureFlexDirection.Column,
                    alignItems = PureFlexAlignItems.Start,
                ),
            ),
        ),
        children = ReactiveList.of(
            ReactiveTextNode(
                data = position.map { positionNow ->
                    when (positionNow) {
                        null -> "(none)"
                        else -> "$positionNow"
                    }
                },
            ),
            button,
            checkbox,
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
