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
import com.example.model.ItemCategory
import com.example.ui.BorrowViewModel
import com.example.ui.components.CategoryIcon
import com.example.ui.components.PrivacyShieldBanner
import com.example.ui.theme.PrimaryContainerGreen
import com.example.ui.theme.PrimaryGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddListingScreen(
    viewModel: BorrowViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ItemCategory.TOOLS) }
    var condition by remember { mutableStateOf("Excellent condition") }
    var preferredTimes by remember { mutableStateOf("Evenings (after 5 PM) or weekends") }
    var depositRequired by remember { mutableStateOf(false) }
    var depositAmount by remember { mutableStateOf(20) }

    val presetRules = listOf(
        "Return clean and wiped down",
        "Dry-weather / indoor use only",
        "Keep all bits and accessories in case",
        "Adults only operation"
    )
    val selectedRules = remember { mutableStateListOf("Return clean and wiped down") }
    var customRule by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("List an Item", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("add_listing_back_btn")
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .navigationBarsPadding()
                ) {
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                viewModel.createListing(
                                    title = title.trim(),
                                    category = selectedCategory,
                                    description = description.ifBlank { "Available to lend to respectful neighbors." },
                                    rules = selectedRules.toList(),
                                    depositRequired = depositRequired,
                                    depositAmount = if (depositRequired) depositAmount else 0,
                                    condition = condition,
                                    preferredTimes = preferredTimes
                                )
                            }
                        },
                        enabled = title.isNotBlank(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("publish_listing_button")
                    ) {
                        Icon(Icons.Default.AddCircleOutline, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Publish to Neighbors", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Privacy Shield Notice
            PrivacyShieldBanner(neighborhood = userProfile.neighborhood)

            // Item Title
            Column {
                Text("Item Title *", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("e.g. DeWalt Cordless Drill, 4-Person Tent, Bundt Pan") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("listing_title_input")
                )
            }

            // Category Picker
            Column {
                Text("Category", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(ItemCategory.TOOLS, ItemCategory.OUTDOOR, ItemCategory.KITCHEN, ItemCategory.LAWN).forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat.displayName, fontSize = 11.sp) },
                            leadingIcon = { CategoryIcon(cat, modifier = Modifier.size(14.dp)) }
                        )
                    }
                }
            }

            // Description
            Column {
                Text("Description & Key Details", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Include condition, included accessories, or any helpful usage tips...") },
                    maxLines = 4,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("listing_desc_input")
                )
            }

            // Condition & Times
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Condition", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = condition,
                        onValueChange = { condition = it },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text("Pickup Timing", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = preferredTimes,
                        onValueChange = { preferredTimes = it },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Borrowing Rules
            Column {
                Text("Borrowing Expectations & Rules", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(6.dp))
                presetRules.forEach { rule ->
                    val checked = selectedRules.contains(rule)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (checked) selectedRules.remove(rule) else selectedRules.add(rule)
                            }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = checked,
                            onCheckedChange = {
                                if (it) selectedRules.add(rule) else selectedRules.remove(rule)
                            }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(rule, fontSize = 13.sp)
                    }
                }

                // Custom rule
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = customRule,
                        onValueChange = { customRule = it },
                        placeholder = { Text("Add custom rule...") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (customRule.isNotBlank()) {
                                selectedRules.add(customRule.trim())
                                customRule = ""
                            }
                        }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Rule", tint = PrimaryGreen)
                    }
                }
            }

            // Deposit Settings
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Refundable Security Deposit", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Free borrowing is recommended for neighbors", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = depositRequired,
                            onCheckedChange = { depositRequired = it }
                        )
                    }

                    if (depositRequired) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(10, 20, 30, 50).forEach { amt ->
                                FilterChip(
                                    selected = depositAmount == amt,
                                    onClick = { depositAmount = amt },
                                    label = { Text("$$amt") }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
