package com.example.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.MainViewModel
import com.example.Trim
import com.example.trims
import com.example.ui.theme.MDarkBlue
import com.example.ui.theme.MLightBlue
import com.example.ui.theme.MRed

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToConfigure: () -> Unit = {},
    onNavigateToTco: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Executive Header with M Motorsport Stripe Banner
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                "BMW 2 SERIES",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                color = Color.White
                            )
                            MBadge("2023 G42 / G87")
                        }
                        Text(
                            "Executive Benchmark & Modding Platform",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Currency Quick Pill
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .border(1.dp, Color(0x35FFFFFF), RoundedCornerShape(20.dp)),
                        color = Color(0x18FFFFFF)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                state.selectedCurrency.code,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                MStripeLine(modifier = Modifier.fillMaxWidth(), height = 3.dp)
            }
        }

        // Hero 2023 Lineup Horizontal Showcase
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "2023 Lineup Models",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        "3 Trims Available",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 4.dp)
                ) {
                    items(trims) { trim ->
                        HeroCarCard(
                            trim = trim,
                            selectedCurrency = state.selectedCurrency,
                            isSelected = trim.id == state.selectedTrimId,
                            onSelect = {
                                viewModel.selectTrim(trim.id)
                                onNavigateToConfigure()
                            }
                        )
                    }
                }
            }
        }

        // Business Visual 1: Grouped Powertrain Bar Chart (HP vs Torque)
        item {
            GlassCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Powertrain Output Benchmark",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            "Horsepower (HP) vs Peak Torque (lb-ft)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Chart Legend
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(MLightBlue))
                            Text("HP", style = MaterialTheme.typography.labelSmall, color = Color.White)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(MRed))
                            Text("Torque", style = MaterialTheme.typography.labelSmall, color = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))
                BusinessGroupedBarChart(trims = trims)
            }
        }

        // Business Visual 2: Sprint Acceleration Curve (0-60 mph)
        item {
            GlassCard {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "0-60 MPH Acceleration Velocity",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                "Launch control velocity telemetry curve",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        MBadge("Lower is faster", containerColor = Color(0x22FFFFFF))
                    }

                    Spacer(modifier = Modifier.height(18.dp))
                    BusinessAccelerationCurveChart(trims = trims)
                }
            }
        }

        // Kenya Importation & Tax Quick Insight Card
        item {
            val currentTrim = trims.first { it.id == state.selectedTrimId }
            val importCalc = viewModel.calculateKenyaImport(currentTrim, currentTrim.startingPrice.toDouble())

            GlassCard(
                cornerRadius = 24.dp,
                onClick = onNavigateToTco
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(
                            "Kenya Port Landed Cost (KRA)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "View TCO", tint = MaterialTheme.colorScheme.primary)
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Calculated for ${currentTrim.name} (${currentTrim.cc} cc engine bracket)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ExecutiveStatItem(
                        title = "Base MSRP",
                        value = "$${currentTrim.startingPrice}",
                        subtitle = "US Port FOB",
                        modifier = Modifier.weight(1f)
                    )
                    ExecutiveStatItem(
                        title = "Excise Tier",
                        value = "${importCalc.exciseDutyRatePercent}%",
                        subtitle = if (currentTrim.cc <= 2000) "B48 <=2.0L advantage" else ">2.0L bracket",
                        modifier = Modifier.weight(1f)
                    )
                    ExecutiveStatItem(
                        title = "Est. Landed",
                        value = formatCurrency(importCalc.totalLandedCostUsd, state.selectedCurrency),
                        subtitle = "Incl. Duty, VAT & Plates",
                        modifier = Modifier.weight(1.3f)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun HeroCarCard(
    trim: Trim,
    selectedCurrency: com.example.Currency,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color(0x35FFFFFF)

    Box(
        modifier = Modifier
            .width(310.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0x28FFFFFF),
                        Color(0x12FFFFFF),
                        Color(0x08FFFFFF)
                    )
                )
            )
            .border(width = if (isSelected) 1.5.dp else 1.dp, color = borderColor, shape = RoundedCornerShape(22.dp))
            .clickable { onSelect() }
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Hero Photo with Aspect Ratio
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(14.dp))
            ) {
                AsyncImage(
                    model = trim.heroImageUrl,
                    contentDescription = trim.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Engine code overlay badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xCC050608))
                        .border(0.8.dp, Color(0x40FFFFFF), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        trim.engineCode,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MLightBlue,
                        fontSize = 10.sp
                    )
                }

                // 0-60 tag
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xD9000000))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        "0-60: ${trim.zeroToSixty}s",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 11.sp
                    )
                }
            }

            // Name and Price
            Column {
                Text(
                    trim.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    trim.tag,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    formatCurrency(trim.startingPrice.toDouble(), selectedCurrency),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            MStripeLine(modifier = Modifier.fillMaxWidth(), height = 2.dp)

            // Specs grid
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                SpecRow("Engine", trim.engineDesc)
                SpecRow("Power", "${trim.hp} HP @ 6,000 rpm")
                SpecRow("Torque", "${trim.torque} lb-ft")
                SpecRow("Drivetrain", trim.drivetrain)
                SpecRow("Displacement", "${trim.cc} cc (${if (trim.cc <= 2000) "25% KRA" else "35% KRA"})")
            }

            // Action Button
            Button(
                onClick = onSelect,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color(0x25FFFFFF)
                )
            ) {
                Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Configure & Mod Parts")
            }
        }
    }
}

