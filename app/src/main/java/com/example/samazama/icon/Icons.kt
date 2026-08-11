package com.example.samazama.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Suppress("CheckReturnValue")
public val chevron_right: ImageVector
    get() {
        if (_chevron_right != null) {
            return _chevron_right!!
        }
        _chevron_right =
            ImageVector.Builder(
                name = "chevron_right",
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
                        pathFillType = PathFillType.NonZero,
                    ) {
                        moveTo(12.6f, 12f)
                        lineTo(8f, 7.4f)
                        lineTo(9.4f, 6f)
                        lineToRelative(6f, 6f)
                        lineToRelative(-6f, 6f)
                        lineTo(8f, 16.6f)
                        lineTo(12.6f, 12f)
                        close()
                    }
                }
                .build()
        return _chevron_right!!
    }

private var _chevron_right: ImageVector? = null

@Suppress("CheckReturnValue")
public val home: ImageVector
    get() {
        if (_home != null) {
            return _home!!
        }
        _home =
            ImageVector.Builder(
                name = "home",
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
                        pathFillType = PathFillType.NonZero,
                    ) {
                        moveTo(6f, 19f)
                        horizontalLineTo(9f)
                        verticalLineTo(13f)
                        horizontalLineToRelative(6f)
                        verticalLineToRelative(6f)
                        horizontalLineToRelative(3f)
                        verticalLineTo(10f)
                        lineTo(12f, 5.5f)
                        lineTo(6f, 10f)
                        verticalLineToRelative(9f)
                        close()
                        moveTo(4f, 21f)
                        verticalLineTo(9f)
                        lineTo(12f, 3f)
                        lineToRelative(8f, 6f)
                        verticalLineTo(21f)
                        horizontalLineTo(13f)
                        verticalLineTo(15f)
                        horizontalLineTo(11f)
                        verticalLineToRelative(6f)
                        horizontalLineTo(4f)
                        close()
                        moveToRelative(8f, -8.75f)
                        close()
                    }
                }
                .build()
        return _home!!
    }

private var _home: ImageVector? = null

@Suppress("CheckReturnValue")
public val search: ImageVector
    get() {
        if (_search != null) {
            return _search!!
        }
        _search =
            ImageVector.Builder(
                name = "search",
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
                        pathFillType = PathFillType.NonZero,
                    ) {
                        moveTo(19.6f, 21f)
                        lineTo(13.3f, 14.7f)
                        quadToRelative(-0.75f, 0.6f, -1.72f, 0.95f)
                        reflectiveQuadTo(9.5f, 16f)
                        quadTo(6.78f, 16f, 4.89f, 14.11f)
                        quadTo(3f, 12.23f, 3f, 9.5f)
                        quadTo(3f, 6.77f, 4.89f, 4.89f)
                        reflectiveQuadTo(9.5f, 3f)
                        reflectiveQuadToRelative(4.61f, 1.89f)
                        reflectiveQuadTo(16f, 9.5f)
                        quadToRelative(0f, 1.1f, -0.35f, 2.07f)
                        reflectiveQuadTo(14.7f, 13.3f)
                        lineTo(21f, 19.6f)
                        lineTo(19.6f, 21f)
                        close()
                        moveTo(9.5f, 14f)
                        quadToRelative(1.88f, 0f, 3.19f, -1.31f)
                        reflectiveQuadTo(14f, 9.5f)
                        reflectiveQuadTo(12.69f, 6.31f)
                        reflectiveQuadTo(9.5f, 5f)
                        reflectiveQuadTo(6.31f, 6.31f)
                        reflectiveQuadTo(5f, 9.5f)
                        reflectiveQuadToRelative(1.31f, 3.19f)
                        reflectiveQuadTo(9.5f, 14f)
                        close()
                    }
                }
                .build()
        return _search!!
    }

private var _search: ImageVector? = null

@Suppress("CheckReturnValue")
public val menu: ImageVector
    get() {
        if (_menu != null) {
            return _menu!!
        }
        _menu =
            ImageVector.Builder(
                name = "menu",
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
                        pathFillType = PathFillType.NonZero,
                    ) {
                        moveTo(3f, 18f)
                        verticalLineTo(16f)
                        horizontalLineTo(21f)
                        verticalLineToRelative(2f)
                        horizontalLineTo(3f)
                        close()
                        moveTo(3f, 13f)
                        verticalLineTo(11f)
                        horizontalLineTo(21f)
                        verticalLineToRelative(2f)
                        horizontalLineTo(3f)
                        close()
                        moveTo(3f, 8f)
                        verticalLineTo(6f)
                        horizontalLineTo(21f)
                        verticalLineTo(8f)
                        horizontalLineTo(3f)
                        close()
                    }
                }
                .build()
        return _menu!!
    }

private var _menu: ImageVector? = null

@Suppress("CheckReturnValue")
public val close: ImageVector
    get() {
        if (_close != null) {
            return _close!!
        }
        _close =
            ImageVector.Builder(
                name = "close",
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
                        pathFillType = PathFillType.NonZero,
                    ) {
                        moveTo(6.4f, 19f)
                        lineTo(5f, 17.6f)
                        lineTo(10.6f, 12f)
                        lineTo(5f, 6.4f)
                        lineTo(6.4f, 5f)
                        lineTo(12f, 10.6f)
                        lineTo(17.6f, 5f)
                        lineTo(19f, 6.4f)
                        lineTo(13.4f, 12f)
                        lineTo(19f, 17.6f)
                        lineTo(17.6f, 19f)
                        lineTo(12f, 13.4f)
                        lineTo(6.4f, 19f)
                        close()
                    }
                }
                .build()
        return _close!!
    }

