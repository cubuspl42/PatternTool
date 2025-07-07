package diy.lingerie.web_tool

import dev.toolkt.dom.pure.PureColor
import dev.toolkt.dom.pure.PureUnit
import dev.toolkt.dom.pure.input.PureInputType
import dev.toolkt.dom.pure.percent
import dev.toolkt.dom.pure.px
import dev.toolkt.dom.pure.style.PureBorderStyle
import dev.toolkt.dom.pure.style.PureBoxSizing
import dev.toolkt.dom.pure.style.PureFlexAlignItems
import dev.toolkt.dom.pure.style.PureFlexDirection
import dev.toolkt.dom.pure.style.PureFlexJustifyContent
import dev.toolkt.dom.pure.style.PureFlexStyle
import dev.toolkt.dom.pure.style.PureStrokeStyle
import dev.toolkt.dom.reactive.style.ReactiveStyle
import dev.toolkt.dom.reactive.utils.createReactiveTextNode
import dev.toolkt.dom.reactive.utils.gestures.MouseGesture
import dev.toolkt.dom.reactive.utils.gestures.onMouseDragGestureStarted
import dev.toolkt.dom.reactive.utils.gestures.onMouseOverGestureStarted
import dev.toolkt.dom.reactive.utils.gestures.track
import dev.toolkt.dom.reactive.utils.html.createReactiveHtmlDivElement
import dev.toolkt.dom.reactive.utils.html.createReactiveHtmlInputElement
import dev.toolkt.dom.reactive.utils.html.getValueCell
import dev.toolkt.dom.reactive.utils.svg.createReactiveSvgCircleElement
import dev.toolkt.dom.reactive.utils.svg.createReactiveSvgPathElement
import dev.toolkt.dom.reactive.utils.svg.createReactiveSvgSvgElement
import dev.toolkt.geometry.Point
import dev.toolkt.geometry.curves.BezierCurve
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.PropertyCell
import dev.toolkt.reactive.cell.separateNonNull
import dev.toolkt.reactive.reactive_list.ReactiveList
import kotlinx.browser.document
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.svg.SVGCircleElement
import svgPathData.SVGPathSegment

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
    trackedMouseOverGesture: Cell<MouseGesture?>,
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
    mouseOverGestureNow: MouseGesture?,
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
    val trackedMouseOverGesture: Cell<MouseGesture?>,
)

private fun createPrimaryViewport(): PrimaryViewport = ReactiveList.looped { childrenLooped ->
    val svgElement = document.createReactiveSvgSvgElement(
        style = ReactiveStyle(
            width = Cell.of(100.percent),
            height = Cell.of(100.percent),
        ),
        children = childrenLooped,
    )

    val savedPosition = PropertyCell<Point?>(
        initialValue = null,
    )

    svgElement.onMouseDragGestureStarted(button = 0).forEach { gesture ->
        savedPosition.bindUntil(
            boundValue = gesture.offsetPosition,
            until = gesture.onFinished,
        )
    }

    val bezierCurve = ReactiveBezierCurve.diff(
        savedPosition.map {
            BezierCurve(
                start = Point.origin,
                firstControl = Point(100.0, 100.0),
                secondControl = Point(200.0, 100.0),
                end = it ?: Point(300.0, 0.0),
            )
        },
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
        ReactiveList.concatAll(
            ReactiveList.of(
                bezierCurve.createReactiveSvgPathElement(
                    style = ReactiveStyle(
                        strokeStyle = PureStrokeStyle(
                            color = PureColor.black,
                            width = 4.px,
                        ),
                    ),
                ),
            ),
            ReactiveList.singleNotNull(
                savedPosition.separateNonNull().map { savedPositionOrNull ->
                    savedPositionOrNull?.let { savedPosition ->
                        createCircleElement(
                            position = savedPosition,
                        )
                    }
                },
            ),
        ),
    )
}

private fun createCircleElement(
    position: Cell<Point>,
): SVGCircleElement = Cell.looped(
    placeholderValue = null,
) { mouseOverGesture: Cell<MouseGesture?> ->
    val circleElement = document.createReactiveSvgCircleElement(
        style = ReactiveStyle(
            fill = mouseOverGesture.map {
                when (it) {
                    null -> PureColor.black
                    else -> PureColor.blue
                }
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
