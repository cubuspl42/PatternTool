package diy.lingerie.web_tool

import dev.toolkt.dom.pure.PureColor
import dev.toolkt.dom.pure.PureUnit
import dev.toolkt.dom.pure.px
import dev.toolkt.dom.pure.style.PureFlexAlignItems
import dev.toolkt.dom.pure.style.PureFlexDirection
import dev.toolkt.dom.pure.style.PureFlexStyle
import dev.toolkt.dom.reactive.style.ReactiveStyle
import dev.toolkt.dom.reactive.utils.createReactiveTextNode
import dev.toolkt.dom.reactive.utils.gestures.GenericMouseGesture
import dev.toolkt.dom.reactive.utils.html.createReactiveHtmlDivElement
import dev.toolkt.geometry.LineSegment
import dev.toolkt.geometry.Point
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
    val bezierCurve = BezierCurve(
        start = Point(492.59773540496826, 197.3452272415161),
        firstControl = Point(393.3277416229248, 180.14210319519043),
        secondControl = Point(287.3950023651123, 260.3726043701172),
        end = Point(671.4185047149658, 490.2051086425781),
    )

    val lineSegment = LineSegment(
        start = Point(401.14355433959827, 374.2024184921395),
        end = Point(601.1435543395982, 374.2024184921395),
    )

    val userCurveSystem = UserCurveSystem(
        userCurve2 = UserBezierCurve(
            start = PropertyCell(initialValue = bezierCurve.start),
            firstControl = PropertyCell(initialValue = bezierCurve.firstControl),
            secondControl = PropertyCell(initialValue = bezierCurve.secondControl),
            end = PropertyCell(initialValue = bezierCurve.end),
        ),
        userCurve1 = UserLineSegment(
            start = PropertyCell(initialValue = lineSegment.start),
            end = PropertyCell(initialValue = lineSegment.end),
        ),
    )

    val primaryViewport = createPrimaryViewport(
        userCurveSystem = userCurveSystem,
    )

    return document.createReactiveHtmlDivElement(
        style = ReactiveStyle(
            displayStyle = Cell.of(
                PureFlexStyle(
                    direction = PureFlexDirection.Row,
                ),
            ),
            width = Cell.of(PureUnit.Vw.full),
            height = Cell.of(PureUnit.Vh.full),
            backgroundColor = Cell.of(PureColor.lightGray),
        ),
        children = ReactiveList.of(
            document.createReactiveHtmlDivElement(
                style = ReactiveStyle(
                    displayStyle = Cell.of(
                        PureFlexStyle(
                            direction = PureFlexDirection.Column,
                            grow = 1.0,
                        ),
                    ),
                    backgroundColor = Cell.of(PureColor.lightGray),
                ),
                children = ReactiveList.of(
                    createTopBar(
                        trackedMouseOverGesture = primaryViewport.trackedMouseOverGesture,
                    ),
                    primaryViewport.element,
                ),
            ),
            document.createReactiveHtmlDivElement(
                style = ReactiveStyle(
                    displayStyle = Cell.of(
                        PureFlexStyle(
                            direction = PureFlexDirection.Column,
                        ),
                    ),
                    width = Cell.of(1024.px),
                ),
                children = ReactiveList.of(
                    listOfNotNull(
                        (userCurveSystem.userCurve1 as? UserBezierCurve)?.let {
                            createCurveInfoView(
                                userBezierCurve = it,
                                intersectionPolynomial = userCurveSystem.intersectionInfo.map { it.intersectionPolynomial1 },
                            )
                        },
                        (userCurveSystem.userCurve2 as? UserBezierCurve)?.let {
                            createCurveInfoView(
                                userBezierCurve = userCurveSystem.userCurve2,
                                intersectionPolynomial = userCurveSystem.intersectionInfo.map { it.intersectionPolynomial2 },
                            )
                        },
                    )
                ),
            ),
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
                        data = mouseOverGestureNow.offsetPosition.map {
                            "[${it.x}, ${it.y}]"
                        },
                    ),
                ),
            ),
        ),
    )
}
