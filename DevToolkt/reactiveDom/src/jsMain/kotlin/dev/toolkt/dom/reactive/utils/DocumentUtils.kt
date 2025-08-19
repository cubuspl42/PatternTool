package dev.toolkt.dom.reactive.utils

import dev.toolkt.dom.pure.collections.childNodesList
import dev.toolkt.dom.reactive.components.Component
import dev.toolkt.dom.reactive.style.ReactiveStyle
import dev.toolkt.reactive.managed_io.Effect
import dev.toolkt.reactive.managed_io.MomentContext
import dev.toolkt.reactive.managed_io.Trigger
import dev.toolkt.reactive.managed_io.joinOf
import dev.toolkt.reactive.managed_io.map
import dev.toolkt.reactive.managed_io.startBound
import dev.toolkt.reactive.reactive_list.ReactiveList
import dev.toolkt.reactive.reactive_list.actuateOf
import dev.toolkt.reactive.reactive_list.bind
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.css.ElementCSSInlineStyle

/**
 * Creates a reactive element of type [ElementT] in the [Document].
 *
 * Binds to [ElementCSSInlineStyle.style] and [Node] child list.
 */
context(momentContext: MomentContext) fun <ElementT : Element> Document.createReactiveElement(
    createElement: Document.() -> ElementT,
    style: ReactiveStyle? = null,
    children: ReactiveList<Node>? = null,
): ElementT = createElement().apply {
    @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE") (this as ElementCSSInlineStyle)

    style?.bind(
        styleDeclaration = this.style,
    )?.startBound(target = this)

    children?.bind(
        mutableList = this.childNodesList,
    )?.startBound(target = this)
}

fun <ElementT : Element> Document.createReactiveComponent(
    createElement: Document.() -> ElementT,
    style: ReactiveStyle? = null,
    children: ReactiveList<Component<Node>>? = null,
): Component<ElementT> = object : Component<ElementT> {
    override fun buildLeaf(): Effect<ElementT> = Effect.prepared {
        val element = createElement()

        @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE") (element as ElementCSSInlineStyle)

        Effect.pureTriggering(
            result = element,
            triggers = listOfNotNull(
                style?.bind(
                    styleDeclaration = element.style,
                ),
                children?.actuateOf {
                    it.buildLeaf()
                }?.joinOf { actuatedChildren ->
                    actuatedChildren.bind(
                        mutableList = element.childNodesList,
                    )
                },
            ),
        )
    }
}
