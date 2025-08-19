package dev.toolkt.dom.reactive.utils.html

import dev.toolkt.dom.pure.input.PureInputType
import dev.toolkt.dom.reactive.style.ReactiveStyle
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.cast
import dev.toolkt.reactive.event_stream.getEventStream
import dev.toolkt.reactive.event_stream.holdUnmanaged
import dev.toolkt.reactive.managed_io.MomentContext
import dev.toolkt.reactive.reactive_list.ReactiveList
import org.w3c.dom.Document
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.Node
import org.w3c.dom.events.Event
import org.w3c.dom.events.InputEvent

// This is a type hack
abstract external class HTMLTypedInputElement<T : PureInputType> : HTMLInputElement

typealias HTMLCheckboxElement = HTMLTypedInputElement<PureInputType.Checkbox>

context(momentContext: MomentContext)
fun <T : PureInputType> Document.createReactiveHtmlInputElement(
    type: T,
    style: ReactiveStyle? = null,
    children: ReactiveList<Node>? = null,
): HTMLTypedInputElement<T> {
    val inputElement = createReactiveHtmlInputElement(
        style = style,
        children = children,
    )

    inputElement.type = type.type

    return inputElement.unsafeCast<HTMLTypedInputElement<T>>()
}

fun HTMLInputElement.getChangeEventStream(): EventStream<Event> = this.getEventStream(
    type = "change"
)

fun HTMLInputElement.getInputEventStream(): EventStream<InputEvent> = this.getEventStream(
    type = "input"
).cast()

fun HTMLInputElement.getValueCell(): Cell<String> = this.getInputEventStream().map {
    this.value
}.holdUnmanaged(
    initialValue = this.value,
)

fun HTMLCheckboxElement.getCheckedEventStream() = this.getChangeEventStream().map {
    val target = it.target as HTMLInputElement
    target.checked
}

fun HTMLCheckboxElement.getCheckedCell(): Cell<Boolean> = getCheckedEventStream().holdUnmanaged(initialValue = checked)
