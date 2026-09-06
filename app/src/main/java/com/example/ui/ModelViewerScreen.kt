package com.example.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MainViewModel
import com.example.exteriorColors
import com.example.modCatalog
import com.example.trims
import com.example.ui.theme.MDarkBlue
import com.example.ui.theme.MLightBlue
import com.example.ui.theme.MRed
import kotlin.math.*

// 3D Point for native canvas perspective projection
private data class Point3D(val x: Float, val y: Float, val z: Float) {
    fun rotateY(angleRad: Float): Point3D {
        val cosA = cos(angleRad)
        val sinA = sin(angleRad)
        return Point3D(
            x = x * cosA + z * sinA,
            y = y,
            z = -x * sinA + z * cosA
        )
    }

    fun rotateX(angleRad: Float): Point3D {
        val cosA = cos(angleRad)
        val sinA = sin(angleRad)
        return Point3D(
            x = x,
            y = y * cosA - z * sinA,
            z = y * sinA + z * cosA
        )
    }

    fun project(centerX: Float, centerY: Float, fov: Float = 600f, scale: Float = 1.0f): Offset {
        val depth = max(100f, fov + z)
        val factor = (fov / depth) * scale
        return Offset(
            x = centerX + x * factor,
            y = centerY + y * factor
        )
    }
}

private data class Polygon3D(
    val points: List<Point3D>,
    val color: Color,
    val isOutlineOnly: Boolean = false,
    val strokeWidth: Float = 2f
) {
    val avgZ: Float get() = points.map { it.z }.average().toFloat()
}

