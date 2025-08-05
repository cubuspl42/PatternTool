package dev.toolkt.dom.reactive.utils.svg.transforms

import dev.toolkt.geometry.transformations.EffectiveTransformation
import dev.toolkt.geometry.transformations.PrimitiveTransformation
import dev.toolkt.geometry.transformations.PrimitiveTransformation.Rotation
import dev.toolkt.geometry.transformations.PrimitiveTransformation.Scaling
import dev.toolkt.geometry.transformations.PrimitiveTransformation.Translation
import dev.toolkt.geometry.transformations.PrimitiveTransformation.Universal
import dev.toolkt.geometry.transformations.Transformation
import dev.toolkt.reactive.cell.Cell
import org.w3c.dom.DOMMatrix
import org.w3c.dom.svg.SVGSVGElement
import org.w3c.dom.svg.SVGTransform
import org.w3c.dom.svg.SVGTransformList

fun Translation.toSvgTransform(
    svgElement: SVGSVGElement,
): SVGTransform = svgElement.createSVGTransform().apply {
    setTranslate(
        tx = this@toSvgTransform.tx.toFloat(),
        ty = this@toSvgTransform.ty.toFloat(),
    )
}

fun Scaling.toSvgTransform(
    svgElement: SVGSVGElement,
): SVGTransform = svgElement.createSVGTransform().apply {
    setScale(
        sx = this@toSvgTransform.sx.toFloat(),
        sy = this@toSvgTransform.sy.toFloat(),
    )
}

fun Rotation.toSvgTransform(
    svgElement: SVGSVGElement,
): SVGTransform = svgElement.createSVGTransform().apply {
    setRotate(
        angle = this@toSvgTransform.angle.fiInDegrees.toFloat(),
        cx = 0f,
        cy = 0f,
    )
}

fun Universal.toSvgTransform(
    svgElement: SVGSVGElement,
): SVGTransform = svgElement.createSVGTransform().apply {
    setMatrix(
        DOMMatrix(
            arrayOf(a, b, c, d, tx, ty),
        ),
    )
}

fun PrimitiveTransformation.FlipY.toSvgTransform(
    svgElement: SVGSVGElement,
): SVGTransform = svgElement.createSVGTransform().apply {
    setScale(
        sx = 1f,
        sy = -1f,
    )
}

fun PrimitiveTransformation.toSvgTransform(
    svgElement: SVGSVGElement,
): SVGTransform = when (this) {
    is Rotation -> toSvgTransform(svgElement)
    is Scaling -> toSvgTransform(svgElement)
    is Translation -> toSvgTransform(svgElement)
    is Universal -> toSvgTransform(svgElement)
    PrimitiveTransformation.FlipY -> toSvgTransform(svgElement)
}

fun SVGTransformList.bind(
    svgElement: SVGSVGElement,
    transformation: Cell<Transformation>,
) {
    transformation.bind(
        target = this@bind,
    ) { transformList, transformation ->
        transformList.clear()

        transformation.primitiveTransformations.forEach { primitiveTransformation ->
            transformList.appendItem(
                primitiveTransformation.toSvgTransform(svgElement = svgElement),
            )
        }
    }
}
