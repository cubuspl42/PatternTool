package diy.lingerie.pattern_tool

import diy.lingerie.geometry.LineSegment
import diy.lingerie.geometry.Point
import diy.lingerie.geometry.curves.OpenCurve
import diy.lingerie.geometry.curves.BezierCurve
import diy.lingerie.geometry.splines.OpenSpline
import diy.lingerie.geometry.splines.Spline
import diy.lingerie.pattern_tool.Outline.Verge
import diy.lingerie.simple_dom.mm
import diy.lingerie.simple_dom.svg.SvgPath
import diy.lingerie.simple_dom.svg.SvgRoot
import diy.lingerie.simple_dom.svg.SvgShape
import diy.lingerie.test_utils.assertEqualsWithTolerance
import diy.lingerie.utils.getResourceAsReader
import kotlin.test.Ignore
import kotlin.test.Test

class OutlineTests {
    @Test
    fun testVergeReconstruct() {
        val point0FreeJoint = Point(849.97, 1083.35)
        val point1Control = Point(923.62, 1116.05)
        val point2Control = Point(1015.23, 1213.44)
        val point3SmoothJoint = Point(1061.95, 1336.57)
        val point4Control = Point(1111.89, 1468.18)
        val point5Control = Point(1143.21, 1618.69)
        val point6FreeJoint = Point(1149.75, 1695.65)

        val openSpline = OpenSpline(
            firstCurve = BezierCurve(
                start = point0FreeJoint,
                firstControl = point1Control,
                secondControl = point2Control,
                end = point3SmoothJoint,
            ),
            trailingSequentialLinks = listOf(
                Spline.Link(
                    edge = BezierCurve.Edge(
                        firstControl = point4Control,
                        secondControl = point5Control,
                    ),
                    end = point6FreeJoint,
                ),
            ),
        )

        val edgeMetadata = Outline.EdgeMetadata(
            seamAllowance = SeamAllowance(allowanceMm = 6.0),
        )

        val verge = Verge.reconstruct(
            smoothCurve = openSpline,
            edgeMetadata = edgeMetadata,
        )

        assertEqualsWithTolerance(
            expected = Outline.Verge(
                startAnchor = Outline.Anchor(
                    position = point0FreeJoint,
                ),
                edge = Outline.Edge(
                    startHandle = Outline.Handle(
                        position = point1Control,
                    ),
                    intermediateJoints = listOf(
                        Outline.Joint.Smooth(
                            rearHandle = Outline.Handle(
                                position = point2Control,
                            ),
                            anchorCoord = OpenCurve.Coord(
                                t = 0.48335,
                            ),
                            frontHandle = Outline.Handle(
                                position = point4Control,
                            ),
                        ),
                    ),
                    endHandle = Outline.Handle(
                        position = point5Control,
                    ),
                    metadata = edgeMetadata,
                ),
                endAnchor = Outline.Anchor(
                    position = point6FreeJoint,
                ),
            ),
            actual = verge,
        )
    }

