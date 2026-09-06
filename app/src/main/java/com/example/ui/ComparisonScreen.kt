package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.MainViewModel
import com.example.trims
import com.example.ui.theme.MDarkBlue
import com.example.ui.theme.MLightBlue
import com.example.ui.theme.MRed

@Composable
fun ComparisonScreen(viewModel: MainViewModel) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Header
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Benchmark & Spec Matrix",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        "Side-by-side 2023 BMW 2 Series & Kenya Landed Schedule",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                MBadge("MY 2023")
            }
            Spacer(modifier = Modifier.height(10.dp))
            MStripeLine(modifier = Modifier.fillMaxWidth(), height = 3.dp)
        }

        // Key Takeaways Highlights
        GlassCard {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(
                    "Strategic Buying Verdict (Kenya Market)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "• 230i M Sport: Optimal tax efficiency in Kenya with 25% excise duty (under 2,000 cc threshold), saving over KSh 2.4M in KRA duties vs 3-liter models.\n" +
                "• M240i xDrive: Best all-weather performance with rear-biased AWD, B58 bulletproof tuning potential, and 3.82 HP per \$1k value.\n" +
                "• M2 (G87): Pure motorsport pedigree with widebody track stance, genuine S58 twin-turbo powerplant, and standard active differential.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )
        }

        // Scrollable Side-by-Side Matrix Table
        GlassCard(cornerRadius = 22.dp) {
            Row(
                modifier = Modifier.horizontalScroll(scrollState)
            ) {
                // Feature Label Column
                Column(
                    modifier = Modifier.width(150.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    Box(modifier = Modifier.height(180.dp), contentAlignment = Alignment.BottomStart) {
                        Text(
                            "Specification\nParameter",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }

                    TableSectionHeader("1. PRICING & IMPORT")
                    TableLabelCell("Base US MSRP")
                    TableLabelCell("Kenya Landed (KRA)")
                    TableLabelCell("KRA Excise Duty")
                    TableLabelCell("Displacement (cc)")

                    TableSectionHeader("2. POWERTRAIN")
                    TableLabelCell("Engine Family")
                    TableLabelCell("Peak Power")
                    TableLabelCell("Peak Torque")
                    TableLabelCell("Drivetrain")
                    TableLabelCell("Transmission")

                    TableSectionHeader("3. PERFORMANCE")
                    TableLabelCell("0-60 MPH")
                    TableLabelCell("Top Speed")
                    TableLabelCell("Curb Weight")
                    TableLabelCell("Fuel Economy (MPG)")
                }

                // Vehicle Columns
                trims.forEach { trim ->
                    val importCalc = viewModel.calculateKenyaImport(trim, trim.startingPrice.toDouble())
                    val isM2 = trim.id == "m2"
                    val is230i = trim.id == "230i"

                    Column(
                        modifier = Modifier
                            .width(170.dp)
                            .padding(horizontal = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        // Vehicle Card Header
                        Box(
                            modifier = Modifier
                                .height(180.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0x18FFFFFF))
                                .border(1.dp, Color(0x30FFFFFF), RoundedCornerShape(14.dp))
                                .padding(8.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(85.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                ) {
                                    AsyncImage(
                                        model = trim.heroImageUrl,
                                        contentDescription = trim.name,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                Text(
                                    trim.name,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1
                                )
                                Text(
                                    formatCurrency(trim.startingPrice.toDouble(), state.selectedCurrency),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        TableSectionHeader("")
                        TableValueCell("$${trim.startingPrice}")
                        TableValueCell(formatCurrency(importCalc.totalLandedCostUsd, state.selectedCurrency), highlight = true)
                        TableValueCell("${importCalc.exciseDutyRatePercent}% (${if (is230i) "Tax Saver" else "3.0L Tier"})")
                        TableValueCell("${trim.cc} cc")

                        TableSectionHeader("")
                        TableValueCell(trim.engineCode, highlight = true)
                        TableValueCell("${trim.hp} HP @ 6k")
                        TableValueCell("${trim.torque} lb-ft")
                        TableValueCell(trim.drivetrain)
                        TableValueCell(trim.transmission.take(18))

                        TableSectionHeader("")
                        TableValueCell("${trim.zeroToSixty}s", highlight = isM2)
                        TableValueCell(if (isM2) "177 mph" else "155 mph")
                        TableValueCell("${trim.curbWeightLbs} lbs")
                        TableValueCell("${trim.mpgCity} / ${trim.mpgHwy}")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun TableSectionHeader(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(34.dp)
            .padding(top = 10.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        if (title.isNotEmpty()) {
            Text(
                title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MLightBlue,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun TableLabelCell(label: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .border(0.5.dp, Color(0x10FFFFFF))
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp
        )
    }
}

@Composable
fun TableValueCell(value: String, highlight: Boolean = false) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(if (highlight) Color(0x140082D1) else Color.Transparent)
            .border(0.5.dp, Color(0x10FFFFFF))
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (highlight) FontWeight.Bold else FontWeight.Normal,
            color = if (highlight) Color.White else Color(0xFFDDDDDD),
            fontSize = 12.sp
        )
    }
}
