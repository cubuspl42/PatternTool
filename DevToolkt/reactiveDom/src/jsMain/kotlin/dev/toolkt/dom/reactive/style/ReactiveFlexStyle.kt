package dev.toolkt.dom.reactive.style

import dev.toolkt.dom.pure.PureDimension
import dev.toolkt.dom.pure.style.PureDisplayInsideType
import dev.toolkt.dom.pure.style.PureDisplayOutsideType
import dev.toolkt.dom.pure.style.PureFlexAlignItems
import dev.toolkt.dom.pure.style.PureFlexDirection
import dev.toolkt.dom.pure.style.PureFlexJustifyContent
import dev.toolkt.dom.reactive.utils.gap
import dev.toolkt.reactive.Subscription
import org.w3c.dom.css.CSSStyleDeclaration

data class ReactiveFlexStyle(
    override val outsideType: PureDisplayOutsideType? = null,
    val direction: PureFlexDirection? = null,
    val alignItems: PureFlexAlignItems? = null,
    val justifyContent: PureFlexJustifyContent? = null,
    val gap: PureDimension<*>? = null,
) : ReactiveDisplayStyle() {
    override val insideType: PureDisplayInsideType = PureDisplayInsideType.Flex

    override fun bind(
        styleDeclaration: CSSStyleDeclaration,
    ): Subscription {
        direction?.let {
            styleDeclaration.flexDirection = it.cssValue
        }

        alignItems?.let {
            styleDeclaration.alignItems = it.cssString
        }

        justifyContent?.let {
            styleDeclaration.justifyContent = it.cssValue
        }

        gap?.let {
            styleDeclaration.gap = it.toDimensionString()
        }

        return object : Subscription {
            override fun cancel() {
                styleDeclaration.apply {
                    flexDirection = ""
                    alignItems = ""
                    justifyContent = ""
                    gap = ""
                }
            }
        }
    }
}
