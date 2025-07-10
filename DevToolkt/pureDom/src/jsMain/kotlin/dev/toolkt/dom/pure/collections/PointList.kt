package dev.toolkt.dom.pure.collections

import org.w3c.dom.svg.SVGPolylineElement
import svg.SVGPoint
import svg.SVGPointList11
import svg.set

class SvgPointList(
    private val pointList: SVGPointList11,
) : AbstractMutableList<SVGPoint>() {
    override val size: Int
        get() = pointList.length

    override fun get(
        index: Int,
    ): SVGPoint = when {
        index >= 0 && index < pointList.length -> pointList.getItem(index)
        else -> throw IndexOutOfBoundsException(
            "Index $index is out of bounds for list of size $size",
        )
    }

    override fun add(
        index: Int,
        element: SVGPoint,
    ) {
        pointList.insertItemBefore(element, index)
    }

    override fun removeAt(
        index: Int,
    ): SVGPoint = pointList.removeItem(index)

    override fun set(
        index: Int,
        element: SVGPoint,
    ): SVGPoint {
        val oldPoint = this[index]

        pointList[index] = element

        return oldPoint
    }
}

val SVGPolylineElement.pointList: MutableList<SVGPoint>
    get() = SvgPointList(this.points as SVGPointList11)
