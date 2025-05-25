package diy.lingerie.dynamic_html

import diy.lingerie.frp.dynamic_list.DynamicList
import diy.lingerie.frp.event_stream.EventEmitter
import diy.lingerie.html.HtmlEventHandler
import diy.lingerie.html.HtmlMouseEvent
import diy.lingerie.html.attach
import org.w3c.dom.events.EventTarget

class DynamicButtonElement(
    override val children: DynamicList<DynamicHtmlNode>,
    private val handleClick: HtmlEventHandler<HtmlMouseEvent> = HtmlEventHandler.Accepting,
) : DynamicGenericHtmlElement() {
    override val elementName: String = "button"

    private val onClickEmitter = EventEmitter<HtmlMouseEvent>()

    val onClick: EventEmitter<HtmlMouseEvent>
        get() = onClickEmitter

    override fun attachEventHandlers(target: EventTarget) {
        handleClick.attach(
            target = target,
            eventName = "click",
            wrapper = HtmlMouseEvent.Companion,
            emitter = onClickEmitter,
        )
    }

    init {
        rawElement
    }
}
