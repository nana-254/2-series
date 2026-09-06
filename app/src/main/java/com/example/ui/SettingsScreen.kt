package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Currency
import com.example.MainViewModel
import com.example.ui.theme.AccentTheme

@Composable
fun SettingsScreen(viewModel: MainViewModel) {
    val state by viewModel.state.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Executive Settings",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            "Personalize theme accents, currency & KRA parameters",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    MBadge("v2.4")
                }
                Spacer(modifier = Modifier.height(10.dp))
                MStripeLine(modifier = Modifier.fillMaxWidth(), height = 3.dp)
            }
        }

        // Accent Theme Chooser (User Request: "multiple and custom ascent chooser")
        item {
            GlassCard(cornerRadius = 24.dp) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.ColorLens, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text(
                        "Custom Accent Theme",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Text(
                    "Dynamic ambient lighting and UI highlight colors",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                AccentTheme.values().forEach { accent ->
                    val isSelected = accent == state.selectedAccentTheme
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) Color(0x22FFFFFF) else Color(0x0CFFFFFF))
                            .border(
                                1.2.dp,
                                if (isSelected) accent.primaryColor else Color(0x18FFFFFF),
                                RoundedCornerShape(14.dp)
                            )
                            .clickable { viewModel.setAccentTheme(accent) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(accent.primaryColor)
                                    .border(1.5.dp, Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Column {
                                Text(
                                    accent.displayName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    if (isSelected) "Active Accent" else "Tap to apply",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) accent.primaryColor else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        RadioButton(
                            selected = isSelected,
                            onClick = { viewModel.setAccentTheme(accent) },
                            colors = RadioButtonDefaults.colors(selectedColor = accent.primaryColor)
                        )
                    }
                }
            }
        }

        // Currency Selector
        item {
            GlassCard(cornerRadius = 24.dp) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Paid, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text(
                        "Market Currency",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Text(
                    "All landed calculations & MSRP convert automatically",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                Currency.values().forEach { currency ->
                    val isSelected = currency == state.selectedCurrency
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) Color(0x22FFFFFF) else Color(0x0CFFFFFF))
                            .border(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else Color(0x18FFFFFF), RoundedCornerShape(12.dp))
                            .clickable { viewModel.setCurrency(currency) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                "${currency.name} (${currency.symbol})",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                            Text(
                                "1 USD ≈ ${currency.rateFromUsd} ${currency.code}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }

                        RadioButton(
                            selected = isSelected,
                            onClick = { viewModel.setCurrency(currency) },
                            colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                        )
                    }
                }
            }
        }

        // Platform & Compliance Info
        item {
            GlassCard(cornerRadius = 20.dp) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text(
                        "About This Platform",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "BMW 2 Series (G42/G87) Executive Intelligence Suite\n" +
                    "Designed specifically for performance automotive enthusiasts and vehicle importers in Kenya.\n" +
                    "Features authentic 2023 B48, B58, and S58 engine telemetry, live KRA EAC tariff schedules, aftermarket M2 conversion builder, and 3D digital asset viewer.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
