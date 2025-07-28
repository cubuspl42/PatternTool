package diy.lingerie.web_tool_3d

import dev.toolkt.dom.pure.PureColor
import dev.toolkt.dom.pure.PureUnit
import dev.toolkt.dom.pure.px
import dev.toolkt.dom.pure.style.PureBorderStyle
import dev.toolkt.dom.pure.style.PureBoxSizing
import dev.toolkt.dom.pure.style.PureFlexDirection
import dev.toolkt.dom.pure.style.PureFlexStyle
import dev.toolkt.dom.reactive.style.ReactiveStyle
import dev.toolkt.dom.reactive.utils.html.createReactiveHtmlDivElement
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.reactive_list.ReactiveList
import diy.lingerie.web_tool_3d.application_state.DocumentState
import kotlinx.browser.document
import org.w3c.dom.HTMLDivElement

fun createRootElement(
    documentState: DocumentState,
): HTMLDivElement = document.createReactiveHtmlDivElement(
    style = ReactiveStyle(
        displayStyle = Cell.Companion.of(
            PureFlexStyle(
                direction = PureFlexDirection.Column,
            ),
        ),
        boxSizing = PureBoxSizing.BorderBox,
        borderStyle = PureBorderStyle(
            width = 4.px,
            color = PureColor.Companion.darkGray,
            style = PureBorderStyle.Style.Solid,
        ),
        width = Cell.Companion.of(PureUnit.Vw.full),
        height = Cell.Companion.of(PureUnit.Vh.full),
        backgroundColor = Cell.Companion.of(PureColor.Companion.lightGray),
    ),
    children = ReactiveList.Companion.of(
        createRendererElement(
            documentState = documentState,
        ),
    ),
)
