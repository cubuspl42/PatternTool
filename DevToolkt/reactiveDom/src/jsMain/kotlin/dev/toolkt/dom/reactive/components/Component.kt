package dev.toolkt.dom.reactive.components

import dev.toolkt.reactive.managed_io.Effect
import dev.toolkt.reactive.managed_io.Trigger
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.Node

interface Component<out ElementT : Node> {
    companion object {
        fun <ElementT : Node> of(
            element: ElementT,
        ): Component<ElementT> = object : Component<ElementT> {
            override fun buildLeaf(): Effect<ElementT> {
                return Effect.pure(element)
            }
        }
    }

    fun buildLeaf(): Effect<ElementT>
}

typealias HtmlComponent = Component<HTMLElement>

abstract class IntermediateComponent<ElementT : Element> : Component<ElementT> {
    final override fun buildLeaf(): Effect<ElementT> = build().buildLeaf()

    abstract fun build(): Component<ElementT>
}

fun <ElementT : Element, ResultElementT : Element> Component<ElementT>.repackVia(
    transform: (ElementT) -> Component<ResultElementT>,
): Component<ResultElementT> {
    TODO("Not yet implemented")
}


fun <ElementT : Element, ResultElementT : Element> Component<ElementT>.alsoTriggering(
    buildTrigger: (ElementT) -> Trigger,
): Component<ResultElementT> {
    TODO("Not yet implemented")
}
