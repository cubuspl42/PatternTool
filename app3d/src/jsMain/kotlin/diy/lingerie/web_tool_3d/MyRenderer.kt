package diy.lingerie.web_tool_3d

import dev.toolkt.dom.pure.PureSize
import dev.toolkt.geometry.Point
import dev.toolkt.reactive.cell.Cell
import org.w3c.dom.HTMLCanvasElement
import three.THREE

class MyRenderer(
    val myScene: MyScene,
    val viewportSize: Cell<PureSize>,
    val renderer: THREE.WebGLRenderer,
) {
    companion object {
        fun create(
            myScene: MyScene,
            canvas: HTMLCanvasElement,
            viewportSize: Cell<PureSize>,
        ): MyRenderer {
            val renderer = createReactiveRenderer(
                canvas = canvas,
                camera = myScene.camera,
                viewportSize = viewportSize,
            ) { time ->
                myScene.scene
            }

            return MyRenderer(
                myScene = myScene,
                renderer = renderer,
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
                camera = myScene.camera,
            )
        }.intersectObjects(
            objects = objects.toTypedArray(),
        ).firstOrNull()
    }
}
