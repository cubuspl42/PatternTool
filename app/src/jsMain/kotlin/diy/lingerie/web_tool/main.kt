package diy.lingerie.web_tool

import dev.toolkt.dom.pure.PureColor
import dev.toolkt.dom.pure.PureUnit
import dev.toolkt.dom.pure.px
import dev.toolkt.dom.pure.style.PureFlexAlignItems
import dev.toolkt.dom.pure.style.PureFlexDirection
import dev.toolkt.dom.pure.style.PureFlexStyle
import dev.toolkt.dom.pure.style.PureTableDisplayStyle
import dev.toolkt.dom.reactive.style.PureEdgeInsets
import dev.toolkt.dom.reactive.style.ReactiveStyle
import dev.toolkt.dom.reactive.utils.createReactiveTextNode
import dev.toolkt.dom.reactive.utils.gestures.GenericMouseGesture
import dev.toolkt.dom.reactive.utils.html.createReactiveHtmlDivElement
import dev.toolkt.geometry.Point
import dev.toolkt.geometry.curves.BezierCurve
import dev.toolkt.geometry.curves.OpenCurve
import dev.toolkt.geometry.math.parametric_curve_functions.bezier_binomials.CubicBezierBinomial
import dev.toolkt.math.algebra.linear.vectors.Vector2
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
    val firstBezierCurve = BezierCurve(
        start = Point(233.92449010844575, 500.813035986871),
        firstControl = Point(422.77519184542564, 441.5255275486571),
        secondControl = Point(482.0980368984025, 387.5853838361354),
        end = Point(486.0476425340348, 351.778389940191),
    )

    val secondBezierCurve = BezierCurve(
        start = Point(382.2960291124364, 335.5675928528492),
        firstControl = Point(370.41409366476535, 370.845949740462),
        secondControl = Point(402.03174182196125, 441.30516989916543),
        end = Point(551.3035908506827, 559.7310384198445),
    )

    val userCurveSystem = UserCurveSystem(
        userBezierCurve1 = UserBezierCurve(
            start = PropertyCell(initialValue = firstBezierCurve.start),
            firstControl = PropertyCell(initialValue = firstBezierCurve.firstControl),
            secondControl = PropertyCell(initialValue = firstBezierCurve.secondControl),
            end = PropertyCell(initialValue = firstBezierCurve.end),
        ),
        userBezierCurve2 = UserBezierCurve(
            start = PropertyCell(initialValue = secondBezierCurve.start),
            firstControl = PropertyCell(initialValue = secondBezierCurve.firstControl),
            secondControl = PropertyCell(initialValue = secondBezierCurve.secondControl),
            end = PropertyCell(initialValue = secondBezierCurve.end),
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
                    createCurveInfoView(
                        userBezierCurve = userCurveSystem.userBezierCurve1,
                        intersectionPolynomial = userCurveSystem.intersectionInfo.map { it.intersectionPolynomial1 },
                    ),
                    createCurveInfoView(
                        userBezierCurve = userCurveSystem.userBezierCurve2,
                        intersectionPolynomial = userCurveSystem.intersectionInfo.map { it.intersectionPolynomial2 },
                    ),
                ),
            ),
        ),
    )
}

private fun createSideBar(
    userCurveSystem: UserCurveSystem,
): HTMLDivElement {
    fun createEntryTableRow(
        key: String,
        value: Cell<String>,
    ): HTMLDivElement = document.createReactiveHtmlDivElement(
        style = ReactiveStyle(
            displayStyle = Cell.of(PureTableDisplayStyle.Row),
        ),
        children = ReactiveList.of(
            document.createReactiveHtmlDivElement(
                style = ReactiveStyle(
                    displayStyle = Cell.of(PureTableDisplayStyle.Cell),
                ),
                children = ReactiveList.of(
                    document.createTextNode(key),
                ),
            ),
            document.createReactiveHtmlDivElement(
                style = ReactiveStyle(
                    displayStyle = Cell.of(PureTableDisplayStyle.Cell),
                ),
                children = ReactiveList.of(
                    document.createReactiveTextNode(
                        data = value,
                    ),
                ),
            ),
        ),
    )

    fun createUserBezierCurveRow(
        index: Int,
        userBezierCurve: UserBezierCurve,
    ): HTMLDivElement = createEntryTableRow(
        key = "Curve #$index",
        value = userBezierCurve.bezierCurve.map { it.toReprString() },
    )

    fun createIntersectionRow(
        intersection: OpenCurve.Intersection,
    ): HTMLDivElement = createEntryTableRow(
        key = "Intersection",
        value = Cell.of("${intersection.point.toReprString()} [t_s = ${intersection.subjectCoord.t}, t_o = ${intersection.objectCoord.t}]"),
    )

    return document.createReactiveHtmlDivElement(
        style = ReactiveStyle(
            displayStyle = Cell.of(
                PureTableDisplayStyle(
                    borderSpacing = 8.px,
                ),
            ),
            padding = PureEdgeInsets.all(8.px),
        ),
        children = ReactiveList.concatAll(
            ReactiveList.of(
                createUserBezierCurveRow(
                    index = 1,
                    userBezierCurve = userCurveSystem.userBezierCurve1,
                ),
                createUserBezierCurveRow(
                    index = 2,
                    userBezierCurve = userCurveSystem.userBezierCurve2,
                ),
            ),
            userCurveSystem.intersections.map {
                createIntersectionRow(intersection = it)
            },
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