    @Test
    fun testOutlineReconstruct() {
        val point0FreeJoint = Point(849.97, 1083.35)
        val point1Control = Point(923.62, 1116.05)
        val point2Control = Point(1015.23, 1213.44)
        val point3SmoothJoint = Point(1061.95, 1336.57)
        val point4Control = Point(1111.89, 1468.18)
        val point5Control = Point(1143.21, 1618.69)

        val point6FreeJoint = Point(1149.75, 1695.65)
        val point7Control = Point(1228.37, 1673.57)
        val point8Control = Point(1302.86, 1563.13)
        val point9SmoothJoint = Point(1506.80, 1553.93)
        val point10Control = Point(1750.43, 1542.92)
        val point11Control = Point(1680.53, 1442.38)
        val point12SmoothJoint = Point(1877.12, 1435.89)
        val point13Control = Point(2069.22, 1429.54)
        val point14Control = Point(2160.31, 1476.10)

        val point15FreeJoint = Point(2305.43, 1341.55)
        val point16Control = Point(2267.93, 1108.87)
        val point17Control = Point(2203.85, 790.42)

        val point18FreeJoint = Point(1838.79, 561.24)
        val point19Control = Point(1478.44, 611.55)
        val point20Control = Point(1447.10, 715.54)
        val point21SmoothJoint = Point(1283.83, 758.94)
        val point22Control = Point(1084.05, 812.07)
        val point23Control = Point(997.57, 842.14)

        val cyclicEdgeCurves = listOf(
            OpenSpline(
                firstCurve = BezierCurve(
                    start = point0FreeJoint,
                    firstControl = point1Control,
                    secondControl = point2Control,
                    end = point3SmoothJoint,
                ),
                trailingSequentialLinks = listOf(
                    Spline.Link(
                        edge = BezierCurve.Edge(
                            firstControl = point4Control,
                            secondControl = point5Control,
                        ),
                        end = point6FreeJoint,
                    ),
                ),
            ),
            OpenSpline(
                firstCurve = BezierCurve(
                    start = point6FreeJoint,
                    firstControl = point7Control,
                    secondControl = point8Control,
                    end = point9SmoothJoint,
                ),
                trailingSequentialLinks = listOf(
                    Spline.Link(
                        edge = BezierCurve.Edge(
                            firstControl = point10Control,
                            secondControl = point11Control,
                        ),
                        end = point12SmoothJoint,
                    ),
                    Spline.Link(
                        edge = BezierCurve.Edge(
                            firstControl = point13Control,
                            secondControl = point14Control,
                        ),
                        end = point15FreeJoint,
                    ),
                ),
            ),
            OpenSpline(
                firstCurve = BezierCurve(
                    start = point15FreeJoint,
                    firstControl = point16Control,
                    secondControl = point17Control,
                    end = point18FreeJoint,
                ),
                trailingSequentialLinks = emptyList(),
            ),
            OpenSpline(
                firstCurve = BezierCurve(
                    start = point18FreeJoint,
                    firstControl = point19Control,
                    secondControl = point20Control,
                    end = point21SmoothJoint,
                ),
                trailingSequentialLinks = listOf(
                    Spline.Link(
                        edge = BezierCurve.Edge(
                            firstControl = point22Control,
                            secondControl = point23Control,
                        ),
                        end = point0FreeJoint,
                    ),
                ),
            ),
        )

        val edgeMetadata = Outline.EdgeMetadata(
            seamAllowance = SeamAllowance(allowanceMm = 6.0),
        )

        val outline = Outline.reconstruct(
            cyclicSmoothCurves = cyclicEdgeCurves,
            edgeMetadataMap = Outline.EdgeMetadataMap(
                edgeMetadataByEdgeIndex = emptyMap(),
                defaultEdgeMetadata = edgeMetadata,
            ),
        )

        assertEqualsWithTolerance(
            expected = Outline(
                cyclicLinks = listOf(
                    Outline.Link(
                        edge = Outline.Edge(
                            startHandle = Outline.Handle(
                                position = point1Control,
                            ),
                            intermediateJoints = listOf(
                                Outline.Joint.Smooth(
                                    rearHandle = Outline.Handle(
                                        position = point2Control,
                                    ),
                                    anchorCoord = OpenCurve.Coord(
                                        t = 0.48335,
                                    ),
                                    frontHandle = Outline.Handle(
                                        position = point4Control,
                                    ),
                                ),
                            ),
                            endHandle = Outline.Handle(
                                position = point5Control,
                            ),
                            metadata = edgeMetadata,
                        ),
                        endAnchor = Outline.Anchor(
                            position = point6FreeJoint,
                        ),
                    ),
                    Outline.Link(
                        edge = Outline.Edge(
                            startHandle = Outline.Handle(
                                position = point7Control,
                            ),
                            intermediateJoints = listOf(
                                Outline.Joint.Smooth(
                                    rearHandle = Outline.Handle(
                                        position = point8Control,
                                    ),
                                    anchorCoord = OpenCurve.Coord(
                                        t = 0.45566,
                                    ),
                                    frontHandle = Outline.Handle(
                                        position = point10Control,
                                    ),
                                ),
                                Outline.Joint.Smooth(
                                    rearHandle = Outline.Handle(
                                        position = point11Control,
                                    ),
                                    anchorCoord = OpenCurve.Coord(
                                        t = 0.50578,
                                    ),
                                    frontHandle = Outline.Handle(
                                        position = point13Control,
                                    ),
                                ),
                            ),
                            endHandle = Outline.Handle(
                                position = point14Control,
                            ),
                            metadata = edgeMetadata,
                        ),
                        endAnchor = Outline.Anchor(
                            position = point15FreeJoint,
                        ),
                    ),
                    Outline.Link(
                        edge = Outline.Edge(
                            startHandle = Outline.Handle(
                                position = point16Control,
                            ),
                            intermediateJoints = emptyList(),
                            endHandle = Outline.Handle(
                                position = point17Control,
                            ),
                            metadata = edgeMetadata,
                        ),
                        endAnchor = Outline.Anchor(
                            position = point18FreeJoint,
                        ),
                    ),
                    Outline.Link(
                        edge = Outline.Edge(
                            startHandle = Outline.Handle(
                                position = point19Control,
                            ),
                            intermediateJoints = listOf(
                                Outline.Joint.Smooth(
                                    rearHandle = Outline.Handle(
                                        position = point20Control,
                                    ),
                                    anchorCoord = OpenCurve.Coord(
                                        t = 0.44971,
                                    ),
                                    frontHandle = Outline.Handle(
                                        position = point22Control,
                                    ),
                                ),
                            ),
                            endHandle = Outline.Handle(
                                position = point23Control,
                            ),
                            metadata = edgeMetadata,
                        ),
                        endAnchor = Outline.Anchor(
                            position = point0FreeJoint,
                        ),
                    ),
                ),
            ),
            actual = outline,
        )
    }

