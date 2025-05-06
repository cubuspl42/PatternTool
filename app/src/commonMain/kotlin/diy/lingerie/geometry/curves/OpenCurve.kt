package diy.lingerie.geometry.curves

import diy.lingerie.ReprObject
import diy.lingerie.geometry.BoundingBox
import diy.lingerie.geometry.Direction
import diy.lingerie.math.algebra.NumericObject
import diy.lingerie.math.algebra.equalsWithTolerance
import diy.lingerie.geometry.Point
import diy.lingerie.geometry.Ray
import diy.lingerie.geometry.transformations.Transformation
import diy.lingerie.math.algebra.RealFunction
import diy.lingerie.utils.split
import kotlin.jvm.JvmInline

/**
 * A curve defined in range t [0, 1] that is open (having a specified start and
 * end point), assumed to be tangent-continuous (G1).
 */
abstract class OpenCurve : NumericObject, ReprObject {
    abstract class FeatureFunction<out A> {
        companion object {
            fun <A> constant(
                value: A,
            ): FeatureFunction<A> = object : FeatureFunction<A>() {
                override fun evaluate(
                    coord: Coord,
                ): A = value
            }

            fun <A> piecewise(
                pieces: List<FeatureFunction<A>>,
            ): FeatureFunction<A> {
                require(pieces.isNotEmpty())

                return object : FeatureFunction<A>() {
                    override fun evaluate(
                        coord: Coord,
                    ): A = when (coord.t) {
                        1.0 -> pieces.last().end

                        else -> {
                            val ts = coord.t * pieces.size
                            val (index, t) = ts.split()

                            val piece = pieces[index]

                            piece.evaluate(
                                coord = Coord(t = t)
                            )
                        }
                    }
                }
            }

            fun <A, B, C> map2(
                functionA: FeatureFunction<A>,
                functionB: FeatureFunction<B>,
                transform: (A, B) -> C,
            ): FeatureFunction<C> = object : FeatureFunction<C>() {
                override fun evaluate(
                    coord: Coord,
                ): C = transform(
                    functionA.evaluate(coord),
                    functionB.evaluate(coord),
                )
            }

            fun <A> wrap(
                realFunction: RealFunction<A>,
            ): FeatureFunction<A> = object : FeatureFunction<A>() {
                override fun evaluate(
                    coord: Coord,
                ): A = realFunction.apply(coord.t)
            }
        }

        fun <B> map(
            transform: (A) -> B,
        ): FeatureFunction<B> = object : FeatureFunction<B>() {
            override fun evaluate(
                coord: Coord,
            ): B = transform(
                this@FeatureFunction.evaluate(coord),
            )
        }

        val start: A
            get() = evaluate(Coord.start)

        val end: A
            get() = evaluate(Coord.end)

        abstract fun evaluate(
            coord: Coord,
        ): A
    }

    @JvmInline
    value class Coord(
        /**
         * The t-value for the basis function
         */
        val t: Double,
    ) : NumericObject {
        companion object {
            val start = Coord(
                t = 0.0,
            )

            val end = Coord(
                t = 1.0,
            )
        }

        val complement: Coord
            get() = Coord(
                t = 1.0 - t,
            )

        init {
            require(t in 0.0..1.0)
        }

        override fun equalsWithTolerance(
            other: NumericObject,
            tolerance: NumericObject.Tolerance,
        ): Boolean = when {
            other !is Coord -> false
            !t.equalsWithTolerance(other.t, tolerance) -> false
            else -> true
        }
    }

    /**
     * The intersection of two curves
     */
    abstract class Intersection {
        /**
         * The point of intersection
         */
        abstract val point: Point

        /**
         * The coordinate of the intersection point on the subject curve
         */
        abstract val coord: Coord

        /**
         * The coordinate of the intersection point on the other curve
         */
        abstract val otherCoord: Coord
    }

    val startTangent: Direction?
        get() = tangentDirection.start

    val endTangent: Direction?
        get() = tangentDirection.end

    val tangentRay: FeatureFunction<Ray?> by lazy {
        FeatureFunction.map2(
            functionA = path,
            functionB = tangentDirection,
        ) { point, direction ->
            direction?.let { point.castRay(it) }
        }
    }

    abstract val start: Point

    abstract val end: Point

    abstract val subCurves: List<PrimitiveCurve>

    abstract val path: FeatureFunction<Point>

    abstract val tangentDirection: FeatureFunction<Direction?>

    abstract fun transformBy(
        transformation: Transformation,
    ): OpenCurve

    abstract fun splitAt(
        coord: Coord,
    ): Pair<OpenCurve, OpenCurve>

    /**
     * Find the offset curve (or its close approximation) of this curve
     */
    abstract fun findOffsetCurve(
        offset: Double,
    ): OpenCurve

    abstract fun findBoundingBox(): BoundingBox

    /**
     * Find the intersections of this curve (also referred to as the "subject
     * curve") with the [other] curve.
     */
    fun findIntersections(
        other: OpenCurve,
    ): Set<Intersection> {
        TODO()
    }
}
