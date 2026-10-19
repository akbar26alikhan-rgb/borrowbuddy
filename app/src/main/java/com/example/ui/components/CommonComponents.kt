package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ItemCategory
import com.example.ui.theme.*

@Composable
fun CategoryIcon(category: ItemCategory, modifier: Modifier = Modifier) {
    val icon = when (category) {
        ItemCategory.ALL -> Icons.Default.AllInclusive
        ItemCategory.TOOLS -> Icons.Default.Build
        ItemCategory.OUTDOOR -> Icons.Default.Terrain
        ItemCategory.KITCHEN -> Icons.Default.SoupKitchen
        ItemCategory.LAWN -> Icons.Default.Yard
        ItemCategory.BABY -> Icons.Default.ChildFriendly
        ItemCategory.HOBBY -> Icons.Default.SportsEsports
    }
    Icon(icon, contentDescription = category.displayName, modifier = modifier)
}

@Composable
fun TrustBadge(text: String, modifier: Modifier = Modifier) {
    Surface(
        color = PrimaryContainerGreen,
        shape = RoundedCornerShape(6.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.Verified,
                contentDescription = null,
                tint = PrimaryGreen,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = text,
                color = OnPrimaryContainerGreen,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun DistanceChip(distance: String, modifier: Modifier = Modifier) {
    Surface(
        color = SurfaceVariantLight,
        shape = RoundedCornerShape(6.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Outlined.LocationOn,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = distance,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun UserAvatar(name: String, size: Int = 36, modifier: Modifier = Modifier) {
    val initial = name.firstOrNull()?.uppercase() ?: "B"
    val bgColor = when (name.hashCode() % 4) {
        0 -> PrimaryGreen
        1 -> WarmAmber
        2 -> TrustBlue
        else -> Color(0xFF5B4B8A)
    }

    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = (size * 0.45).sp
        )
    }
}

@Composable
fun PrivacyShieldBanner(
    neighborhood: String,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = PrimaryContainerGreen.copy(alpha = 0.45f)),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Outlined.Shield,
                contentDescription = "Privacy Shield",
                tint = PrimaryGreen,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "Approximate Location Protection",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnPrimaryContainerGreen
                )
                Text(
                    text = "Area: $neighborhood. Exact street address is only unlocked after the lender approves your request.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
