package diy.lingerie.geometry_sandbox_tool

import dev.toolkt.dom.pure.PureColor
import dev.toolkt.dom.pure.PureUnit
import dev.toolkt.dom.pure.px
import dev.toolkt.dom.pure.style.PureFlexAlignItems
import dev.toolkt.dom.pure.style.PureFlexDirection
import dev.toolkt.dom.pure.style.PureFlexItemStyle
import dev.toolkt.dom.pure.style.PureFlexStyle
import dev.toolkt.dom.reactive.components.Component
import dev.toolkt.dom.reactive.style.ReactiveStyle
import dev.toolkt.dom.reactive.utils.createReactiveTextNode
import dev.toolkt.dom.reactive.utils.gestures.GenericMouseGesture
import dev.toolkt.dom.reactive.utils.html.createReactiveHtmlDivComponent
import dev.toolkt.dom.reactive.utils.html.createReactiveHtmlDivElement
import dev.toolkt.geometry.LineSegment
import dev.toolkt.geometry.Point
import dev.toolkt.geometry.Vector2
import dev.toolkt.geometry.curves.BezierCurve
import dev.toolkt.geometry.math.parametric_curve_functions.bezier_binomials.CubicBezierBinomial
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.PropertyCell
import dev.toolkt.reactive.effect.Actions
import dev.toolkt.reactive.effect.MomentContext
import dev.toolkt.reactive.reactive_list.ReactiveList
import kotlinx.browser.document
import org.w3c.dom.HTMLDivElement

fun main() {
    val rootElement = Actions.external {
        createRootElement().buildLeaf().start()
    }.result

    document.body!!.apply {
        appendChild(rootElement)
    }
}

context(momentContext: MomentContext) private fun createRootElement(): Component<HTMLDivElement> {
    val lineSegment = LineSegment(
        start = Point(401.14355433959827, 374.2024184921395),
        end = Point(601.1435543395982, 374.2024184921395),
    )

    // Part of a loop
    val bezierCurve = BezierCurve(
        basisFunction = CubicBezierBinomial(
            point0 = Vector2(492.59773540496826, 197.3452272415161),
            point1 = Vector2(393.3277416229248, 180.14210319519043),
            point2 = Vector2(287.3950023651123, 260.3726043701172),
            point3 = Vector2(577.0, 439.0),
        ),
    )

    val userCurveSystem = UserCurveSystem(
        userCurve2 = UserBezierCurve.create(
            initialStart = bezierCurve.start,
            initialFirstControl = bezierCurve.firstControl,
            initialSecondControl = bezierCurve.secondControl,
            initialEnd = bezierCurve.end,
        ),
        userCurve1 = UserLineSegment.create(
            initialStart = lineSegment.start,
            initialEnd = lineSegment.end,
        ),
    )

    val primaryViewport = createPrimaryViewport(
        userCurveSystem = userCurveSystem,
    )

    return document.createReactiveHtmlDivComponent(
        style = ReactiveStyle(
            flexItemStyle = PureFlexItemStyle(
                grow = 1.0,
            ),
            displayStyle = Cell.of(
                PureFlexStyle(
                    direction = PureFlexDirection.Row,
                    alignItems = PureFlexAlignItems.Stretch,
                ),
            ),
            backgroundColor = Cell.of(PureColor.lightGray),
        ),
        children = ReactiveList.of(
            Component.of(
                document.createReactiveHtmlDivElement(
                    style = ReactiveStyle(
                        flexItemStyle = PureFlexItemStyle(
                            grow = 1.0,
                        ),
                        displayStyle = Cell.of(
                            PureFlexStyle(
                                direction = PureFlexDirection.Column,
                            ),
                        ),
                        backgroundColor = Cell.of(PureColor.lightGray),
                    ),
                    children = ReactiveList.of(

                        primaryViewport.element,
                    ),
                ),
            ),
            document.createReactiveHtmlDivComponent(
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
                        createCurveInfoView(
                            userCurve = userCurveSystem.userCurve1,
                            intersectionPolynomial = userCurveSystem.intersectionInfo.map { it.intersectionPolynomial1 },
                        ),
                        createCurveInfoView(
                            userCurve = userCurveSystem.userCurve2,
                            intersectionPolynomial = userCurveSystem.intersectionInfo.map { it.intersectionPolynomial2 },
                        ),
                    ),
                ),
            ),
        ),
    )
}

context(momentContext: MomentContext) private fun createTopBar(
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


context(momentContext: MomentContext) private fun createMouseOverGesturePreview(
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
