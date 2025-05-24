package diy.lingerie.web_tool

import diy.lingerie.frp.Cell
import diy.lingerie.frp.DynamicList
import diy.lingerie.frp.EventEmitter
import diy.lingerie.frp.EventStream
import diy.lingerie.frp.Listener
import diy.lingerie.frp.applyTo
import diy.lingerie.frp.hold
import kotlinx.browser.document
import org.w3c.dom.Element
import org.w3c.dom.ItemArrayLike
import org.w3c.dom.Node
import org.w3c.dom.Text
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventTarget
import org.w3c.fetch.ResponseInit

abstract class DynamicHtmlNode {
    abstract val rawNode: Node
}


abstract class DynamicHtmlElement : DynamicHtmlNode() {
    final override val rawNode: Node
        get() = rawElement

    abstract val rawElement: Element
}


class DynamicCssStyle(
) {

}

sealed class HtmlEvent {
    interface Wrapper<E : HtmlEvent> {
        fun wrap(rawEvent: Event): E
    }

}

interface HtmlEventHandler<in E : HtmlEvent> {
    object Accepting : HtmlEventHandler<HtmlEvent> {
        override fun handle(event: HtmlEvent): Resolution = Resolution.Accept
    }

    enum class Resolution {
        Accept, PreventDefault,
    }

    fun handle(event: E): Resolution
}

fun <E : HtmlEvent> HtmlEventHandler<E>.attach(
    target: EventTarget,
    eventName: String,
    wrapper: HtmlEvent.Wrapper<E>,
    emitter: EventEmitter<E>,
) {
    target.addEventListener(
        type = eventName,
        callback = { rawEvent ->
            val wrappedEvent = wrapper.wrap(rawEvent = rawEvent)
            val resolution = handle(wrappedEvent)

            if (resolution == HtmlEventHandler.Resolution.PreventDefault) {
                rawEvent.preventDefault()
            } else {
                emitter.emit(wrappedEvent)
            }
        },
    )
}

interface DomList<out E> : BasicList<E> {
    override val size: Int

    override fun isEmpty(): Boolean {
        return !isNotEmpty()
    }

    fun isNotEmpty(): Boolean {
        return size > 0
    }

    override fun get(index: Int): E = getOrNull(index = index) ?: throw IndexOutOfBoundsException(
        "Index $index is out of bounds for size $size",
    )

    fun getOrNull(index: Int): E?

    val firstElement: E?
        get() = getOrNull(0)
}


interface ItemArrayLikeDomList<out E> : DomList<E> {
    override val size: Int
        get() = itemArrayLike.length

    override fun getOrNull(
        index: Int,
    ): E? = itemArrayLike.item(index)

    val itemArrayLike: ItemArrayLike<E>
}

interface MutableDomList<E> : BasicMutableList<E>, DomList<E>

fun <E> MutableDomList<E>.bind(
    target: Any,
    source: DynamicList<E>,
) {
    clear()

    source.currentElements.forEach { currentElement ->
        add(currentElement)
    }

    source.changes.subscribeFullyBound(
        target = target,
        listener = object : Listener<DynamicList.Change<E>> {
            override fun handle(change: DynamicList.Change<E>) {
                change.applyTo(mutableList = this@bind)
            }
        },
    )
}

class ChildNodesDomList(
    private val node: Node,
) : MutableDomList<Node>, ItemArrayLikeDomList<Node> {
    override val firstElement: Node?
        get() = node.firstChild

    override fun isNotEmpty(): Boolean = node.hasChildNodes()

    override fun isEmpty(): Boolean = !node.hasChildNodes()

    override fun remove(value: Node): Boolean = when {
        node.contains(value) -> {
            node.removeChild(value)

            true
        }

        else -> false
    }

    override fun set(
        index: Int,
        element: Node,
    ): Node {
        val oldNode =
            getOrNull(index) ?: throw IndexOutOfBoundsException("Index $index is out of bounds for list of size $size")

        node.replaceChild(
            oldNode,
            element,
        )

        return oldNode
    }

    override fun add(index: Int, element: Node) {
        val nextNode = getOrNull(index)

        if (nextNode == null && index != size) {
            throw IndexOutOfBoundsException("Index $index is out of bounds for list of size $size")
        }

        node.insertBefore(
            element,
            nextNode,
        )
    }

    override fun removeAt(index: Int): Node {
        val oldNode =
            getOrNull(index) ?: throw IndexOutOfBoundsException("Index $index is out of bounds for list of size $size")

        node.removeChild(oldNode)

        return oldNode
    }

    override fun add(element: Node): Boolean {
        node.appendChild(element)

        return true
    }

    override val itemArrayLike: ItemArrayLike<Node>
        get() = node.childNodes
}

abstract class DynamicGenericHtmlElement() : DynamicHtmlElement() {
    companion object {
        private fun bindChildren(
            target: Node,
            children: DynamicList<DynamicHtmlNode>,
        ) {
            val childNodesDomList = ChildNodesDomList(node = target)

            childNodesDomList.bind(
                target = target,
                source = children.map { it.rawNode },
            )
        }
    }


    override val rawElement: Element by lazy {
        document.createElement(
            localName = elementName,
        ).also { element ->
            attachEventHandlers(
                target = element,
            )

            bindChildren(
                target = element,
                children = children,
            )
        }
    }

    abstract val elementName: String

    abstract val children: DynamicList<DynamicHtmlNode>

    protected abstract fun attachEventHandlers(
        target: EventTarget,
    )
}

class DynamicWrapperElement(
    override val rawElement: Element,
) : DynamicHtmlElement()

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
            wrapper = HtmlMouseEvent,
            emitter = onMouseDownEmitter,
        )
    }

    init {
        rawElement
    }
}

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
            wrapper = HtmlMouseEvent,
            emitter = onClickEmitter,
        )
    }

    init {
        rawElement
    }
}

class DynamicHtmlText(
    val data: Cell<String>,
) : DynamicHtmlNode() {
    override val rawNode: Text = document.createTextNode(
        data = data.currentValue,
    ).also { textNode ->
        data.newValues.subscribeFullyBound(
            target = textNode,
            listener = object : Listener<String> {
                override fun handle(newValue: String) {
                    textNode.data = newValue
                }
            },
        )
    }
}

fun main() {
    val button = DynamicButtonElement(
        children = DynamicList.of(
            DynamicHtmlText(
                data = Cell.of("Click me!"),
            ),
        ),
    )

    button.onClick.subscribe(
        listener = object : Listener<HtmlMouseEvent> {
            override fun handle(event: HtmlMouseEvent) {
                println("Button clicked via event handler! ${event.position}")
            }
        },
    )

    val position = button.onClick.map {
        it.position
    }.hold(initialValue = null)

    val root = DynamicDivElement(
        children = DynamicList.of(
            DynamicHtmlText(
                data = position.map { positionNow ->
                    when (positionNow) {
                        null -> "(none)"
                        else -> "$positionNow"
                    }
                },
            ),
            button,
            DynamicWrapperElement(
                document.createElement("h1").apply {
                    textContent = "Hello, world!"
                },
            ),
        ),
    )

    document.body!!.appendChild(
        root.rawNode,
    )
}
