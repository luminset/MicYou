package com.lanrhyme.micyou

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

// 使用expect/actual模式支持不同平台的haze效果
expect class HazeState

@Composable
expect fun rememberHazeState(): HazeState?

@Composable
fun CustomBackground(
    settings: BackgroundSettings,
    modifier: Modifier = Modifier,
    hazeState: HazeState? = null,
    forcePureBlackBackground: Boolean = false
) {
    if (forcePureBlackBackground) {
        Box(
            modifier = modifier.background(Color.Black)
        )
        return
    }

    if (!settings.hasCustomBackground) {
        return
    }

    val imageBitmap = remember(settings.imagePath) {
        loadImageBitmap(settings.imagePath)
    }

    if (imageBitmap != null) {
        Box(modifier = modifier) {
            Image(
                bitmap = imageBitmap,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(radius = settings.blurRadius.dp),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 1f - settings.brightness))
            )
        }
    }
}

@Composable
expect fun HazeCard(
    hazeState: HazeState?,
    enabled: Boolean,
    modifier: Modifier,
    hazeColor: Color,
    content: @Composable () -> Unit
)

@Composable
expect fun HazeSurface(
    hazeState: HazeState?,
    enabled: Boolean,
    modifier: Modifier,
    shape: Shape,
    color: Color,
    hazeColor: Color = color,
    content: @Composable () -> Unit
)

@Composable
fun CardWithOpacity(
    opacity: Float,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.graphicsLayer { alpha = opacity }) {
        content()
    }
}

expect fun loadImageBitmap(path: String): ImageBitmap?
