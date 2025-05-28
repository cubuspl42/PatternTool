package diy.lingerie.web_tool

import dev.toolkt.dom.pure.style.PureFlexAlignItems
import dev.toolkt.dom.pure.style.PureFlexDirection
import dev.toolkt.dom.reactive.node.ReactiveTextNode
import dev.toolkt.dom.reactive.node.element.ReactiveButtonElement
import dev.toolkt.dom.reactive.node.element.ReactiveCheckboxElement
import dev.toolkt.dom.reactive.node.element.ReactiveDivElement
import dev.toolkt.dom.reactive.node.element.ReactiveSpanElement
import dev.toolkt.dom.reactive.node.element.ReactiveWrapperNode
import dev.toolkt.dom.reactive.style.ReactiveFlexStyle
import dev.toolkt.dom.reactive.style.ReactiveStyle
import dev.toolkt.dom.reactive.widget.Button
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.hold
import dev.toolkt.reactive.reactive_list.ReactiveList
import kotlinx.browser.document

fun main() {
    val positionButton = Button.of(
        text = Cell.of("Click me!!"),
    )

    val checkbox = ReactiveCheckboxElement()

    val position = positionButton.onPressed.map {
        "pressed!"
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
            ReactiveSpanElement(
                children = ReactiveList.of(
                    ReactiveTextNode(
                        data = checkbox.isChecked.map { isCheckedNow ->
                            val symbol = if (isCheckedNow) "✅" else "❌"
                            "Checkbox state: $symbol"
                        },
                    ),
                ),
            ),
            ReactiveSpanElement(
                children = ReactiveList.of(
                    ReactiveTextNode(
                        data = position.map { positionNow ->
                            val positionString = when (positionNow) {
                                null -> "(none)"
                                else -> positionNow
                            }

                            "Position: $positionString"
                        },
                    ),
                ),
            ),
            positionButton.asReactiveElement,
            checkbox,
            ReactiveWrapperNode(
                document.createElement("h1").apply {
                    textContent = "Hello, world!"
                },
            ),
            ReactiveSpanElement(
                children = ReactiveList.of(
                    ReactiveButtonElement(
                        children = ReactiveList.of(
                            ReactiveTextNode(
                                data = Cell.of("Check!"),
                            ),
                        ),
                    ).apply {
                        onClick.pipe(
                            target = checkbox
                        ) { checkbox, _ ->
                            checkbox.setChecked(true)
                        }
                    },
                )
            ),
        ),
    )

    document.body!!.appendChild(
        root.rawNode,
    )
}
