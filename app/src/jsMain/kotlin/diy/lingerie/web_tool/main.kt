package diy.lingerie.web_tool

import dev.toolkt.core.platform.PlatformSystem
import dev.toolkt.dom.pure.PureColor
import dev.toolkt.dom.pure.PureUnit
import dev.toolkt.dom.pure.input.PureInputType
import dev.toolkt.dom.pure.percent
import dev.toolkt.dom.pure.px
import dev.toolkt.dom.pure.style.PureFlexAlignItems
import dev.toolkt.dom.pure.style.PureFlexDirection
import dev.toolkt.dom.pure.style.PureFlexJustifyContent
import dev.toolkt.dom.pure.style.PureFlexStyle
import dev.toolkt.dom.pure.style.PurePropertyValue
import dev.toolkt.dom.pure.style.PureTableDisplayStyle
import dev.toolkt.dom.pure.style.PureTextAlign
import dev.toolkt.dom.pure.style.PureVerticalAlign
import dev.toolkt.dom.reactive.style.ReactiveStyle
import dev.toolkt.dom.reactive.utils.createReactiveTextNode
import dev.toolkt.dom.reactive.utils.gestures.MouseOverGesture
import dev.toolkt.dom.reactive.utils.gestures.trackMouseOverGesture
import dev.toolkt.dom.reactive.utils.html.createReactiveHtmlDivElement
import dev.toolkt.dom.reactive.utils.html.createReactiveHtmlInputElement
import dev.toolkt.dom.reactive.utils.html.getValueCell
import dev.toolkt.dom.reactive.utils.svg.createReactiveSvgCircleElement
import dev.toolkt.dom.reactive.utils.svg.createReactiveSvgSvgElement
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.reactive_list.ReactiveList
import dev.toolkt.reactive.reactive_list.fuseOf
import kotlinx.browser.document
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.svg.SVGSVGElement

fun main() {
    val rootElement = createRootElement()

    document.body!!.apply {
        style.margin = "0"

        appendChild(rootElement)
    }
}

private fun createRootElement(): HTMLDivElement {
    val primaryViewport = Cell.looped(
        placeholderValue = null,
    ) { mouseOverGestureLooped: Cell<MouseOverGesture?> ->
        val primaryViewport = createPrimaryViewport(
            mouseOverGesture = mouseOverGestureLooped,
        )

        Pair(primaryViewport, primaryViewport.trackMouseOverGesture())
    }

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
            createRootElement2(),
            createTextInputRow(),
            createTopBar(
                mouseOverGesture = primaryViewport.trackMouseOverGesture(),
            ),
            primaryViewport,
        ),
    )
}

private fun createTopBar(
    mouseOverGesture: Cell<MouseOverGesture?>,
): HTMLDivElement = document.createReactiveHtmlDivElement(
    style = ReactiveStyle(
        displayStyle = Cell.of(
            PureFlexStyle(
                alignItems = PureFlexAlignItems.Center,
                justifyContent = PureFlexJustifyContent.Start,
            ),
        ),
        width = Cell.of(PureUnit.Percent.full),
        height = Cell.of(24.px),
        backgroundColor = Cell.of(PureColor.lightGray),
    ),
    children = ReactiveList.single(
        mouseOverGesture.map {
            createMouseOverGesturePreview(mouseOverGestureNow = it)
        },
    ),
)

private fun createMouseOverGesturePreview(
    mouseOverGestureNow: MouseOverGesture?,
): HTMLDivElement = when (mouseOverGestureNow) {
    null -> document.createReactiveHtmlDivElement(
        style = ReactiveStyle(
            backgroundColor = Cell.of(PureColor.red),
        ),
        children = ReactiveList.of(
            document.createTextNode("(no gesture)"),
        ),
    )

    else -> document.createReactiveHtmlDivElement(
        style = ReactiveStyle(
            backgroundColor = Cell.of(PureColor.green),
        ),
        children = ReactiveList.of(
            document.createReactiveHtmlDivElement(
                children = ReactiveList.of(
                    document.createReactiveTextNode(
                        data = mouseOverGestureNow.clientPosition.map {
                            "[${it.x}, ${it.y}]"
                        },
                    ),
                ),
            ),
        ),
    )
}

private fun createPrimaryViewport(
    mouseOverGesture: Cell<MouseOverGesture?>,
): SVGSVGElement = ReactiveList.looped { childrenLooped ->
    val svgElement = document.createReactiveSvgSvgElement(
        style = ReactiveStyle(
            width = Cell.of(100.percent),
            height = Cell.of(100.percent),
        ),
        children = childrenLooped,
    )

    val children = ReactiveList.singleNotNull(
        mouseOverGesture.map { mouseOverGestureOrNull ->
            PlatformSystem.collectGarbage()

            mouseOverGestureOrNull?.let {
                document.createReactiveSvgCircleElement(
                    position = it.offsetPosition,
                    radius = 4.0,
                )
            }
        },
    )

    return@looped Pair(svgElement, children)
}

private fun createRootElement2(): HTMLDivElement {
    val textInput = createTextInput()

    val intData = textInput.data.map { it.toIntOrNull() }

    return document.createReactiveHtmlDivElement(
        style = ReactiveStyle(
            backgroundColor = Cell.of(PureColor.lightGray),
        ),
        children = ReactiveList.of(
            textInput.element,
            document.createReactiveHtmlDivElement(
                children = ReactiveList.singleNotNull(
                    intData.map { integer ->
                        integer?.let {
                            document.createTextNode("Int: $it")
                        }
                    },
                ),
            ),
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

    val fusedChildren = textInputs.fuseOf { textInput ->
        textInput.data.map {
            document.createReactiveHtmlDivElement(
                style = ReactiveStyle(
                    displayStyle = Cell.of(PureTableDisplayStyle.Cell),
                    textAlign = Cell.of(PureTextAlign.Center),
                    verticalAlign = Cell.of(PureVerticalAlign.Middle),
                ),
                children = ReactiveList.of(
                    document.createTextNode(it),
                ),
            )
        }
    }

    return document.createReactiveHtmlDivElement(
        style = ReactiveStyle(
            displayStyle = Cell.of(
                PureTableDisplayStyle(
                    borderCollapse = PureTableDisplayStyle.BorderCollapse.Separate,
                    borderSpacing = 10.px,
                ),
            ),
            margin = Cell.of(PurePropertyValue.Dynamic("0 auto")),
        ),
        children = ReactiveList.of(
            document.createReactiveHtmlDivElement(
                style = ReactiveStyle(
                    displayStyle = Cell.of(PureTableDisplayStyle.Row),
                ),
                children = textInputs.map {
                    document.createReactiveHtmlDivElement(
                        style = ReactiveStyle(
                            displayStyle = Cell.of(PureTableDisplayStyle.Cell),
                            textAlign = Cell.of(PureTextAlign.Center),
                            verticalAlign = Cell.of(PureVerticalAlign.Middle),
                        ),
                        children = ReactiveList.of(it.element),
                    )
                },
            ),
            document.createReactiveHtmlDivElement(
                style = ReactiveStyle(
                    displayStyle = Cell.of(PureTableDisplayStyle.Row),
                ),
                children = fusedChildren,
            ),
        ),
    )
}

private data class TextInput(
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
