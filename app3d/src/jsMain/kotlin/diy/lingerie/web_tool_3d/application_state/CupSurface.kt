package diy.lingerie.web_tool_3d.application_state

import dev.toolkt.core.iterable.withNextCyclic
import dev.toolkt.geometry.Plane
import dev.toolkt.geometry.Point
import dev.toolkt.geometry.Point3D
import dev.toolkt.geometry.Ray
import dev.toolkt.geometry.Ray3
import dev.toolkt.geometry.Span
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.PropertyCell
import dev.toolkt.reactive.reactive_list.ReactiveList
import dev.toolkt.reactive.reactive_list.fuseOf

class CupSurface {
    companion object {
        val underwirePlane = Plane.Xy
    }

    class ControlBar2D {
        val previousControlPointPosition = PropertyCell<Point>(initialValue = Point.origin)

        val innerControlPointPosition = PropertyCell<Point>(initialValue = Point.origin)

        val nextControlChordLength = PropertyCell(initialValue = 1.0)

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

    class ControlBar3D {
        val previousControlPointPosition = PropertyCell<Point3D>(initialValue = Point3D.origin)

        val innerControlPointPosition = PropertyCell<Point3D>(initialValue = Point3D.origin)

        val nextControlChordLength = PropertyCell(initialValue = 1.0)

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
            TODO()
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

    class UnderwireLink : Link() {
        // The control bar on the underwire (in the underwire 2D space)
        val wireControlBar = ControlBar2D()

        // The control bar floating over the underwire (in the cup 3D space)
        val supportControlBar = ControlBar3D()

        // The control bar on the apex side (in the apex 2D space)
        val apexControlBar = ControlBar2D()

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
        ) {
                effectiveWireControlBarNow,
                effectiveSupportControlBarNow,
                effectiveApexControlBarNow,
            ->
            EffectiveLink(
                wireControlBar = effectiveWireControlBarNow,
                supportControlBar = effectiveSupportControlBarNow,
                apexControlBar = effectiveApexControlBarNow,
            )
        }
    }

    class OverwireLink : Link() {
        // The control bar on the overwire (in the cup 3D space)
        val wireControlBar = ControlBar3D()

        // The control bar floating over the overwire (in the cup 3D space)
        val supportControlBar = ControlBar3D()

        // The control bar near the apex (in the apex 2D space)
        val apexControlBar = ControlBar2D()

        override fun toEffectiveLink(
            apexPosition: Cell<Point3D>,
        ): Cell<EffectiveLink> = Cell.map3(
            wireControlBar.toEffectiveControlBar(),
            supportControlBar.toEffectiveControlBar(),
            apexControlBar.toEffectiveControlBar(
                plane = apexPosition.map { it.xyPlane },
            ),
        ) {
                effectiveWireControlBarNow,
                effectiveSupportControlBarNow,
                effectiveApexControlBarNow,
            ->
            EffectiveLink(
                wireControlBar = effectiveWireControlBarNow,
                supportControlBar = effectiveSupportControlBarNow,
                apexControlBar = effectiveApexControlBarNow,
            )
        }
    }

    // The position of the apex (in the cup 3D space)
    val apexPosition = PropertyCell(initialValue = Point3D.origin)

    // The underwire link closest to the body center
    val startUnderwireLink = UnderwireLink()

    // The underwire link closest to the lower body
    val intermediateUnderwireLink = UnderwireLink()

    // The underwire link closest to the upper/outer body
    val endUnderwireLink = UnderwireLink()

    // Overwire links in the CCW order
    val overwireLinks = listOf(
        OverwireLink(),
        OverwireLink(),
        OverwireLink(),
    )

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
