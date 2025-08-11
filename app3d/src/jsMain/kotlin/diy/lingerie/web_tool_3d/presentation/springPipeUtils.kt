package diy.lingerie.web_tool_3d.presentation

import dev.toolkt.dom.pure.PureColor
import dev.toolkt.geometry.Point3D
import dev.toolkt.geometry.transformations.PrimitiveTransformation3D
import dev.toolkt.reactive.cell.Cell
import three.MeshLambertMaterialParams
import three.THREE

private const val springPipeRadius = 0.75

private val springPipeGeometry = THREE.CylinderGeometry(
    radialSegments = 8,
)

private val springPipeMaterial = THREE.MeshLambertMaterial(
    params = MeshLambertMaterialParams(
        color = PureColor.blue.value,
    ),
)

fun createSpringPipe(
    start: Cell<Point3D>,
    end: Cell<Point3D>,
) = createReactiveMesh(
    geometry = springPipeGeometry, material = Cell.of(springPipeMaterial),
    scale = Cell.map2(
        start, end,
    ) { startNow, endNow ->
        val length = Point3D.distanceBetween(startNow, endNow).value

        PrimitiveTransformation3D.Scaling(
            sx = springPipeRadius,
            sy = length,
            sz = springPipeRadius,
        )
    },
    position = Cell.map2(
        start, end,
    ) { startNow, endNow ->
        Point3D.midPoint(startNow, endNow)
    },
)
