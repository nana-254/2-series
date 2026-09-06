package com.example.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MDarkBlue
import com.example.ui.theme.MLightBlue
import com.example.ui.theme.MRed
import com.example.ui.theme.OledBlack
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    val alphaAnim = remember { Animatable(0f) }
    val scaleAnim = remember { Animatable(0.85f) }
    val stripeProgress = remember { Animatable(0f) }
    val pulseAnim = rememberInfiniteTransition(label = "pulse")
    val glowAlpha by pulseAnim.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    LaunchedEffect(Unit) {
        alphaAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
        )
        scaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
        )
        stripeProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing)
        )
        delay(1200)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OledBlack)
            .clickable { onTimeout() },
        contentAlignment = Alignment.Center
    ) {
        // Subtle ambient radial glow behind BMW emblem
        Canvas(modifier = Modifier.size(320.dp)) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        MLightBlue.copy(alpha = glowAlpha * 0.25f),
                        MDarkBlue.copy(alpha = 0.1f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = size.minDimension / 1.5f
                )
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .alpha(alphaAnim.value)
                .scale(scaleAnim.value)
                .padding(24.dp)
        ) {
            // Realistic High-Precision BMW Roundel Canvas Emblem
            BmwRoundelLogo(modifier = Modifier.size(120.dp))

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "THE 2",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Black,
                letterSpacing = 6.sp,
                color = Color.White
            )

            Text(
                text = "SERIES HUB • 2023 LINEUP",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp,
                color = MLightBlue
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Animated M Sport Heritage Tri-Color Parallelogram Stripes
            MStriperAnimated(stripeProgress = stripeProgress.value)

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Tap to enter",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.4f),
                letterSpacing = 1.5.sp
            )
        }
    }
}

@Composable
fun BmwRoundelLogo(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val outerRadius = size.minDimension / 2f
        val innerRadius = outerRadius * 0.65f

        // Outer brushed silver chrome ring
        drawCircle(
            brush = Brush.sweepGradient(
                colors = listOf(
                    Color(0xFFE0E0E0),
                    Color(0xFF7D838A),
                    Color(0xFFFFFFFF),
                    Color(0xFF5A5E66),
                    Color(0xFFE0E0E0)
                )
            ),
            radius = outerRadius,
            center = center
        )

        // Inner black border ring
        drawCircle(
            color = Color(0xFF0F1114),
            radius = outerRadius * 0.96f,
            center = center
        )

        // Silver divider between black ring and center quarters
        drawCircle(
            color = Color(0xFFD0D5DD),
            radius = innerRadius * 1.03f,
            center = center,
            style = Stroke(width = 2.5f)
        )

        // Inner quarters (Sky Blue & White)
        // Top-right: Blue, Top-left: White, Bottom-left: Blue, Bottom-right: White
        val quarterRadius = innerRadius
        val topLeft = Offset(center.x - quarterRadius, center.y - quarterRadius)
        val diameter = quarterRadius * 2f

        // 1. Top-Right Quarter: Sky Blue
        drawArc(
            color = Color(0xFF0066B3),
            startAngle = 270f,
            sweepAngle = 90f,
            useCenter = true,
            topLeft = topLeft,
            size = Size(diameter, diameter)
        )

        // 2. Bottom-Right Quarter: Alpine White
        drawArc(
            color = Color(0xFFFAFAFA),
            startAngle = 0f,
            sweepAngle = 90f,
            useCenter = true,
            topLeft = topLeft,
            size = Size(diameter, diameter)
        )

        // 3. Bottom-Left Quarter: Sky Blue
        drawArc(
            color = Color(0xFF0066B3),
            startAngle = 90f,
            sweepAngle = 90f,
            useCenter = true,
            topLeft = topLeft,
            size = Size(diameter, diameter)
        )

        // 4. Top-Left Quarter: Alpine White
        drawArc(
            color = Color(0xFFFAFAFA),
            startAngle = 180f,
            sweepAngle = 90f,
            useCenter = true,
            topLeft = topLeft,
            size = Size(diameter, diameter)
        )

        // Crisp cross dividers
        drawLine(
            color = Color(0xFF1E2229),
            start = Offset(center.x - innerRadius, center.y),
            end = Offset(center.x + innerRadius, center.y),
            strokeWidth = 2.5f
        )
        drawLine(
            color = Color(0xFF1E2229),
            start = Offset(center.x, center.y - innerRadius),
            end = Offset(center.x, center.y + innerRadius),
            strokeWidth = 2.5f
        )
    }
}

@Composable
fun MStriperAnimated(stripeProgress: Float) {
    Canvas(modifier = Modifier.width(180.dp).height(24.dp)) {
        val width = size.width * stripeProgress
        val slant = 18f
        val stripeWidth = width / 3.4f
        val spacing = 6f

        fun drawSlantedStripe(color: Color, index: Int) {
            val startX = index * (stripeWidth + spacing)
            val path = Path().apply {
                moveTo(startX + slant, 0f)
                lineTo(startX + stripeWidth + slant, 0f)
                lineTo(startX + stripeWidth, size.height)
                lineTo(startX, size.height)
                close()
            }
            drawPath(path = path, color = color)
        }

        drawSlantedStripe(MLightBlue, 0)
        drawSlantedStripe(MDarkBlue, 1)
        drawSlantedStripe(MRed, 2)
    }
}
