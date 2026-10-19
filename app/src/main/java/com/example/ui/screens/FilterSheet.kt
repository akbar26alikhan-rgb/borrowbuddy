package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ItemCategory
import com.example.ui.BorrowViewModel
import com.example.ui.theme.PrimaryGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSheet(
    viewModel: BorrowViewModel,
    onDismiss: () -> Unit
) {
    val filterState by viewModel.filterState.collectAsState()

    var tempDistance by remember { mutableStateOf(filterState.maxDistanceKm) }
    var tempDeposit by remember { mutableStateOf(filterState.depositOnly) }
    var tempSort by remember { mutableStateOf(filterState.sortBy) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Search & Filters", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Divider()

            // Distance Slider
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Maximum Distance", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Text("${tempDistance.toInt()} km", fontWeight = FontWeight.Bold, color = PrimaryGreen)
                }
                Slider(
                    value = tempDistance.toFloat(),
                    onValueChange = { tempDistance = it.toDouble() },
                    valueRange = 1f..15f,
                    steps = 13,
                    colors = SliderDefaults.colors(thumbColor = PrimaryGreen, activeTrackColor = PrimaryGreen)
                )
                Text(
                    "Show items within your neighborhood cluster",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Borrowing Terms
            Column {
                Text("Borrowing Terms", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = tempDeposit == null,
                        onClick = { tempDeposit = null },
                        label = { Text("All Terms") }
                    )
                    FilterChip(
                        selected = tempDeposit == false,
                        onClick = { tempDeposit = false },
                        label = { Text("Free Only") }
                    )
                    FilterChip(
                        selected = tempDeposit == true,
                        onClick = { tempDeposit = true },
                        label = { Text("With Deposit") }
                    )
                }
            }

            // Sort By
            Column {
                Text("Sort Results By", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(6.dp))
                listOf("Distance", "Rating", "Value").forEach { sort ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = tempSort == sort,
                            onClick = { tempSort = sort }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            when (sort) {
                                "Distance" -> "Closest First"
                                "Rating" -> "Highest Rated Lender"
                                else -> "High Value Equipment"
                            },
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        viewModel.resetFilters()
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Reset")
                }
                Button(
                    onClick = {
                        viewModel.setMaxDistance(tempDistance)
                        viewModel.setDepositFilter(tempDeposit)
                        viewModel.setSortBy(tempSort)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                    modifier = Modifier
                        .weight(1.5f)
                        .testTag("apply_filters_button")
                ) {
                    Text("Apply Filters")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