private var _close: ImageVector? = null

@Suppress("CheckReturnValue")
public val settings: ImageVector
    get() {
        if (_settings != null) {
            return _settings!!
        }
        _settings =
            ImageVector.Builder(
                name = "settings",
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
                        pathFillType = PathFillType.NonZero,
                    ) {
                        moveTo(9.25f, 22f)
                        lineTo(8.85f, 18.8f)
                        quadTo(8.53f, 18.68f, 8.24f, 18.5f)
                        reflectiveQuadTo(7.68f, 18.13f)
                        lineTo(4.7f, 19.38f)
                        lineTo(1.95f, 14.63f)
                        lineTo(4.53f, 12.68f)
                        quadTo(4.5f, 12.5f, 4.5f, 12.34f)
                        quadToRelative(0f, -0.16f, 0f, -0.34f)
                        reflectiveQuadToRelative(0f, -0.34f)
                        reflectiveQuadTo(4.53f, 11.33f)
                        lineTo(1.95f, 9.38f)
                        lineTo(4.7f, 4.63f)
                        lineTo(7.68f, 5.88f)
                        quadTo(7.95f, 5.68f, 8.25f, 5.5f)
                        reflectiveQuadTo(8.85f, 5.2f)
                        lineTo(9.25f, 2f)
                        horizontalLineToRelative(5.5f)
                        lineToRelative(0.4f, 3.2f)
                        quadToRelative(0.33f, 0.13f, 0.61f, 0.3f)
                        reflectiveQuadToRelative(0.56f, 0.38f)
                        lineTo(19.3f, 4.63f)
                        lineToRelative(2.75f, 4.75f)
                        lineToRelative(-2.57f, 1.95f)
                        quadToRelative(0.02f, 0.18f, 0.02f, 0.34f)
                        reflectiveQuadToRelative(0f, 0.34f)
                        reflectiveQuadToRelative(0f, 0.34f)
                        reflectiveQuadToRelative(-0.05f, 0.34f)
                        lineToRelative(2.57f, 1.95f)
                        lineToRelative(-2.75f, 4.75f)
                        lineTo(16.33f, 18.13f)
                        quadToRelative(-0.27f, 0.2f, -0.57f, 0.38f)
                        reflectiveQuadToRelative(-0.6f, 0.3f)
                        lineTo(14.75f, 22f)
                        horizontalLineTo(9.25f)
                        close()
                        moveTo(11f, 20f)
                        horizontalLineToRelative(1.98f)
                        lineToRelative(0.35f, -2.65f)
                        quadToRelative(0.78f, -0.2f, 1.44f, -0.59f)
                        reflectiveQuadToRelative(1.21f, -0.94f)
                        lineToRelative(2.47f, 1.03f)
                        lineToRelative(0.98f, -1.7f)
                        lineTo(17.28f, 13.52f)
                        quadToRelative(0.13f, -0.35f, 0.17f, -0.74f)
                        reflectiveQuadTo(17.5f, 12f)
                        reflectiveQuadTo(17.45f, 11.21f)
                        quadTo(17.4f, 10.83f, 17.28f, 10.48f)
                        lineTo(19.43f, 8.85f)
                        lineTo(18.45f, 7.15f)
                        lineTo(15.98f, 8.2f)
                        quadTo(15.43f, 7.63f, 14.76f, 7.24f)
                        reflectiveQuadTo(13.33f, 6.65f)
                        lineTo(13f, 4f)
                        horizontalLineTo(11.03f)
                        lineTo(10.68f, 6.65f)
                        quadTo(9.9f, 6.85f, 9.24f, 7.24f)
                        reflectiveQuadTo(8.03f, 8.17f)
                        lineTo(5.55f, 7.15f)
                        lineTo(4.58f, 8.85f)
                        lineToRelative(2.15f, 1.6f)
                        quadTo(6.6f, 10.83f, 6.55f, 11.2f)
                        reflectiveQuadTo(6.5f, 12f)
                        quadToRelative(0f, 0.4f, 0.05f, 0.77f)
                        reflectiveQuadToRelative(0.17f, 0.75f)
                        lineTo(4.58f, 15.15f)
                        lineToRelative(0.98f, 1.7f)
                        lineTo(8.03f, 15.8f)
                        quadToRelative(0.55f, 0.58f, 1.21f, 0.96f)
                        reflectiveQuadToRelative(1.44f, 0.59f)
                        lineTo(11f, 20f)
                        close()
                        moveToRelative(1.05f, -4.5f)
                        quadToRelative(1.45f, 0f, 2.47f, -1.03f)
                        reflectiveQuadTo(15.55f, 12f)
                        reflectiveQuadTo(14.53f, 9.52f)
                        reflectiveQuadTo(12.05f, 8.5f)
                        quadToRelative(-1.47f, 0f, -2.49f, 1.02f)
                        reflectiveQuadTo(8.55f, 12f)
                        reflectiveQuadToRelative(1.01f, 2.47f)
                        reflectiveQuadToRelative(2.49f, 1.03f)
                        close()
                        moveTo(12f, 12f)
                        close()
                    }
                }
                .build()
        return _settings!!
    }

private var _settings: ImageVector? = null
