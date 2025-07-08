package diy.lingerie.web_tool

import dev.toolkt.dom.pure.style.PurePointerEvents
import dev.toolkt.dom.reactive.utils.gestures.onSvgDragGestureStarted
import dev.toolkt.dom.reactive.utils.gestures.track
import dev.toolkt.geometry.Point
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.PropertyCell
import dev.toolkt.reactive.event_stream.filterNotNull
import org.w3c.dom.svg.SVGElement

fun <ElementT : SVGElement> createDraggableSvgElement(
    container: SVGElement,
    position: PropertyCell<Point>,
    create: (position: Cell<Point>) -> ElementT,
): ElementT = Cell.looped(
    placeholderValue = PurePointerEvents.Auto,
) { pointerEventsLooped: Cell<PurePointerEvents> ->
    val draggableElement = create(position)

    val trackedDragGesture = draggableElement.onSvgDragGestureStarted(
        container = container,
        button = 0,
    ).track()

    trackedDragGesture.newValues.filterNotNull().forEach { newDragGesture ->
        position.bindUntil(
            boundValue = newDragGesture.point,
            until = newDragGesture.onFinished,
        )
    }

    val pointerEvents = trackedDragGesture.map { trackedDragGestureNow ->
        when (trackedDragGestureNow) {
            null -> PurePointerEvents.Auto
            else -> PurePointerEvents.None
        }
    }

    Pair(
        draggableElement,
        pointerEvents,
    )
}
