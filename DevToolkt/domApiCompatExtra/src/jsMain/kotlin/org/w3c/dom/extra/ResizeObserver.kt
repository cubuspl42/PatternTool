package org.w3c.dom.extra

import org.w3c.dom.DOMRectReadOnly
import org.w3c.dom.Element

external interface ResizeObserverEntry {
    val target: Element
    val contentRect: DOMRectReadOnly
}

typealias ResizeObserverCallback = (
    entries: Array<ResizeObserverEntry>,
    observer: ResizeObserver,
) -> Unit

external class ResizeObserver {
    constructor(
        callback: ResizeObserverCallback,
    )

    fun observe(target: Element)

    fun unobserve(target: Element)

    fun disconnect()
}
