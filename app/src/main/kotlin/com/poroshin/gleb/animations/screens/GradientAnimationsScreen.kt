import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    LazyColumn(
        modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            GradientCard(
                title = "1) Вращающийся линейный градиент",
                subtitle = "LinearGradient: направление крутится вокруг центра",
            ) { RotatingLinearGradientBox() }
        }

        item {
            GradientCard(
                title = "2) Линейный вертикальный",
                subtitle = "сдвиг по Y (вверх-вниз)",
            ) { VerticalMovingLinearGradientBox() }
        }

        item {
            GradientCard(
                title = "3) Синусоид",
                subtitle = "Циклические колебание синусоида",
            ) { ParabolaWaveGradientBox() }
        }
        item {
            GradientCard(
                title = "4) Параболоид",
                subtitle = "Изменение коэффицента k,  отвечающего за скорость роста ветвей (шире - уже), а также вершины параболы. Одна ула, вторая началась. \n 0 <=k<=2 ",
            ) {
                ParabolaGradientCarouselBox(
                    Modifier.blur(16.dp)
                )
            }
        }
    }
}

@Composable
private fun GradientCard(
    title: String,
    subtitle: String,
    content: @Composable BoxScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, fontWeight = FontWeight.SemiBold)
        Text(subtitle, color = Color(0xFF9AA4AF))

        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            content()
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
