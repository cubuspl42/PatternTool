package dev.toolkt.dom.reactive.utils.html

import dev.toolkt.dom.reactive.style.ReactiveStyle
import dev.toolkt.dom.reactive.utils.createReactiveElement
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.cast
import dev.toolkt.reactive.event_stream.getEventStream
import dev.toolkt.reactive.reactive_list.ReactiveList
import org.w3c.dom.Document
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLSpanElement
import org.w3c.dom.Node
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.extra.createButtonElement
import org.w3c.dom.extra.createCanvasElement
import org.w3c.dom.extra.createDivElement
import org.w3c.dom.extra.createInputElement
import org.w3c.dom.extra.createSpanElement

fun Document.createReactiveHtmlButtonElement(
    style: ReactiveStyle? = null,
    children: ReactiveList<Node>? = null,
): HTMLButtonElement = createReactiveElement(
    createElement = Document::createButtonElement,
    style = style,
    children = children,
)

fun Document.createReactiveHtmlDivElement(
    style: ReactiveStyle? = null,
    children: ReactiveList<Node>? = null,
): HTMLDivElement = createReactiveElement(
    createElement = Document::createDivElement,
    style = style,
    children = children,
)

fun Document.createReactiveHtmlSpanElement(
    style: ReactiveStyle? = null,
    children: ReactiveList<Node>? = null,
): HTMLSpanElement = createReactiveElement(
    createElement = Document::createSpanElement,
    style = style,
    children = children,
)

fun Document.createReactiveHtmlInputElement(
    style: ReactiveStyle? = null,
    children: ReactiveList<Node>? = null,
): HTMLInputElement = createReactiveElement(
    createElement = Document::createInputElement,
    style = style,
    children = children,
)

fun Document.createReactiveHtmlCanvasElement(
    style: ReactiveStyle? = null,
): HTMLCanvasElement = createReactiveElement(
    createElement = Document::createCanvasElement,
    style = style,
)

fun HTMLElement.getClickEventStream(): EventStream<MouseEvent> = this.getEventStream(
    type = "click"
).cast()
