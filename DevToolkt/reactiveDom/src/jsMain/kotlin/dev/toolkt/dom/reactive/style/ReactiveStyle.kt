package dev.toolkt.dom.reactive.style

import dev.toolkt.dom.pure.PureColor
import dev.toolkt.dom.pure.PureDimension
import dev.toolkt.dom.pure.style.PureBorderStyle
import dev.toolkt.dom.pure.style.PureBoxSizing
import dev.toolkt.dom.pure.style.PureDisplayStyle
import dev.toolkt.dom.pure.style.PureFill
import dev.toolkt.dom.pure.style.PurePropertyKind
import dev.toolkt.dom.pure.style.PurePropertyValue
import dev.toolkt.dom.pure.style.PureStrokeStyle
import dev.toolkt.dom.pure.style.PureTextAlign
import dev.toolkt.dom.pure.style.PureVerticalAlign
import dev.toolkt.reactive.cell.Cell
import org.w3c.dom.css.CSSStyleDeclaration

data class ReactiveStyle(
    val displayStyle: Cell<PureDisplayStyle>? = null,
    val width: Cell<PureDimension<*>>? = null,
    val height: Cell<PureDimension<*>>? = null,
    val backgroundColor: Cell<PureColor>? = null,
    val margin: Cell<PurePropertyValue.Dynamic>? = null,
    val textAlign: Cell<PureTextAlign>? = null,
    val verticalAlign: Cell<PureVerticalAlign>? = null,
    val borderStyle: PureBorderStyle? = null,
    val boxSizing: PureBoxSizing? = null,
    val fill: Cell<PureFill>? = null,
    val strokeStyle: PureStrokeStyle? = null,
) {
    companion object {
        val Default = ReactiveStyle()
    }

    fun bind(
        styleDeclaration: CSSStyleDeclaration,
    ) {
        displayStyle?.bind(
            styleDeclaration = styleDeclaration,
        )

        width?.bind(
            styleDeclaration = styleDeclaration,
            kind = PurePropertyKind.Width,
        )

        height?.bind(
            styleDeclaration = styleDeclaration,
            kind = PurePropertyKind.Height,
        )

        backgroundColor?.bind(
            styleDeclaration = styleDeclaration,
            kind = PurePropertyKind.BackgroundColor,
        )

        margin?.bind(
            styleDeclaration = styleDeclaration,
            kind = PurePropertyKind.Margin,
        )

        textAlign?.bind(
            styleDeclaration = styleDeclaration,
            kind = PurePropertyKind.TextAlign,
        )

        verticalAlign?.bind(
            styleDeclaration = styleDeclaration,
            kind = PurePropertyKind.VerticalAlign,
        )

        borderStyle?.applyTo(
            styleDeclaration = styleDeclaration,
        )

        boxSizing?.applyTo(
            styleDeclaration = styleDeclaration,
            kind = PurePropertyKind.BoxSizing,
        )

        fill?.bind(
            styleDeclaration = styleDeclaration,
            kind = PurePropertyKind.Fill,
        )

        strokeStyle?.applyTo(
            styleDeclaration = styleDeclaration,
        )
    }
}
