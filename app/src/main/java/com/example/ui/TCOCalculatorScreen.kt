package com.example.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Timeline
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Currency
import com.example.MainViewModel
import com.example.trims
import com.example.ui.theme.MDarkBlue
import com.example.ui.theme.MLightBlue
import com.example.ui.theme.MRed

@Composable
fun TCOCalculatorScreen(viewModel: MainViewModel) {
    val state by viewModel.state.collectAsState()
    val selectedTrim = trims.first { it.id == state.selectedTrimId }

    // Accurate KRA Import calculation from ViewModel
    val importCalc = viewModel.calculateKenyaImport(selectedTrim, selectedTrim.startingPrice.toDouble())

    // Operating Parameters
    val annualFuel = (state.annualMileage.toDouble() / selectedTrim.mpgCity) * state.gasPricePerGallon
    val annualInsurance = if (selectedTrim.id == "m2") 2200.0 else if (selectedTrim.id == "m240i") 1800.0 else 1400.0
    val annualMaint = if (selectedTrim.id == "m2") 1400.0 else if (selectedTrim.id == "m240i") 950.0 else 750.0
    val annualOperatingCost = annualFuel + annualInsurance + annualMaint

    val fiveYearProjections: List<Double> = List(5) { year: Int ->
        val yearIndex = year + 1
        val cumulativeOp = annualOperatingCost * yearIndex
        importCalc.totalLandedCostUsd + cumulativeOp
    }

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
                        "TCO & Kenya Import Duty",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        "5-Year Projection & Mombasa Port Clearance",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                MBadge("KRA 2023/24")
            }
            Spacer(modifier = Modifier.height(10.dp))
            MStripeLine(modifier = Modifier.fillMaxWidth(), height = 3.dp)
        }

        // Vehicle Selector Quick Strip
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            trims.forEach { trim ->
                val isSelected = trim.id == state.selectedTrimId
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color(0x18FFFFFF))
                        .border(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else Color(0x20FFFFFF), RoundedCornerShape(12.dp))
                        .clickable { viewModel.selectTrim(trim.id) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        trim.id.uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White
                    )
                }
            }
        }

        // Card 1: Official KRA Customs & Taxes (Mombasa Port)
        GlassCard(cornerRadius = 22.dp) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.AccountBalance, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(
                    "KRA Mombasa Import Schedule",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Based on 2023 ${selectedTrim.name} (${selectedTrim.cc} cc engine)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))
            SpecRow("Vehicle Purchase FOB (USD)", "$${selectedTrim.startingPrice}")
            SpecRow("Freight, Marine Insurance & Handling", "$2,150")
            SpecRow("Customs Valuation (CIF Mombasa)", formatCurrency(importCalc.cifUsd, state.selectedCurrency))
            SpecRow("Import Duty (35% EAC Tariff)", formatCurrency(importCalc.importDutyKsh / 130.0, state.selectedCurrency))
            SpecRow(
                "Excise Duty (${importCalc.exciseDutyRatePercent}% on ${selectedTrim.cc} cc)",
                formatCurrency(importCalc.exciseDutyKsh / 130.0, state.selectedCurrency),
                highlight = selectedTrim.cc <= 2000
            )
            SpecRow("VAT (16% Compounded)", formatCurrency(importCalc.vatKsh / 130.0, state.selectedCurrency))
            SpecRow("IDF (2.5%) & RDL (2.0%)", formatCurrency((importCalc.idfKsh + importCalc.rdlKsh) / 130.0, state.selectedCurrency))
            SpecRow("Port Wharfage, Radiation & NTSA Plates", formatCurrency(importCalc.portAndRegistrationKsh / 130.0, state.selectedCurrency))

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0x30FFFFFF))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Total Landed Cost",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        "Duty Paid & Registered in Nairobi",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    formatCurrency(importCalc.totalLandedCostUsd, state.selectedCurrency),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Card 2: Operating Cost Inputs
        GlassCard(cornerRadius = 22.dp) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.LocalGasStation, contentDescription = null, tint = MLightBlue)
                Text(
                    "Annual Usage & Fuel Parameters",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = state.annualMileage.toString(),
                    onValueChange = { it.toIntOrNull()?.let { v -> viewModel.setAnnualMileage(v) } },
                    label = { Text("Annual Miles") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color(0x30FFFFFF)
                    )
                )

                OutlinedTextField(
                    value = state.gasPricePerGallon.toString(),
                    onValueChange = { it.toDoubleOrNull()?.let { v -> viewModel.setGasPrice(v) } },
                    label = { Text("Gas $/Gal") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color(0x30FFFFFF)
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            SpecRow("Est. Annual Fuel (${selectedTrim.mpgCity} MPG)", formatCurrency(annualFuel, state.selectedCurrency))
            SpecRow("Comprehensive Annual Insurance", formatCurrency(annualInsurance, state.selectedCurrency))
            SpecRow("Routine Maintenance & Wear", formatCurrency(annualMaint, state.selectedCurrency))
            SpecRow("Total Annual Operating Cost", formatCurrency(annualOperatingCost, state.selectedCurrency), highlight = true)
        }

        // Card 3: Business 5-Year Cost Projection Chart
        GlassCard(cornerRadius = 22.dp) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Timeline, contentDescription = null, tint = MRed)
                    Text(
                        "5-Year Cumulative TCO Projection",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                MBadge("5-Year Horizon")
            }

            Spacer(modifier = Modifier.height(16.dp))

            val maxProjection = fiveYearProjections.last().toFloat()
            val minProjection = importCalc.totalLandedCostUsd.toFloat()

            // Executive Stacked Bar Chart
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val bottomAxisY = canvasHeight - 30f
                val topAxisY = 20f
                val usableHeight = bottomAxisY - topAxisY

                // Grid lines
                for (i in 0..4) {
                    val y = bottomAxisY - (usableHeight * (i / 4f))
                    drawLine(
                        color = Color(0x18FFFFFF),
                        start = Offset(0f, y),
                        end = Offset(canvasWidth, y),
                        strokeWidth = 1f
                    )
                }

                val barCount = fiveYearProjections.size
                val stepX = canvasWidth / barCount
                val barWidth = 36f

                fiveYearProjections.forEachIndexed { index, cumulativeCost ->
                    val x = index * stepX + (stepX / 2f) - (barWidth / 2f)

                    // Landed acquisition portion
                    val acquisitionRatio = (importCalc.totalLandedCostUsd.toFloat() / maxProjection) * usableHeight
                    val acquisitionTopY = bottomAxisY - acquisitionRatio

                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(MLightBlue, MDarkBlue)
                        ),
                        topLeft = Offset(x, acquisitionTopY),
                        size = Size(barWidth, acquisitionRatio),
                        cornerRadius = CornerRadius(4f, 4f)
                    )

                    // Cumulative operating cost on top
                    val totalRatio = (cumulativeCost.toFloat() / maxProjection) * usableHeight
                    val opHeight = totalRatio - acquisitionRatio
                    val opTopY = bottomAxisY - totalRatio

                    if (opHeight > 0) {
                        drawRoundRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(MRed, Color(0xFF8B0015))
                            ),
                            topLeft = Offset(x, opTopY),
                            size = Size(barWidth, opHeight),
                            cornerRadius = CornerRadius(4f, 4f)
                        )
                    }
                }

                // Baseline
                drawLine(
                    color = Color(0x40FFFFFF),
                    start = Offset(0f, bottomAxisY),
                    end = Offset(canvasWidth, bottomAxisY),
                    strokeWidth = 1.5f
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Year Labels & Amounts
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                fiveYearProjections.forEachIndexed { index, cost ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Year ${index + 1}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            formatCurrency(cost, state.selectedCurrency),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 9.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(MLightBlue))
                    Text("Landed Car Cost", style = MaterialTheme.typography.labelSmall, color = Color.White)
                }
                Spacer(modifier = Modifier.width(18.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(MRed))
                    Text("Cumulative Fuel & Maint", style = MaterialTheme.typography.labelSmall, color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}


