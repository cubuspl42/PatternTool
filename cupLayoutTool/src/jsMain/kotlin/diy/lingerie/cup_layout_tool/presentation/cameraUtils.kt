package diy.lingerie.cup_layout_tool.presentation

import dev.toolkt.dom.pure.PureSize
import dev.toolkt.geometry.Point3D
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.js.threejs.THREE
import kotlin.math.PI

data class MyCamera(
    val camera: THREE.PerspectiveCamera,
    val wrapperGroup: THREE.Group,
)

/**
 * Create a camera focused on a point [height] units above origin, rotating
 * around that point.
 */
fun createMyCamera(
    height: Double,
    distance: Double,
    viewportSize: Cell<PureSize>,
    rotation: Cell<Double>,
): MyCamera {
    val camera = createReactivePerspectiveCamera(
        position = Cell.of(
            Point3D(
                x = 0.0,
                y = -distance,
                z = 0.0,
            ),
        ),
        rotation = Cell.of(
            THREE.Euler(
                x = PI / 2,
            ),
        ),
        size = viewportSize,
        fov = 75.0,
        near = 0.1,
        far = 1000.0,
    )

    val group = createReactiveGroup(
        position = Cell.of(Point3D(x = 0.0, y = 0.0, z = height)),
        rotation = rotation.map { THREE.Euler(z = it) },
        children = listOf(camera),
    )

    return MyCamera(
        camera = camera,
        wrapperGroup = group,
    )
}
