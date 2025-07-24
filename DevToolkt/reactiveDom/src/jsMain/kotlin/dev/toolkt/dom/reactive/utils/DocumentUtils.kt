package dev.toolkt.dom.reactive.utils

import dev.toolkt.dom.pure.PureSize
import dev.toolkt.dom.pure.collections.childNodesList
import dev.toolkt.dom.reactive.style.ReactiveStyle
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.getEventStream
import dev.toolkt.reactive.event_stream.fetch
import dev.toolkt.reactive.reactive_list.ReactiveList
import dev.toolkt.reactive.reactive_list.bind
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.Window
import org.w3c.dom.css.ElementCSSInlineStyle
import org.w3c.dom.events.Event

fun Document.createReactiveElement(
    namespace: String? = null,
    /**
     * A name of a styleable element
     */
    name: String,
    style: ReactiveStyle? = null,
    children: ReactiveList<Node>? = null,
): Element {
    val element = when {
        namespace != null -> this.createElementNS(
            namespace = namespace,
            qualifiedName = name,
        )

        else -> this.createElement(
            localName = name,
        )

    }

    @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE") (element as ElementCSSInlineStyle)

    style?.bind(
        styleDeclaration = element.style,
    )

    children?.bind(
        target = element,
        extract = Node::childNodesList,
    )

    return element
}

val Window.currentSize: PureSize
    get() = PureSize(
        width = this.innerWidth.toDouble(),
        height = this.innerHeight.toDouble(),
    )

fun Window.onResize(): EventStream<Event> = this.getEventStream("resize")

fun Window.trackSize(): Cell<PureSize> = onResize().fetch { this.currentSize }
