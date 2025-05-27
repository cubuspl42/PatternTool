package diy.lingerie.dynamic_html

import diy.lingerie.reactive.dynamic_list.DynamicList
import diy.lingerie.reactive.event_stream.EventEmitter
import diy.lingerie.html.HtmlEventHandler
import diy.lingerie.html.HtmlMouseEvent
import diy.lingerie.html.attach
import org.w3c.dom.events.EventTarget

class DynamicDivElement(
    override val children: DynamicList<DynamicHtmlNode>,
    private val handleMouseDown: HtmlEventHandler<HtmlMouseEvent> = HtmlEventHandler.Accepting,
) : DynamicGenericHtmlElement() {
    override val elementName: String = "div"

    private val onMouseDownEmitter = EventEmitter<HtmlMouseEvent>()

    val onMouseDown: EventEmitter<HtmlMouseEvent>
        get() = onMouseDownEmitter

    override fun attachEventHandlers(
        target: EventTarget,
    ) {
        handleMouseDown.attach(
            target = target,
            eventName = "mouseDown",
            wrapper = HtmlMouseEvent.Companion,
            emitter = onMouseDownEmitter,
        )
    }

    init {
        rawElement
    }
}
