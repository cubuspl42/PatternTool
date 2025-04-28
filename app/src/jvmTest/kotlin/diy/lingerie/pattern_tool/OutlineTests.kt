package diy.lingerie.pattern_tool

import diy.lingerie.geometry.ClosedSplineSvgTests
import diy.lingerie.geometry.Point
import diy.lingerie.simple_dom.svg.SvgRoot
import diy.lingerie.test_utils.assertEqualsWithTolerance
import diy.lingerie.utils.getResourceAsReader
import kotlin.io.path.Path
import kotlin.test.Test

class OutlineTests {
    @Test
    fun testLoadSvg() {
        val reader = ClosedSplineSvgTests::class.java.getResourceAsReader("closedPath1.svg")!!
        val svgRoot = SvgRoot.parse(reader = reader)

        val outline = Outline.loadSvg(svgRoot = svgRoot)

        val expectedEdgeMetadata = Outline.EdgeMetadata(
            seamAllowance = SeamAllowance(allowanceMm = 6.0),
        )

        assertEqualsWithTolerance(
            expected = Outline(
                links = listOf(
                    Outline.Link(
                        startAnchor = Outline.Joint.Anchor(
                            position = Point(32.551998138427734, 125.20800018310547),
                        ),
                        edge = Outline.Edge(
                            startHandle = Outline.Joint.Handle(
                                position = Point(43.84199905395508, 108.19000244140625),
                            ),
                            intermediateJoints = emptyList(),
                            endHandle = Outline.Joint.Handle(
                                position = Point(50.24800109863281, 84.95600128173828),
                            ),
                            metadata = expectedEdgeMetadata,
                        ),
                    ),
                    Outline.Link(
                        startAnchor = Outline.Joint.Anchor(
                            position = Point(65.63800048828125, 72.54399871826172),
                        ),
                        edge = Outline.Edge(
                            startHandle = Outline.Joint.Handle(
                                position = Point(82.27100372314453, 59.12900161743164),
                            ),
                            intermediateJoints = emptyList(),
                            endHandle = Outline.Joint.Handle(
                                position = Point(108.29000091552734, 56.98699951171875),
                            ),
                            metadata = expectedEdgeMetadata,
                        ),
                    ),
                    Outline.Link(
                        startAnchor = Outline.Joint.Anchor(
                            position = Point(131.3769989013672, 50.821998596191406),
                        ),
                        edge = Outline.Edge(
                            startHandle = Outline.Joint.Handle(
                                position = Point(126.60900115966797, 85.4229965209961),
                            ),
                            intermediateJoints = emptyList(),
                            endHandle = Outline.Joint.Handle(
                                position = Point(146.66000366210938, 103.48999786376953),
                            ),
                            metadata = expectedEdgeMetadata,
                        ),
                    ),
                    Outline.Link(
                        startAnchor = Outline.Joint.Anchor(
                            position = Point(181.7220001220703, 111.55500030517578),
                        ),
                        edge = Outline.Edge(
                            startHandle = null,
                            intermediateJoints = emptyList(),
                            endHandle = null,
                            metadata = expectedEdgeMetadata
                        ),
                    ),
                    Outline.Link(
                        startAnchor = Outline.Joint.Anchor(
                            position = Point(131.70599365234375, 177.86399841308594),
                        ),
                        edge = Outline.Edge(
                            startHandle = Outline.Joint.Handle(
                                position = Point(85.72000122070312, 174.21600341796875),
                            ),
                            intermediateJoints = emptyList(),
                            endHandle = Outline.Joint.Handle(
                                position = Point(49.132999420166016, 160.46400451660156),
                            ),
                            metadata = expectedEdgeMetadata,
                        ),
                    ),
                ),
            ),
            actual = outline,
        )
    }

    @Test
    fun testDumpSvg() {
        val edgeMetadata = Outline.EdgeMetadata(
            seamAllowance = SeamAllowance(allowanceMm = 6.0),
        )

        val outline = Outline(
            links = listOf(
                Outline.Link(
                    startAnchor = Outline.Joint.Anchor(
                        position = Point(32.551998138427734, 125.20800018310547),
                    ),
                    edge = Outline.Edge(
                        startHandle = Outline.Joint.Handle(
                            position = Point(43.84199905395508, 108.19000244140625),
                        ),
                        intermediateJoints = emptyList(),
                        endHandle = Outline.Joint.Handle(
                            position = Point(50.24800109863281, 84.95600128173828),
                        ),
                        metadata = edgeMetadata,
                    ),
                ),
                Outline.Link(
                    startAnchor = Outline.Joint.Anchor(
                        position = Point(65.63800048828125, 72.54399871826172),
                    ),
                    edge = Outline.Edge(
                        startHandle = Outline.Joint.Handle(
                            position = Point(82.27100372314453, 59.12900161743164),
                        ),
                        intermediateJoints = emptyList(),
                        endHandle = Outline.Joint.Handle(
                            position = Point(108.29000091552734, 56.98699951171875),
                        ),
                        metadata = edgeMetadata,
                    ),
                ),
                Outline.Link(
                    startAnchor = Outline.Joint.Anchor(
                        position = Point(131.3769989013672, 50.821998596191406),
                    ),
                    edge = Outline.Edge(
                        startHandle = Outline.Joint.Handle(
                            position = Point(126.60900115966797, 85.4229965209961),
                        ),
                        intermediateJoints = emptyList(),
                        endHandle = Outline.Joint.Handle(
                            position = Point(146.66000366210938, 103.48999786376953),
                        ),
                        metadata = edgeMetadata,
                    ),
                ),
                Outline.Link(
                    startAnchor = Outline.Joint.Anchor(
                        position = Point(181.7220001220703, 111.55500030517578),
                    ),
                    edge = Outline.Edge(
                        startHandle = null, intermediateJoints = emptyList(), endHandle = null, metadata = edgeMetadata
                    ),
                ),
                Outline.Link(
                    startAnchor = Outline.Joint.Anchor(
                        position = Point(131.70599365234375, 177.86399841308594),
                    ),
                    edge = Outline.Edge(
                        startHandle = Outline.Joint.Handle(
                            position = Point(85.72000122070312, 174.21600341796875),
                        ),
                        intermediateJoints = emptyList(),
                        endHandle = Outline.Joint.Handle(
                            position = Point(49.132999420166016, 160.46400451660156),
                        ),
                        metadata = edgeMetadata,
                    ),
                ),
            ),
        )

        val svgRoot = outline.dumpSvg()

        svgRoot.writeToFile(
            Path("../output/outlineDumpSvg1.svg")
        )
    }
}
