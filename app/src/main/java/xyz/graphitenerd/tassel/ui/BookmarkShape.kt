package xyz.graphitenerd.tassel.ui

import android.graphics.Matrix
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

val BookmarkShape: Shape = object: Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val baseWidth = 18.666667938232422f
        val baseHeight = 24f

        val path = Path()

        path.moveTo(16f, 0f)
        path.lineTo(2.6667f, 0f)
        path.cubicTo(1.2f, 0f, 0.0133f, 1.2f, 0.0133f, 2.6667f)
        path.lineTo(0f, 24f)
        path.lineTo(9.3333f, 20f)
        path.lineTo(18.6667f, 24f)
        path.lineTo(18.6667f, 2.6667f)
        path.cubicTo(18.6667f, 1.2f, 17.4667f, 0f, 16f, 0f)
        path.close()
        path.moveTo(16f, 20f)
        path.lineTo(9.3333f, 17.0933f)
        path.lineTo(2.6667f, 20f)
        path.lineTo(2.6667f, 2.6667f)
        path.lineTo(16f, 2.6667f)
        path.lineTo(16f, 20f)
        path.close()

        return Outline.Generic(
            path
                .asAndroidPath()
                .apply {
                    transform(Matrix().apply {
                        setScale(size.width / baseWidth, size.height / baseHeight)
                    })
                }
                .asComposePath()
        )
    }
}