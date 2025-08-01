package diy.lingerie.web_tool

import dev.toolkt.dom.pure.PureColor
import dev.toolkt.dom.pure.px
import dev.toolkt.dom.pure.style.PureBorderStyle
import dev.toolkt.dom.pure.style.PureBoxSizing
import dev.toolkt.dom.pure.style.PureFill
import dev.toolkt.dom.pure.style.PureFlexStyle
import dev.toolkt.dom.pure.style.PurePointerEvents
import dev.toolkt.dom.reactive.style.ReactiveStyle
import dev.toolkt.dom.reactive.utils.gestures.GenericMouseGesture
import dev.toolkt.dom.reactive.utils.gestures.onMouseOverGestureStarted
import dev.toolkt.dom.reactive.utils.gestures.track
import dev.toolkt.dom.reactive.utils.html.createReactiveHtmlDivElement
import dev.toolkt.dom.reactive.utils.svg.createReactiveSvgCircleElement
import dev.toolkt.dom.reactive.utils.svg.createReactiveSvgGroupElement
import dev.toolkt.dom.reactive.utils.svg.createReactiveSvgSvgElement
import dev.toolkt.geometry.transformations.Transformation
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.reactive_list.ReactiveList
import kotlinx.browser.document
import org.w3c.dom.HTMLDivElement

data class PrimaryViewport(
    val element: HTMLDivElement,
    val trackedMouseOverGesture: Cell<GenericMouseGesture?>,
)

internal fun createPrimaryViewport(
    userCurveSystem: UserCurveSystem,
): PrimaryViewport = ReactiveList.looped { childrenLooped ->
    val svgElement = document.createReactiveSvgSvgElement(
        style = ReactiveStyle(
            displayStyle = Cell.of(
                PureFlexStyle(
                    grow = 1.0,
                ),
            ),
        ),
        children = childrenLooped,
    )

    val children = ReactiveList.of(
        createControlledSvgBezierCurve(
            svgElement = svgElement,
            userBezierCurve = userCurveSystem.userBezierCurve1,
            color = PureColor.black,
        ),
        createControlledSvgBezierCurve(
            svgElement = svgElement,
            userBezierCurve = userCurveSystem.userBezierCurve2,
            color = PureColor.darkBlue,
        ),
        document.createReactiveSvgGroupElement(
            svgElement = svgElement,
            transformation = Cell.of(Transformation.Identity),
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
    )

    return@looped Pair(
        PrimaryViewport(
            element = document.createReactiveHtmlDivElement(
                style = ReactiveStyle(
                    boxSizing = PureBoxSizing.BorderBox,
                    displayStyle = Cell.of(
                        PureFlexStyle(
                            grow = 1.0,
                        ),
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
        children,
    )
}
