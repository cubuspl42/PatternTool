package dev.toolkt.dom.reactive.style

import dev.toolkt.dom.pure.PureColor
import dev.toolkt.dom.pure.PureDimension
import dev.toolkt.dom.pure.style.PureBorderStyle
import dev.toolkt.dom.pure.style.PureBoxSizing
import dev.toolkt.dom.pure.style.PureDisplayStyle
import dev.toolkt.dom.pure.style.PureFill
import dev.toolkt.dom.pure.style.PureFlexItemStyle
import dev.toolkt.dom.pure.style.PurePointerEvents
import dev.toolkt.dom.pure.style.PurePosition
import dev.toolkt.dom.pure.style.PurePropertyKind
import dev.toolkt.dom.pure.style.PureStrokeStyle
import dev.toolkt.dom.pure.style.PureTextAlign
import dev.toolkt.dom.pure.style.PureVerticalAlign
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.managed_io.Actions
import dev.toolkt.reactive.managed_io.Trigger
import dev.toolkt.reactive.managed_io.Triggers
import org.w3c.dom.css.CSSStyleDeclaration

data class ReactiveStyle(
    val flexItemStyle: PureFlexItemStyle? = null,
    val displayStyle: Cell<PureDisplayStyle>? = null,
    val width: Cell<PureDimension<*>>? = null,
    val height: Cell<PureDimension<*>>? = null,
    val minWidth: Cell<PureDimension<*>>? = null,
    val minHeight: Cell<PureDimension<*>>? = null,
    val backgroundColor: Cell<PureColor>? = null,
    val textAlign: Cell<PureTextAlign>? = null,
    val verticalAlign: Cell<PureVerticalAlign>? = null,
    val borderStyle: PureBorderStyle? = null,
    val boxSizing: PureBoxSizing? = null,
    val fill: Cell<PureFill>? = null,
    val strokeStyle: PureStrokeStyle? = null,
    val pointerEvents: Cell<PurePointerEvents>? = null,
    val padding: PureEdgeInsets? = null,
    val margin: PureEdgeInsets? = null,
    val position: PurePosition? = null,
    val inset: PureInset? = null,
) {
    companion object {
        val Default = ReactiveStyle()
    }

    fun bind(
        styleDeclaration: CSSStyleDeclaration,
    ): Trigger = Trigger.prepared {
        Actions.mutate {
            flexItemStyle?.applyTo(
                styleDeclaration = styleDeclaration,
            )

            borderStyle?.applyTo(
                styleDeclaration = styleDeclaration,
            )

            boxSizing?.applyTo(
                styleDeclaration = styleDeclaration,
                kind = PurePropertyKind.BoxSizing,
            )

            strokeStyle?.applyTo(
                styleDeclaration = styleDeclaration,
            )

            margin?.applyProperties(
                insetKind = PureEdgeInsets.InsetKind.Margin,
                applier = StyleDeclarationApplier(
                    styleDeclaration = styleDeclaration,
                ),
            )

            padding?.applyProperties(
                insetKind = PureEdgeInsets.InsetKind.Padding,
                applier = StyleDeclarationApplier(
                    styleDeclaration = styleDeclaration,
                ),
            )

            position?.applyTo(
                styleDeclaration = styleDeclaration,
                kind = PurePropertyKind.Position,
            )

            inset?.applyProperties(
                applier = StyleDeclarationApplier(
                    styleDeclaration = styleDeclaration,
                ),
            )
        }

        Triggers.combine(
            triggers = listOfNotNull(
                displayStyle?.bind(
                    styleDeclaration = styleDeclaration,
                ),
                width?.bind(
                    styleDeclaration = styleDeclaration,
                    kind = PurePropertyKind.Width,
                ),
                height?.bind(
                    styleDeclaration = styleDeclaration,
                    kind = PurePropertyKind.Height,
                ),
                minWidth?.bind(
                    styleDeclaration = styleDeclaration,
                    kind = PurePropertyKind.MinWidth,
                ),
                minHeight?.bind(
                    styleDeclaration = styleDeclaration,
                    kind = PurePropertyKind.MinHeight,
                ),
                backgroundColor?.bind(
                    styleDeclaration = styleDeclaration,
                    kind = PurePropertyKind.BackgroundColor,
                ),
                textAlign?.bind(
                    styleDeclaration = styleDeclaration,
                    kind = PurePropertyKind.TextAlign,
                ),
                verticalAlign?.bind(
                    styleDeclaration = styleDeclaration,
                    kind = PurePropertyKind.VerticalAlign,
                ),
                fill?.bind(
                    styleDeclaration = styleDeclaration,
                    kind = PurePropertyKind.Fill,
                ),
                pointerEvents?.bind(
                    styleDeclaration = styleDeclaration,
                    kind = PurePropertyKind.PointerEvents,
                ),
            ),
        )
    }
}
