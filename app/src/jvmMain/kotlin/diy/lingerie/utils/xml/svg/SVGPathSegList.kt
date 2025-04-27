package diy.lingerie.utils.xml.svg

import diy.lingerie.geometry.Point
import org.w3c.dom.DOMException
import org.w3c.dom.svg.SVGPathSeg
import org.w3c.dom.svg.SVGPathSegCurvetoCubicAbs
import org.w3c.dom.svg.SVGPathSegLinetoAbs
import org.w3c.dom.svg.SVGPathSegList
import org.w3c.dom.svg.SVGPathSegMovetoAbs

fun SVGPathSegList.asList(): List<SVGPathSeg> = object : AbstractList<SVGPathSeg>() {
    override val size: Int
        get() = numberOfItems

    override fun get(
        index: Int,
    ): SVGPathSeg {
        try {
            return getItem(index)
        } catch (e: DOMException) {
            throw IndexOutOfBoundsException()
        }
    }
}

val SVGPathSeg.asSVGPathSegMovetoAbs: SVGPathSegMovetoAbs?
    get() = when {
        pathSegType == SVGPathSeg.PATHSEG_MOVETO_ABS -> this as SVGPathSegMovetoAbs
        else -> null
    }

val SVGPathSeg.asSVGPathSegLinetoAbs: SVGPathSegLinetoAbs?
    get() = when {
        pathSegType == SVGPathSeg.PATHSEG_LINETO_ABS -> this as SVGPathSegLinetoAbs
        else -> null
    }

val SVGPathSeg.asSVGPathSegCurvetoCubicAbs: SVGPathSegCurvetoCubicAbs?
    get() = when {
        pathSegType == SVGPathSeg.PATHSEG_CURVETO_CUBIC_ABS -> this as SVGPathSegCurvetoCubicAbs
        else -> null
    }

val SVGPathSegMovetoAbs.p: Point
    get() = Point(
        x.toDouble(),
        y.toDouble(),
    )

val SVGPathSegLinetoAbs.p: Point
    get() = Point(
        x.toDouble(),
        y.toDouble(),
    )

val SVGPathSegCurvetoCubicAbs.p: Point
    get() = Point(
        x.toDouble(),
        y.toDouble(),
    )

val SVGPathSegCurvetoCubicAbs.p1: Point
    get() = Point(
        x1.toDouble(),
        y1.toDouble(),
    )

val SVGPathSegCurvetoCubicAbs.p2: Point
    get() = Point(
        x2.toDouble(),
        y2.toDouble(),
    )
