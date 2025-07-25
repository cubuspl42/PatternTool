package dev.toolkt.dom.reactive.utils.gestures

import dev.toolkt.dom.reactive.utils.event.clientPoint
import dev.toolkt.dom.reactive.utils.event.offsetPoint
import dev.toolkt.dom.reactive.utils.getMouseDownEventStream
import dev.toolkt.dom.reactive.utils.getMouseEnterEventStream
import dev.toolkt.dom.reactive.utils.getMouseLeaveEventStream
import dev.toolkt.dom.reactive.utils.getMouseMoveEventStream
import dev.toolkt.dom.reactive.utils.getMouseUpEventStream
import dev.toolkt.geometry.Point
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.hold
import dev.toolkt.reactive.event_stream.mergeWith
import dev.toolkt.reactive.future.Future
import org.w3c.dom.Element
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.svg.SVGElement

interface MouseGesture {
    val onFinished: Future<Unit>
}

class GenericMouseGesture(
    private val newestMouseEvent: Cell<MouseEvent>,
    override val onFinished: Future<Unit>,
) : MouseGesture {
    val clientPosition: Cell<Point>
        get() = newestMouseEvent.map { it.clientPoint }

    val offsetPosition: Cell<Point>
        get() = newestMouseEvent.map { it.offsetPoint }
}

class SvgMouseGesture(
    val point: Cell<Point>,
    override val onFinished: Future<Unit>,
) : MouseGesture

fun Element.onMouseOverGestureStarted(): EventStream<GenericMouseGesture> =
    this.getMouseEnterEventStream().map { mouseEnterEvent ->
        GenericMouseGesture(
            newestMouseEvent = getMouseMoveEventStream().hold(mouseEnterEvent),
            onFinished = getMouseLeaveEventStream().next().unit(),
        )
    }

enum class ButtonId(val id: Short) {
    LEFT(0), MIDDLE(1), RIGHT(2),
}

fun Element.onMouseDragGestureStarted(
    button: ButtonId,
): EventStream<GenericMouseGesture> = this.getMouseDownEventStream(
    button = button,
).map { mouseDownEvent ->
    val terminatingEventStream = getMouseUpEventStream(button = button).mergeWith(
        getMouseLeaveEventStream(),
    )

    GenericMouseGesture(
        newestMouseEvent = getMouseMoveEventStream().hold(mouseDownEvent),
        onFinished = terminatingEventStream.next().unit(),
    )
}

fun SVGElement.onSvgDragGestureStarted(
    container: SVGElement,
    button: ButtonId,
): EventStream<SvgMouseGesture> = this.getMouseDownEventStream(
    button = button,
).map { targetMouseDownEvent ->
    val initialTargetOffsetPoint = targetMouseDownEvent.offsetPoint

    val terminatingEventStream = container.getMouseUpEventStream(button = button).mergeWith(
        container.getMouseLeaveEventStream(),
    )

    SvgMouseGesture(
        point = container.getMouseMoveEventStream().map {
            it.offsetPoint
        }.hold(initialTargetOffsetPoint),
        onFinished = terminatingEventStream.next().unit(),
    )
}

fun <MouseGestureT : MouseGesture> EventStream<MouseGestureT>.track(): Cell<MouseGestureT?> = Future.oscillate(
    initialValue = null,
    switchPhase1 = { next() },
    switchPhase2 = { gesture -> gesture.onFinished.null_() },
)
