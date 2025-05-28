package diy.lingerie.web_tool

import dev.toolkt.dom.pure.input.PureInputType
import dev.toolkt.dom.pure.style.PureFlexAlignItems
import dev.toolkt.dom.pure.style.PureFlexDirection
import dev.toolkt.dom.reactive.style.ReactiveFlexStyle
import dev.toolkt.dom.reactive.style.ReactiveStyle
import dev.toolkt.dom.reactive.utils.createReactiveHtmlButtonElement
import dev.toolkt.dom.reactive.utils.createReactiveHtmlDivElement
import dev.toolkt.dom.reactive.utils.createReactiveHtmlInputElement
import dev.toolkt.dom.reactive.utils.createReactiveHtmlSpanElement
import dev.toolkt.dom.reactive.utils.createReactiveTextNode
import dev.toolkt.dom.reactive.utils.getCheckedCell
import dev.toolkt.dom.reactive.utils.getClickEventStream
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.hold
import dev.toolkt.reactive.reactive_list.ReactiveList
import kotlinx.browser.document

fun main() {
    val positionButton = document.createReactiveHtmlButtonElement(
        children = ReactiveList.of(
            document.createTextNode("Click!"),
        ),
    )

    val checkbox = document.createReactiveHtmlInputElement(
        type = PureInputType.Checkbox,
    )

    val position = positionButton.getClickEventStream().map {
        "pressed!"
    }.hold(initialValue = null)

    val root = document.createReactiveHtmlDivElement(
        style = ReactiveStyle(
            displayStyle = Cell.of(
                ReactiveFlexStyle(
                    direction = PureFlexDirection.Column,
                    alignItems = PureFlexAlignItems.Start,
                ),
            ),
        ),
        children = ReactiveList.of(
            document.createReactiveHtmlSpanElement(
                children = ReactiveList.of(
                    document.createReactiveTextNode(
                        data = checkbox.getCheckedCell().map { isCheckedNow ->
                            val symbol = if (isCheckedNow) "✅" else "❌"
                            "Checkbox state: $symbol"
                        },
                    ),
                ),
            ),
            document.createReactiveHtmlSpanElement(
                children = ReactiveList.of(
                    document.createReactiveTextNode(
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
            positionButton,
            checkbox,
            document.createElement("h1").apply {
                textContent = "Hello, world!"
            },
            document.createReactiveHtmlSpanElement(
                children = ReactiveList.of(
                    document.createReactiveHtmlButtonElement(
                        children = ReactiveList.of(
                            document.createTextNode("Check!"),
                        ),
                    ).also { buttonElement ->
                        buttonElement.getClickEventStream().pipe(
                            target = checkbox
                        ) { checkbox, _ ->
                            checkbox.checked = true
                        }
                    },
                )
            ),
        ),
    )

    document.body!!.appendChild(root)
}
