package dev.toolkt.geometry.transformations

sealed class DescriptiveTransformation3D : LinearTransformation3D() {
    abstract override fun invert(): DescriptiveTransformation3D
}
