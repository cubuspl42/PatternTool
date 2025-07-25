package three

import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.extra.jsObject

@JsModule("three")
@JsNonModule
external object THREE {
    class Vector3(x: Double = definedExternally, y: Double = definedExternally, z: Double = definedExternally) {
        var x: Double
        var y: Double
        var z: Double
        fun toArray(): Array<Double>
    }

    class Scene {
        fun add(obj: Object3D): Scene
    }

    open class Camera : Object3D

    class PerspectiveCamera(fov: Double, aspect: Double, near: Double, far: Double) : Camera {
        var position: Vector3
        var aspect: Double
        fun updateProjectionMatrix()
    }

    class WebGLRenderer(params: WebGLRendererParams = definedExternally) {
        fun setSize(width: Double, height: Double)
        val domElement: HTMLElement
        fun render(scene: Scene, camera: Camera)
    }

    interface WebGLRendererParams {
        var canvas: HTMLElement?
    }

    open class Object3D {
        var rotation: Euler
    }

    class Euler(x: Double = definedExternally, y: Double = definedExternally, z: Double = definedExternally) {
        var x: Double
        var y: Double
        var z: Double
    }


    class BufferGeometry {
        fun setAttribute(name: String, attribute: BufferAttribute): BufferGeometry
        fun setIndex(indices: Array<Int>): BufferGeometry
        fun computeVertexNormals(): BufferGeometry
    }

    class BufferAttribute(
        array: Float32Array,
        itemSize: Int,
    ) {
        var needsUpdate: Boolean
    }

    open class Material {
        val wireframe: Boolean
    }

    class MeshBasicMaterial(params: MeshBasicMaterialParams = definedExternally) : Material

    interface MeshBasicMaterialParams {
        val color: Int
        val wireframe: Boolean?
    }

    class Mesh(geometry: BufferGeometry, material: Material) : Object3D
}

@Suppress("NOTHING_TO_INLINE")
inline fun MeshBasicMaterialParams(
    color: Int,
    wireframe: Boolean,
): THREE.MeshBasicMaterialParams {
    val obj = jsObject()
    obj["color"] = color
    obj["wireframe"] = wireframe
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

// Helper functions to enable requestAnimationFrame use from Kotlin
@JsName("requestAnimationFrame")
external fun requestAnimationFrame(callback: () -> Unit): Int

fun requestAnimationFrames(
    callback: () -> Unit,
) {
    fun animate() {
        requestAnimationFrame(::animate)
        callback()
    }

    requestAnimationFrame(::animate)
}
