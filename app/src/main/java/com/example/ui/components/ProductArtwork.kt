package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BorrowItem
import com.example.model.ItemCategory
import com.example.ui.theme.PrimaryContainerGreen
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.WarmAmber

@Composable
fun ProductArtwork(
    item: BorrowItem,
    modifier: Modifier = Modifier,
    viewMode: Int = 0 // 0: Main Product, 1: Kit & Accessories, 2: Packaged / Case
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(getProductBackground(item.id, viewMode)),
        contentAlignment = Alignment.Center
    ) {
        when (item.id) {
            "bb-101" -> DrillArtwork(viewMode = viewMode)
            "bb-102" -> TentArtwork(viewMode = viewMode)
            "bb-103" -> BundtPanArtwork(viewMode = viewMode)
            "bb-104" -> PressureWasherArtwork(viewMode = viewMode)
            "bb-105" -> LadderArtwork(viewMode = viewMode)
            "bb-106" -> IceCreamMakerArtwork(viewMode = viewMode)
            "bb-107" -> CargoBoxArtwork(viewMode = viewMode)
            else -> GenericProductArtwork(item = item, viewMode = viewMode)
        }

        // View mode indicator pill (if multiple views)
        Surface(
            color = Color.Black.copy(alpha = 0.55f),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    when (viewMode) {
                        1 -> Icons.Default.Extension
                        2 -> Icons.Default.Inventory2
                        else -> Icons.Default.CameraAlt
                    },
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(11.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = when (viewMode) {
                        1 -> "Kit & Bits"
                        2 -> "Carry Case"
                        else -> "Studio View"
                    },
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

private fun getProductBackground(itemId: String, viewMode: Int): Brush {
    return when (itemId) {
        "bb-101" -> Brush.linearGradient(listOf(Color(0xFF263238), Color(0xFF37474F), Color(0xFF1E282D)))
        "bb-102" -> Brush.linearGradient(listOf(Color(0xFF1B3B2B), Color(0xFF2E5A44), Color(0xFF132B1F)))
        "bb-103" -> Brush.linearGradient(listOf(Color(0xFF3E2723), Color(0xFF5D4037), Color(0xFF2B1B17)))
        "bb-104" -> Brush.linearGradient(listOf(Color(0xFF0F3057), Color(0xFF00587A), Color(0xFF00363A)))
        "bb-105" -> Brush.linearGradient(listOf(Color(0xFF212121), Color(0xFF424242), Color(0xFF1E1E1E)))
        "bb-106" -> Brush.linearGradient(listOf(Color(0xFF4A154B), Color(0xFF6B2D5C), Color(0xFF330E34)))
        "bb-107" -> Brush.linearGradient(listOf(Color(0xFF1C2833), Color(0xFF2C3E50), Color(0xFF17202A)))
        else -> Brush.linearGradient(listOf(Color(0xFF2D3748), Color(0xFF1A202C)))
    }
}

// 1. DeWalt 20V Cordless Drill Visual
@Composable
fun DrillArtwork(viewMode: Int) {
    Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        val w = size.width
        val h = size.height

        if (viewMode == 1) {
            // Bit set organizer view
            drawRoundRect(
                color = Color(0xFF212121),
                topLeft = Offset(w * 0.15f, h * 0.2f),
                size = Size(w * 0.7f, h * 0.6f),
                cornerRadius = CornerRadius(16f, 16f)
            )
            // Bit slots in grid
            val yellowDeWalt = Color(0xFFFFB300)
            for (r in 0..2) {
                for (c in 0..7) {
                    val x = w * (0.22f + c * 0.07f)
                    val y = h * (0.28f + r * 0.16f)
                    drawRoundRect(
                        color = if (r == 0) Color(0xFFB0BEC5) else yellowDeWalt,
                        topLeft = Offset(x, y),
                        size = Size(w * 0.04f, h * 0.1f),
                        cornerRadius = CornerRadius(4f, 4f)
                    )
                }
            }
            return@Canvas
        }

        if (viewMode == 2) {
            // Rugged Hard Tool Case
            drawRoundRect(
                color = Color(0xFFFFB300),
                topLeft = Offset(w * 0.18f, h * 0.22f),
                size = Size(w * 0.64f, h * 0.56f),
                cornerRadius = CornerRadius(20f, 20f)
            )
            drawRoundRect(
                color = Color(0xFF212121),
                topLeft = Offset(w * 0.22f, h * 0.26f),
                size = Size(w * 0.56f, h * 0.48f),
                cornerRadius = CornerRadius(14f, 14f)
            )
            // Handle
            drawRoundRect(
                color = Color(0xFF212121),
                topLeft = Offset(w * 0.4f, h * 0.14f),
                size = Size(w * 0.2f, h * 0.1f),
                cornerRadius = CornerRadius(8f, 8f)
            )
            return@Canvas
        }

        // Main Drill Isometric Silhouette
        val dewaltYellow = Color(0xFFFFB300)
        val rubberCharcoal = Color(0xFF212121)
        val metalSilver = Color(0xFFCFD8DC)

        // Chuck / Bit Barrel (Front Left)
        drawRoundRect(
            color = metalSilver,
            topLeft = Offset(w * 0.12f, h * 0.35f),
            size = Size(w * 0.18f, h * 0.14f),
            cornerRadius = CornerRadius(6f, 6f)
        )
        // Drill bit sticking out
        drawRect(
            color = Color(0xFFECEFF1),
            topLeft = Offset(w * 0.04f, h * 0.39f),
            size = Size(w * 0.09f, h * 0.06f)
        )

        // Drill Body / Motor Housing
        drawRoundRect(
            color = dewaltYellow,
            topLeft = Offset(w * 0.28f, h * 0.28f),
            size = Size(w * 0.48f, h * 0.26f),
            cornerRadius = CornerRadius(16f, 16f)
        )
        // Charcoal ergonomic rubber overmold
        drawRoundRect(
            color = rubberCharcoal,
            topLeft = Offset(w * 0.52f, h * 0.29f),
            size = Size(w * 0.22f, h * 0.24f),
            cornerRadius = CornerRadius(14f, 14f)
        )

        // Handle Grip
        drawRoundRect(
            color = dewaltYellow,
            topLeft = Offset(w * 0.46f, h * 0.48f),
            size = Size(w * 0.18f, h * 0.32f),
            cornerRadius = CornerRadius(12f, 12f)
        )
        drawRoundRect(
            color = rubberCharcoal,
            topLeft = Offset(w * 0.50f, h * 0.50f),
            size = Size(w * 0.12f, h * 0.28f),
            cornerRadius = CornerRadius(8f, 8f)
        )

        // Trigger Switch
        drawRoundRect(
            color = Color(0xFFD32F2F),
            topLeft = Offset(w * 0.42f, h * 0.54f),
            size = Size(w * 0.06f, h * 0.10f),
            cornerRadius = CornerRadius(4f, 4f)
        )

        // 20V Max Lithium Battery Base
        drawRoundRect(
            color = rubberCharcoal,
            topLeft = Offset(w * 0.38f, h * 0.76f),
            size = Size(w * 0.32f, h * 0.14f),
            cornerRadius = CornerRadius(8f, 8f)
        )
        drawRoundRect(
            color = dewaltYellow,
            topLeft = Offset(w * 0.42f, h * 0.79f),
            size = Size(w * 0.24f, h * 0.06f),
            cornerRadius = CornerRadius(4f, 4f)
        )
    }
}

// 2. Coleman Camping Tent Visual
@Composable
fun TentArtwork(viewMode: Int) {
    Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        val w = size.width
        val h = size.height

        val tentBlue = Color(0xFF1E88E5)
        val tentGreen = Color(0xFF43A047)
        val tentPole = Color(0xFFFFB300)

        // Mountain silhouettes in background
        val bgPath = Path().apply {
            moveTo(0f, h * 0.65f)
            lineTo(w * 0.25f, h * 0.35f)
            lineTo(w * 0.5f, h * 0.55f)
            lineTo(w * 0.75f, h * 0.3f)
            lineTo(w, h * 0.65f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(bgPath, color = Color(0x25FFFFFF))

        // Tent Dome Main Body
        val domePath = Path().apply {
            moveTo(w * 0.15f, h * 0.78f)
            cubicTo(w * 0.25f, h * 0.28f, w * 0.75f, h * 0.28f, w * 0.85f, h * 0.78f)
            close()
        }
        drawPath(domePath, color = tentBlue)

        // Rainfly Cover
        val rainfly = Path().apply {
            moveTo(w * 0.18f, h * 0.72f)
            cubicTo(w * 0.32f, h * 0.22f, w * 0.68f, h * 0.22f, w * 0.82f, h * 0.72f)
            lineTo(w * 0.5f, h * 0.62f)
            close()
        }
        drawPath(rainfly, color = tentGreen)

        // Tent Doorway
        val door = Path().apply {
            moveTo(w * 0.38f, h * 0.78f)
            lineTo(w * 0.5f, h * 0.5f)
            lineTo(w * 0.62f, h * 0.78f)
            close()
        }
        drawPath(door, color = Color(0xFF0D47A1))

        // Arching Fiberglass Poles
        drawArc(
            color = tentPole,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(w * 0.14f, h * 0.26f),
            size = Size(w * 0.72f, h * 0.95f),
            style = Stroke(width = 4f)
        )
    }
}

// 3. Nordic Ware Bundt Cake Pan Visual
@Composable
fun BundtPanArtwork(viewMode: Int) {
    Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        val w = size.width
        val h = size.height
        val center = Offset(w / 2f, h / 2f)

        // Outer Metallic Rim
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFD7CCC8), Color(0xFF8D6E63), Color(0xFF4E342E)),
                center = center,
                radius = w * 0.45f
            ),
            radius = w * 0.40f,
            center = center
        )

        // Fluted decorative ridges around periphery
        val ribs = 16
        for (i in 0 until ribs) {
            val angle = (i * (360.0 / ribs)) * (Math.PI / 180.0)
            val outerX = center.x + (w * 0.36f * Math.cos(angle)).toFloat()
            val outerY = center.y + (w * 0.36f * Math.sin(angle)).toFloat()
            val innerX = center.x + (w * 0.16f * Math.cos(angle)).toFloat()
            val innerY = center.y + (w * 0.16f * Math.sin(angle)).toFloat()

            drawLine(
                color = Color(0xFFEFEBE9),
                start = Offset(outerX, outerY),
                end = Offset(innerX, innerY),
                strokeWidth = 5f
            )
            drawLine(
                color = Color(0xFF3E2723),
                start = Offset(outerX + 4f, outerY + 4f),
                end = Offset(innerX + 2f, innerY + 2f),
                strokeWidth = 3f
            )
        }

        // Center Chimney Tube (Classic Bundt Hole)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF2B1B17), Color(0xFF4E342E), Color(0xFF8D6E63)),
                center = center,
                radius = w * 0.15f
            ),
            radius = w * 0.14f,
            center = center
        )
        drawCircle(
            color = Color(0xFF1E1410),
            radius = w * 0.08f,
            center = center
        )
    }
}

