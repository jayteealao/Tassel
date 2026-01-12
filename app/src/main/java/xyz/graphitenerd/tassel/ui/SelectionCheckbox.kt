package xyz.graphitenerd.tassel.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * A reusable checkbox component for multiselect functionality.
 *
 * This checkbox displays a circular selection indicator with a checkmark
 * when selected. It uses AnimatedVisibility for smooth enter/exit animations.
 *
 * @param isSelected Whether the checkbox is currently selected
 * @param modifier Modifier to be applied to the checkbox
 */
@Composable
fun SelectionCheckbox(
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(24.dp)
            .then(
                if (isSelected) {
                    Modifier
                        .background(Color.Black, CircleShape)
                } else {
                    Modifier
                        .border(2.dp, Color.Black, CircleShape)
                        .background(Color.White, CircleShape)
                }
            ),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Selected",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
