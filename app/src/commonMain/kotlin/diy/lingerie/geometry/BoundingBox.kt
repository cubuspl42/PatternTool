package diy.lingerie.geometry

import diy.lingerie.geometry.transformations.PrimitiveTransformation
import diy.lingerie.geometry.transformations.Transformation

/**
 * A bounding box described by its diagonal from a to b
 */
data class BoundingBox(
    val topLeft: Point,
    val width: Double,
    val height: Double,
) {
    companion object {
        fun of(
            pointA: Point,
            pointB: Point,
        ): BoundingBox {
            val xMin = minOf(pointA.x, pointB.x)
            val xMax = maxOf(pointA.x, pointB.x)
            val yMin = minOf(pointA.y, pointB.y)
            val yMax = maxOf(pointA.y, pointB.y)

            return of(
                xMin = xMin,
                xMax = xMax,
                yMin = yMin,
                yMax = yMax,
            )
        }

        fun of(
            xMin: Double,
            xMax: Double,
            yMin: Double,
            yMax: Double,
        ): BoundingBox {
            require(xMin <= xMax)
            require(yMin <= yMax)

            return BoundingBox(
                topLeft = Point(
                    x = xMin,
                    y = yMin,
                ),
                width = xMax - xMin,
                height = yMax - yMin,
            )
        }

        fun unionAll(
            boundingBoxes: List<BoundingBox>,
        ): BoundingBox {
            require(boundingBoxes.isNotEmpty())

            val xMin = boundingBoxes.minOf { it.xMin }
            val xMax = boundingBoxes.maxOf { it.xMax }
            val yMin = boundingBoxes.minOf { it.yMin }
            val yMax = boundingBoxes.maxOf { it.yMax }

            return of(
                xMin = xMin,
                xMax = xMax,
                yMin = yMin,
                yMax = yMax,
            )
        }
    }

    val center: Point
        get() = Point(
            x = xMin + width / 2,
            y = yMin + height / 2,
        )

    val area: Double
        get() = width * height

    val bottomLeft: Point
        get() = topLeft.transformBy(
            transformation = PrimitiveTransformation.Translation(
                tx = width,
                ty = height,
            ),
        )

    val xMin: Double
        get() = topLeft.x

    val xMax: Double
        get() = bottomLeft.x

    val yMin: Double
        get() = topLeft.y

    val yMax: Double
        get() = bottomLeft.y

    fun unionWith(
        other: BoundingBox,
    ): BoundingBox {
        val xMin = minOf(xMin, other.xMin)
        val xMax = maxOf(xMax, other.xMax)
        val yMin = minOf(yMin, other.yMin)
        val yMax = maxOf(yMax, other.yMax)

        return BoundingBox.of(
            xMin = xMin,
            xMax = xMax,
            yMin = yMin,
            yMax = yMax,
        )
    }

    fun transformVia(
        transformation: Transformation,
    ): BoundingBox = BoundingBox.of(
        pointA = transformation.transform(topLeft),
        pointB = transformation.transform(bottomLeft),
    )

    fun overlaps(
        other: BoundingBox,
    ): Boolean {
        val overlapsHorizontally = xMin <= other.xMax && xMax >= other.xMin
        val overlapsVertically = yMin <= other.yMax && yMax >= other.yMin
        return overlapsHorizontally && overlapsVertically
    }

    fun expand(
        bleed: Double,
    ): BoundingBox = BoundingBox.of(
        xMin = xMin - bleed,
        xMax = xMax + bleed,
        yMin = yMin - bleed,
        yMax = yMax + bleed,
    )

    init {
        require(width >= 0)
        require(height >= 0)
    }
}
