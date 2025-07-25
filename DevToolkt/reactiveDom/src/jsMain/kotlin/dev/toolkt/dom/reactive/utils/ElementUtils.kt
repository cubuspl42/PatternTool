package dev.toolkt.dom.reactive.utils

import dev.toolkt.dom.reactive.utils.event.offsetPoint
import dev.toolkt.dom.reactive.utils.gestures.ButtonId
import dev.toolkt.geometry.Point
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.cast
import dev.toolkt.reactive.event_stream.getEventStream
import dev.toolkt.reactive.event_stream.hold
import org.w3c.dom.Element
import org.w3c.dom.events.MouseEvent

fun Element.getMouseEnterEventStream(): EventStream<MouseEvent> = this.getEventStream(
    type = "mouseenter",
).cast()

fun Element.getMouseDownEventStream(): EventStream<MouseEvent> = this.getEventStream(
    type = "mousedown",
).cast()

fun Element.getMouseDownEventStream(
    button: ButtonId,
): EventStream<MouseEvent> = this.getMouseDownEventStream().filter { it.button == button.id }

fun Element.getMouseUpEventStream(): EventStream<MouseEvent> = this.getEventStream(
    type = "mouseup",
).cast()

fun Element.getMouseUpEventStream(
    button: ButtonId,
): EventStream<MouseEvent> = this.getMouseUpEventStream().filter { it.button == button.id }

fun Element.getMouseMoveEventStream(): EventStream<MouseEvent> = this.getEventStream(
    type = "mousemove",
).cast()

fun Element.getMouseLeaveEventStream(): EventStream<MouseEvent> = this.getEventStream(
    type = "mouseleave",
).cast()

fun Element.getMouseOffsetCell(): Cell<Point?> = getMouseMoveEventStream().map { it.offsetPoint }.hold(null)