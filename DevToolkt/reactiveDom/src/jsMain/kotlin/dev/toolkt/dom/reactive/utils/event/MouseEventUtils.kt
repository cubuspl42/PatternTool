package dev.toolkt.dom.reactive.utils.event

import dev.toolkt.dom.pure.PureSize
import dev.toolkt.dom.pure.toPureSize
import dev.toolkt.geometry.Point
import dev.toolkt.geometry.negateY
import dev.toolkt.math.algebra.linear.vectors.minus
import org.w3c.dom.Element
import org.w3c.dom.events.MouseEvent

val MouseEvent.clientPoint: Point
    get() = Point(
        x = clientX.toDouble(),
        y = clientY.toDouble(),
    )

val MouseEvent.offsetPoint: Point
    get() = Point(
        x = offsetX,
        y = offsetY,
    )

val MouseEvent.offsetPointNdc: Point
    get() {
        val targetElement = target as? Element ?: return Point.origin

        val size = targetElement.getBoundingClientRect().toPureSize()

        return offsetPoint.toNdc(size = size)
    }

private fun Point.toNdc(
    size: PureSize,
): Point = Point(
    (pointVector / size.sizeVector * 2.0 - 1.0).negateY()
)