@Composable
fun ModelViewerScreen(viewModel: MainViewModel) {
    val state by viewModel.state.collectAsState()
    val selectedTrim = trims.first { it.id == state.selectedTrimId }
    val selectedColor = exteriorColors.firstOrNull { it.id == state.selectedColorId } ?: exteriorColors.first()
    val activeModsCount = state.selectedModIds.size

    val hasCarbonSplitter = state.selectedModIds.contains("mod_front_lip")
    val hasValvedExhaust = state.selectedModIds.contains("mod_valved_exhaust")
    val hasCarbonHood = state.selectedModIds.contains("mod_carbon_hood")
    val hasCsWing = state.selectedModIds.contains("mod_wing")
    val hasWheels = state.selectedModIds.contains("mod_wheels_963m")
    val hasKwCoilovers = state.selectedModIds.contains("mod_kw_v3")

    // Camera angles (Euler angles in degrees)
    var yawDeg by remember { mutableStateOf(35f) }
    var pitchDeg by remember { mutableStateOf(15f) }
    var zoomScale by remember { mutableStateOf(1.05f) }
    var isAutoRotating by remember { mutableStateOf(true) }
    var showWireframe by remember { mutableStateOf(false) }
    var selectedHotspot by remember { mutableStateOf<String?>(null) }

    // Auto rotate continuous loop
    val infiniteTransition = rememberInfiniteTransition(label = "autoRotate")
    val autoYawOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(22000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "yawSpin"
    )

    val currentYaw = if (isAutoRotating) (yawDeg + autoYawOffset) % 360f else yawDeg
    val bodyBaseColor = Color(selectedColor.colorHex)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF181C26),
                        Color(0xFF090A0E)
                    )
                )
            )
    ) {
        // Native 3D Interactive Canvas
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {
                            isAutoRotating = false
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            yawDeg = (yawDeg + dragAmount.x * 0.45f) % 360f
                            pitchDeg = (pitchDeg - dragAmount.y * 0.35f).coerceIn(-10f, 65f)
                        }
                    )
                }
        ) {
            val centerX = size.width / 2f
            val centerY = size.height * 0.52f
            val yawRad = Math.toRadians(currentYaw.toDouble()).toFloat()
            val pitchRad = Math.toRadians(pitchDeg.toDouble()).toFloat()

            // 1. Draw Studio Ground Reflection Grid
            drawStudioGround(centerX, centerY, yawRad, pitchRad, zoomScale)

            // 2. Generate 3D Polygons for BMW G42/G87 Chassis
            val polygons = buildCarGeometry(
                bodyColor = bodyBaseColor,
                trimId = selectedTrim.id,
                hasCarbonSplitter = hasCarbonSplitter,
                hasValvedExhaust = hasValvedExhaust,
                hasCarbonHood = hasCarbonHood,
                hasCsWing = hasCsWing,
                hasWheels = hasWheels,
                hasKwCoilovers = hasKwCoilovers,
                showWireframe = showWireframe
            )

            // 3. Transform 3D Points through Yaw & Pitch
            val transformedPolys = polygons.map { poly ->
                val transformedPoints = poly.points.map { pt ->
                    pt.rotateY(yawRad).rotateX(pitchRad)
                }
                Polygon3D(transformedPoints, poly.color, poly.isOutlineOnly, poly.strokeWidth)
            }

            // 4. Painter's Algorithm: Sort by depth (farthest Z first)
            val sortedPolys = transformedPolys.sortedBy { it.avgZ }

            // 5. Draw Polygons
            sortedPolys.forEach { poly ->
                val projectedPoints = poly.points.map { it.project(centerX, centerY, 700f, zoomScale) }
                if (projectedPoints.size >= 3) {
                    val path = Path().apply {
                        moveTo(projectedPoints[0].x, projectedPoints[0].y)
                        for (i in 1 until projectedPoints.size) {
                            lineTo(projectedPoints[i].x, projectedPoints[i].y)
                        }
                        close()
                    }

                    if (poly.isOutlineOnly || showWireframe) {
                        drawPath(
                            path = path,
                            color = if (showWireframe) Color(0x7000B4D8) else poly.color,
                            style = Stroke(width = poly.strokeWidth)
                        )
                    } else {
                        // Ambient shading based on face orientation / light
                        val normalZ = computeNormalZ(poly.points)
                        val lightFactor = (0.70f + normalZ * 0.30f).coerceIn(0.45f, 1.25f)
                        val shadedColor = Color(
                            red = (poly.color.red * lightFactor).coerceIn(0f, 1f),
                            green = (poly.color.green * lightFactor).coerceIn(0f, 1f),
                            blue = (poly.color.blue * lightFactor).coerceIn(0f, 1f),
                            alpha = poly.color.alpha
                        )

                        drawPath(path = path, color = shadedColor)
                        // Specular subtle edge
                        drawPath(
                            path = path,
                            color = Color(0x30FFFFFF),
                            style = Stroke(width = 1f)
                        )
                    }
                }
            }

            // 6. Draw Hotspot Markers in 3D Space
            drawHotspots(
                centerX = centerX,
                centerY = centerY,
                yawRad = yawRad,
                pitchRad = pitchRad,
                zoomScale = zoomScale,
                selectedHotspot = selectedHotspot
            )
        }

        // Top Floating HUD Overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xEA08090C),
                                Color(0xD012151C)
                            )
                        )
                    )
                    .border(1.dp, Color(0x35FFFFFF), RoundedCornerShape(20.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(0x250082D1)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.ViewInAr,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                "3D Digital Twin Viewer",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                "${selectedTrim.name} • ${selectedColor.name}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    MBadge(if (activeModsCount > 0) "$activeModsCount Aero Mods" else "Factory OEM")
                }

                MStripeLine(modifier = Modifier.fillMaxWidth(), height = 2.5.dp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Drag to orbit 360° • Pinch / view buttons for camera angles",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                    Text(
                        "${currentYaw.toInt()}° Yaw",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (selectedHotspot != null) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp)),
                        color = Color(0x300082D1)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                selectedHotspot ?: "",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable { selectedHotspot = null }
                            )
                        }
                    }
                }
            }
        }

        // Bottom Controls HUD & View Switcher
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.BottomCenter)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Camera Angle Preset Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CameraPresetChip("Front 3/4", modifier = Modifier.weight(1f)) {
                        yawDeg = 45f
                        pitchDeg = 15f
                        isAutoRotating = false
                    }
                    CameraPresetChip("Side View", modifier = Modifier.weight(1f)) {
                        yawDeg = 90f
                        pitchDeg = 5f
                        isAutoRotating = false
                    }
                    CameraPresetChip("Rear Aero", modifier = Modifier.weight(1f)) {
                        yawDeg = 220f
                        pitchDeg = 18f
                        isAutoRotating = false
                    }
                    CameraPresetChip("Top Down", modifier = Modifier.weight(1f)) {
                        yawDeg = 0f
                        pitchDeg = 58f
                        isAutoRotating = false
                    }
                }

                // Control Toolbar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xEA08090C))
                        .border(1.dp, Color(0x30FFFFFF), RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Auto Orbit Toggle
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { isAutoRotating = !isAutoRotating }
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = null,
                            tint = if (isAutoRotating) MaterialTheme.colorScheme.primary else Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            if (isAutoRotating) "Orbiting" else "Paused",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isAutoRotating) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    }

                    // Wireframe View Toggle
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { showWireframe = !showWireframe }
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Default.Layers,
                            contentDescription = null,
                            tint = if (showWireframe) MLightBlue else Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            "Wireframe",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (showWireframe) MLightBlue else Color.White
                        )
                    }

                    // Zoom Scale Controls
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(
                            onClick = { zoomScale = (zoomScale - 0.15f).coerceAtLeast(0.65f) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Zoom out", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                        Text(
                            "${(zoomScale * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(
                            onClick = { zoomScale = (zoomScale + 0.15f).coerceAtMost(1.85f) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Zoom in", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CameraPresetChip(label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, Color(0x30FFFFFF), RoundedCornerShape(12.dp))
            .clickable { onClick() },
        color = Color(0xBB12151F)
    ) {
        Box(
            modifier = Modifier.padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                fontSize = 11.sp
            )
        }
    }
}

