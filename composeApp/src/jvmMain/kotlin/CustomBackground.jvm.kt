package com.lanrhyme.micyou

import androidx.compose.foundation.Box
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource

// JVM平台使用真正的haze库实现

actual typealias HazeState = dev.chrisbanes.haze.HazeState

@Composable
actual fun rememberHazeState(): HazeState? {
    return remember { HazeState() }
}

@Composable
actual fun HazeCard(
    hazeState: HazeState?,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    hazeColor: Color = Color.White.copy(alpha = 0.7f),
    content: @Composable () -> Unit
) {
    if (enabled && hazeState != null) {
        Box(
            modifier = modifier.hazeEffect(
                state = hazeState,
                style = HazeStyle(
                    backgroundColor = hazeColor,
                    tints = listOf(HazeTint(color = hazeColor))
                )
            )
        ) {
            content()
        }
    } else {
        Box(modifier = modifier) {
            content()
        }
    }
}

@Composable
actual fun HazeSurface(
    hazeState: HazeState?,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    color: Color = Color.Transparent,
    hazeColor: Color = Color.White.copy(alpha = 0.7f),
    content: @Composable () -> Unit
) {
    if (enabled && hazeState != null) {
        Box(
            modifier = modifier
                .clip(shape)
                .hazeEffect(
                    state = hazeState,
                    style = HazeStyle(
                        backgroundColor = hazeColor,
                        tints = listOf(HazeTint(color = hazeColor))
                    )
                )
        ) {
            content()
        }
    } else {
        Box(
            modifier = modifier
                .clip(shape)
                .background(color)
        ) {
            content()
        }
    }
}