// 4. Bosch Electric Pressure Washer Visual
@Composable
fun PressureWasherArtwork(viewMode: Int) {
    Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        val w = size.width
        val h = size.height

        val boschGreen = Color(0xFF00897B)
        val washerBlack = Color(0xFF263238)
        val metalSpray = Color(0xFFECEFF1)

        // Wheels
        drawCircle(color = washerBlack, radius = w * 0.12f, center = Offset(w * 0.32f, h * 0.80f))
        drawCircle(color = Color(0xFFCFD8DC), radius = w * 0.05f, center = Offset(w * 0.32f, h * 0.80f))

        // Main Upright Body
        drawRoundRect(
            color = boschGreen,
            topLeft = Offset(w * 0.28f, h * 0.28f),
            size = Size(w * 0.44f, h * 0.52f),
            cornerRadius = CornerRadius(18f, 18f)
        )
        // Ergonomic handle on top
        drawRoundRect(
            color = washerBlack,
            topLeft = Offset(w * 0.36f, h * 0.12f),
            size = Size(w * 0.28f, h * 0.18f),
            cornerRadius = CornerRadius(10f, 10f),
            style = Stroke(width = 12f)
        )

        // Spray Wand Lance attached on side
        drawLine(
            color = metalSpray,
            start = Offset(w * 0.78f, h * 0.15f),
            end = Offset(w * 0.78f, h * 0.82f),
            strokeWidth = 8f
        )
        // High pressure brass nozzle tip
        drawCircle(color = Color(0xFFFFB300), radius = 6f, center = Offset(w * 0.78f, h * 0.15f))
        // Water spray effect
        drawLine(
            color = Color(0x8080D8FF),
            start = Offset(w * 0.78f, h * 0.15f),
            end = Offset(w * 0.88f, h * 0.05f),
            strokeWidth = 3f
        )
        drawLine(
            color = Color(0x8080D8FF),
            start = Offset(w * 0.78f, h * 0.15f),
            end = Offset(w * 0.74f, h * 0.05f),
            strokeWidth = 3f
        )
    }
}

