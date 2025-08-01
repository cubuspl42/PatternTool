package dev.toolkt.dom.reactive.utils.html

import dev.toolkt.dom.reactive.style.ReactiveStyle
import dev.toolkt.dom.reactive.utils.createReactiveElement
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.cast
import dev.toolkt.reactive.event_stream.getEventStream
import dev.toolkt.reactive.reactive_list.ReactiveList
import org.w3c.dom.Document
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLSpanElement
import org.w3c.dom.Node
import org.w3c.dom.events.MouseEvent

fun Document.createReactiveHtmlButtonElement(
    style: ReactiveStyle? = null,
    children: ReactiveList<Node>? = null,
): HTMLButtonElement = createReactiveElement(
    name = "button",
    style = style,
    children = children,
) as HTMLButtonElement

fun Document.createReactiveHtmlDivElement(
    style: ReactiveStyle? = null,
    children: ReactiveList<Node>? = null,
): HTMLDivElement = createReactiveElement(
    name = "div",
    style = style,
    children = children,
) as HTMLDivElement

fun Document.createReactiveHtmlSpanElement(
    style: ReactiveStyle? = null,
    children: ReactiveList<Node>? = null,
): HTMLSpanElement = createReactiveElement(
    name = "span",
    style = style,
    children = children,
) as HTMLSpanElement

fun HTMLElement.getClickEventStream(): EventStream<MouseEvent> = this.getEventStream(
    type = "click"
).cast()
