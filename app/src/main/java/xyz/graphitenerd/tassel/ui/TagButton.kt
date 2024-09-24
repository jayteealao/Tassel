package xyz.graphitenerd.tassel.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class TagButtonParts {
    TEXT,
    SHADOW
}
@OptIn(ExperimentalTextApi::class)
@Composable
fun TB2(value: String, selected: Boolean = false, onSelected: (Boolean) -> Unit, offset: Dp = 2.dp) {

    val animatedOffset = animateDpAsState(
        targetValue = if (selected) offset + 2.dp else offset,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioHighBouncy,
            stiffness = Spring.StiffnessMediumLow
        )
    )

    val textStyle = TextStyle(
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        ),
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center,
            trim = LineHeightStyle.Trim.Both
        )
    )

    Layout(
        content = {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Red)
                    .layoutId(TagButtonParts.SHADOW)
            )
            Text(
                text = value,
                style = LocalTextStyle.current.merge(textStyle),
                modifier = Modifier
                    .border(width = 2.dp, color = Color.Black)
                    .background(color = Color.White)
                    .padding(horizontal = 8.dp, vertical = 2.dp)
                    .layoutId(TagButtonParts.TEXT),
            )
        },
        modifier = Modifier
            .wrapContentSize()
            .clickable { onSelected(!selected) }
    ) { measurables, constraints ->

        val offsetPx = animatedOffset.value.roundToPx()

        val textPlaceable = measurables.first {
                measurable ->
            measurable.layoutId == TagButtonParts.TEXT
        }.measure(constraints)

        val shadowPlaceable = measurables.first {
                measurable ->
            measurable.layoutId == TagButtonParts.SHADOW
        }.measure(Constraints.fixed(textPlaceable.width, textPlaceable.height))

        layout(textPlaceable.width + offsetPx, textPlaceable.height + offsetPx) {
            shadowPlaceable.place(offsetPx, offsetPx, 0f)
            textPlaceable.place(0, 0, 1f)
        }
    }
}

@Preview
@Composable
fun previewTB2() {

    var selected by remember {
        mutableStateOf(false)
    }

    TB2(value = "CSS", selected = selected, onSelected = { selected = !it })
}

