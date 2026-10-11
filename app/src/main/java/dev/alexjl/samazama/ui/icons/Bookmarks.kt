package dev.alexjl.samazama.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Suppress("CheckReturnValue")
public val bookmarks: ImageVector
    get() {
        if (_bookmarks != null) {
            return _bookmarks!!
        }
        _bookmarks =
            ImageVector.Builder(
                name = "bookmarks",
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
                        moveTo(4f, 22f)
                        verticalLineTo(8f)
                        quadTo(4f, 7.18f, 4.59f, 6.59f)
                        reflectiveQuadTo(6f, 6f)
                        horizontalLineToRelative(8f)
                        quadToRelative(0.83f, 0f, 1.41f, 0.59f)
                        quadTo(16f, 7.18f, 16f, 8f)
                        verticalLineTo(22f)
                        lineTo(10f, 19f)
                        lineTo(4f, 22f)
                        close()
                        moveTo(6f, 18.98f)
                        lineToRelative(4f, -2.15f)
                        lineToRelative(4f, 2.15f)
                        verticalLineTo(8f)
                        horizontalLineTo(6f)
                        verticalLineTo(18.98f)
                        close()
                        moveTo(18f, 18f)
                        verticalLineTo(4f)
                        horizontalLineTo(7f)
                        verticalLineTo(2f)
                        horizontalLineTo(18f)
                        quadToRelative(0.82f, 0f, 1.41f, 0.59f)
                        reflectiveQuadTo(20f, 4f)
                        verticalLineTo(18f)
                        horizontalLineTo(18f)
                        close()
                        moveTo(6f, 8f)
                        horizontalLineToRelative(8f)
                        horizontalLineTo(10f)
                        horizontalLineTo(6f)
                        close()
                    }
                }
                .build()
        return _bookmarks!!
    }

private var _bookmarks: ImageVector? = null
