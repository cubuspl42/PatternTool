package diy.lingerie.web_tool_3d

import dev.toolkt.core.platform.PlatformSystem
import dev.toolkt.dom.pure.PureSize
import dev.toolkt.reactive.cell.Cell
import org.w3c.dom.HTMLCanvasElement
import three.THREE
import three.THREE.Object3D
import three.WebGLRendererParams
import three.requestAnimationFrames

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
    canvas: HTMLCanvasElement,
    size: Cell<PureSize>,
    scene: THREE.Scene,
    camera: THREE.Camera,
    process: () -> Unit,
): THREE.WebGLRenderer {
    val renderer = THREE.WebGLRenderer(
        WebGLRendererParams(
            canvas = canvas,
        ),
    )

    size.bind(
        target = renderer,
    ) { renderer, sizeNow ->
        renderer.setSize(sizeNow.width, sizeNow.height)
    }

    requestAnimationFrames {
        process()

        renderer.render(
            scene = scene,
            camera = camera,
        )
    }

    return renderer
}

fun createReactiveScene(
    mainObject: Object3D,
): THREE.Scene = THREE.Scene().apply {
    add(mainObject)
}
