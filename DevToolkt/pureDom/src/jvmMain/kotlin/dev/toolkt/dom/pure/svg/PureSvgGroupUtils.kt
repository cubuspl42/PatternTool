package dev.toolkt.dom.pure.svg

import dev.toolkt.geometry.transformations.PrimitiveTransformation
import dev.toolkt.geometry.transformations.Transformation
import dev.toolkt.dom.pure.utils.xml.childElements
import org.w3c.dom.svg.SVGGElement
import org.w3c.dom.svg.SVGTransformList

fun SVGGElement.toPureGroup(): PureSvgGroup = PureSvgGroup(
    id = id,
    transformation = Transformation.fromSvgTransformList(
        transformList = transform.baseVal,
    ),
    children = childElements.mapNotNull { it.toSvgGraphicsElements() },
)

fun Transformation.Companion.fromSvgTransformList(
    transformList: SVGTransformList,
): Transformation {
    val consolidatedMatrix = transformList.consolidate()?.matrix ?: return Transformation.Identity

    return PrimitiveTransformation.Universal(
        a = consolidatedMatrix.a.toDouble(),
        b = consolidatedMatrix.b.toDouble(),
        c = consolidatedMatrix.c.toDouble(),
        d = consolidatedMatrix.d.toDouble(),
        tx = consolidatedMatrix.e.toDouble(),
        ty = consolidatedMatrix.f.toDouble(),
    )
}
