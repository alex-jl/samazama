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
public val category: ImageVector
    get() {
        if (_category != null) {
            return _category!!
        }
        _category =
            ImageVector.Builder(
                name = "category",
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
                        moveTo(6.5f, 11f)
                        lineTo(12f, 2f)
                        lineToRelative(5.5f, 9f)
                        horizontalLineTo(6.5f)
                        close()
                        moveToRelative(11f, 11f)
                        quadToRelative(-1.88f, 0f, -3.19f, -1.31f)
                        reflectiveQuadTo(13f, 17.5f)
                        reflectiveQuadToRelative(1.31f, -3.19f)
                        reflectiveQuadTo(17.5f, 13f)
                        reflectiveQuadToRelative(3.19f, 1.31f)
                        reflectiveQuadTo(22f, 17.5f)
                        reflectiveQuadToRelative(-1.31f, 3.19f)
                        reflectiveQuadTo(17.5f, 22f)
                        close()
                        moveTo(3f, 21.5f)
                        verticalLineToRelative(-8f)
                        horizontalLineToRelative(8f)
                        verticalLineToRelative(8f)
                        horizontalLineTo(3f)
                        close()
                        moveTo(17.5f, 20f)
                        quadToRelative(1.05f, 0f, 1.78f, -0.73f)
                        reflectiveQuadTo(20f, 17.5f)
                        reflectiveQuadTo(19.28f, 15.73f)
                        reflectiveQuadTo(17.5f, 15f)
                        reflectiveQuadToRelative(-1.77f, 0.72f)
                        reflectiveQuadTo(15f, 17.5f)
                        reflectiveQuadToRelative(0.73f, 1.77f)
                        reflectiveQuadTo(17.5f, 20f)
                        close()
                        moveTo(5f, 19.5f)
                        horizontalLineTo(9f)
                        verticalLineToRelative(-4f)
                        horizontalLineTo(5f)
                        verticalLineToRelative(4f)
                        close()
                        moveTo(10.05f, 9f)
                        horizontalLineToRelative(3.9f)
                        lineTo(12f, 5.85f)
                        lineTo(10.05f, 9f)
                        close()
                        moveTo(12f, 9f)
                        close()
                        moveTo(9f, 15.5f)
                        close()
                        moveToRelative(8.5f, 2f)
                        close()
                    }
                }
                .build()
        return _category!!
    }

private var _category: ImageVector? = null
