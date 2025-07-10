package dev.toolkt.dom.reactive.utils

import dev.toolkt.dom.pure.PureSize
import dev.toolkt.dom.pure.percent
import dev.toolkt.dom.pure.size
import dev.toolkt.dom.reactive.style.ReactiveStyle
import dev.toolkt.dom.reactive.utils.html.createReactiveHtmlDivElement
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.mapNotNull
import dev.toolkt.reactive.future.resultOrNull
import dev.toolkt.reactive.reactive_list.ReactiveList
import kotlinx.browser.document
import org.w3c.dom.Element

fun createResponsiveElement(
    buildInner: (size: Cell<PureSize>) -> Element,
): Element = ReactiveList.Companion.looped { childrenLooped ->
    val divElement = document.createReactiveHtmlDivElement(
        style = ReactiveStyle(
            width = Cell.Companion.of(100.percent),
            height = Cell.Companion.of(100.percent),
        ),
        children = childrenLooped,
    )

    val children = ReactiveList.Companion.singleNotNull(
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
