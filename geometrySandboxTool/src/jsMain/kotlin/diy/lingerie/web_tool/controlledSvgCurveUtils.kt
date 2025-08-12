package diy.lingerie.web_tool

import dev.toolkt.dom.pure.PureColor
import dev.toolkt.dom.pure.px
import dev.toolkt.dom.pure.style.PureFill
import dev.toolkt.dom.pure.style.PurePointerEvents
import dev.toolkt.dom.pure.style.PureStrokeStyle
import dev.toolkt.dom.reactive.style.ReactiveStyle
import dev.toolkt.dom.reactive.utils.gestures.GenericMouseGesture
import dev.toolkt.dom.reactive.utils.gestures.onMouseOverGestureStarted
import dev.toolkt.dom.reactive.utils.gestures.track
import dev.toolkt.dom.reactive.utils.svg.createReactiveSvgCircleElement
import dev.toolkt.dom.reactive.utils.svg.createReactiveSvgLineElement
import dev.toolkt.geometry.Point
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.PropertyCell
import kotlinx.browser.document
import org.w3c.dom.svg.SVGCircleElement
import org.w3c.dom.svg.SVGElement
import org.w3c.dom.svg.SVGLineElement
import org.w3c.dom.svg.SVGSVGElement

fun createControlledSvgCurve(
    svgElement: SVGSVGElement,
    userCurve: UserCurve<*>,
    color: PureColor,
): SVGElement = when (userCurve) {
    is UserBezierCurve -> createControlledSvgBezierCurve(
        svgElement = svgElement,
        userBezierCurve = userCurve,
        color = color,
    )

    is UserLineSegment -> createControlledSvgLineCurve(
        svgElement = svgElement,
        userLineSegment = userCurve,
        color = color,
    )
}

internal fun createControlLineElement(
    start: Cell<Point>,
    end: Cell<Point>,
): SVGLineElement = document.createReactiveSvgLineElement(
    style = ReactiveStyle(
        strokeStyle = PureStrokeStyle(
            color = PureColor.darkGray,
            width = 1.px,
        ),
    ),
    start = start,
    end = end,
)

internal fun createCircleHandleElement(
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
                pointerEvents = Cell.of(PurePointerEvents.All),
            ),
            position = position,
            radius = 4.0,
        )

        Pair(
            circleElement,
            circleElement.onMouseOverGestureStarted().track(),
        )
    }
}
