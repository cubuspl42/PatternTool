package diy.lingerie.web_tool_3d

import dev.toolkt.dom.pure.PureSize
import dev.toolkt.geometry.Point
import dev.toolkt.reactive.cell.Cell
import org.w3c.dom.HTMLCanvasElement
import three.THREE

class MyRenderer(
    val renderer: THREE.WebGLRenderer,
    val camera: THREE.PerspectiveCamera,
    val viewportSize: Cell<PureSize>,
) {
    companion object {
        fun create(
            canvas: HTMLCanvasElement,
            viewportSize: Cell<PureSize>,
            buildScene: () -> Pair<THREE.Scene, THREE.PerspectiveCamera>,
        ): MyRenderer {
            val (scene, camera) = buildScene()

            val renderer = createReactiveRenderer(
                canvas = canvas,
                camera = camera,
                viewportSize = viewportSize,
            ) { time ->
                scene
            }

            return MyRenderer(
                renderer = renderer,
                camera = camera,
                viewportSize = viewportSize,
            )
        }
    }

    fun castRay(
        viewportPoint: Cell<Point>,
        objects: List<THREE.Object3D>,
    ): Cell<THREE.Intersection?> = Cell.map2(
        cell1 = viewportPoint,
        cell2 = viewportSize,
    ) {
            viewportPointNow,
            sizeNow,
        ->
        castRayNow(
            viewportPointNow = viewportPointNow,
            viewportSizeNow = sizeNow,
            objects = objects,
        )
    }

    fun castRay(
        viewportPoint: Point,
        objects: List<THREE.Object3D>,
    ): THREE.Intersection? = castRayNow(
        viewportPointNow = viewportPoint,
        viewportSizeNow = viewportSize.currentValue,
        objects = objects,
    )

    private fun castRayNow(
        viewportPointNow: Point,
        viewportSizeNow: PureSize,
        objects: List<THREE.Object3D>,
    ): THREE.Intersection? {
        val ndcPointNow = viewportPointNow.toNdc(size = viewportSizeNow)

        return THREE.Raycaster().apply {
            setFromCamera(
                pointer = ndcPointNow.toThreeVector2(),
                camera = camera,
            )
        }.intersectObjects(
            objects = objects.toTypedArray(),
        ).firstOrNull()
    }
}
