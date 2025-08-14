package org.w3c.dom.extra

import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement

fun HTMLCanvasElement.getContext2D(): CanvasRenderingContext2D {
    val context = getContext("2d") ?: throw IllegalStateException("Failed to get 2D context from HTMLCanvasElement")
    return context as CanvasRenderingContext2D
}