    @Test
    fun testLoadSvg() {
        val point0FreeJoint = Point(849.97, 1083.35)
        val point1Control = Point(923.62, 1116.05)
        val point2Control = Point(1015.23, 1213.44)
        val point3SmoothJoint = Point(1061.95, 1336.57)
        val point4Control = Point(1111.89, 1468.18)
        val point5Control = Point(1143.21, 1618.69)

        val point6FreeJoint = Point(1149.75, 1695.65)
        val point7Control = Point(1228.37, 1673.57)
        val point8Control = Point(1302.86, 1563.13)
        val point9SmoothJoint = Point(1506.80, 1553.93)
        val point10Control = Point(1750.43, 1542.92)
        val point11Control = Point(1680.53, 1442.38)
        val point12SmoothJoint = Point(1877.12, 1435.89)
        val point13Control = Point(2069.22, 1429.54)
        val point14Control = Point(2160.31, 1476.10)

        val point15FreeJoint = Point(2305.43, 1341.55)
        val point16Control = Point(2267.93, 1108.87)
        val point17Control = Point(2203.85, 790.42)

        val point18FreeJoint = Point(1838.79, 561.24)
        val point19Control = Point(1478.44, 611.55)
        val point20Control = Point(1447.10, 715.54)
        val point21SmoothJoint = Point(1283.83, 758.94)
        val point22Control = Point(1084.05, 812.07)
        val point23Control = Point(997.57, 842.14)

        val svgRoot = SvgRoot(
            children = listOf(
                SvgPath(
                    segments = listOf(
                        SvgPath.Segment.MoveTo(
                            targetPoint = point0FreeJoint,
                        ),
                        SvgPath.Segment.CubicBezierCurveTo(
                            controlPoint1 = point1Control,
                            controlPoint2 = point2Control,
                            finalPoint = point3SmoothJoint,
                        ),
                        SvgPath.Segment.CubicBezierCurveTo(
                            controlPoint1 = point4Control,
                            controlPoint2 = point5Control,
                            finalPoint = point6FreeJoint,
                        ),
                        SvgPath.Segment.CubicBezierCurveTo(
                            controlPoint1 = point7Control,
                            controlPoint2 = point8Control,
                            finalPoint = point9SmoothJoint,
                        ),
                        SvgPath.Segment.CubicBezierCurveTo(
                            controlPoint1 = point10Control,
                            controlPoint2 = point11Control,
                            finalPoint = point12SmoothJoint,
                        ),
                        SvgPath.Segment.CubicBezierCurveTo(
                            controlPoint1 = point13Control,
                            controlPoint2 = point14Control,
                            finalPoint = point15FreeJoint,
                        ),
                        SvgPath.Segment.CubicBezierCurveTo(
                            controlPoint1 = point16Control,
                            controlPoint2 = point17Control,
                            finalPoint = point18FreeJoint,
                        ),
                        SvgPath.Segment.CubicBezierCurveTo(
                            controlPoint1 = point19Control,
                            controlPoint2 = point20Control,
                            finalPoint = point21SmoothJoint,
                        ),
                        SvgPath.Segment.CubicBezierCurveTo(
                            controlPoint1 = point22Control,
                            controlPoint2 = point23Control,
                            finalPoint = point0FreeJoint,
                        ),
                        SvgPath.Segment.ClosePath,
                    ),
                ),
            ),
            width = 4000.mm,
            height = 4000.mm,
        )

        val outline = Outline.loadSvg(
            svgRoot = svgRoot,
            Outline.EdgeMetadataMap(
                edgeMetadataByEdgeIndex = mapOf(),
                defaultEdgeMetadata = defaultEdgeMetadata,
            ),
        )

        assertEqualsWithTolerance(
            expected = Outline(
                cyclicLinks = listOf(
                    Outline.Link(
                        edge = Outline.Edge(
                            startHandle = Outline.Handle(
                                position = point1Control,
                            ),
                            intermediateJoints = listOf(
                                Outline.Joint.Smooth(
                                    rearHandle = Outline.Handle(
                                        position = point2Control,
                                    ),
                                    anchorCoord = OpenCurve.Coord(
                                        t = 0.48335,
                                    ),
                                    frontHandle = Outline.Handle(
                                        position = point4Control,
                                    ),
                                ),
                            ),
                            endHandle = Outline.Handle(
                                position = point5Control,
                            ),
                            metadata = defaultEdgeMetadata,
                        ),
                        endAnchor = Outline.Anchor(
                            position = point6FreeJoint,
                        ),
                    ),
                    Outline.Link(
                        edge = Outline.Edge(
                            startHandle = Outline.Handle(
                                position = point7Control,
                            ),
                            intermediateJoints = listOf(
                                Outline.Joint.Smooth(
                                    rearHandle = Outline.Handle(
                                        position = point8Control,
                                    ),
                                    anchorCoord = OpenCurve.Coord(
                                        t = 0.45566,
                                    ),
                                    frontHandle = Outline.Handle(
                                        position = point10Control,
                                    ),
                                ),
                                Outline.Joint.Smooth(
                                    rearHandle = Outline.Handle(
                                        position = point11Control,
                                    ),
                                    anchorCoord = OpenCurve.Coord(
                                        t = 0.50578,
                                    ),
                                    frontHandle = Outline.Handle(
                                        position = point13Control,
                                    ),
                                ),
                            ),
                            endHandle = Outline.Handle(
                                position = point14Control,
                            ),
                            metadata = defaultEdgeMetadata,
                        ),
                        endAnchor = Outline.Anchor(
                            position = point15FreeJoint,
                        ),
                    ),
                    Outline.Link(
                        edge = Outline.Edge(
                            startHandle = Outline.Handle(
                                position = point16Control,
                            ),
                            intermediateJoints = emptyList(),
                            endHandle = Outline.Handle(
                                position = point17Control,
                            ),
                            metadata = defaultEdgeMetadata,
                        ),
                        endAnchor = Outline.Anchor(
                            position = point18FreeJoint,
                        ),
                    ),
                    Outline.Link(
                        edge = Outline.Edge(
                            startHandle = Outline.Handle(
                                position = point19Control,
                            ),
                            intermediateJoints = listOf(
                                Outline.Joint.Smooth(
                                    rearHandle = Outline.Handle(
                                        position = point20Control,
                                    ),
                                    anchorCoord = OpenCurve.Coord(
                                        t = 0.44971,
                                    ),
                                    frontHandle = Outline.Handle(
                                        position = point22Control,
                                    ),
                                ),
                            ),
                            endHandle = Outline.Handle(
                                position = point23Control,
                            ),
                            metadata = defaultEdgeMetadata,
                        ),
                        endAnchor = Outline.Anchor(
                            position = point0FreeJoint,
                        ),
                    ),
                ),
            ),
            actual = outline,
        )
    }

