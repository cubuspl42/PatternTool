package diy.lingerie.web_tool

import diy.lingerie.frp.Cell
import diy.lingerie.frp.DynamicList
import diy.lingerie.frp.EventStream
import diy.lingerie.frp.Listener
import diy.lingerie.geometry.Point
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

    data class MouseEvent(
        val position: Point,
    ) : HtmlEvent() {
        companion object : Wrapper<MouseEvent> {
            override fun wrap(rawEvent: Event): MouseEvent {
                TODO("Not yet implemented")
            }
        }
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


interface DomList<out E> {
    val size: Int

    fun isEmpty(): Boolean {
        return size == 0
    }

    fun isNotEmpty(): Boolean {
        return size > 0
    }

    fun getOrNull(index: Int): E?

    val firstElement: E?
        get() = getOrNull(0)
}

interface DomSet<E> {
    fun contains(element: E): Boolean
}

interface OrderedDomSet<E> : DomList<E>, DomSet<E>

class ItemArrayLikeDomList<out E>(
    private val itemArrayLike: ItemArrayLike<E>
) : DomList<E> {
    override val size: Int
        get() = itemArrayLike.length

    override fun getOrNull(
        index: Int,
    ): E? = itemArrayLike.item(index)
}

interface MutableDomList<E> : DomList<E> {
    fun set(index: Int, value: E)

    fun removeAt(index: Int)

    fun append(element: E)

    fun clear() {
        while (!isEmpty()) {
            removeAt(0)
        }
    }
}

fun <E> MutableDomList<E>.bind(
    target: Any,
    source: DynamicList<E>,
) {
    clear()

    source.currentElements.forEach { currentElement ->
        append(currentElement)
    }

    source.onChange.subscribeBound(
        listener = object : Listener<DynamicList.Change> {
            override fun handle(event: DynamicList.Change) {


                TODO("Not yet implemented")
            }
        },
        target = target,
    )
}

interface MutableDomSet<E> : DomList<E> {
    fun remove(value: E)

    fun replace(oldValue: E, newValue: E)
}

interface MutableOrderedDomSet<E> : MutableDomList<E>, MutableDomSet<E> {
    override fun clear() {
        // Similar to Kotlin's extension `Node.clear()`
        while (isNotEmpty()) {
            remove(firstElement!!)
        }
    }
}

abstract class MutableOrderedDomSetBase<E>(
    itemArrayLike: ItemArrayLike<E>,
) : MutableOrderedDomSet<E>, DomList<E> by ItemArrayLikeDomList(itemArrayLike = itemArrayLike) {
    override fun set(index: Int, value: E) {
        val oldValue = getOrNull(index = index) ?: throw IllegalStateException()
        replace(oldValue, value)
    }

    final override fun removeAt(index: Int) {
        val element = getOrNull(index = index) ?: throw IllegalStateException()
        remove(element)
    }
}


class ChildNodesDomList(
    private val node: Node,
) : MutableOrderedDomSetBase<Node>(
    itemArrayLike = node.childNodes,
) {
    override val firstElement: Node?
        get() = node.firstChild

    override fun isNotEmpty(): Boolean = node.hasChildNodes()

    override fun isEmpty(): Boolean = !isNotEmpty()

    override fun remove(value: Node) {
        node.removeChild(value)
    }

    override fun replace(oldValue: Node, newValue: Node) {
        node.replaceChild(oldValue, newValue)
    }

    override fun append(element: Node) {
        node.appendChild(element)
    }
}

abstract class DynamicGenericDivElement(
    val children: DynamicList<DynamicHtmlNode>,
    val style: DynamicCssStyle? = null,
    val handleMouseDown: HtmlEventHandler<HtmlEvent.MouseEvent>? = null,
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

    val onMouseDown: EventStream<HtmlEvent.MouseEvent>
        get() = TODO()

    override val rawElement: Element = document.createElement(
        localName = elementName,
    ).also { element ->
        handleMouseDown?.attach(
            target = element,
            eventName = "mouseDown",
            wrapper = HtmlEvent.MouseEvent,
        )

        bindChildren(
            target = element,
            children = children,
        )
    }

    abstract val elementName: String
}

class DynamicDivElement(
    children: DynamicList<DynamicHtmlNode>,
    style: DynamicCssStyle? = null,
    handleMouseDown: HtmlEventHandler<HtmlEvent.MouseEvent>? = null,
) : DynamicGenericDivElement(
    children,
    style,
    handleMouseDown,
) {
    override val elementName: String = "div"
}

class DynamicHtmlText(
    val data: Cell<String>,
) : DynamicHtmlNode() {
    override val rawNode: Text = document.createTextNode(
        data = data.currentValue,
    ).also { textNode ->
        TODO()
    }
}

fun main() {
    val root = DynamicDivElement(
        children = DynamicList.of(
            DynamicHtmlText(
                data = Cell.of("Hello, world"),
            ),
        ),
    )

    println("Hello, world")

    document.body!!.appendChild(
        document.createElement("h1").apply {
            textContent = "Hello, world!"
        },
    )
}
