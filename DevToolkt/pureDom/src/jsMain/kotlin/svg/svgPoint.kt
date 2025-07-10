/**
 * External declarations for [SVG PathData](https://developer.mozilla.org/en-US/docs/Web/API/SVGPathElement/getPathData),
 * which needs a polyfill on some browsers.
 */
package svg

import org.w3c.dom.svg.SVGSVGElement

fun SVGSVGElement.createLegacySVGPoint(): SVGPoint = createSVGPoint() as SVGPoint

fun SVGSVGElement.createLegacySVGPoint(
    x: Double,
    y: Double,
): SVGPoint = createLegacySVGPoint().apply {
    this.x = x
    this.y = y
}

external interface SVGPointList11 {
    val length: Int
    val numberOfItems: Int
    fun clear()
    fun initialize(newItem: SVGPoint): SVGPoint
    fun insertItemBefore(newItem: SVGPoint, index: Int): SVGPoint
    fun replaceItem(newItem: SVGPoint, index: Int): SVGPoint
    fun removeItem(index: Int): SVGPoint
    fun appendItem(newItem: SVGPoint): SVGPoint
    fun getItem(index: Int): SVGPoint
}

operator fun SVGPointList11.get(index: Int): SVGPoint? = asDynamic()[index]

operator fun SVGPointList11.set(index: Int, newItem: SVGPoint) {
    asDynamic()[index] = newItem
}

open external class SVGPoint {
    constructor()

    var x: Double
    var y: Double
}
