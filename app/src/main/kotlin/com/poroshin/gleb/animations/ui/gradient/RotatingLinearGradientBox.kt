package com.poroshin.gleb.animations.ui.gradient

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

@Composable
fun RotatingLinearGradientBox() {
    // Бесконечный цикл анимации для вращения градиента
    val inf = rememberInfiniteTransition(label = "rotLinear")
    
    // Угол поворота от 0 до 360 градусов
    val deg by inf.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(animation = tween(2200, easing = LinearEasing)),
        label = "deg",
    )

    Box(
        Modifier
            .fillMaxSize()
            .drawWithCache {
                val w = size.width
                val h = size.height
                val cx = w / 2f
                val cy = h / 2f
                
                // Половина диагонали прямоугольника, чтобы градиент всегда перекрывал всю область при вращении
                val halfDiag = hypot(w, h) / 2f

                // Перевод градусов в радианы для вычисления вектора направления
                val rad = (deg * Math.PI / 180.0).toFloat()
                val vx = cos(rad)
                val vy = sin(rad)

                // Вычисление начальной и конечной точек градиента симметрично относительно центра
                val start = Offset(cx - vx * halfDiag, cy - vy * halfDiag)
                val end = Offset(cx + vx * halfDiag, cy + vy * halfDiag)

                // Создание линейного градиента с тремя яркими цветами
                val brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFF5252),
                        Color(0xFF40C4FF),
                        Color(0xFF69F0AE),
                    ),
                    start = start,
                    end = end,
                )

                // Отрисовка прямоугольника с градиентной заливкой
                onDrawBehind { drawRect(brush) }
            },
    )
}