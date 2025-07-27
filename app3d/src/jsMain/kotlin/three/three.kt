package three

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

        fun toArray(): Array<Double>
    }

    class Scene {
        fun add(obj: Object3D): Scene
    }

    open class Camera : Object3D

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

    open class Object3D {
        val position: Vector3

        val rotation: Euler

        val scale: Vector3

        var userData: Any

        fun add(child: Object3D): Object3D
    }

    class Group : Object3D

    class Euler(x: Double = definedExternally, y: Double = definedExternally, z: Double = definedExternally) {
        var x: Double
        var y: Double
        var z: Double
    }


    open class BufferGeometry {
        fun setAttribute(name: String, attribute: BufferAttribute): BufferGeometry

        fun setIndex(indices: Array<Int>): BufferGeometry

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

    class BufferAttribute(
        array: Float32Array,
        itemSize: Int,
    ) {
        var needsUpdate: Boolean
    }

    open class Material {
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

    class Mesh(
        geometry: BufferGeometry,
        material: Material,
    ) : Object3D

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

        val `object`: Object3D
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

// TypedArray definitions needed for Three.js
@JsName("Float32Array")
external class Float32Array(array: Array<Double>) {
    constructor(size: Int)
}

@Suppress("NOTHING_TO_INLINE")
inline operator fun Float32Array.get(index: Int): Double = asDynamic()[index]

@Suppress("NOTHING_TO_INLINE")
inline operator fun Float32Array.set(index: Int, newItem: Double) {
    asDynamic()[index] = newItem
}
