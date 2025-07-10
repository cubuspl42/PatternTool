package org.w3c.dom.extra.svg

import org.w3c.dom.extra.jsObject
import org.w3c.dom.svg.SVGPathElement

external interface SVGPathDataSettings {
    var normalize: Boolean
}

external interface SVGPathSegment {
    var type: String /* "A" | "a" | "C" | "c" | "H" | "h" | "L" | "l" | "M" | "m" | "Q" | "q" | "S" | "s" | "T" | "t" | "V" | "v" | "Z" | "z" */
    var values: Array<Number>
}

@Suppress("NOTHING_TO_INLINE")
inline fun SVGPathSegment(
    type: String,
    values: Array<Number> = emptyArray(),
): SVGPathSegment {
    val obj = jsObject()
    obj["type"] = type
    obj["values"] = values
    return obj
}

@Suppress("NOTHING_TO_INLINE")
inline fun SVGPathDataSettings(
    normalize: Boolean = false,
): SVGPathDataSettings {
    val obj = jsObject()
    obj["normalize"] = normalize
    return obj
}

fun SVGPathElement.getPathData(
    settings: SVGPathDataSettings? = null,
): Array<SVGPathSegment> = asDynamic().getPathData(settings)

fun SVGPathElement.setPathData(
    pathData: Array<SVGPathSegment>,
) {
    asDynamic().setPathData(pathData)
}
