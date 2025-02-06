package cl.emilym.compose.audiocontrol

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SliderState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressBar(
    progress: Float,
    onSeek: (Float) -> Unit,
    modifier: Modifier = Modifier,
    thumb: (@Composable (SliderState, MutableInteractionSource) -> Unit)? = null,
    track: (@Composable (SliderState, MutableInteractionSource) -> Unit)? = null,
) {
    // This mess makes sure the track correctly follows the media when not
    // interacted with, follows the thumb when interacted with, and doesn't
    // jump while the backend registers that the media has been seeked
    var trackProgress by remember { mutableFloatStateOf(0f) }
    val contentProgress = progress
    var contentProgressOnDragStart by remember { mutableStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    var isUpToDate by remember { mutableStateOf(true) }
    val interactionSource = remember { MutableInteractionSource() }
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is DragInteraction.Start -> {
                    isDragging = true
                    isUpToDate = false
                    contentProgressOnDragStart = contentProgress
                    trackProgress = contentProgress
                }
            }
        }
    }
    LaunchedEffect(trackProgress, contentProgress, isUpToDate, isDragging) {
        if (isDragging || isUpToDate) return@LaunchedEffect
        isUpToDate = if (trackProgress < contentProgressOnDragStart)
            contentProgress <= trackProgress
        else
            contentProgress >= trackProgress
    }

    val progress = if (!isUpToDate) {
        trackProgress
    } else {
        contentProgress
    }

    val colors = SliderDefaults.colors(
        // Always keep thumb white, it's impossible to see otherwise
        thumbColor = Color.White,
        activeTrackColor = MaterialTheme.colorScheme.primary
    )
    Slider(
        value = progress,
        onValueChange = {
            trackProgress = it
        },
        onValueChangeFinished = {
            onSeek(trackProgress)
            isDragging = false
        },
        thumb = { sliderState ->
            thumb?.invoke(sliderState, interactionSource) ?: run {
                Surface(
                    shadowElevation = 3.dp,
                    shape = CircleShape
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(colors.thumbColor)
                    )
                }
            }
        },
        track = { sliderState ->
            track?.invoke(sliderState, interactionSource) ?: run {
                Row(
                    Modifier.clip(RoundedCornerShape(4.dp/2))
                ) {
                    Box(
                        Modifier.height(4.dp)
                            .fillMaxWidth(sliderState.value)
                            .background(colors.activeTrackColor)
                    )
                    Box(
                        Modifier.height(4.dp)
                            .fillMaxWidth(1f)
                            .background(colors.inactiveTrackColor)
                    )
                }
            }
        },
        modifier = Modifier.then(modifier),
        colors = colors,
        interactionSource = interactionSource
    )
}