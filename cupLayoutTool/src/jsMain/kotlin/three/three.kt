package three

import dev.toolkt.js.TypedArray
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.extra.jsObject

@JsModule("three")
@JsNonModule
external object THREE {
    class Vector2(
        x: Double = definedExternally,
        y: Double = definedExternally,
    ) {
        var x: Double

        var y: Double

        fun toArray(): Array<Double>
    }

    class Vector3(
        x: Double = definedExternally,
        y: Double = definedExternally,
        z: Double = definedExternally,
    ) {
        var x: Double

        var y: Double

        var z: Double

        fun clone(): Vector3

        fun applyMatrix4(matrix: Matrix4)

        fun project(camera: Camera)

        fun unproject(camera: Camera)

        fun toArray(): Array<Double>
    }

    class Scene {
        fun add(obj: Object3D): Scene
    }

    abstract class Camera : Object3D

    class PerspectiveCamera(
        fov: Double,
        aspect: Double,
        near: Double,
        far: Double,
    ) : Camera {
        var aspect: Double

        fun updateProjectionMatrix()

        fun lookAt(target: Vector3)
    }

    class WebGLRenderer(params: WebGLRendererParams = definedExternally) {
        fun setSize(width: Double, height: Double)

        val domElement: HTMLElement

        fun render(scene: Scene, camera: Camera)

        fun setClearColor(
            color: Int,
            alpha: Double = definedExternally,
        ): Unit
    }

    interface WebGLRendererParams {
        var canvas: HTMLElement?
    }

    abstract class Object3D {
        val position: Vector3

        val rotation: Euler

        val scale: Vector3

        var userData: Any

        val matrixWorld: Matrix4

        fun add(child: Object3D): Object3D

        fun getWorldPosition(target: Vector3)

    }

    class Group : Object3D

    class Euler(
        x: Double = definedExternally,
        y: Double = definedExternally,
        z: Double = definedExternally,
    ) {
        var x: Double

        var y: Double

        var z: Double
    }

    open class BufferGeometry {
        fun setAttribute(name: String, attribute: BufferAttributeBase): BufferGeometry

        fun setIndex(indices: Array<Int>): BufferGeometry

        fun setIndex(indices: BufferAttributeBase): BufferGeometry

        fun computeVertexNormals(): BufferGeometry
    }

    class BoxGeometry(
        width: Double = definedExternally,
        height: Double = definedExternally,
        depth: Double = definedExternally,
        widthSegments: Int = definedExternally,
        heightSegments: Int = definedExternally,
        depthSegments: Int = definedExternally,
    ) : BufferGeometry

    class SphereGeometry(
        radius: Double = definedExternally,
        widthSegments: Int = definedExternally,
        heightSegments: Int = definedExternally,
        phiStart: Double = definedExternally,
        phiLength: Double = definedExternally,
        thetaStart: Double = definedExternally,
        thetaLength: Double = definedExternally,
    ) : BufferGeometry

    class PlaneGeometry(
        width: Double = definedExternally,
        height: Double = definedExternally,
        widthSegments: Int = definedExternally,
        heightSegments: Int = definedExternally,
    ) : BufferGeometry

    class CylinderGeometry(
        radiusTop: Double = definedExternally,
        radiusBottom: Double = definedExternally,
        height: Double = definedExternally,
        radialSegments: Int = definedExternally,
        heightSegments: Int = definedExternally,
        openEnded: Boolean = definedExternally,
        thetaStart: Double = definedExternally,
        thetaLength: Double = definedExternally,
    ) : BufferGeometry

    abstract class Material {
        val wireframe: Boolean
    }

    interface MeshBasicMaterialParams {
        val color: Int
        val wireframe: Boolean?
        val opacity: Double
    }

    class MeshBasicMaterial(
        params: MeshBasicMaterialParams = definedExternally,
    ) : Material

    interface MeshLambertMaterialParams {
        val color: Int
        val wireframe: Boolean?
    }

    class MeshLambertMaterial(
        params: MeshLambertMaterialParams = definedExternally,
    ) : Material

    interface LineBasicMaterialParams {
        val color: Int
        val linewidth: Int?
    }

    class LineBasicMaterial(
        params: LineBasicMaterialParams = definedExternally,
    ) : Material {
        var color: Int
        var linewidth: Int
    }

    class InstancedBufferGeometry : BufferGeometry {
        var instanceCount: Int
    }

    abstract class BufferAttributeBase {
        var needsUpdate: Boolean
    }

    class BufferAttribute(
        array: TypedArray<*>,
        itemSize: Int,
        normalized: Boolean = definedExternally,
    ) : BufferAttributeBase

    class InstancedBufferAttribute(
        array: TypedArray<*>,
        itemSize: Int,
        normalized: Boolean = definedExternally,
    ) : BufferAttributeBase

    interface RawShaderMaterialParams {
        val vertexShader: String
        val fragmentShader: String
    }

    class RawShaderMaterial(
        params: RawShaderMaterialParams = definedExternally,
    ) : Material

    class LineSegments(
        geometry: BufferGeometry,
        material: Material = definedExternally,
    ) : Object3D

    class Mesh(
        geometry: BufferGeometry,
        material: Material = definedExternally,
    ) : Object3D {
        var material: Material
    }

    abstract class Light() : Object3D {
        var color: Int

        var intensity: Double
    }

    class AmbientLight(
        color: Int = definedExternally,
        intensity: Double = definedExternally,
    ) : Light

    class PointLight(
        color: Int = definedExternally,
        intensity: Double = definedExternally,
        distance: Double = definedExternally,
        decay: Double = definedExternally,
    ) : Light {
        var distance: Double

        var decay: Double
    }

    interface Intersection {
        val distance: Double

        val point: Vector3

        @Suppress("PropertyName")
        @JsName("object")
        val object_: Object3D
    }

    class Raycaster {
        fun setFromCamera(
            pointer: Vector2,
            camera: Camera,
        ): Raycaster

        fun intersectObjects(
            objects: Array<Object3D>,
        ): Array<Intersection>
    }

    class Matrix4 {
        fun clone(): Matrix4

        fun invert()
    }
}

