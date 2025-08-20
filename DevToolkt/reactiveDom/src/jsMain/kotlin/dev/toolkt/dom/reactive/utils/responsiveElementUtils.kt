package dev.toolkt.dom.reactive.utils

import dev.toolkt.dom.pure.PureSize
import dev.toolkt.dom.pure.size
import dev.toolkt.dom.pure.style.PureFlexItemStyle
import dev.toolkt.dom.pure.style.PureFlexStyle
import dev.toolkt.dom.pure.style.PurePosition
import dev.toolkt.dom.reactive.components.Component
import dev.toolkt.dom.reactive.style.ReactiveStyle
import dev.toolkt.dom.reactive.utils.html.createReactiveHtmlDivComponent
import dev.toolkt.dom.reactive.utils.html.createReactiveHtmlDivElement
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.mapNotNull
import dev.toolkt.reactive.future.Future
import dev.toolkt.reactive.future.actuateOf
import dev.toolkt.reactive.future.resultOrNull
import dev.toolkt.reactive.effect.ActionContext
import dev.toolkt.reactive.effect.Effect
import dev.toolkt.reactive.effect.joinOf
import dev.toolkt.reactive.effect.map
import dev.toolkt.reactive.reactive_list.ReactiveList
import kotlinx.browser.document
import org.w3c.dom.DOMRectReadOnly
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.Node

context(actionContext: ActionContext) fun createResponsiveFlexElement(
    position: PurePosition? = null,
    buildChild: context(ActionContext) (size: Cell<PureSize>) -> Element,
): HTMLElement = createResponsiveElement(
    createGrowingWrapper = { wrappedChildren ->
        document.createReactiveHtmlDivElement(
            style = ReactiveStyle(
                position = position,
                flexItemStyle = PureFlexItemStyle(
                    grow = 1.0,
                ),
                displayStyle = Cell.of(PureFlexStyle()),
            ),
            children = wrappedChildren,
        )
    },
    buildChild = buildChild,
)

fun createResponsiveFlexComponent(
    position: PurePosition? = null,
    buildChild: (size: Cell<PureSize>) -> Component<Element>,
): Component<HTMLDivElement> = createResponsiveComponent(
    createGrowingWrapper = { wrappedChildren ->
        document.createReactiveHtmlDivComponent(
            style = ReactiveStyle(
                position = position,
                flexItemStyle = PureFlexItemStyle(
                    grow = 1.0,
                ),
                displayStyle = Cell.of(PureFlexStyle()),
            ),
            children = wrappedChildren,
        )
    },
    buildChild = buildChild,
)

context(actionContext: ActionContext) fun <ElementT : Element> createResponsiveElement(
    createGrowingWrapper: (wrappedChildren: ReactiveList<Node>) -> ElementT,
    buildChild: context(ActionContext) (size: Cell<PureSize>) -> Element,
): ElementT = ReactiveList.loopedUnmanaged { childrenLooped ->
    val wrapperElement = createGrowingWrapper(childrenLooped)

    val children = ReactiveList.singleNotNull(
        wrapperElement.getNewestContentRect().resultOrNull.mapNotNull { rect ->
            buildChild(
                rect.map { it.size },
            )
        },
    )

    return@loopedUnmanaged Pair(
        wrapperElement,
        children,
    )
}

fun <ElementT : Element> createResponsiveComponent(
    createGrowingWrapper: (wrappedChildren: ReactiveList<Component<Element>>) -> Component<ElementT>,
    buildChild: (size: Cell<PureSize>) -> Component<Element>,
): Component<ElementT> = object : Component<ElementT> {
    override fun buildLeaf(): Effect<ElementT> = ReactiveList.loopedInEffect(
        placeholderReactiveList = ReactiveList.Empty,
    ) { childrenLooped: ReactiveList<Element> ->
        val wrapperComponent = createGrowingWrapper(
            childrenLooped.map {
                Component.of(it)
            },
        )

        wrapperComponent.buildLeaf().joinOf { wrapperElement ->
            wrapperElement.getNewestContentRect().actuateOf { rect: Cell<DOMRectReadOnly> ->
                buildChild(
                    rect.map { it.size },
                ).buildLeaf()
            }.map { elementFuture: Future<Element> ->
                Pair(
                    wrapperElement,
                    ReactiveList.singleNotNull(elementFuture.resultOrNull),
                )
            }
        }
    }
}
