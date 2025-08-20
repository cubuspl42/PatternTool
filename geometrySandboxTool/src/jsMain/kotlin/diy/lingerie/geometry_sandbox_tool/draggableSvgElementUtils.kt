package diy.lingerie.geometry_sandbox_tool

import dev.toolkt.dom.reactive.utils.gestures.ButtonId
import dev.toolkt.dom.reactive.utils.gestures.onSvgDragGestureStarted
import dev.toolkt.dom.reactive.utils.gestures.track
import dev.toolkt.geometry.Point
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.PropertyCell
import dev.toolkt.reactive.effect.MomentContext
import dev.toolkt.reactive.event_stream.filterNotNull
import dev.toolkt.reactive.event_stream.forEach
import org.w3c.dom.svg.SVGElement

context(momentContext: MomentContext) fun <SvgElementT : SVGElement> createDraggableSvgElement(
    container: SVGElement,
    position: PropertyCell<Point>,
    create: (position: Cell<Point>) -> SvgElementT,
): SvgElementT {
    val draggableElement = create(position)

    val trackedDragGesture = draggableElement.onSvgDragGestureStarted(
        container = container,
        button = ButtonId.LEFT,
    ).track()

    trackedDragGesture.newValues.filterNotNull().forEach { newDragGesture ->
        position.bindUntil(
            boundValue = newDragGesture.point,
            until = newDragGesture.onFinished,
        )
    }

    return draggableElement
}
