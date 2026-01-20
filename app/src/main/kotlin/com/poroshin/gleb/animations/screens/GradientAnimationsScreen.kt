package com.poroshin.gleb.animations.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.poroshin.gleb.animations.ui.gradient.ParabolaGradientCarouselBox
import com.poroshin.gleb.animations.ui.gradient.ParabolaWaveGradientBox
import com.poroshin.gleb.animations.ui.gradient.RotatingLinearGradientBox
import com.poroshin.gleb.animations.ui.gradient.VerticalMovingLinearGradientBox
import com.poroshin.gleb.animations.ui.theme.AnimationsTheme

@Composable
fun GradientAnimationsScreen(modifier: Modifier = Modifier) {
    val animations = remember {
        listOf<@Composable () -> Unit>(
            { RotatingLinearGradientBox() },
            { VerticalMovingLinearGradientBox() },
            { ParabolaWaveGradientBox() },
            { ParabolaGradientCarouselBox(Modifier.blur(16.dp)) }
        )
    }

    val titles = remember {
        listOf(
            "1) Вращающийся линейный градиент",
            "2) Линейный вертикальный",
            "3) Синусоид",
            "4) Параболоид"
        )
    }

    val subtitles = remember {
        listOf(
            "LinearGradient: направление крутится вокруг центра",
            "сдвиг по Y (вверх-вниз)",
            "Циклические колебание синусоида",
            "Изменение коэффицента k, отвечающего за скорость роста ветвей (шире - уже), а также вершины параболы."
        )
    }

    val pagerState = rememberPagerState(pageCount = { animations.size })

    Box(modifier = modifier.fillMaxSize()) {
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
        ) { page ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = titles[page],
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Text(
                        text = subtitles[page],
                        color = Color(0xFF9AA4AF),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        animations[page]()
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(animations.size) { iteration ->
                val color = if (pagerState.currentPage == iteration) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                }
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }

        if (pagerState.currentPage == 0 && !pagerState.isScrollInProgress) {
            val infiniteTransition = rememberInfiniteTransition(label = "hint")
            val translateY by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 15f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "translateY"
            )

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
                    .alpha(0.6f),
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp)
                        .padding(top = translateY.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 420, heightDp = 980)
@Composable
private fun GradientAnimationsScreenPreview() {
    AnimationsTheme {
        Surface {
            GradientAnimationsScreen()
        }
    }
}
