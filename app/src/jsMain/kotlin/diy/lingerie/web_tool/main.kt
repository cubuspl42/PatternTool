package diy.lingerie.web_tool

import diy.lingerie.frp.Cell
import diy.lingerie.frp.DynamicList
import diy.lingerie.frp.EventStream
import diy.lingerie.frp.Listener
import diy.lingerie.frp.applyTo
import kotlinx.browser.document
import org.w3c.dom.Element
import org.w3c.dom.ItemArrayLike
import org.w3c.dom.Node
import org.w3c.dom.Text
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventTarget

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

interface HtmlEventHandler<E : HtmlEvent> {
    enum class Resolution {
        Accept, PreventDefault,
    }

    fun handle(event: E): Resolution
}

fun <E : HtmlEvent> HtmlEventHandler<E>.attach(
    target: EventTarget,
    eventName: String,
    wrapper: HtmlEvent.Wrapper<E>,
) {
    target.addEventListener(
        type = eventName,
        callback = { rawEvent ->
            val wrappedEvent = wrapper.wrap(rawEvent = rawEvent)
            val resolution = handle(wrappedEvent)

            if (resolution == HtmlEventHandler.Resolution.PreventDefault) {
                rawEvent.preventDefault()
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

abstract class DynamicGenericDivElement(
    val children: DynamicList<DynamicHtmlNode>,
    val style: DynamicCssStyle? = null,
    val handleMouseDown: HtmlEventHandler<HtmlMouseEvent>? = null,
) : DynamicHtmlElement() {
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

    val onMouseDown: EventStream<HtmlMouseEvent>
        get() = EventStream.Never

    override val rawElement: Element = document.createElement(
        localName = elementName,
    ).also { element ->
        handleMouseDown?.attach(
            target = element,
            eventName = "mouseDown",
            wrapper = HtmlMouseEvent,
        )

        bindChildren(
            target = element,
            children = children,
        )
    }

    abstract val elementName: String
}

class DynamicWrapperElement(
    override val rawElement: Element,
) : DynamicHtmlElement()

class DynamicDivElement(
    children: DynamicList<DynamicHtmlNode>,
    style: DynamicCssStyle? = null,
    handleMouseDown: HtmlEventHandler<HtmlMouseEvent>? = null,
) : DynamicGenericDivElement(
    children,
    style,
    handleMouseDown,
) {
    override val elementName: String = "div"
}

class DynamicButtonElement(
    children: DynamicList<DynamicHtmlNode>,
    style: DynamicCssStyle? = null,
    handleMouseDown: HtmlEventHandler<HtmlMouseEvent>? = null,
) : DynamicGenericDivElement(
    children,
    style,
    handleMouseDown,
) {
    override val elementName: String = "button"
}

class DynamicHtmlText(
    val data: Cell<String>,
) : DynamicHtmlNode() {
    override val rawNode: Text = document.createTextNode(
        data = data.currentValue,
    ).also { textNode ->
        data.newValues.subscribeFullyBound(
            target = this,
            listener = object : Listener<String> {
                override fun handle(newValue: String) {
                    textNode.data = newValue
                }
            },
        )
    }
}

fun main() {
    println("Hello!")

    val root = DynamicDivElement(
        children = DynamicList.of(
            DynamicHtmlText(
                data = Cell.of("Hello, world"),
            ),
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
