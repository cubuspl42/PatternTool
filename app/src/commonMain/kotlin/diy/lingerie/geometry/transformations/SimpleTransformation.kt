package diy.lingerie.geometry.transformations

sealed class SimpleTransformation : Transformation() {
    final override val simpleTransformations: List<SimpleTransformation>
        get() = listOf(this)

    final override fun combineWith(
        laterTransformations: List<SimpleTransformation>,
    ): CombinedTransformation = CombinedTransformation(
        simpleTransformations = listOf(this) + laterTransformations,
    )

    abstract fun invert(): SimpleTransformation
}
