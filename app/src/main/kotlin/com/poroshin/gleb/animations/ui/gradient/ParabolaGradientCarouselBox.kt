package com.poroshin.gleb.animations.ui.gradient

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import kotlinx.coroutines.isActive
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ParabolaGradientCarouselBox(
    modifier: Modifier = Modifier
) {
    val palettes = remember {
        listOf(
            listOf(Color(0xFFFFFFFF), Color(0xFFFFD8A0), Color(0xFFFF7A00), Color(0xFF7A1400)),
            listOf(Color(0xFFFFFFFF), Color(0xFFEFE7B0), Color(0xFF6A6A00), Color(0xFF1A1A1A)),
            listOf(Color(0xFFFFFFFF), Color(0xFFFFE9B8), Color(0xFFFF3D00), Color(0xFF4A0A00)),
            listOf(Color(0xFFFFFFFF), Color(0xFFD7F0FF), Color(0xFF40C4FF), Color(0xFF003A66)),
            listOf(Color(0xFFFFFFFF), Color(0xFFE7E1FF), Color(0xFF7C4DFF), Color(0xFF1A0033)),
            listOf(Color(0xFFFFFFFF), Color(0xFFDFF7E8), Color(0xFF69F0AE), Color(0xFF003319)),
        )
    }

    fun lerp(a: Float, b: Float, t: Float) = a + (b - a) * t
    fun clamp01(x: Float) = x.coerceIn(0f, 1f)

    // Сглаживание внутри фазы для более мягкого ускорения/замедления
    fun smoothstep01(x: Float): Float {
        val t = clamp01(x)
        return t * t * (3f - 2f * t)
    }

    // Индекс текущей палитры; следующая вычисляется как (curIdx + 1) % size
    var curIdx by remember { mutableIntStateOf(0) }

    // Прогресс прохода параболы по всему пути: от 0 до 1 (вершина проходит весь путь сверху вниз)
    val sweep = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // кривая для всего прохода, можно настроить на разных участках разную скорость при необходимости
        val easing = CubicBezierEasing(0.22f, 0.12f, 0.18f, 1f)

        while (isActive) {
            // Начало прохода: текущий градиент полностью виден
            sweep.snapTo(0f)

            // Проход: следующий градиент раскрывается маской в виде параболы сверху вниз
            sweep.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 5600, easing = easing)
            )

            // Конец прохода: следующий градиент полностью закрыл круг.
            // Сдвиг индекса делает получившийся градиент стартовым для следующего прохода.
            // В следующей итерации sweep снова станет 0 — кадр будет визуально эквивалентен концу предыдущеей итерации.
            curIdx = (curIdx + 1) % palettes.size
        }
    }

    Box(
        modifier
            .fillMaxSize()
            .drawWithCache {
                val w = size.width
                val h = size.height

                // Круглая область заливки
                val r = minOf(w, h) * 0.42f
                val center = Offset(w * 0.5f, h * 0.5f)
                val left = center.x - r
                val top = center.y - r
                val right = center.x + r
                val bottom = center.y + r

                val circlePath = Path().apply {
                    addOval(Rect(left, top, right, bottom))
                }

                // Маска строится по X-слайсам:
                val slices = 280
                val sliceW = (2f * r) / slices

                // Feather-эффект для мягкого перехода ниже границы
                val featherPx = maxOf(18f, r * 0.085f)
                val featherSteps = 9

                // Glow-эффект: полупрозрачное свечение вокруг границы маски (без blur)
                // glowUp/glowDown задают толщину свечения вверх/вниз от границы
                val glowUp = maxOf(18f, r * 0.10f)
                val glowDown = maxOf(26f, r * 0.16f)
                val glowSteps = 12
                val glowMaxAlpha = 0.22f

                // Высветление краёв к белому (без blur)
                val vignette = Brush.radialGradient(
                    colors = listOf(Color.Transparent, Color.White),
                    center = center,
                    radius = r * 1.28f
                )

                onDrawBehind {
                    // Текущая и следующая палитры
                    val cur = palettes[curIdx]
                    val nxt = palettes[(curIdx + 1) % palettes.size]

                    // u = 0..1 — единый параметр прогресса прохода
                    val u = sweep.value

                    // Дрейф направления/положения градиента делается детерминированно от u:
                    // sin(2πu) и cos(2πu) периодичны, поэтому значения на u=0 и u=1 совпадают.
                    // Это устраняет шов по направлению градиента между концом и началом соседних проходов.
                    val twoPi = (2.0 * Math.PI).toFloat()
                    val driftX = sin((twoPi * u).toDouble()).toFloat()
                    val driftY = cos((twoPi * u).toDouble()).toFloat()

                    val dx = driftX * r * 0.16f
                    val dy = driftY * r * 0.10f

                    // Текущий градиент (фон)
                    val curBrush = Brush.linearGradient(
                        colors = cur,
                        start = Offset(left + dx, top + dy),
                        end = Offset(right + dx, bottom + dy)
                    )

                    // Следующий градиент (накрывающий, раскрывается маской)
                    val nxtBrush = Brush.linearGradient(
                        colors = nxt,
                        start = Offset(left + dx, top + dy),
                        end = Offset(right + dx, bottom + dy)
                    )

                    // Фазирование k (0..2) и движения вершины. k - коэффицент отвечающий за ширину ветвей параболы.
                    // Чем больше k - тем уже порабола
                    // 1) k от 0 до 0.66, вершина в y=0, вершина не движется, парабола сужается
                    // 2) k от 0.66 до 1.4, вершина проходит 70% пути
                    // 3) k от 1.4 -до 2, вершина доходит до конца пути
                    //
                    // Параллельно с ростом k меняется halfWidth: чтобы концы параболы тоже опускались
                    // halfWidth уменьшается => парабола становится "уже" (ветви сходятся).
                    val p1End = 0.22f
                    val p2End = 0.82f

                    // Путь вершины по Y в локальной системе круга [0..2r] с запасом
                    val pathLen = 2f * r + 0.35f * r

                    val k: Float
                    val vY: Float
                    val halfWidth: Float

                    val hwWide = r * 1.05f
                    val hwNarrow = r * 0.42f

                    when {
                        u <= p1End -> {
                            val t = smoothstep01(u / p1End)
                            k = lerp(0f, 0.66f, t)
                            vY = 0f
                            halfWidth = lerp(hwWide, lerp(hwWide, hwNarrow, 0.45f), t)
                        }

                        u <= p2End -> {
                            val t = smoothstep01((u - p1End) / (p2End - p1End))
                            k = lerp(0.66f, 1.4f, t)
                            vY = lerp(0f, 0.70f * pathLen, t)
                            halfWidth = lerp(
                                lerp(hwWide, hwNarrow, 0.45f),
                                lerp(hwWide, hwNarrow, 0.85f),
                                t
                            )
                        }

                        else -> {
                            val t = smoothstep01((u - p2End) / (1f - p2End))
                            k = lerp(1.4f, 2.0f, t)
                            vY = lerp(0.70f * pathLen, pathLen, t)
                            halfWidth = lerp(lerp(hwWide, hwNarrow, 0.85f), hwNarrow, t)
                        }
                    }

                    // Геометрия маски
                    // Используется U-образная перевернутая парабола (∪):
                    // boundaryY(x) = vY + a * dx^2
                    //
                    // vY задаёт положение вершины по Y (двигается сверху вниз),
                    // halfWidth задаёт "сужение".
                    // k влияет на "глубину" ветвей через edgeDrop.
                    val k01 = (k / 2f).coerceIn(0f, 1f)
                    val edgeDrop = lerp(r * 0.10f, r * 0.78f, k01)
                    val a = edgeDrop / (halfWidth * halfWidth)

                    fun boundaryY(localX: Float): Float {
                        val dxLocal = (localX - r) // [-r..r] в локальных координатах круга
                        val y = vY + a * dxLocal * dxLocal
                        return y.coerceIn(0f, 2f * r)
                    }

                    // Отрисовка
                    // 1) Текущий градиент рисуется целиком внутри круга.
                    // 2) Следующий градиент рисуется поверх только в области y <= boundaryY(x),
                    //    то есть раскрывается сверху вниз по параболической границе.
                    clipPath(circlePath) {
                        drawRect(curBrush)

                        for (i in 0 until slices) {
                            val lx0 = i * sliceW
                            val lx1 = lx0 + sliceW
                            val lxm = (lx0 + lx1) * 0.5f

                            val y = boundaryY(lxm)
                            if (y <= 0.5f) continue

                            val x0 = left + lx0
                            val x1 = left + lx1
                            val y0 = top
                            val y1 = top + y

                            // Основная маска
                            clipRect(left = x0, top = y0, right = x1, bottom = y1) {
                                drawRect(nxtBrush)
                            }

                            // Feather: смягчение перехода ниже границы
                            val step = featherPx / featherSteps
                            for (s in 1..featherSteps) {
                                val alpha = (s / featherSteps.toFloat()) * 0.16f
                                clipRect(
                                    left = x0,
                                    top = y0,
                                    right = x1,
                                    bottom = (y1 + s * step).coerceAtMost(bottom)
                                ) {
                                    drawRect(nxtBrush, alpha = alpha)
                                }
                            }

                            // Glow: полупрозрачное свечение вокруг границы маски
                            // Для каждого шага берется полоса вокруг y1:
                            // [y1 - glowUp*t .. y1 + glowDown*t], alpha спадает квадратично (мягкий falloff)
                            for (g in 1..glowSteps) {
                                val tGlow = g / glowSteps.toFloat()
                                val falloff = (1f - tGlow)
                                val alpha = (falloff * falloff) * glowMaxAlpha

                                val yTopGlow = (y1 - glowUp * tGlow).coerceAtLeast(top)
                                val yBotGlow = (y1 + glowDown * tGlow).coerceAtMost(bottom)

                                clipRect(left = x0, top = yTopGlow, right = x1, bottom = yBotGlow) {
                                    drawRect(nxtBrush, alpha = alpha)
                                }
                            }
                        }
                    }

                    // Высветление краёв за пределами круга
                    // drawRect(vignette)
                }
            }
    )
}