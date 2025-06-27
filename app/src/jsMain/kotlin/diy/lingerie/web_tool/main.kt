package diy.lingerie.web_tool

import dev.toolkt.dom.pure.PureColor
import dev.toolkt.dom.pure.PureUnit
import dev.toolkt.dom.pure.input.PureInputType
import dev.toolkt.dom.pure.px
import dev.toolkt.dom.pure.style.PureFlexAlignItems
import dev.toolkt.dom.pure.style.PureFlexDirection
import dev.toolkt.dom.pure.style.PureFlexStyle
import dev.toolkt.dom.reactive.style.ReactiveStyle
import dev.toolkt.dom.reactive.utils.html.createReactiveHtmlDivElement
import dev.toolkt.dom.reactive.utils.html.createReactiveHtmlInputElement
import dev.toolkt.dom.reactive.utils.html.getValueCell
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.reactive_list.ReactiveList
import dev.toolkt.reactive.reactive_list.fuseOf
import kotlinx.browser.document
import org.w3c.dom.HTMLDivElement

fun main() {
    val rootElement = createRootElement()

    document.body!!.apply {
        style.margin = "0"

        appendChild(rootElement)
    }
}

private fun createRootElement(): HTMLDivElement {
    val textInput = createTextInput()

    return document.createReactiveHtmlDivElement(
        style = ReactiveStyle(
            displayStyle = Cell.of(
                PureFlexStyle(
                    direction = PureFlexDirection.Column,
                    alignItems = PureFlexAlignItems.Start,
                ),
            ),
            width = Cell.of(PureUnit.Vw.full),
            height = Cell.of(PureUnit.Vh.full),
            backgroundColor = Cell.of(PureColor.lightGray),
        ),
        children = ReactiveList.of(
            createTextInputRow(),
        ),
    )
}

private fun createTextInputRow(): HTMLDivElement {
    val textInputs = ReactiveList.of(
        createTextInput(),
        createTextInput(),
        createTextInput(),
        createTextInput(),
    )

    val textInputElements = textInputs.map { it.element }

    val textInputDataNodes = textInputs.fuseOf { textInput ->
        textInput.data.map {
            document.createTextNode(it)
        }
    }

    return document.createReactiveHtmlDivElement(
        style = ReactiveStyle(
            displayStyle = Cell.of(
                PureFlexStyle(
                    direction = PureFlexDirection.Column,
                ),
            ),
        ),
        children = ReactiveList.of(
            document.createReactiveHtmlDivElement(
                style = ReactiveStyle(
                    displayStyle = Cell.of(
                        PureFlexStyle(
                            direction = PureFlexDirection.Row,
                        ),
                    ),
                ),
                children = textInputElements,
            ),
            document.createReactiveHtmlDivElement(
                style = ReactiveStyle(
                    displayStyle = Cell.of(
                        PureFlexStyle(
                            direction = PureFlexDirection.Row,
                        ),
                    ),
                ),
                children = textInputDataNodes,
            ),
        ),
    )
}


data class TextInput(
    val element: HTMLDivElement,
    val data: Cell<String>,
)

private fun createTextInput(): TextInput {
    val textInput = document.createReactiveHtmlInputElement(
        style = ReactiveStyle(
            displayStyle = Cell.of(
                PureFlexStyle(
                    alignItems = PureFlexAlignItems.Start,
                ),
            ),
            width = Cell.of(24.px),
        ),
        type = PureInputType.Text,
    )

    textInput.value = "0"

    return TextInput(
        element = document.createReactiveHtmlDivElement(
            children = ReactiveList.of(
                textInput,
            ),
        ),
        data = textInput.getValueCell(),
    )
}