private fun DrawScope.drawStudioGround(centerX: Float, centerY: Float, yawRad: Float, pitchRad: Float, scale: Float) {
    val groundY = 115f
    val gridExtent = 420f
    val step = 70f

    // Concentric ground circles
    for (r in listOf(120f, 240f, 360f)) {
        val pts = (0..36).map { i ->
            val ang = Math.toRadians((i * 10).toDouble()).toFloat()
            val x = cos(ang) * r
            val z = sin(ang) * r
            Point3D(x, groundY, z).rotateY(yawRad).rotateX(pitchRad).project(centerX, centerY, 700f, scale)
        }
        val path = Path().apply {
            moveTo(pts[0].x, pts[0].y)
            for (i in 1 until pts.size) lineTo(pts[i].x, pts[i].y)
            close()
        }
        drawPath(path, color = Color(0x15FFFFFF), style = Stroke(1f))
    }

    // Shadow blob under the vehicle
    val shadowCenter = Point3D(0f, groundY - 2f, 0f).rotateY(yawRad).rotateX(pitchRad).project(centerX, centerY, 700f, scale)
    drawOval(
        color = Color(0x80000000),
        topLeft = Offset(shadowCenter.x - 140f * scale, shadowCenter.y - 45f * scale),
        size = Size(280f * scale, 90f * scale)
    )
}

private fun DrawScope.drawHotspots(
    centerX: Float,
    centerY: Float,
    yawRad: Float,
    pitchRad: Float,
    zoomScale: Float,
    selectedHotspot: String?
) {
    val hotspots = listOf(
        Pair(Point3D(0f, -40f, -140f), "TwinPower Turbo Engine (B48/B58/S58)"),
        Pair(Point3D(65f, 40f, -110f), "M Sport Brakes (4-Piston Calipers)"),
        Pair(Point3D(0f, -70f, 150f), "Carbon Aero Diffuser & Exhaust"),
        Pair(Point3D(0f, -65f, 0f), "M Sport Cockpit & Curved Display")
    )

    hotspots.forEach { (pt, label) ->
        val transformed = pt.rotateY(yawRad).rotateX(pitchRad)
        // Only draw if facing towards camera
        if (transformed.z < 200f) {
            val proj = transformed.project(centerX, centerY, 700f, zoomScale)
            val isSelected = selectedHotspot == label

            drawCircle(
                color = if (isSelected) MRed else MLightBlue,
                radius = if (isSelected) 10f else 7f,
                center = proj
            )
            drawCircle(
                color = Color.White,
                radius = 3f,
                center = proj
            )
        }
    }
}

