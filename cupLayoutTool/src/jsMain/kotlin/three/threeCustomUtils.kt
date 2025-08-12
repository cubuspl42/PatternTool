package three

import dev.toolkt.geometry.Point3D
import dev.toolkt.math.algebra.linear.vectors.Vector3

fun List<Point3D>.toBufferAttribute(): THREE.BufferAttribute = map { it.pointVector }.toBufferAttribute()

fun List<Vector3>.toBufferAttribute(): THREE.BufferAttribute {
    return THREE.BufferAttribute(
        array = Float32Array(
            this.flatMap { it.toList() }.toTypedArray(),
        ),
        itemSize = 3,
    )
}

fun THREE.BufferGeometry.setPositionAttribute(
    positions: List<Point3D>,
) {
    setAttribute(
        name = "position",
        attribute = positions.toBufferAttribute(),
    )
}
