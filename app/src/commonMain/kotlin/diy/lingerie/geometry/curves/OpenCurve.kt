package diy.lingerie.geometry.curves

import diy.lingerie.ReprObject
import diy.lingerie.geometry.BoundingBox
import diy.lingerie.geometry.Direction
import diy.lingerie.geometry.LineSegment
import diy.lingerie.math.algebra.NumericObject
import diy.lingerie.math.algebra.equalsWithTolerance
import diy.lingerie.geometry.Point
import diy.lingerie.geometry.Ray
import diy.lingerie.geometry.curves.bezier.BezierCurve
import diy.lingerie.geometry.splines.OpenSpline
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
    ) : NumericObject, ReprObject {
        companion object {
            val range = 0.0..1.0

            /**
             * @param t t-value, unconstrained
             * @return coord if t is in [0, 1], null otherwise
             */
            fun of(t: Double): Coord? = when (t) {
                in range -> Coord(t = t)
                else -> null
            }

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
            require(t in range)
        }

        override fun equalsWithTolerance(
            other: NumericObject,
            tolerance: NumericObject.Tolerance,
        ): Boolean = when {
            other !is Coord -> false
            !t.equalsWithTolerance(other.t, tolerance) -> false
            else -> true
        }

        override fun toReprString(): String = "Coord(t = $t)"
    }

    /**
     * The intersection of two curves
     */
    abstract class Intersection : NumericObject {
        companion object {
            fun swap(
                intersections: Set<Intersection>,
            ): Set<Intersection> = intersections.map {
                it.swap()
            }.toSet()
        }

        fun swap(): Intersection {
            return object : Intersection() {
                override val point: Point
                    get() = this@Intersection.point

                override val subjectCoord: Coord
                    get() = this@Intersection.objectCoord

                override val objectCoord: Coord
                    get() = this@Intersection.subjectCoord
            }
        }

        final override fun equalsWithTolerance(
            other: NumericObject,
            tolerance: NumericObject.Tolerance,
        ): Boolean = when {
            other !is Intersection -> false
            !point.equalsWithTolerance(other.point, tolerance) -> false
            !subjectCoord.equalsWithTolerance(other.subjectCoord, tolerance) -> false
            !objectCoord.equalsWithTolerance(other.objectCoord, tolerance) -> false
            else -> true
        }

        override fun toString(): String {
            return """
                |Intersection(
                |  point = ${point.toReprString()},
                |  subjectCoord = ${subjectCoord.toReprString()},
                |  objectCoord = ${objectCoord.toReprString()},
                |)
            """.trimMargin()
        }

        /**
         * The point of intersection
         */
        abstract val point: Point

        /**
         * The coordinate of the intersection point on the subject curve
         */
        abstract val subjectCoord: Coord

        /**
         * The coordinate of the intersection point on the object curve
         */
        abstract val objectCoord: Coord
    }

    val startTangent: Direction?
        get() = tangentDirectionFunction.start

    val endTangent: Direction?
        get() = tangentDirectionFunction.end

    val tangentRay: FeatureFunction<Ray?> by lazy {
        FeatureFunction.map2(
            functionA = pathFunction,
            functionB = tangentDirectionFunction,
        ) { point, direction ->
            direction?.let { point.castRay(it) }
        }
    }

    abstract val start: Point

    abstract val end: Point

    abstract val subCurves: List<PrimitiveCurve>

    abstract val pathFunction: FeatureFunction<Point>

    abstract val tangentDirectionFunction: FeatureFunction<Direction?>

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
     * curve") with the [objectCurve] curve.
     */
    abstract fun findIntersections(
        objectCurve: OpenCurve,
    ): Set<Intersection>

    abstract fun findIntersectionsLineSegment(
        subjectLineSegment: LineSegment,
    ): Set<Intersection>

    abstract fun findIntersectionsBezierCurve(
        subjectBezierCurve: BezierCurve,
    ): Set<Intersection>

    abstract fun findIntersectionsOpenSpline(
        subjectSpline: OpenSpline,
    ): Set<Intersection>
}