private fun computeNormalZ(points: List<Point3D>): Float {
    if (points.size < 3) return 0f
    val v1x = points[1].x - points[0].x
    val v1y = points[1].y - points[0].y
    val v2x = points[2].x - points[0].x
    val v2y = points[2].y - points[0].y
    return (v1x * v2y - v1y * v2x)
}

// 3D Geometry model builder for BMW G42 2 Series / G87 M2 Coupe
private fun buildCarGeometry(
    bodyColor: Color,
    trimId: String,
    hasCarbonSplitter: Boolean,
    hasValvedExhaust: Boolean,
    hasCarbonHood: Boolean,
    hasCsWing: Boolean,
    hasWheels: Boolean,
    hasKwCoilovers: Boolean,
    showWireframe: Boolean
): List<Polygon3D> {
    val polys = mutableListOf<Polygon3D>()

    val isM2 = trimId == "m2"
    val isM240i = trimId == "m240i"

    val suspensionDrop = if (hasKwCoilovers) 10f else 0f
    val groundY = 85f + suspensionDrop
    val roofY = -55f + suspensionDrop
    val beltY = -10f + suspensionDrop
    val hoodY = -25f + suspensionDrop

    val halfWidth = if (isM2) 64f else 58f
    val roofHalfWidth = 42f
    val hoodFrontZ = -160f
    val cowlZ = -50f
    val rearWindowZ = 70f
    val trunkLipZ = 150f
    val rearBumperZ = 165f
    val frontBumperZ = -175f

    val windowColor = Color(0xCC1A2333)
    val carbonColor = Color(0xFF14171A)
    val chromeBlackColor = Color(0xFF1B1F24)
    val wheelColor = if (hasWheels) Color(0xFFC0A060) else Color(0xFF8E95A0)

    // 1. Hood
    val hoodColor = if (hasCarbonHood) carbonColor else bodyColor
    polys.add(
        Polygon3D(
            listOf(
                Point3D(-halfWidth + 8f, hoodY, cowlZ),
                Point3D(halfWidth - 8f, hoodY, cowlZ),
                Point3D(halfWidth - 14f, hoodY + 8f, hoodFrontZ),
                Point3D(-halfWidth + 14f, hoodY + 8f, hoodFrontZ)
            ),
            hoodColor
        )
    )

    // Hood Powerdome (M2 / M240i characteristic)
    if (isM2 || isM240i || hasCarbonHood) {
        polys.add(
            Polygon3D(
                listOf(
                    Point3D(-16f, hoodY - 4f, cowlZ + 10f),
                    Point3D(16f, hoodY - 4f, cowlZ + 10f),
                    Point3D(12f, hoodY + 4f, hoodFrontZ + 20f),
                    Point3D(-12f, hoodY + 4f, hoodFrontZ + 20f)
                ),
                if (hasCarbonHood) Color(0xFF23272B) else bodyColor.copy(alpha = 0.95f)
            )
        )
    }

    // 2. Windshield
    polys.add(
        Polygon3D(
            listOf(
                Point3D(-roofHalfWidth, roofY, cowlZ + 25f),
                Point3D(roofHalfWidth, roofY, cowlZ + 25f),
                Point3D(halfWidth - 8f, hoodY, cowlZ),
                Point3D(-halfWidth + 8f, hoodY, cowlZ)
            ),
            windowColor
        )
    )

    // 3. Roof Panel (M Carbon roof for M2)
    val roofColor = if (isM2) carbonColor else bodyColor
    polys.add(
        Polygon3D(
            listOf(
                Point3D(-roofHalfWidth, roofY, cowlZ + 25f),
                Point3D(roofHalfWidth, roofY, cowlZ + 25f),
                Point3D(roofHalfWidth - 2f, roofY, rearWindowZ - 10f),
                Point3D(-roofHalfWidth + 2f, roofY, rearWindowZ - 10f)
            ),
            roofColor
        )
    )

    // 4. Rear Window
    polys.add(
        Polygon3D(
            listOf(
                Point3D(-roofHalfWidth + 2f, roofY, rearWindowZ - 10f),
                Point3D(roofHalfWidth - 2f, roofY, rearWindowZ - 10f),
                Point3D(halfWidth - 10f, beltY, rearWindowZ + 45f),
                Point3D(-halfWidth + 10f, beltY, rearWindowZ + 45f)
            ),
            windowColor
        )
    )

    // 5. Trunk Deck
    polys.add(
        Polygon3D(
            listOf(
                Point3D(-halfWidth + 10f, beltY, rearWindowZ + 45f),
                Point3D(halfWidth - 10f, beltY, rearWindowZ + 45f),
                Point3D(halfWidth - 12f, beltY + 5f, trunkLipZ),
                Point3D(-halfWidth + 12f, beltY + 5f, trunkLipZ)
            ),
            bodyColor
        )
    )

    // 6. Carbon CS Wing / Trunk Lip Spoiler
    if (hasCsWing) {
        polys.add(
            Polygon3D(
                listOf(
                    Point3D(-halfWidth + 6f, beltY - 22f, trunkLipZ - 10f),
                    Point3D(halfWidth - 6f, beltY - 22f, trunkLipZ - 10f),
                    Point3D(halfWidth - 8f, beltY - 20f, trunkLipZ + 8f),
                    Point3D(-halfWidth + 8f, beltY - 20f, trunkLipZ + 8f)
                ),
                carbonColor
            )
        )
        // Wing uprights
        polys.add(Polygon3D(listOf(Point3D(-24f, beltY, trunkLipZ), Point3D(-24f, beltY - 22f, trunkLipZ), Point3D(-20f, beltY - 22f, trunkLipZ), Point3D(-20f, beltY, trunkLipZ)), carbonColor))
        polys.add(Polygon3D(listOf(Point3D(20f, beltY, trunkLipZ), Point3D(20f, beltY - 22f, trunkLipZ), Point3D(24f, beltY - 22f, trunkLipZ), Point3D(24f, beltY, trunkLipZ)), carbonColor))
    } else {
        // Subtle OEM spoiler lip
        polys.add(
            Polygon3D(
                listOf(
                    Point3D(-halfWidth + 14f, beltY + 1f, trunkLipZ - 4f),
                    Point3D(halfWidth - 14f, beltY + 1f, trunkLipZ - 4f),
                    Point3D(halfWidth - 16f, beltY + 3f, trunkLipZ + 4f),
                    Point3D(-halfWidth + 16f, beltY + 3f, trunkLipZ + 4f)
                ),
                chromeBlackColor
            )
        )
    }

    // 7. Left Body Flank
    polys.add(
        Polygon3D(
            listOf(
                Point3D(-halfWidth, beltY, frontBumperZ + 20f),
                Point3D(-halfWidth + 8f, hoodY, cowlZ),
                Point3D(-halfWidth + 10f, beltY, rearWindowZ + 45f),
                Point3D(-halfWidth, beltY + 6f, rearBumperZ - 10f),
                Point3D(-halfWidth, groundY - 18f, rearBumperZ - 10f),
                Point3D(-halfWidth, groundY - 18f, frontBumperZ + 20f)
            ),
            bodyColor
        )
    )

    // 8. Right Body Flank
    polys.add(
        Polygon3D(
            listOf(
                Point3D(halfWidth, beltY, frontBumperZ + 20f),
                Point3D(halfWidth - 8f, hoodY, cowlZ),
                Point3D(halfWidth - 10f, beltY, rearWindowZ + 45f),
                Point3D(halfWidth, beltY + 6f, rearBumperZ - 10f),
                Point3D(halfWidth, groundY - 18f, rearBumperZ - 10f),
                Point3D(halfWidth, groundY - 18f, frontBumperZ + 20f)
            ),
            bodyColor
        )
    )

    // 9. Front Bumper Fascia & Iconic Kidney Grilles
    polys.add(
        Polygon3D(
            listOf(
                Point3D(-halfWidth + 14f, hoodY + 8f, hoodFrontZ),
                Point3D(halfWidth - 14f, hoodY + 8f, hoodFrontZ),
                Point3D(halfWidth - 4f, groundY - 20f, frontBumperZ),
                Point3D(-halfWidth + 4f, groundY - 20f, frontBumperZ)
            ),
            bodyColor
        )
    )

    // Kidney Grilles (Horizontal slats for G87 M2 or active shutters for M240i)
    polys.add(
        Polygon3D(
            listOf(
                Point3D(-28f, hoodY + 12f, frontBumperZ + 2f),
                Point3D(-4f, hoodY + 12f, frontBumperZ + 2f),
                Point3D(-4f, groundY - 45f, frontBumperZ + 2f),
                Point3D(-28f, groundY - 45f, frontBumperZ + 2f)
            ),
            chromeBlackColor
        )
    )
    polys.add(
        Polygon3D(
            listOf(
                Point3D(4f, hoodY + 12f, frontBumperZ + 2f),
                Point3D(28f, hoodY + 12f, frontBumperZ + 2f),
                Point3D(28f, groundY - 45f, frontBumperZ + 2f),
                Point3D(4f, groundY - 45f, frontBumperZ + 2f)
            ),
            chromeBlackColor
        )
    )

    // LED Headlights (Left & Right)
    polys.add(
        Polygon3D(
            listOf(
                Point3D(-halfWidth + 12f, hoodY + 10f, frontBumperZ + 6f),
                Point3D(-34f, hoodY + 12f, frontBumperZ + 4f),
                Point3D(-36f, groundY - 55f, frontBumperZ + 4f),
                Point3D(-halfWidth + 8f, groundY - 50f, frontBumperZ + 6f)
            ),
            Color(0xFFE8F4F8)
        )
    )
    polys.add(
        Polygon3D(
            listOf(
                Point3D(34f, hoodY + 12f, frontBumperZ + 4f),
                Point3D(halfWidth - 12f, hoodY + 10f, frontBumperZ + 6f),
                Point3D(halfWidth - 8f, groundY - 50f, frontBumperZ + 6f),
                Point3D(36f, groundY - 55f, frontBumperZ + 4f)
            ),
            Color(0xFFE8F4F8)
        )
    )

    // Carbon Front Splitter
    if (hasCarbonSplitter) {
        polys.add(
            Polygon3D(
                listOf(
                    Point3D(-halfWidth - 4f, groundY - 14f, frontBumperZ - 10f),
                    Point3D(halfWidth + 4f, groundY - 14f, frontBumperZ - 10f),
                    Point3D(halfWidth - 4f, groundY - 12f, frontBumperZ + 12f),
                    Point3D(-halfWidth + 4f, groundY - 12f, frontBumperZ + 12f)
                ),
                carbonColor
            )
        )
    }

    // 10. Rear Fascia & Diffuser
    polys.add(
        Polygon3D(
            listOf(
                Point3D(-halfWidth + 12f, beltY + 5f, trunkLipZ),
                Point3D(halfWidth - 12f, beltY + 5f, trunkLipZ),
                Point3D(halfWidth - 4f, groundY - 24f, rearBumperZ),
                Point3D(-halfWidth + 4f, groundY - 24f, rearBumperZ)
            ),
            bodyColor
        )
    )

    // Rear Diffuser
    polys.add(
        Polygon3D(
            listOf(
                Point3D(-halfWidth + 8f, groundY - 24f, rearBumperZ),
                Point3D(halfWidth - 8f, groundY - 24f, rearBumperZ),
                Point3D(halfWidth - 12f, groundY - 10f, rearBumperZ - 10f),
                Point3D(-halfWidth + 12f, groundY - 10f, rearBumperZ - 10f)
            ),
            carbonColor
        )
    )

    // Exhaust Tips (Quad for M2 or Valved Exhaust mod)
    val hasQuadExhaust = isM2 || hasValvedExhaust
    val exhaustTipColor = if (hasValvedExhaust) Color(0xFF5B8DF0) else Color(0xFFC4CBD8) // Titanium burnt blue or polished steel
    if (hasQuadExhaust) {
        // Left pair
        polys.add(Polygon3D(listOf(Point3D(-38f, groundY - 16f, rearBumperZ + 4f), Point3D(-32f, groundY - 16f, rearBumperZ + 4f), Point3D(-32f, groundY - 10f, rearBumperZ + 4f), Point3D(-38f, groundY - 10f, rearBumperZ + 4f)), exhaustTipColor))
        polys.add(Polygon3D(listOf(Point3D(-28f, groundY - 16f, rearBumperZ + 4f), Point3D(-22f, groundY - 16f, rearBumperZ + 4f), Point3D(-22f, groundY - 10f, rearBumperZ + 4f), Point3D(-28f, groundY - 10f, rearBumperZ + 4f)), exhaustTipColor))
        // Right pair
        polys.add(Polygon3D(listOf(Point3D(22f, groundY - 16f, rearBumperZ + 4f), Point3D(28f, groundY - 16f, rearBumperZ + 4f), Point3D(28f, groundY - 10f, rearBumperZ + 4f), Point3D(22f, groundY - 10f, rearBumperZ + 4f)), exhaustTipColor))
        polys.add(Polygon3D(listOf(Point3D(32f, groundY - 16f, rearBumperZ + 4f), Point3D(38f, groundY - 16f, rearBumperZ + 4f), Point3D(38f, groundY - 10f, rearBumperZ + 4f), Point3D(32f, groundY - 10f, rearBumperZ + 4f)), exhaustTipColor))
    } else {
        // Dual Trapezoid tips for M240i / 230i
        polys.add(Polygon3D(listOf(Point3D(-36f, groundY - 16f, rearBumperZ + 4f), Point3D(-22f, groundY - 16f, rearBumperZ + 4f), Point3D(-24f, groundY - 10f, rearBumperZ + 4f), Point3D(-34f, groundY - 10f, rearBumperZ + 4f)), exhaustTipColor))
        polys.add(Polygon3D(listOf(Point3D(22f, groundY - 16f, rearBumperZ + 4f), Point3D(36f, groundY - 16f, rearBumperZ + 4f), Point3D(34f, groundY - 10f, rearBumperZ + 4f), Point3D(24f, groundY - 10f, rearBumperZ + 4f)), exhaustTipColor))
    }

    // 11. Wheels & Tires (Front Left, Front Right, Rear Left, Rear Right)
    val wheelRadius = 24f
    val wheelZPositions = listOf(
        Pair(-halfWidth - 4f, -100f), // Front Left
        Pair(halfWidth + 4f, -100f),  // Front Right
        Pair(-halfWidth - 4f, 100f),  // Rear Left
        Pair(halfWidth + 4f, 100f)    // Rear Right
    )

    wheelZPositions.forEach { (x, z) ->
        val tirePts = mutableListOf<Point3D>()
        val rimPts = mutableListOf<Point3D>()
        for (i in 0..12) {
            val a = Math.toRadians((i * 30).toDouble()).toFloat()
            val wy = groundY - wheelRadius + cos(a) * wheelRadius
            val wz = z + sin(a) * wheelRadius
            tirePts.add(Point3D(x, wy, wz))

            val rwy = groundY - wheelRadius + cos(a) * (wheelRadius * 0.72f)
            val rwz = z + sin(a) * (wheelRadius * 0.72f)
            rimPts.add(Point3D(x + if (x > 0) 1f else -1f, rwy, rwz))
        }
        polys.add(Polygon3D(tirePts, Color(0xFF141416)))
        polys.add(Polygon3D(rimPts, wheelColor))

        // Brake Caliper behind rim (M Blue / M Red)
        val caliperColor = if (isM2) MRed else MLightBlue
        polys.add(
            Polygon3D(
                listOf(
                    Point3D(x, groundY - wheelRadius - 4f, z - 8f),
                    Point3D(x, groundY - wheelRadius + 6f, z - 8f),
                    Point3D(x, groundY - wheelRadius + 6f, z + 2f),
                    Point3D(x, groundY - wheelRadius - 4f, z + 2f)
                ),
                caliperColor
            )
        )
    }

    return polys
}

