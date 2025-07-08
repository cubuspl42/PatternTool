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
import dev.toolkt.dom.pure.style.PurePointerEvents
import dev.toolkt.dom.reactive.style.ReactiveStyle
import dev.toolkt.dom.reactive.utils.createReactiveTextNode
import dev.toolkt.dom.reactive.utils.gestures.GenericMouseGesture
import dev.toolkt.dom.reactive.utils.gestures.onMouseOverGestureStarted
import dev.toolkt.dom.reactive.utils.gestures.track
import dev.toolkt.dom.reactive.utils.html.createReactiveHtmlDivElement
import dev.toolkt.dom.reactive.utils.svg.createReactiveSvgCircleElement
import dev.toolkt.dom.reactive.utils.svg.createReactiveSvgGroupElement
import dev.toolkt.dom.reactive.utils.svg.createReactiveSvgSvgElement
import dev.toolkt.geometry.Point
import dev.toolkt.geometry.SpatialObject
import dev.toolkt.geometry.curves.BezierCurve
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.PropertyCell
import dev.toolkt.reactive.reactive_list.ReactiveList
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

    val userCurveSystem = UserCurveSystem(
        userBezierCurve1 = UserBezierCurve(
            start = PropertyCell(initialValue = Point(1547.0, 893.0)),
            firstControl = PropertyCell(initialValue = Point(964.0, 592.0)),
            secondControl = PropertyCell(initialValue = Point(1044.0, 207.0)),
            end = PropertyCell(initialValue = Point(1808.0, 680.0)),
        ),
        userBezierCurve2 = UserBezierCurve(
            start = PropertyCell(initialValue = Point(1407.0, 904.0)),
            firstControl = PropertyCell(initialValue = Point(2176.0, 201.0)),
            secondControl = PropertyCell(initialValue = Point(1018.0, 402.0)),
            end = PropertyCell(initialValue = Point(1707.0, 855.0)),
        ),
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
            createControlledSvgBezierCurve(
                svgElement = svgElement,
                userBezierCurve = userCurveSystem.userBezierCurve1,
            ),
            createControlledSvgBezierCurve(
                svgElement = svgElement,
                userBezierCurve = userCurveSystem.userBezierCurve2,
            ),
            document.createReactiveSvgGroupElement(
                children = userCurveSystem.intersections.map { intersection ->
                    document.createReactiveSvgCircleElement(
                        style = ReactiveStyle(
                            fill = Cell.of(PureFill.Colored(PureColor.red)),
                            pointerEvents = Cell.of(PurePointerEvents.None),
                        ),
                        position = Cell.of(intersection.point),
                        radius = 4.0,
                    )
                },
            ),
        ),
    )
}
