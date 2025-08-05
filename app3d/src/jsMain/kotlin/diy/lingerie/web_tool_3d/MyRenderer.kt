package diy.lingerie.web_tool_3d

import dev.toolkt.dom.pure.PureSize
import dev.toolkt.geometry.Point
import dev.toolkt.geometry.Ray3
import dev.toolkt.geometry.negateY
import dev.toolkt.math.algebra.linear.vectors.Vector2
import dev.toolkt.math.algebra.linear.vectors.minus
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
        ndcCoord: Cell<Point>,
        objects: List<THREE.Object3D>,
    ): Cell<THREE.Intersection?> = Cell.map2(
        cell1 = ndcCoord,
        cell2 = viewportSize,
    ) {
            ndcCoordNow,
            sizeNow,
        ->
        castRayNow(
            ndcCoordNow = ndcCoordNow,
            objects = objects,
        )
    }

    fun castRay(
        ndcCoord: Point,
        objects: List<THREE.Object3D>,
    ): THREE.Intersection? = castRayNow(
        ndcCoordNow = ndcCoord,
        objects = objects,
    )

    private fun castRayNow(
        ndcCoordNow: Point,
        objects: List<THREE.Object3D>,
    ): THREE.Intersection? = THREE.Raycaster().apply {
        setFromCamera(
            pointer = ndcCoordNow.toThreeVector2(),
            camera = camera,
        )
    }.intersectObjects(
        objects = objects.toTypedArray(),
    ).firstOrNull()

    fun castRawRay(
        ndcCoord: Cell<Point>,
    ): Cell<Ray3> = Cell.map2(
        cell1 = ndcCoord,
        cell2 = viewportSize,
    ) {
            ndcPointNow,
            sizeNow,
        ->
        castRawRay(
            ndcCoordNow = ndcPointNow,
        )
    }

    fun castRawRay(
        ndcCoordNow: Point,
    ): Ray3 {
        val ndcRay = Ray3.of(
            origin = ndcCoordNow.toPoint3D(z = 0.0),
            target = ndcCoordNow.toPoint3D(z = 0.5),
        )

        return ndcRay.transformBy(
            transformation = camera.unprojectionTransformation,
        )
    }

    val camera: THREE.PerspectiveCamera
        get() = myScene.camera
}



