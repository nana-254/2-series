package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Authentic OLED & Dark Tones
val OledBlack = Color(0xFF050608)
val DarkSurface = Color(0xFF0F1116)
val DarkElevated = Color(0xFF161920)
val CarbonSurface = Color(0xFF1B1F28)

// iOS Frosted Glass Tones
val GlassBackground = Color(0x18FFFFFF)
val GlassSurface = Color(0x28FFFFFF)
val GlassBorder = Color(0x38FFFFFF)
val GlassBorderSubtle = Color(0x1AFFFFFF)

// Authentic BMW M Tri-Color Heritage Palette
val MLightBlue = Color(0xFF0082D1)  // Bavarian Cyan Blue
val MDarkBlue = Color(0xFF003876)   // Velvet Dark Blue
val MRed = Color(0xFFE4002B)        // Motorsport Red

// Iconic BMW Individual Accent Theme Colors
val IsleOfManGreen = Color(0xFF0E7A53)
val SaoPauloYellow = Color(0xFFD7FF00)
val MarinaBayBlue = Color(0xFF0066B3)
val TorontoRed = Color(0xFFE81B23)
val ThundernightPurple = Color(0xFF7928CA)
val FrozenPureGrey = Color(0xFF9EA3A9)
val ZandvoortBlue = Color(0xFF4A90E2)

// Accent Theme Selection
enum class AccentTheme(val displayName: String, val primaryColor: Color, val secondaryColor: Color) {
    M_MOTORSPORT("M Motorsport Tri-Color", MLightBlue, MRed),
    ISLE_OF_MAN_GREEN("Isle of Man Green", IsleOfManGreen, Color(0xFF18A774)),
    SAO_PAULO_YELLOW("Sao Paulo Yellow", SaoPauloYellow, Color(0xFFB5D600)),
    MARINA_BAY_BLUE("Marina Bay Blue", MarinaBayBlue, MLightBlue),
    TORONTO_RED("Toronto Red", TorontoRed, Color(0xFFFF525A)),
    THUNDERNIGHT_PURPLE("Thundernight Violet", ThundernightPurple, Color(0xFFA855F7)),
    FROZEN_PURE_GREY("Frozen Pure Grey", FrozenPureGrey, Color(0xFFCED3D9)),
    CUSTOM("Custom Hue", Color(0xFF00E5FF), Color(0xFF0082D1))
}

