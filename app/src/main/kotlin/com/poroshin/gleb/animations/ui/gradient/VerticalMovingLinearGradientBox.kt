package com.poroshin.gleb.animations.ui.gradient

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode.*
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.unit.dp

@Composable
fun VerticalMovingLinearGradientBox() {
    val inf = rememberInfiniteTransition(label = "vertMove")
    
    // Параметр t меняется от -1 до 1, определяя вертикальный сдвиг градиента
    val t by inf.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = Restart,
        ),
        label = "t",
    )

    // Список цветов для градиента
    val colors = listOf(
        Color(0xFFB08A00),
        Color(0xFF1A1A1A),
        Color(0xFFE00000),
        Color(0xFFF06400),
    )

    Box(
        Modifier
            .fillMaxSize()
            // Размытие всей области для создания мягких переходов между цветами
            .blur(16.dp, BlurredEdgeTreatment.Unbounded)
            .drawWithCache {
                val w = size.width
                val h = size.height

                // Сдвигаем на 55% высоты экрана для плавного зацикливания при TileMode.Repeated.
                val dy = t * h * 0.55f

                // TileMode.Repeated позволяет градиенту бесконечно повторяться при смещении.
                val brush = Brush.linearGradient(
                    colors = colors,
                    start = Offset(w * 0.5f, 0f + dy),
                    end = Offset(w * 0.5f, h + dy),
                    tileMode = TileMode.Repeated,
                )

                // Отрисовка фона с использованием анимированного градиента
                onDrawBehind { drawRect(brush) }
            },
    )
}