val THREE.Object3D.worldPosition: THREE.Vector3
    get() = THREE.Vector3().apply {
        getWorldPosition(this)
    }

fun THREE.Object3D.localize(worldPoint: THREE.Vector3): THREE.Vector3 = matrixWorld.inverted().transform(worldPoint)

fun THREE.Matrix4.inverted(): THREE.Matrix4 = clone().apply {
    invert()
}

fun THREE.Matrix4.transform(
    vector: THREE.Vector3,
): THREE.Vector3 = vector.clone().apply {
    applyMatrix4(this@transform)
}

@Suppress("NOTHING_TO_INLINE")
inline fun MeshBasicMaterialParams(
    color: Int,
    wireframe: Boolean = false,
    transparent: Boolean = false,
    opacity: Double = 1.0,
): THREE.MeshBasicMaterialParams {
    val obj = jsObject()
    obj["color"] = color
    obj["wireframe"] = wireframe
    obj["transparent"] = transparent
    obj["opacity"] = opacity
    return obj
}

@Suppress("NOTHING_TO_INLINE")
inline fun MeshLambertMaterialParams(
    color: Int,
    wireframe: Boolean = false,
    transparent: Boolean = false,
    opacity: Double = 1.0,
): THREE.MeshLambertMaterialParams {
    val obj = jsObject()
    obj["color"] = color
    obj["wireframe"] = wireframe
    obj["transparent"] = transparent
    obj["opacity"] = opacity
    return obj
}

@Suppress("NOTHING_TO_INLINE")
inline fun WebGLRendererParams(
    canvas: HTMLCanvasElement? = null,
): THREE.WebGLRendererParams {
    val obj = jsObject()
    obj["canvas"] = canvas
    return obj
}

@Suppress("NOTHING_TO_INLINE")
inline fun RawShaderMaterialParams(
    vertexShader: String,
    fragmentShader: String,
): THREE.RawShaderMaterialParams {
    val obj = jsObject()
    obj["vertexShader"] = vertexShader
    obj["fragmentShader"] = fragmentShader
    return obj
}

@Suppress("NOTHING_TO_INLINE")
fun LineBasicMaterialParams(
    color: Int,
    linewidth: Double = 1.0,
): THREE.LineBasicMaterialParams {
    val obj = jsObject()
    obj["color"] = color
    obj["linewidth"] = linewidth
    return obj
}

fun THREE.Vector3.toList(): List<Double> = listOf(
    this.x,
    this.y,
    this.z,
)
