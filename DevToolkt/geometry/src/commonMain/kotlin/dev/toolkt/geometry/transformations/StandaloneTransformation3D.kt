package dev.toolkt.geometry.transformations

sealed class StandaloneTransformation3D : EffectiveTransformation3D() {
    final override val standaloneTransformations: List<StandaloneTransformation3D>
        get() = listOf(this)

    final override fun combineWith(
        laterTransformations: List<StandaloneTransformation3D>,
    ): CombinedTransformation3D = CombinedTransformation3D(
        standaloneTransformations = listOf(this) + laterTransformations,
    )

    abstract fun invert(): StandaloneTransformation3D
}
