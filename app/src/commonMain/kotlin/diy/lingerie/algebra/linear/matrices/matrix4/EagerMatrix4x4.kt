package diy.lingerie.algebra.linear.matrices.matrix4

sealed class EagerMatrix4x4 : Matrix4x4() {
    final override operator fun times(
        other: Matrix4x4,
    ): Matrix4x4 = columnMajor(
        column0 = apply(other.column0),
        column1 = apply(other.column1),
        column2 = apply(other.column2),
        column3 = apply(other.column3),
    )
}
