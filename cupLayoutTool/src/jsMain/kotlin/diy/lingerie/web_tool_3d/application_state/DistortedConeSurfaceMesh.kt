package diy.lingerie.web_tool_3d.application_state

import dev.toolkt.geometry.Point3D

class DistortedConeSurfaceMesh(
    /**
     * The position of the apex
     */
    val apex: Point3D,
    /**
     * The vertical wires of the distorted cone (all having the same number of vertices)
     */
    val verticalWires: List<VerticalWire>,
) {
    data class VerticalWire(
        /**
         * Vertices starting from the wire side, towards the apex (but not including the apex)
         */
        val vertices: List<Point3D>,
    )

    init {
        require(verticalWires.size >= 3) {
            "There must be at least 3 vertical wires, but got ${verticalWires.size}"
        }

        val requiredSize = verticalWires.first().vertices.size

        require(
            verticalWires.all { it.vertices.size == requiredSize },
        ) {
            "All vertical wires must have the same number of vertices"
        }
    }
}
