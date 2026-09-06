package com.example.ui

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.*
import com.example.ui.theme.MDarkBlue
import com.example.ui.theme.MLightBlue
import com.example.ui.theme.MRed

@Composable
fun ConfigureScreen(viewModel: MainViewModel) {
    val state by viewModel.state.collectAsState()
    val selectedTrim = trims.first { it.id == state.selectedTrimId }
    val selectedColor = exteriorColors.firstOrNull { it.id == state.selectedColorId } ?: exteriorColors.first()

    val selectedMods = modCatalog.filter { state.selectedModIds.contains(it.id) }
    val modsTotalPrice = selectedMods.sumOf { it.price }
    val netHpGain = selectedMods.sumOf { it.hpDelta }
    val netWeightDelta = selectedMods.sumOf { it.weightDeltaLbs }

    val selectedOptionsTotal = state.selectedOptionIds.sumOf { id -> options.firstOrNull { it.id == id }?.price ?: 0 }
    val paintPrice = selectedColor.price
    val totalBuildUsd = selectedTrim.startingPrice + paintPrice + modsTotalPrice + selectedOptionsTotal

    val kenyaImport = viewModel.calculateKenyaImport(selectedTrim, totalBuildUsd.toDouble())
    var showKenyaTaxesSheet by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Configurator & Mod Studio",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        "2023 BMW 2 Series Customization",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                MBadge("${selectedMods.size} Mods Active")
            }
            Spacer(modifier = Modifier.height(10.dp))
            MStripeLine(modifier = Modifier.fillMaxWidth(), height = 3.dp)
        }

        // Live Configured Vehicle Hero Banner
        item {
            GlassCard(cornerRadius = 24.dp) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    AsyncImage(
                        model = selectedTrim.heroImageUrl,
                        contentDescription = selectedTrim.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Overlay with Paint badge and Mod indicator
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xD9000000))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                selectedColor.name,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                fontSize = 11.sp
                            )
                        }

                        if (netHpGain > 0) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xD9E4002B))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    "+$netHpGain HP Modded",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            selectedTrim.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            "${selectedTrim.engineDesc} • ${selectedTrim.transmission}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            "Total Build",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            formatCurrency(totalBuildUsd.toDouble(), state.selectedCurrency),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Real-time Telemetry Stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ExecutiveStatItem(
                        title = "Power",
                        value = "${selectedTrim.hp + netHpGain} HP",
                        subtitle = if (netHpGain > 0) "+$netHpGain tuned" else "Stock",
                        modifier = Modifier.weight(1f)
                    )
                    ExecutiveStatItem(
                        title = "0-60 MPH",
                        value = "${selectedTrim.zeroToSixty}s",
                        subtitle = selectedTrim.drivetrain.take(4),
                        modifier = Modifier.weight(1f)
                    )
                    ExecutiveStatItem(
                        title = "Weight",
                        value = "${selectedTrim.curbWeightLbs + netWeightDelta} lbs",
                        subtitle = if (netWeightDelta < 0) "$netWeightDelta lbs saved" else "Stock curb",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 1. Select 2023 Trim
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "1. Base Model Trim (2023 MY)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    trims.forEach { trim ->
                        val isSelected = trim.id == state.selectedTrimId
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color(0x18FFFFFF)
                                )
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color(0x28FFFFFF),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable { viewModel.selectTrim(trim.id) }
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(
                                    trim.id.uppercase(),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    "${trim.hp} HP",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "$${trim.startingPrice / 1000}k",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Exterior Paint & Wrap Studio
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.Palette, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        Text(
                            "2. Exterior Paint & Finish",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Text(
                        "${selectedColor.name} (${if (selectedColor.price == 0) "Included" else "+$${selectedColor.price}"})",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(exteriorColors) { color ->
                        val isSelected = color.id == state.selectedColorId
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { viewModel.selectColor(color.id) }
                                .padding(2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color(color.colorHex))
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color(0x45FFFFFF),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        tint = if (color.colorHex == 0xFFF0F2F5L) Color.Black else Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                color.finish,
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 9.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // 3. M-Conversion & Aftermarket Modding Studio
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.Build, contentDescription = null, tint = MRed, modifier = Modifier.size(18.dp))
                        Text(
                            "3. M Aero & Performance Modding Studio",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Text(
                    "Convert 230i with M2-style aggressive bumpers, titanium quad exhaust, and track components:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Mod Categories
                ModCategory.values().forEach { category ->
                    val categoryMods = modCatalog.filter { it.category == category }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color(0x14FFFFFF))
                            .border(1.dp, Color(0x25FFFFFF), RoundedCornerShape(18.dp))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            category.displayName,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MLightBlue
                        )

                        categoryMods.forEach { mod ->
                            val isSelected = state.selectedModIds.contains(mod.id)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) Color(0x280082D1) else Color(0x0EFFFFFF))
                                    .border(
                                        width = if (isSelected) 1.2.dp else 0.8.dp,
                                        color = if (isSelected) MLightBlue else Color(0x18FFFFFF),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { viewModel.toggleMod(mod.id) }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = { viewModel.toggleMod(mod.id) },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = MaterialTheme.colorScheme.primary
                                    )
                                )

                                Column(modifier = Modifier.weight(1f).padding(horizontal = 8.dp)) {
                                    Text(
                                        mod.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        mod.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp
                                    )
                                    if (mod.hpDelta > 0 || mod.weightDeltaLbs != 0) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            if (mod.hpDelta > 0) {
                                                Text("+${mod.hpDelta} HP", color = MRed, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            }
                                            if (mod.weightDeltaLbs != 0) {
                                                Text("${mod.weightDeltaLbs} lbs", color = MLightBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }

                                Text(
                                    "+$${mod.price}",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. BMW Factory Option Packages
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "4. BMW Factory Option Packages",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                options.forEach { option ->
                    val isChecked = state.selectedOptionIds.contains(option.id)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isChecked) Color(0x22FFFFFF) else Color(0x10FFFFFF))
                            .border(1.dp, if (isChecked) MaterialTheme.colorScheme.primary else Color(0x20FFFFFF), RoundedCornerShape(14.dp))
                            .clickable { viewModel.toggleOption(option.id) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { viewModel.toggleOption(option.id) }
                        )
                        Column(modifier = Modifier.weight(1f).padding(horizontal = 8.dp)) {
                            Text(
                                option.name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                            Text(
                                option.description,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.sp
                            )
                        }
                        Text(
                            "+$${option.price}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // 5. Total Cost of Ownership & Kenya KRA Import Breakdown Sheet
        item {
            GlassCard(cornerRadius = 24.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Executive Build & Import Sheet",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            "Comprehensive valuation & KRA customs clearance",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(onClick = { showKenyaTaxesSheet = !showKenyaTaxesSheet }) {
                        Icon(
                            if (showKenyaTaxesSheet) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = "Toggle KRA",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                SpecRow("Base Vehicle MSRP (2023 ${selectedTrim.id.uppercase()})", "$${selectedTrim.startingPrice}")
                if (selectedColor.price > 0) {
                    SpecRow("Paint / Wrap (${selectedColor.name})", "+$${selectedColor.price}")
                }
                if (modsTotalPrice > 0) {
                    SpecRow("Aero & Performance Mods (${selectedMods.size} items)", "+$$modsTotalPrice", highlight = true)
                }
                if (selectedOptionsTotal > 0) {
                    SpecRow("Factory Options Packages", "+$$selectedOptionsTotal")
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0x30FFFFFF))
                SpecRow("Total Build FOB (USD)", "$$totalBuildUsd", highlight = true)
                SpecRow("Total Build (${state.selectedCurrency.code})", formatCurrency(totalBuildUsd.toDouble(), state.selectedCurrency), highlight = true)

                // Kenya KRA Breakdown Section
                AnimatedVisibility(visible = showKenyaTaxesSheet) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 14.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0x18FFFFFF))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            "KRA Motor Vehicle Importation Schedule (Mombasa Port)",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MLightBlue
                        )
                        SpecRow("Freight & Marine Insurance", "$2,150")
                        SpecRow("Customs Value (CIF Mombasa)", formatCurrency(kenyaImport.cifUsd, Currency.USD))
                        SpecRow("Import Duty (35% EAC CET)", formatCurrency(kenyaImport.importDutyKsh / 130.0, state.selectedCurrency))
                        SpecRow("Excise Duty (${kenyaImport.exciseDutyRatePercent}% on ${selectedTrim.cc} cc)", formatCurrency(kenyaImport.exciseDutyKsh / 130.0, state.selectedCurrency))
                        SpecRow("VAT (16% Compounded)", formatCurrency(kenyaImport.vatKsh / 130.0, state.selectedCurrency))
                        SpecRow("IDF (2.5%) & RDL (2.0%)", formatCurrency((kenyaImport.idfKsh + kenyaImport.rdlKsh) / 130.0, state.selectedCurrency))
                        SpecRow("Port, Radiation, KEBS & NTSA Plates", formatCurrency(kenyaImport.portAndRegistrationKsh / 130.0, state.selectedCurrency))
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = Color(0x25FFFFFF))
                        SpecRow("Total KRA Duties & Taxes", formatCurrency(kenyaImport.totalTaxesKsh / 130.0, state.selectedCurrency), highlight = true)
                        SpecRow("Total Landed in Kenya", formatCurrency(kenyaImport.totalLandedCostUsd, state.selectedCurrency), highlight = true)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}


