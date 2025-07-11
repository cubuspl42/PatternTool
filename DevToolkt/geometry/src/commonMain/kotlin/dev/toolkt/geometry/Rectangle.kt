package dev.toolkt.geometry

import dev.toolkt.geometry.transformations.PrimitiveTransformation
import dev.toolkt.geometry.transformations.Transformation
import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.equalsWithTolerance

/**
 * An axis-aligned rectangle
 */
data class Rectangle(
    /**
     * The origin point
     */
    val origin: Point,
    /**
     * Number of units in the +X direction
     */
    val width: Double,
    /**
     * Number of units in the +Y direction
     */
    val height: Double,
) : NumericObject {
    companion object {
        fun of(
            pointA: Point,
            pointB: Point,
        ): Rectangle {
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
        ): Rectangle {
            require(xMin <= xMax)
            require(yMin <= yMax)

            return Rectangle(
                origin = Point(
                    x = xMin,
                    y = yMin,
                ),
                width = xMax - xMin,
                height = yMax - yMin,
            )
        }

        fun unionAll(
            rectangles: List<Rectangle>,
        ): Rectangle {
            require(rectangles.isNotEmpty())

            val xMin = rectangles.minOf { it.xMin }
            val xMax = rectangles.maxOf { it.xMax }
            val yMin = rectangles.minOf { it.yMin }
            val yMax = rectangles.maxOf { it.yMax }

            return of(
                xMin = xMin,
                xMax = xMax,
                yMin = yMin,
                yMax = yMax,
            )
        }
    }

    val size: Size
        get() = Size(
            width = width,
            height = height,
        )

    val center: Point
        get() = Point(
            x = xMin + width / 2,
            y = yMin + height / 2,
        )

    val smallerSide: Double
        get() = minOf(width, height)

    val bottomRight: Point
        get() = origin.transformBy(
            transformation = PrimitiveTransformation.Translation(
                tx = width,
                ty = height,
            ),
        )

    val xMin: Double
        get() = origin.x

    val xMax: Double
        get() = bottomRight.x

    val xRange: ClosedFloatingPointRange<Double>
        get() = xMin..xMax

    val yRange: ClosedFloatingPointRange<Double>
        get() = yMin..yMax

    val yMin: Double
        get() = origin.y

    val yMax: Double
        get() = bottomRight.y

    fun unionWith(
        other: Rectangle,
    ): Rectangle {
        val xMin = minOf(xMin, other.xMin)
        val xMax = maxOf(xMax, other.xMax)
        val yMin = minOf(yMin, other.yMin)
        val yMax = maxOf(yMax, other.yMax)

        return of(
            xMin = xMin,
            xMax = xMax,
            yMin = yMin,
            yMax = yMax,
        )
    }

    fun transformVia(
        transformation: Transformation,
    ): Rectangle = of(
        pointA = transformation.transform(origin),
        pointB = transformation.transform(bottomRight),
    )

    fun overlaps(
        other: Rectangle,
    ): Boolean {
        val overlapsHorizontally = xMin <= other.xMax && xMax >= other.xMin
        val overlapsVertically = yMin <= other.yMax && yMax >= other.yMin
        return overlapsHorizontally && overlapsVertically
    }

    fun expand(
        bleed: Double,
    ): Rectangle = of(
        xMin = xMin - bleed,
        xMax = xMax + bleed,
        yMin = yMin - bleed,
        yMax = yMax + bleed,
    )

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericObject.Tolerance,
    ): Boolean = when {
        other !is Rectangle -> false
        !origin.equalsWithTolerance(other.origin, tolerance = tolerance) -> false
        !width.equalsWithTolerance(other.width, tolerance = tolerance) -> false
        !height.equalsWithTolerance(other.height, tolerance = tolerance) -> false
        else -> true
    }

    fun scalingTo(
        target: Rectangle,
    ): PrimitiveTransformation.Scaling = PrimitiveTransformation.Scaling(
        sx = target.width / width,
        sy = target.height / height,
    )

    init {
        require(width >= 0)
        require(height >= 0)
    }
}
