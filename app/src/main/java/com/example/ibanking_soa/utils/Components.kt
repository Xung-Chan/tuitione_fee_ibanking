package com.example.ibanking_soa.utils

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ibanking_soa.R
import com.example.ibanking_soa.ui.theme.Blue3
import com.example.ibanking_soa.ui.theme.ErrorGradient
import com.example.ibanking_soa.ui.theme.Gray3
import com.example.ibanking_soa.ui.theme.InfoGradient
import com.example.ibanking_soa.ui.theme.SuccessGradient
import com.example.ibanking_soa.ui.theme.WarningGradient


@Composable
fun LoadingScaffold(
    isLoading: Boolean,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        content()
        if (isLoading) {
            Box(
                contentAlignment = Alignment.Center, modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = Gray3.copy(alpha = 0.5f),
                    )
                    .pointerInput(Unit) {}) {
                CircularProgressIndicator(
                    color = Blue3, modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}


@Composable
private fun animateFadeAndScaleSnackBar(): Pair<Float, Float> {
    val alpha by animateFloatAsState(
        targetValue = 1f, animationSpec = tween(
            durationMillis = SnackBarConstants.ALPHA_DURATION, easing = FastOutSlowInEasing
        ), label = "alpha"
    )
    val scale by animateFloatAsState(
        targetValue = 1f, animationSpec = tween(
            durationMillis = SnackBarConstants.SCALE_DURATION, easing = FastOutSlowInEasing
        ), label = "scale"
    )
    return alpha to scale
}

private object SnackBarConstants {
    val PADDING = 8.dp
    val CONTENT_PADDING = 12.dp
    val CORNER_RADIUS = 12.dp
    val ICON_SIZE = 24.dp
    val ICON_PADDING = 8.dp
    val SPACING = 8.dp
    val BUTTON_HEIGHT = 36.dp
    val MESSAGE_FONT_SIZE = 14.sp
    val ACTION_FONT_SIZE = 14.sp
    const val ALPHA_DURATION = 250
    const val SCALE_DURATION = 200
}

enum class SnackBarType {
    SUCCESS, ERROR, WARNING, INFO
}

@Composable
fun GradientSnackBar(
    type: SnackBarType,
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = "Đóng",
    onAction: () -> Unit = {},
) {
    val (alphaAnim, scaleAnim) = animateFadeAndScaleSnackBar()
//    Box(
//        modifier = Modifier.fillMaxSize()
//    ) {
//
//        Box(
//            modifier = Modifier
//                .align(Alignment.BottomCenter)
//                .padding(bottom = 16.dp)
//        ) {
    Card(
        modifier = modifier
            .fillMaxWidth(0.9f)
            .wrapContentHeight()
            .alpha(alphaAnim)
            .scale(scaleAnim)
            .padding(SnackBarConstants.PADDING),
        shape = RoundedCornerShape(SnackBarConstants.CORNER_RADIUS),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = when (type) {
                            SnackBarType.SUCCESS -> SuccessGradient
                            SnackBarType.ERROR -> ErrorGradient
                            SnackBarType.WARNING -> WarningGradient
                            SnackBarType.INFO -> InfoGradient
                        }
                    )
                )
                .padding(SnackBarConstants.CONTENT_PADDING)
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)
            ) {
                when (type) {
                    SnackBarType.SUCCESS -> Icon(
                        painter = painterResource(R.drawable.success),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .size(SnackBarConstants.ICON_SIZE)
                            .padding(end = SnackBarConstants.ICON_PADDING)
                    )

                    else -> Icon(
                        imageVector = Icons.Rounded.Info,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .size(SnackBarConstants.ICON_SIZE)
                            .padding(end = SnackBarConstants.ICON_PADDING)
                    )
                }
                Text(
                    text = message,
                    color = Color.White,
                    fontSize = SnackBarConstants.MESSAGE_FONT_SIZE,
                    textAlign = TextAlign.Start,
                    lineHeight = SnackBarConstants.MESSAGE_FONT_SIZE * 1.2f,
                    modifier = Modifier.padding(end = SnackBarConstants.SPACING)
                )
            }
            actionLabel?.let {
                TextButton(
                    onClick = onAction, modifier = Modifier.height(SnackBarConstants.BUTTON_HEIGHT)
                ) {
                    Text(
                        text = it,
                        color = Color.White,
                        fontSize = SnackBarConstants.ACTION_FONT_SIZE,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
//        }
//    }
}