// 5. Little Giant Folding Ladder Visual
@Composable
fun LadderArtwork(viewMode: Int) {
    Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        val w = size.width
        val h = size.height

        val metalSilver = Color(0xFFECEFF1)
        val hingeOrange = Color(0xFFFF6D00)
        val rubberBlack = Color(0xFF212121)

        // A-Frame Side Beams
        // Left rail
        drawLine(
            color = metalSilver,
            start = Offset(w * 0.48f, h * 0.20f),
            end = Offset(w * 0.25f, h * 0.85f),
            strokeWidth = 14f,
            cap = StrokeCap.Round
        )
        // Right rail
        drawLine(
            color = metalSilver,
            start = Offset(w * 0.52f, h * 0.20f),
            end = Offset(w * 0.75f, h * 0.85f),
            strokeWidth = 14f,
            cap = StrokeCap.Round
        )

        // Rungs
        for (i in 1..4) {
            val progress = i / 5.0f
            val startX = w * (0.48f - progress * 0.23f)
            val endX = w * (0.52f + progress * 0.23f)
            val y = h * (0.20f + progress * 0.65f)
            drawLine(
                color = metalSilver,
                start = Offset(startX, y),
                end = Offset(endX, y),
                strokeWidth = 9f
            )
        }

        // Top Lock Hinge mechanism (Little Giant signature orange dial)
        drawCircle(color = hingeOrange, radius = w * 0.08f, center = Offset(w * 0.50f, h * 0.20f))
        drawCircle(color = rubberBlack, radius = w * 0.04f, center = Offset(w * 0.50f, h * 0.20f))

        // Non-slip rubber feet
        drawRoundRect(color = rubberBlack, topLeft = Offset(w * 0.20f, h * 0.83f), size = Size(w * 0.10f, h * 0.05f), cornerRadius = CornerRadius(4f, 4f))
        drawRoundRect(color = rubberBlack, topLeft = Offset(w * 0.70f, h * 0.83f), size = Size(w * 0.10f, h * 0.05f), cornerRadius = CornerRadius(4f, 4f))
    }
}

