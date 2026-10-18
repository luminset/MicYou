package com.lanrhyme.micyou

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

// Android平台不使用haze库（需要minSdk 23）
// 这里提供一个空实现，haze效果在Android上将被禁用

actual class HazeState

@Composable
actual fun rememberHazeState(): HazeState? {
    // Android上返回null，禁用haze效果
    return null
}

@Composable
actual fun HazeCard(
    hazeState: HazeState?,
    enabled: Boolean,
    modifier: Modifier,
    hazeColor: Color,
    content: @Composable () -> Unit
) {
    // Android上不使用haze效果，只显示透明度
    Box(modifier = modifier) {
        content()
    }
}

@Composable
actual fun HazeSurface(
    hazeState: HazeState?,
    enabled: Boolean,
    modifier: Modifier,
    shape: Shape,
    color: Color,
    hazeColor: Color,
    content: @Composable () -> Unit
) {
    // Android上不使用haze效果，只显示透明背景
    Box(
        modifier = modifier
            .clip(shape)
            .background(color)
    ) {
        content()
    }
}