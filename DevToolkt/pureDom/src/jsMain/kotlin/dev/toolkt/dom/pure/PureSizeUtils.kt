package dev.toolkt.dom.pure

import org.w3c.dom.DOMRectReadOnly

fun DOMRectReadOnly.toPureSize(): PureSize = PureSize(
    width = width,
    height = height,
)