    @Test
    @Ignore
    fun testCut() {
        val inputOutline = Outline.loadSvg(
            svgRoot = SvgRoot.parse(
                reader = OutlineTests::class.java.getResourceAsReader("outlineCut_input.svg")!!,
            ),
            edgeMetadataMap = Outline.EdgeMetadataMap(
                edgeMetadataByEdgeIndex = emptyMap(),
                defaultEdgeMetadata = defaultEdgeMetadata,
            ),
        )

        val expectedFirstCutOutline = Outline.loadSvg(
            svgRoot = SvgRoot.parse(
                reader = OutlineTests::class.java.getResourceAsReader("outlineCut_output1.svg")!!,
            ),
            edgeMetadataMap = Outline.EdgeMetadataMap(
                edgeMetadataByEdgeIndex = emptyMap(),
                defaultEdgeMetadata = defaultEdgeMetadata,
            ),
        )

        val expectedSecondCutOutline = Outline.loadSvg(
            svgRoot = SvgRoot.parse(
                reader = OutlineTests::class.java.getResourceAsReader("outlineCut_output2.svg")!!,
            ),
            edgeMetadataMap = Outline.EdgeMetadataMap(
                edgeMetadataByEdgeIndex = emptyMap(),
                defaultEdgeMetadata = defaultEdgeMetadata,
            ),
        )

        val cutLine = LineSegment(
            start = Point(92.1, 126.4),
            end = Point(97.3, 143.6),
        )

        val (firstCutOutline, secondCutOutline) = inputOutline.cut(
            cutLineSegment = cutLine, cutEdgeMetadata = Outline.EdgeMetadata(
                seamAllowance = SeamAllowance(6.0),
            )
        )

        assertEqualsWithTolerance(
            expected = expectedFirstCutOutline,
            actual = firstCutOutline,
        )

        assertEqualsWithTolerance(
            expected = expectedSecondCutOutline,
            actual = secondCutOutline,
        )
    }
}
