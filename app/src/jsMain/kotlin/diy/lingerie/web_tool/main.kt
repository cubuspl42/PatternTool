package diy.lingerie.web_tool

import dev.toolkt.dom.pure.PureColor
import dev.toolkt.dom.pure.PureUnit
import dev.toolkt.dom.pure.percent
import dev.toolkt.dom.pure.px
import dev.toolkt.dom.pure.style.PureBorderStyle
import dev.toolkt.dom.pure.style.PureBoxSizing
import dev.toolkt.dom.pure.style.PureFill
import dev.toolkt.dom.pure.style.PureFlexAlignItems
import dev.toolkt.dom.pure.style.PureFlexDirection
import dev.toolkt.dom.pure.style.PureFlexJustifyContent
import dev.toolkt.dom.pure.style.PureFlexStyle
import dev.toolkt.dom.pure.style.PureStrokeStyle
import dev.toolkt.dom.reactive.style.ReactiveStyle
import dev.toolkt.dom.reactive.utils.createReactiveTextNode
import dev.toolkt.dom.reactive.utils.gestures.GenericMouseGesture
import dev.toolkt.dom.reactive.utils.gestures.onMouseOverGestureStarted
import dev.toolkt.dom.reactive.utils.gestures.track
import dev.toolkt.dom.reactive.utils.html.createReactiveHtmlDivElement
import dev.toolkt.dom.reactive.utils.svg.createReactiveSvgCircleElement
import dev.toolkt.dom.reactive.utils.svg.createReactiveSvgSvgElement
import dev.toolkt.geometry.Point
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.PropertyCell
import dev.toolkt.reactive.reactive_list.ReactiveList
import kotlinx.browser.document
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.svg.SVGCircleElement
import org.w3c.dom.svg.SVGElement

fun main() {
    val rootElement = createRootElement()

    document.body!!.apply {
        style.margin = "0"

        appendChild(rootElement)
    }
}

private fun createRootElement(): HTMLDivElement {
    val primaryViewport = createPrimaryViewport()

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
            createTopBar(
                trackedMouseOverGesture = primaryViewport.trackedMouseOverGesture,
            ),
            primaryViewport.element,
        ),
    )
}

private fun createTopBar(
    trackedMouseOverGesture: Cell<GenericMouseGesture?>,
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
        trackedMouseOverGesture.map {
            createMouseOverGesturePreview(mouseOverGestureNow = it)
        },
    ),
)

private fun createMouseOverGesturePreview(
    mouseOverGestureNow: GenericMouseGesture?,
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

data class PrimaryViewport(
    val element: HTMLDivElement,
    val trackedMouseOverGesture: Cell<GenericMouseGesture?>,
)

private fun createPrimaryViewport(): PrimaryViewport = ReactiveList.looped { childrenLooped ->
    val svgElement = document.createReactiveSvgSvgElement(
        style = ReactiveStyle(
            width = Cell.of(100.percent),
            height = Cell.of(100.percent),
        ),
        children = childrenLooped,
    )

    val userBezierCurve = UserBezierCurve(
        start = PropertyCell(initialValue = Point.origin),
        firstControl = PropertyCell(initialValue = Point(100.0, 100.0)),
        secondControl = PropertyCell(initialValue = Point(200.0, 100.0)),
        end = PropertyCell(initialValue = Point(300.0, 100.0)),
    )

    return@looped Pair(
        PrimaryViewport(
            element = document.createReactiveHtmlDivElement(
                style = ReactiveStyle(
                    boxSizing = PureBoxSizing.BorderBox,
                    width = Cell.of(100.percent),
                    height = Cell.of(100.percent),
                    displayStyle = Cell.of(
                        PureFlexStyle(),
                    ),
                    borderStyle = PureBorderStyle(
                        width = 4.px,
                        color = PureColor.darkGray,
                        style = PureBorderStyle.Style.Solid,
                    ),
                ),
                children = ReactiveList.of(svgElement),
            ),
            trackedMouseOverGesture = svgElement.onMouseOverGestureStarted().track(),
        ),
        ReactiveList.of(
            userBezierCurve.reactiveBezierCurve.createReactiveSvgPathElement(
                style = ReactiveStyle(
                    fill = Cell.of(PureFill.None),
                    strokeStyle = PureStrokeStyle(
                        color = PureColor.black,
                        width = 4.px,
                    ),
                ),
            ),
            createCircleHandleElement(
                container = svgElement,
                position = userBezierCurve.start,
            ),
            createCircleHandleElement(
                container = svgElement,
                position = userBezierCurve.firstControl,
            ),
            createCircleHandleElement(
                container = svgElement,
                position = userBezierCurve.secondControl,
            ),
            createCircleHandleElement(
                container = svgElement,
                position = userBezierCurve.end,
            ),
        ),
    )
}

private fun createCircleHandleElement(
    container: SVGElement,
    position: PropertyCell<Point>,
): SVGCircleElement = createDraggableSvgElement(
    container = container,
    position = position,
) { position ->
    Cell.looped(
        placeholderValue = null,
    ) { mouseOverGesture: Cell<GenericMouseGesture?> ->
        val circleElement = document.createReactiveSvgCircleElement(
            style = ReactiveStyle(
                fill = mouseOverGesture.map { mouseOverGestureNow ->
                    PureFill.Colored(
                        color = when (mouseOverGestureNow) {
                            null -> PureColor.black
                            else -> PureColor.blue
                        },
                    )
                },
            ),
            position = position,
            radius = 8.0,
        )

        Pair(
            circleElement,
            circleElement.onMouseOverGestureStarted().track(),
        )
    }
}
