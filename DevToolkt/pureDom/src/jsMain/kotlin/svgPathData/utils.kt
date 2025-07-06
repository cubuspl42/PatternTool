package svgPathData

import org.w3c.dom.svg.SVGPathElement

@Suppress("NOTHING_TO_INLINE")
inline fun SVGPathSegment(
    type: String,
    values: Array<Number> = emptyArray(),
): SVGPathSegment = jsObject().also { it ->
    it["type"] = type
    it["values"] = values
}

@Suppress("NOTHING_TO_INLINE")
inline fun SVGPathDataSettings(
    normalize: Boolean = false,
): SVGPathDataSettings = jsObject().also { it ->
    it["normalize"] = normalize
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
