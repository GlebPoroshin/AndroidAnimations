package com.poroshin.gleb.animations.ui.gradient

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import kotlin.math.PI
import kotlin.math.min
import kotlin.math.sin

@Composable
fun ParabolaWaveGradientBox(modifier: Modifier = Modifier) {
    // Бесконечная анимация для непрерывного движения волны и дрейфа
    val inf = rememberInfiniteTransition(label = "parabolaWave")

    // Основной параметр времени 0..1 для управления фазой волны и положением вершины
    val t by inf.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "t",
    )

    // Дополнительный дрейф по горизонтали для придания естественности движению
    val drift by inf.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "drift",
    )

    // Набор цветов для градиента
    val colors = remember {
        arrayOf(
            0.00f to Color(0xFFB08A00),
            0.55f to Color(0xFF1A1A1A),
            0.78f to Color(0xFFE00000),
            1.00f to Color(0xFFF06400),
        )
    }

    Box(
        modifier
            .fillMaxSize()
            .drawWithCache {
                val w = size.width
                val h = size.height
                val cx0 = w * 0.5f

                // Количество вертикальных полос (слайсов) для отрисовки искаженной волны.
                // Чем больше — тем гладче "волна", но выше нагрузка на GPU.
                val slices = 180
                val sliceW = w / slices

                fun lerp(a: Float, b: Float, x: Float) = a + (b - a) * x
                
                // Вспомогательная функция для получения значения синуса в диапазоне [0..1]
                fun sin01(x: Float): Float {
                    val v = sin(x.toDouble()).toFloat()
                    return (v + 1f) * 0.5f
                }

                // Виньетка: мягкое высветление к краям для акцентирования центральной части
                val vignette = Brush.radialGradient(
                    colors = listOf(Color.Transparent, Color.White),
                    center = Offset(w / 2f, h / 2f),
                    radius = min(w, h) * 0.62f,
                )

                onDrawBehind {
                    val twoPi = (2.0 * PI).toFloat()

                    // Положение вершины параболы по вертикали (плавное движение вверх-вниз)
                    val vertexY = lerp(h * 0.18f, h * 0.82f, sin01(twoPi * t))

                    // Горизонтальный центр параболы с легким "дыханием" (колебанием)
                    val cx = cx0 + (w * 0.06f) * sin((twoPi * t * 0.75f).toDouble()).toFloat()

                    // Динамическое изменение ширины ветвей параболы:
                    // Рассчитываем коэффициенты k для широкого и узкого состояний
                    val dropWide = h * 0.18f   // пологий купол
                    val dropNarrow = h * 0.72f // крутой купол
                    val kWide = (4f * dropWide) / (w * w)
                    val kNarrow = (4f * dropNarrow) / (w * w)

                    // Плавный переход между широкой и узкой параболой
                    val k = lerp(kWide, kNarrow, sin01(twoPi * t + twoPi * 0.25f))

                    // Параметры для создания эффекта волны (интерференция двух синусоид)
                    val a1 = h * 0.10f
                    val a2 = h * 0.05f
                    val freq1 = 2.4f
                    val freq2 = 1.3f
                    val phase1 = twoPi * t
                    val phase2 = twoPi * t * 0.7f

                    // Глобальное смещение градиента по X и Y для оживления фона
                    val dx = drift * w * 0.18f
                    val globalDy = (h * 0.08f) * sin((twoPi * t * 0.35f).toDouble()).toFloat()

                    // Общая высота градиентной ленты, достаточная для покрытия всех искажений
                    val span = h * 1.35f

                    // Отрисовка волны через вертикальные слайсы:
                    // Для каждого слайса вычисляем смещение Y на основе параболы и волны
                    for (i in 0 until slices) {
                        val x0 = i * sliceW
                        val x1 = x0 + sliceW
                        val xm = (x0 + x1) * 0.5f

                        // 1. Смещение от параболы (зависит от расстояния до центра cx)
                        val dxm = xm - cx
                        val parabolaY = vertexY - k * dxm * dxm

                        // 2. Смещение от комбинированной волны
                        val wave =
                            a1 * sin((phase1 + (xm / w) * twoPi * freq1).toDouble()).toFloat() +
                                    a2 * sin((phase2 + (xm / w) * twoPi * freq2).toDouble()).toFloat()

                        // Итоговое положение градиента для данного слайса
                        val shiftY = parabolaY + wave + globalDy

                        // Создаем градиентный кисть, центрированную по вычисленной высоте
                        val brush = Brush.linearGradient(
                            colorStops = colors,
                            start = Offset(xm + dx, shiftY - span * 0.5f),
                            end = Offset(xm + dx, shiftY + span * 0.5f),
                        )

                        // Рисуем только текущий вертикальный слайс
                        clipRect(left = x0, top = 0f, right = x1, bottom = h) {
                            drawRect(brush)
                        }
                    }

                    // Наложение виньетки поверх градиента
                    drawRect(vignette)
                }
            },
    )
}