// 6. Cuisinart Ice Cream Maker Visual
@Composable
fun IceCreamMakerArtwork(viewMode: Int) {
    Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        val w = size.width
        val h = size.height

        // Outer brushed cylinder
        drawRoundRect(
            brush = Brush.horizontalGradient(listOf(Color(0xFFB0BEC5), Color(0xFFECEFF1), Color(0xFF78909C))),
            topLeft = Offset(w * 0.25f, h * 0.35f),
            size = Size(w * 0.50f, h * 0.50f),
            cornerRadius = CornerRadius(16f, 16f)
        )

        // Clear domed lid
        drawArc(
            color = Color(0x6080D8FF),
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = true,
            topLeft = Offset(w * 0.28f, h * 0.18f),
            size = Size(w * 0.44f, h * 0.36f)
        )

        // Power switch dial
        drawCircle(color = Color(0xFF263238), radius = w * 0.05f, center = Offset(w * 0.50f, h * 0.65f))
        drawCircle(color = Color(0xFFE91E63), radius = w * 0.02f, center = Offset(w * 0.50f, h * 0.65f))
    }
}

// 7. Yakima Roof Cargo Box Visual
@Composable
fun CargoBoxArtwork(viewMode: Int) {
    Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        val w = size.width
        val h = size.height

        // Sleek aerodynamic shell
        val podPath = Path().apply {
            moveTo(w * 0.10f, h * 0.50f)
            cubicTo(w * 0.25f, h * 0.25f, w * 0.75f, h * 0.25f, w * 0.90f, h * 0.45f)
            lineTo(w * 0.85f, h * 0.65f)
            cubicTo(w * 0.60f, h * 0.68f, w * 0.30f, h * 0.68f, w * 0.10f, h * 0.50f)
            close()
        }
        drawPath(
            podPath,
            brush = Brush.linearGradient(
                listOf(Color(0xFF37474F), Color(0xFF212121), Color(0xFF000000))
            )
        )

        // Speed lines / reflection highlight
        drawLine(
            color = Color(0x80ECEFF1),
            start = Offset(w * 0.20f, h * 0.38f),
            end = Offset(w * 0.78f, h * 0.36f),
            strokeWidth = 3f
        )

        // SKS Key lock
        drawCircle(color = Color(0xFFFFB300), radius = 5f, center = Offset(w * 0.50f, h * 0.54f))
    }
}

@Composable
fun GenericProductArtwork(item: BorrowItem, viewMode: Int) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val displayIcon = when (item.category) {
            ItemCategory.TOOLS -> Icons.Default.Hardware
            ItemCategory.OUTDOOR -> Icons.Default.Cabin
            ItemCategory.KITCHEN -> Icons.Default.SoupKitchen
            ItemCategory.LAWN -> Icons.Default.Yard
            ItemCategory.BABY -> Icons.Default.ChildCare
            else -> Icons.Default.Handshake
        }
        Icon(
            displayIcon,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.size(72.dp)
        )
    }
}
