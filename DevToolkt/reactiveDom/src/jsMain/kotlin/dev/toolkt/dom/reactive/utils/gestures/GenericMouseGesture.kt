package dev.toolkt.dom.reactive.utils.gestures

import dev.toolkt.dom.reactive.utils.event.clientPoint
import dev.toolkt.dom.reactive.utils.event.offsetPoint
import dev.toolkt.dom.reactive.utils.event.offsetPointNdc
import dev.toolkt.dom.reactive.utils.getMouseDownEventStream
import dev.toolkt.dom.reactive.utils.getMouseEnterEventStream
import dev.toolkt.dom.reactive.utils.getMouseLeaveEventStream
import dev.toolkt.dom.reactive.utils.getMouseMoveEventStream
import dev.toolkt.dom.reactive.utils.getMouseUpEventStream
import dev.toolkt.geometry.Point
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.switch
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

    val offsetPositionNdc: Cell<Point>
        get() = newestMouseEvent.map { it.offsetPointNdc }
}

class MouseDragGesture(
    private val button: ButtonId,
    private val targetElement: Element,
    val initialMouseEvent: MouseEvent,
) {
    fun trackMouseMovement(): Cell<MouseEvent> = targetElement.getMouseMoveEventStream().hold(
        initialValue = initialMouseEvent,
    )

    val onReleased: EventStream<MouseEvent>
        get() = targetElement.getMouseUpEventStream(button = button)
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

fun Element.trackMouseClientPoint(): Cell<Point?> = trackMouseFeature { it.clientPoint }

fun Element.trackMouseOffsetPoint(): Cell<Point?> = trackMouseFeature { it.offsetPoint }

fun Element.trackMouseOffsetPointNdc(): Cell<Point?> = trackMouseFeature { it.offsetPointNdc }

private fun <FeatureT : Any> Element.trackMouseFeature(
    extractFeature: (MouseEvent) -> FeatureT,
): Cell<FeatureT?> = Future.oscillate(
    initialValue = Cell.of(null),
    switchPhase1 = {
        getMouseEnterEventStream().next().map { mouseEnterEvent ->
            getMouseMoveEventStream().map(extractFeature).hold(extractFeature(mouseEnterEvent))
        }
    },
    switchPhase2 = {
        getMouseLeaveEventStream().next().map { Cell.of(null) }
    },
).switch()

enum class ButtonId(val id: Short) {
    LEFT(0), MIDDLE(1), RIGHT(2),
}

fun Element.onMouseDragGestureStarted(
    button: ButtonId,
): EventStream<MouseDragGesture> = this.getMouseDownEventStream(
    button = button,
).map { mouseDownEvent ->
    MouseDragGesture(
        button = button,
        targetElement = this,
        initialMouseEvent = mouseDownEvent,
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
