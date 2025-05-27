package diy.lingerie.reactive_html

import diy.lingerie.reactive.reactive_list.ReactiveList
import diy.lingerie.reactive.event_stream.EventEmitter
import diy.lingerie.html.HtmlEventHandler
import diy.lingerie.html.HtmlMouseEvent
import diy.lingerie.html.attach
import org.w3c.dom.events.EventTarget

class ReactiveButtonElement(
    override val children: ReactiveList<ReactiveHtmlNode>,
    private val handleClick: HtmlEventHandler<HtmlMouseEvent> = HtmlEventHandler.Accepting,
) : ReactiveGenericHtmlElement() {
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
