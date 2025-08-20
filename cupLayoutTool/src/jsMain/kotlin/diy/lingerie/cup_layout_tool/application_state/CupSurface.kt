package diy.lingerie.cup_layout_tool.application_state

import dev.toolkt.core.iterable.withNextCyclic
import dev.toolkt.geometry.Plane
import dev.toolkt.geometry.Point
import dev.toolkt.geometry.Point3D
import dev.toolkt.geometry.Ray
import dev.toolkt.geometry.Ray3
import dev.toolkt.geometry.Span
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.PropertyCell
import dev.toolkt.reactive.effect.MomentContext
import dev.toolkt.reactive.reactive_list.ReactiveList
import dev.toolkt.reactive.reactive_list.fuseOf
import kotlin.collections.listOf

class CupSurface private constructor(
    /**
     * The position of the apex (in the cup 3D space)
     */
    val apexPosition: PropertyCell<Point3D>,
    /**
     * The underwire link closest to the body center
     */
    val startUnderwireLink: UnderwireLink,
    /**
     * The underwire link closest to the lower body
     */
    val intermediateUnderwireLink: UnderwireLink,
    /**
     * The underwire link closest to the upper/outer body
     */
    val endUnderwireLink: UnderwireLink,
    /**
     * Overwire links in the CCW order
     */
    val overwireLinks: List<CupSurface.OverwireLink>,
) {
    companion object {
        val underwirePlane = Plane.Xy

        context(momentContext: MomentContext) fun create(): CupSurface = CupSurface(
            apexPosition = PropertyCell.create(initialValue = Point3D.origin),
            startUnderwireLink = UnderwireLink.create(),
            intermediateUnderwireLink = UnderwireLink.create(),
            endUnderwireLink = UnderwireLink.create(),
            overwireLinks = listOf(
                OverwireLink.create(),
                OverwireLink.create(),
                OverwireLink.create(),
            ),
        )
    }

    class ControlBar2D private constructor(
        val previousControlPointPosition: PropertyCell<Point>,
        val innerControlPointPosition: PropertyCell<Point>,
        val nextControlChordLength: PropertyCell<Double>,
    ) {
        companion object {
            context(momentContext: MomentContext) fun create(): ControlBar2D = ControlBar2D(
                previousControlPointPosition = PropertyCell.create(initialValue = Point.origin),
                innerControlPointPosition = PropertyCell.create(initialValue = Point.origin),
                nextControlChordLength = PropertyCell.create(initialValue = 1.0),
            )
        }

        val nextControlPointPosition: Cell<Point> = Cell.map3(
            previousControlPointPosition, innerControlPointPosition,
            nextControlChordLength,
        ) { previousControlPointPositionNow, innerControlPointPositionNow, nextControlChordLengthNow ->
            Ray.between(
                origin = innerControlPointPositionNow,
                target = previousControlPointPositionNow,
            ).opposite.findPoint(
                distance = Span.of(value = nextControlChordLengthNow),
            )
        }

        fun toEffectiveControlBar(
            // FIXME: Parametric plane
            plane: Cell<Plane>,
        ): Cell<EffectiveControlBar> {
            TODO()
        }
    }

    class ControlBar3D private constructor(
        val previousControlPointPosition: PropertyCell<Point3D>,
        val innerControlPointPosition: PropertyCell<Point3D>,
        val nextControlChordLength: PropertyCell<Double>,
    ) {
        companion object {
            context(momentContext: MomentContext) fun create(): ControlBar3D = ControlBar3D(
                previousControlPointPosition = PropertyCell.create(initialValue = Point3D.origin),
                innerControlPointPosition = PropertyCell.create(initialValue = Point3D.origin),
                nextControlChordLength = PropertyCell.create(initialValue = 1.0),
            )
        }

        val nextControlPointPosition: Cell<Point3D> = Cell.map3(
            previousControlPointPosition, innerControlPointPosition,
            nextControlChordLength,
        ) { previousControlPointPositionNow, innerControlPointPositionNow, nextControlChordLengthNow ->
            Ray3.between(
                origin = innerControlPointPositionNow,
                target = previousControlPointPositionNow,
            ).opposite.findPoint(
                distance = Span.of(value = nextControlChordLengthNow),
            )
        }

        fun toEffectiveControlBar(): Cell<EffectiveControlBar> {
            return Cell.map3(
                previousControlPointPosition,
                innerControlPointPosition,
                nextControlPointPosition,
            ) { previous, inner, next ->
                EffectiveControlBar(
                    previousControlPointPosition = previous,
                    innerControlPointPosition = inner,
                    nextControlPointPosition = next,
                )
            }
        }
    }

    class EffectiveControlBar(
        val previousControlPointPosition: Point3D,
        val innerControlPointPosition: Point3D,
        val nextControlPointPosition: Point3D,
    )

    data class EffectiveLink(
        val wireControlBar: EffectiveControlBar,
        val supportControlBar: EffectiveControlBar,
        val apexControlBar: EffectiveControlBar,
    ) {
        fun toWall(
            nextEffectiveLink: EffectiveLink,
        ): BezierConeSurface.Wall = BezierConeSurface.Wall(
            partialVerticalStartCurve = BezierConeSurface.PartialBezierCurve(
                base = wireControlBar.innerControlPointPosition,
                baseControl = supportControlBar.innerControlPointPosition,
                apexControl = apexControlBar.innerControlPointPosition,
            ),
            partialVerticalFirstControlCurve = BezierConeSurface.PartialBezierCurve(
                base = wireControlBar.nextControlPointPosition,
                baseControl = supportControlBar.nextControlPointPosition,
                apexControl = apexControlBar.nextControlPointPosition,
            ),
            partialVerticalSecondControlCurve = BezierConeSurface.PartialBezierCurve(
                base = nextEffectiveLink.wireControlBar.previousControlPointPosition,
                baseControl = nextEffectiveLink.supportControlBar.previousControlPointPosition,
                apexControl = nextEffectiveLink.apexControlBar.previousControlPointPosition,
            ),
        )
    }

    sealed class Link {
        abstract fun toEffectiveLink(
            apexPosition: Cell<Point3D>,
        ): Cell<EffectiveLink>

    }

    class UnderwireLink private constructor(
        // The control bar on the underwire (in the underwire 2D space)
        val wireControlBar: ControlBar2D,

        // The control bar floating over the underwire (in the cup 3D space)
        val supportControlBar: ControlBar3D,

        // The control bar on the apex side (in the apex 2D space)
        val apexControlBar: ControlBar2D,
    ) : Link() {
        companion object {
            context(momentContext: MomentContext) fun create(): UnderwireLink = UnderwireLink(
                wireControlBar = ControlBar2D.create(),
                supportControlBar = ControlBar3D.create(),
                apexControlBar = ControlBar2D.create(),
            )
        }

        override fun toEffectiveLink(
            apexPosition: Cell<Point3D>,
        ): Cell<EffectiveLink> = Cell.map3(
            wireControlBar.toEffectiveControlBar(
                plane = Cell.of(underwirePlane),
            ),
            supportControlBar.toEffectiveControlBar(),
            apexControlBar.toEffectiveControlBar(
                plane = apexPosition.map { it.xyPlane },
            ),
        ) { effectiveWireControlBarNow, effectiveSupportControlBarNow, effectiveApexControlBarNow ->
            EffectiveLink(
                wireControlBar = effectiveWireControlBarNow,
                supportControlBar = effectiveSupportControlBarNow,
                apexControlBar = effectiveApexControlBarNow,
            )
        }
    }

    class OverwireLink private constructor(
        // The control bar on the overwire (in the cup 3D space)
        val wireControlBar: ControlBar3D,

        // The control bar floating over the overwire (in the cup 3D space)
        val supportControlBar: ControlBar3D,

        // The control bar near the apex (in the apex 2D space)
        val apexControlBar: ControlBar2D,
    ) : Link() {
        companion object {
            context(momentContext: MomentContext) fun create(): OverwireLink = OverwireLink(
                wireControlBar = ControlBar3D.create(),
                supportControlBar = ControlBar3D.create(),
                apexControlBar = ControlBar2D.create(),
            )
        }

        override fun toEffectiveLink(
            apexPosition: Cell<Point3D>,
        ): Cell<EffectiveLink> = Cell.map3(
            wireControlBar.toEffectiveControlBar(),
            supportControlBar.toEffectiveControlBar(),
            apexControlBar.toEffectiveControlBar(
                plane = apexPosition.map { it.xyPlane },
            ),
        ) { effectiveWireControlBarNow, effectiveSupportControlBarNow, effectiveApexControlBarNow ->
            EffectiveLink(
                wireControlBar = effectiveWireControlBarNow,
                supportControlBar = effectiveSupportControlBarNow,
                apexControlBar = effectiveApexControlBarNow,
            )
        }
    }


    val links: List<Link>
        get() = listOf(
            startUnderwireLink,
            intermediateUnderwireLink,
            endUnderwireLink,
        ) + overwireLinks

    val effectiveLinks: Cell<List<EffectiveLink>>
        get() = ReactiveList.of(links).fuseOf {
            it.toEffectiveLink(apexPosition = apexPosition)
        }.elements

    val bezierConeSurface: Cell<BezierConeSurface>
        get() = Cell.map2(
            apexPosition,
            effectiveLinks,
        ) { apexPositionNow, effectiveLinksNow ->
            BezierConeSurface(
                apex = apexPositionNow,
                walls = effectiveLinksNow.withNextCyclic().map { (effectiveLink, nextEffectiveLink) ->
                    effectiveLink.toWall(nextEffectiveLink = nextEffectiveLink)
                },
            )
        }
}
