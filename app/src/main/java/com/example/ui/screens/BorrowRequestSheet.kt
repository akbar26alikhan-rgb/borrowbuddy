package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BorrowItem
import com.example.ui.BorrowViewModel
import com.example.ui.theme.PrimaryContainerGreen
import com.example.ui.theme.PrimaryGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BorrowRequestSheet(
    item: BorrowItem,
    viewModel: BorrowViewModel,
    onDismiss: () -> Unit
) {
    var pickupTime by remember { mutableStateOf("Tomorrow, 10:00 AM") }
    var returnTime by remember { mutableStateOf("In 2 days, 6:00 PM") }
    var note by remember { mutableStateOf("Hi ${item.owner.name}! I need this for a weekend project and will take great care of it.") }
    var agreedToPledge by remember { mutableStateOf(true) }

    val pickupOptions = listOf("Tomorrow, 10:00 AM", "Saturday, 9:00 AM", "Today, 5:30 PM")
    val returnOptions = listOf("In 2 days, 6:00 PM", "Sunday, 5:00 PM", "In 3 days, 12:00 PM")

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
                Column {
                    Text(
                        text = "Borrow Request",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Requesting: ${item.title}",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Divider()

            // Pickup time section
            Column {
                Text(
                    text = "1. Proposed Pickup Window",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                pickupOptions.forEach { opt ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { pickupTime = opt }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = pickupTime == opt,
                            onClick = { pickupTime = opt }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(opt, fontSize = 14.sp)
                    }
                }
            }

            // Return time section
            Column {
                Text(
                    text = "2. Proposed Return Time",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                returnOptions.forEach { opt ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { returnTime = opt }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = returnTime == opt,
                            onClick = { returnTime = opt }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(opt, fontSize = 14.sp)
                    }
                }
            }

            // Note to lender
            Column {
                Text(
                    text = "3. Message to ${item.owner.name}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("request_note_input")
                )
            }

            // Neighborhood Care Pledge
            Card(
                colors = CardDefaults.cardColors(containerColor = PrimaryContainerGreen.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = agreedToPledge,
                        onCheckedChange = { agreedToPledge = it },
                        modifier = Modifier.testTag("pledge_checkbox")
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Neighborhood Pledge: I promise to treat this item with care, follow the owner's instructions, and return it clean & on time.",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 16.sp
                    )
                }
            }

            // Action Buttons
            Button(
                onClick = {
                    if (agreedToPledge) {
                        viewModel.submitBorrowRequest(
                            item = item,
                            pickupDateTime = pickupTime,
                            returnDateTime = returnTime,
                            note = note
                        )
                    }
                },
                enabled = agreedToPledge,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("submit_request_button")
            ) {
                Text("Confirm & Send Request", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
