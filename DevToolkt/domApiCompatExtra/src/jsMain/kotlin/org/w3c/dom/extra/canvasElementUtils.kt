package org.w3c.dom.extra

import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement

fun HTMLCanvasElement.getContext2D(): CanvasRenderingContext2D = getContext("2D") as CanvasRenderingContext2D
