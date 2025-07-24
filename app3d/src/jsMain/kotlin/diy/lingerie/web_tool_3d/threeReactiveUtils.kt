package diy.lingerie.web_tool_3d

import dev.toolkt.dom.pure.PureSize
import dev.toolkt.reactive.cell.Cell
import three.THREE

fun createReactivePerspectiveCamera(
    size: Cell<PureSize>,
    fov: Double,
    near: Double,
    far: Double,
): THREE.PerspectiveCamera {
    val camera = THREE.PerspectiveCamera(
        fov,
        size.currentValue.width / size.currentValue.height,
        near,
        far,
    )

    size.newValues.pipe(
        target = camera,
    ) { camera, sizeNow ->
        camera.aspect = sizeNow.width / sizeNow.height
        camera.updateProjectionMatrix()
    }

    return camera
}

fun createReactiveRenderer(
    size: Cell<PureSize>,
): THREE.WebGLRenderer {
    val renderer = THREE.WebGLRenderer()

    size.bind(
        target = renderer,
    ) { renderer, sizeNow ->
        renderer.setSize(sizeNow.width, sizeNow.height)
    }

    return renderer
}
