package diy.lingerie.web_tool

import dev.toolkt.dom.pure.PureColor
import dev.toolkt.dom.pure.PureUnit
import dev.toolkt.dom.pure.percent
import dev.toolkt.dom.pure.px
import dev.toolkt.dom.pure.style.PureFlexAlignItems
import dev.toolkt.dom.pure.style.PureFlexDirection
import dev.toolkt.dom.pure.style.PureFlexJustifyContent
import dev.toolkt.dom.reactive.style.ReactiveFlexStyle
import dev.toolkt.dom.reactive.style.ReactiveStyle
import dev.toolkt.dom.reactive.utils.createReactiveTextNode
import dev.toolkt.dom.reactive.utils.gestures.MouseOverGesture
import dev.toolkt.dom.reactive.utils.gestures.trackMouseOverGesture
import dev.toolkt.dom.reactive.utils.html.createReactiveHtmlDivElement
import dev.toolkt.dom.reactive.utils.html.getMouseMoveEventStream
import dev.toolkt.dom.reactive.utils.svg.createReactiveSvgCircleElement
import dev.toolkt.dom.reactive.utils.svg.createReactiveSvgSvgElement
import dev.toolkt.geometry.Point
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.reactive_list.ReactiveList
import kotlinx.browser.document
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.svg.SVGSVGElement

fun main() {
    val rootElement = createRootElement()

    document.body!!.appendChild(rootElement)
}

private fun createRootElement(): HTMLDivElement {
    val primaryViewport = createPrimaryViewport()

    primaryViewport.getMouseMoveEventStream().listen {
        println(it)
    }

    return document.createReactiveHtmlDivElement(
        style = ReactiveStyle(
            displayStyle = Cell.of(
                ReactiveFlexStyle(
                    direction = PureFlexDirection.Column,
                    alignItems = PureFlexAlignItems.Start,
                ),
            ),
            width = Cell.of(PureUnit.Vw.full),
            height = Cell.of(PureUnit.Vh.full),
            backgroundColor = Cell.of(PureColor.lightGray),
        ),
        children = ReactiveList.of(
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
            ReactiveFlexStyle(
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
            createMouseOverGesturePreview(mouseOverGesture = it)
        },
    ),
)

private fun createMouseOverGesturePreview(
    mouseOverGesture: MouseOverGesture?,
): HTMLDivElement = when (mouseOverGesture) {
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
                        data = mouseOverGesture.clientPosition.map {
                            "[${it.x}, ${it.y}]"
                        },
                    )
                ),
            ),
        ),
    )
}
private fun createPrimaryViewport(): SVGSVGElement = ReactiveList.looped { childrenLooped ->
    val svgElement = document.createReactiveSvgSvgElement(
        style = ReactiveStyle(
            width = Cell.of(100.percent),
            height = Cell.of(100.percent),
        ),
        children = childrenLooped,
    )

    val children = ReactiveList.of(
        document.createReactiveSvgCircleElement(
            position = Cell.of(Point(20.0, 20.0)),
            radius = 4.0,
        ),
    )

    return@looped Pair(svgElement, children)
}
