package svg

import org.w3c.dom.svg.SVGPathElement

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

@Suppress("NOTHING_TO_INLINE")
inline fun jsObject(): dynamic = js("({})")

fun SVGPathElement.getPathData(
    settings: SVGPathDataSettings? = null,
): Array<SVGPathSegment> = asDynamic().getPathData(settings)

fun SVGPathElement.setPathData(
    pathData: Array<SVGPathSegment>,
) {
    asDynamic().setPathData(pathData)
}
