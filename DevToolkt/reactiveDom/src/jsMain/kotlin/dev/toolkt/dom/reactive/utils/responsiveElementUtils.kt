package dev.toolkt.dom.reactive.utils

import dev.toolkt.dom.pure.PureSize
import dev.toolkt.dom.pure.size
import dev.toolkt.dom.pure.style.PureFlexItemStyle
import dev.toolkt.dom.pure.style.PureFlexStyle
import dev.toolkt.dom.pure.style.PurePosition
import dev.toolkt.dom.reactive.style.ReactiveStyle
import dev.toolkt.dom.reactive.utils.html.createReactiveHtmlDivElement
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.mapNotNull
import dev.toolkt.reactive.future.resultOrNull
import dev.toolkt.reactive.reactive_list.ReactiveList
import kotlinx.browser.document
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.Node

fun createResponsiveFlexElement(
    position: PurePosition? = null,
    buildChild: (size: Cell<PureSize>) -> Element,
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

fun <ElementT : Element> createResponsiveElement(
    createGrowingWrapper: (wrappedChildren: ReactiveList<Node>) -> ElementT,
    buildChild: (size: Cell<PureSize>) -> Element,
): ElementT = ReactiveList.looped { childrenLooped ->
    val divElement = createGrowingWrapper(childrenLooped)

    val children = ReactiveList.singleNotNull(
        divElement.getNewestContentRect().resultOrNull.mapNotNull { rect ->
            buildChild(
                rect.map { it.size },
            )
        },
    )

    return@looped Pair(
        divElement,
        children,
    )
}
