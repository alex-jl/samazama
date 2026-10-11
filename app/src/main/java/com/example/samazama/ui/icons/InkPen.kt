package com.example.samazama.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Suppress("CheckReturnValue")
public val ink_pen: ImageVector
    get() {
        if (_ink_pen != null) {
            return _ink_pen!!
        }
        _ink_pen =
            ImageVector.Builder(
                name = "ink_pen",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f,
            )
                .apply {
                    path(
                        fill = SolidColor(Color.Black),
                        fillAlpha = 1f,
                        stroke = null,
                        strokeAlpha = 1f,
                        strokeLineWidth = 1f,
                        strokeLineCap = StrokeCap.Butt,
                        strokeLineJoin = StrokeJoin.Bevel,
                        strokeLineMiter = 1f,
                        pathFillType = PathFillType.Companion.NonZero,
                    ) {
                        moveTo(12.25f, 10.83f)
                        lineToRelative(0.93f, 0.93f)
                        lineTo(18.6f, 6.32f)
                        lineTo(17.68f, 5.4f)
                        lineToRelative(-5.43f, 5.43f)
                        close()
                        moveTo(5f, 19f)
                        horizontalLineTo(5.93f)
                        lineToRelative(5.82f, -5.83f)
                        lineTo(10.83f, 12.25f)
                        lineTo(5f, 18.08f)
                        verticalLineTo(19f)
                        close()
                        moveToRelative(8.88f, -5.13f)
                        lineTo(10.13f, 10.13f)
                        lineTo(14.3f, 5.95f)
                        lineTo(13.58f, 5.22f)
                        lineTo(8.1f, 10.7f)
                        lineTo(6.7f, 9.3f)
                        lineTo(12.15f, 3.82f)
                        quadToRelative(0.6f, -0.6f, 1.41f, -0.6f)
                        reflectiveQuadToRelative(1.41f, 0.6f)
                        lineTo(15.7f, 4.55f)
                        lineTo(16.95f, 3.3f)
                        quadTo(17.25f, 3f, 17.66f, 3f)
                        reflectiveQuadToRelative(0.71f, 0.3f)
                        lineTo(20.7f, 5.63f)
                        quadTo(21f, 5.93f, 21f, 6.34f)
                        reflectiveQuadTo(20.7f, 7.05f)
                        lineToRelative(-6.83f, 6.82f)
                        close()
                        moveTo(6.75f, 21f)
                        horizontalLineTo(3f)
                        verticalLineTo(17.25f)
                        lineToRelative(7.13f, -7.13f)
                        lineToRelative(3.75f, 3.75f)
                        lineTo(6.75f, 21f)
                        close()
                    }
                }
                .build()
        return _ink_pen!!
    }

private var _ink_pen: ImageVector? = null
