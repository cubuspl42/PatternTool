package diy.lingerie.web_tool_3d.application_state

import kotlin.time.Duration
import kotlin.time.DurationUnit

val Duration.inSeconds: Double
    get() = toDouble(DurationUnit.SECONDS)
