package dev.alexjl.samazama.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.text.NumberFormat

@Composable
internal fun BookCount(
    @StringRes labelRes: Int,
    count: Int?,
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    skeletonAlpha: (() -> Float)? = null
) {
    Column(modifier = modifier, horizontalAlignment = horizontalAlignment) {
        val countStyle = MaterialTheme.typography.titleLarge.copy(fontFeatureSettings = "tnum")
        if (skeletonAlpha != null) {
            TextSkeleton(style = countStyle, width = 40.dp, alpha = skeletonAlpha)
        } else {
            Text(
                text = count?.let { NumberFormat.getIntegerInstance().format(it) } ?: "–",
                style = countStyle,
                maxLines = 1
            )
        }
        Text(
            text = stringResource(labelRes),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelMedium,
            textAlign = if (horizontalAlignment == Alignment.CenterHorizontally) {
                TextAlign.Center
            } else {
                TextAlign.Start
            },
            overflow = TextOverflow.Ellipsis,
            maxLines = 2
        )
    }
}

@Composable
internal fun TextSkeleton(
    style: TextStyle,
    width: Dp,
    alpha: () -> Float,
    modifier: Modifier = Modifier,
    minLines: Int = 1
) {
    val color = MaterialTheme.colorScheme.surfaceVariant
    Text(
        text = " ",
        modifier = modifier
            .width(width)
            .graphicsLayer { this.alpha = alpha() }
            .drawBehind {
                val lineHeight = size.height / minLines
                drawRoundRect(
                    color = color,
                    topLeft = Offset(0f, lineHeight / 5),
                    size = Size(size.width, lineHeight * 3 / 5),
                    cornerRadius = CornerRadius(4.dp.toPx())
                )
            },
        color = Color.Transparent,
        style = style,
        minLines = minLines
    )
}
