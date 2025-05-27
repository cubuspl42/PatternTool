package diy.lingerie.web_tool

import dev.toolkt.dom.pure.style.PureFlexAlignItems
import dev.toolkt.dom.pure.style.PureFlexDirection
import dev.toolkt.dom.reactive.node.ReactiveTextNode
import dev.toolkt.dom.reactive.node.element.ReactiveButtonElement
import dev.toolkt.dom.reactive.node.element.ReactiveCheckboxElement
import dev.toolkt.dom.reactive.node.element.ReactiveDivElement
import dev.toolkt.dom.reactive.node.element.ReactiveSpanElement
import dev.toolkt.dom.reactive.node.element.ReactiveWrapperElement
import dev.toolkt.dom.reactive.style.ReactiveFlexStyle
import dev.toolkt.dom.reactive.style.ReactiveStyle
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.hold
import dev.toolkt.reactive.reactive_list.ReactiveList
import kotlinx.browser.document

fun main() {
    val positionButton = ReactiveButtonElement(
        children = ReactiveList.of(
            ReactiveTextNode(
                data = Cell.of("Click me!"),
            ),
        ),
    )

    val checkbox = ReactiveCheckboxElement()

    val position = positionButton.onClick.map {
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
                                else -> positionNow.toString()
                            }

                            "Position: $positionString"
                        },
                    ),
                ),
            ),
            positionButton,
            checkbox,
            ReactiveWrapperElement(
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
                            target = checkbox,
                        ) {
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
