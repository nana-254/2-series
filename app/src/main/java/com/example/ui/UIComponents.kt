package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Currency
import com.example.ui.theme.MDarkBlue
import com.example.ui.theme.MLightBlue
import com.example.ui.theme.MRed

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 22.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    val glassBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0x28FFFFFF),
            Color(0x10FFFFFF),
            Color(0x08FFFFFF)
        )
    )
    val borderBrush = Brush.linearGradient(
        colors = listOf(
            Color(0x45FFFFFF),
            Color(0x15FFFFFF),
            Color(0x35FFFFFF)
        )
    )

    Box(
        modifier = modifier
            .clip(shape)
            .background(brush = glassBrush)
            .border(width = 1.dp, brush = borderBrush, shape = shape)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            content = content
        )
    }
}

@Composable
fun MStripeLine(
    modifier: Modifier = Modifier,
    height: Dp = 4.dp
) {
    Row(
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(height / 2))
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(MLightBlue)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(MDarkBlue)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(MRed)
        )
    }
}

@Composable
fun MBadge(
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color = Color(0x280082D1)
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(30.dp))
            .background(containerColor)
            .border(1.dp, Color(0x35FFFFFF), RoundedCornerShape(30.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(MLightBlue)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
    }
}

@Composable
fun SpecRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    highlight: Boolean = false
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (highlight) FontWeight.Bold else FontWeight.SemiBold,
            color = if (highlight) MaterialTheme.colorScheme.primary else Color.White
        )
    }
}

@Composable
fun ExecutiveStatItem(
    title: String,
    value: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0x18FFFFFF))
            .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        if (subtitle != null) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 10.sp
            )
        }
    }
}

fun formatCurrency(amountUsd: Double, currency: Currency): String {
    val converted = amountUsd * currency.rateFromUsd
    return when (currency) {
        Currency.KSH -> {
            // E.g. KSh 11,250,000
            "KSh %,.0f".format(converted)
        }
        Currency.USD -> {
            // E.g. $49,900
            "$%,.0f".format(converted)
        }
        Currency.USDT -> {
            "%,.0f USDT".format(converted)
        }
        Currency.BTC -> {
            "%.4f BTC".format(converted)
        }
        Currency.SOL -> {
            "%.2f SOL".format(converted)
        }
        Currency.MONERO -> {
            "%.3f XMR".format(converted)
        }
    }
}
