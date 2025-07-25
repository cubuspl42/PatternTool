package dev.toolkt.dom.reactive.utils

import dev.toolkt.dom.pure.PureSize
import dev.toolkt.dom.pure.size
import dev.toolkt.dom.pure.style.PureFlexStyle
import dev.toolkt.dom.reactive.style.ReactiveStyle
import dev.toolkt.dom.reactive.utils.html.createReactiveHtmlDivElement
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.mapNotNull
import dev.toolkt.reactive.future.resultOrNull
import dev.toolkt.reactive.reactive_list.ReactiveList
import kotlinx.browser.document
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement

fun createResponsiveElement(
    style: ReactiveStyle = ReactiveStyle(
        displayStyle = Cell.of(
            PureFlexStyle(
                grow = 1.0,
            ),
        ),
    ),
    buildInner: (size: Cell<PureSize>) -> Element,
): HTMLElement = ReactiveList.looped { childrenLooped ->
    val divElement = document.createReactiveHtmlDivElement(
        style = style,
        children = childrenLooped,
    )

    val children = ReactiveList.singleNotNull(
        divElement.getNewestContentRect().resultOrNull.mapNotNull { rect ->
            buildInner(
                rect.map { it.size },
            )
        },
    )

    return@looped Pair(
        divElement,
        children,
    )
}