@Composable
fun BusinessGroupedBarChart(trims: List<Trim>) {
    val maxHp = 500f
    val maxTorque = 500f

    Column(modifier = Modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val bottomAxisY = canvasHeight - 25f
            val topAxisY = 20f
            val usableHeight = bottomAxisY - topAxisY

            // 1. Draw horizontal subtle executive grid lines
            val gridSteps = 4
            for (i in 0..gridSteps) {
                val y = bottomAxisY - (usableHeight * (i.toFloat() / gridSteps))
                drawLine(
                    color = Color(0x18FFFFFF),
                    start = Offset(0f, y),
                    end = Offset(canvasWidth, y),
                    strokeWidth = 1f
                )
            }

            // 2. Draw Grouped Bars for each trim
            val groupWidth = canvasWidth / trims.size
            val barWidth = 24f
            val barSpacing = 6f

            trims.forEachIndexed { index, trim ->
                val groupCenterX = index * groupWidth + (groupWidth / 2f)

                // HP Bar (Cyan)
                val hpHeight = (trim.hp / maxHp) * usableHeight
                val hpTopY = bottomAxisY - hpHeight
                val hpLeftX = groupCenterX - barWidth - (barSpacing / 2f)

                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(MLightBlue, MDarkBlue)
                    ),
                    topLeft = Offset(hpLeftX, hpTopY),
                    size = Size(barWidth, hpHeight),
                    cornerRadius = CornerRadius(4f, 4f)
                )

                // Torque Bar (Red)
                val torqueHeight = (trim.torque / maxTorque) * usableHeight
                val torqueTopY = bottomAxisY - torqueHeight
                val torqueLeftX = groupCenterX + (barSpacing / 2f)

                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(MRed, Color(0xFF8B0015))
                    ),
                    topLeft = Offset(torqueLeftX, torqueTopY),
                    size = Size(barWidth, torqueHeight),
                    cornerRadius = CornerRadius(4f, 4f)
                )
            }

            // Bottom axis line
            drawLine(
                color = Color(0x40FFFFFF),
                start = Offset(0f, bottomAxisY),
                end = Offset(canvasWidth, bottomAxisY),
                strokeWidth = 1.5f
            )
        }

        // Labels under bars
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            trims.forEach { trim ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        trim.id.uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        "${trim.hp} HP / ${trim.torque} lb-ft",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 9.sp
                    )
                }
            }
        }
    }
}

@Composable
fun BusinessAccelerationCurveChart(trims: List<Trim>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val bottomY = canvasHeight - 25f
            val topY = 20f
            val chartHeight = bottomY - topY
            val startX = 40f
            val chartWidth = canvasWidth - startX - 20f

            // Grid lines (Speed 0, 20, 40, 60 mph)
            for (speed in 0..60 step 20) {
                val y = bottomY - (chartHeight * (speed / 60f))
                drawLine(
                    color = Color(0x18FFFFFF),
                    start = Offset(startX, y),
                    end = Offset(canvasWidth, y),
                    strokeWidth = 1f
                )
            }

            // Draw acceleration curves for each trim
            val maxSeconds = 6.0f

            trims.forEach { trim ->
                val curveColor = when (trim.id) {
                    "230i" -> Color(0xFF7CA6D8)
                    "m240i" -> MLightBlue
                    "m2" -> MRed
                    else -> Color.White
                }

                val finishX = startX + (chartWidth * (trim.zeroToSixty / maxSeconds))
                val targetY = topY

                val path = Path().apply {
                    moveTo(startX, bottomY)
                    // Quadratic curve simulating turbo launch ramp-up
                    quadraticTo(
                        startX + (finishX - startX) * 0.35f,
                        bottomY - (chartHeight * 0.2f),
                        finishX,
                        targetY
                    )
                }

                drawPath(
                    path = path,
                    color = curveColor,
                    style = Stroke(width = 3f)
                )

                // Finish dot at 60 mph
                drawCircle(
                    color = curveColor,
                    radius = 5f,
                    center = Offset(finishX, targetY)
                )
            }

            // Bottom axis line
            drawLine(
                color = Color(0x40FFFFFF),
                start = Offset(startX, bottomY),
                end = Offset(canvasWidth, bottomY),
                strokeWidth = 1.5f
            )
        }

        // Time indicators
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            trims.forEach { trim ->
                val labelColor = when (trim.id) {
                    "230i" -> Color(0xFF7CA6D8)
                    "m240i" -> MLightBlue
                    "m2" -> MRed
                    else -> Color.White
                }
                Text(
                    "${trim.id.uppercase()}: ${trim.zeroToSixty}s",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = labelColor
                )
            }
        }
    }
}
