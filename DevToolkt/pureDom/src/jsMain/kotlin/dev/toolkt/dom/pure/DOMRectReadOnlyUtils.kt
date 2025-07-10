package dev.toolkt.dom.pure

import org.w3c.dom.DOMRectReadOnly

val DOMRectReadOnly.size: PureSize
    get() = PureSize(
        width = width,
        height = height,
    )
