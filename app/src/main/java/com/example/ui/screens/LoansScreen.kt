package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LoanExchange
import com.example.model.LoanStatus
import com.example.ui.BorrowViewModel
import com.example.ui.components.CategoryIcon
import com.example.ui.components.UserAvatar
import com.example.ui.theme.*

@Composable
fun LoansScreen(
    viewModel: BorrowViewModel,
    onOpenChat: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val loans by viewModel.allLoans.collectAsState()
    var selectedTab by remember { mutableStateOf(0) } // 0 = Borrowing, 1 = Lending
    val showReviewDialog by viewModel.showReviewDialog.collectAsState()

    val filteredLoans = loans.filter { loan ->
        if (selectedTab == 0) loan.isBorrowerPerspective else !loan.isBorrowerPerspective
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Loans & Exchanges",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Track handovers, active gear, and return expectations",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Segmented Tabs: Borrowing vs Lending
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = PrimaryGreen
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            val count = loans.count { it.isBorrowerPerspective }
                            Text("Borrowing ($count)", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal)
                        },
                        modifier = Modifier.testTag("tab_borrowing")
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            val count = loans.count { !it.isBorrowerPerspective }
                            Text("Lending ($count)", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal)
                        },
                        modifier = Modifier.testTag("tab_lending")
                    )
                }
            }
        }

        // Loans List
        if (filteredLoans.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        color = SurfaceVariantLight,
                        shape = CircleShape,
                        modifier = Modifier.size(72.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Outlined.SwapHoriz,
                                contentDescription = null,
                                tint = PrimaryGreen,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (selectedTab == 0) "No items currently borrowed" else "No items currently lent out",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Requests, ongoing loans, and return histories will appear here.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(filteredLoans, key = { it.id }) { loan ->
                    LoanExchangeCard(
                        loan = loan,
                        onOpenChat = { onOpenChat(loan.id) },
                        onApprove = { viewModel.approveLoan(loan.id) },
                        onDecline = { viewModel.declineLoan(loan.id) },
                        onHandover = { viewModel.confirmHandover(loan.id) },
                        onReturn = { viewModel.confirmReturn(loan.id) },
                        onOpenReview = { viewModel.openReviewDialog(loan) }
                    )
                }
            }
        }
    }

    // Review & Rating Modal
    if (showReviewDialog != null) {
        val targetLoan = showReviewDialog!!
        ReviewDialog(
            loan = targetLoan,
            onDismiss = { viewModel.closeReviewDialog() },
            onSubmit = { rating, comment, tags ->
                viewModel.submitReview(targetLoan.id, rating, comment, tags)
            }
        )
    }
}

@Composable
fun LoanExchangeCard(
    loan: LoanExchange,
    onOpenChat: () -> Unit,
    onApprove: () -> Unit,
    onDecline: () -> Unit,
    onHandover: () -> Unit,
    onReturn: () -> Unit,
    onOpenReview: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statusColor = when (loan.status) {
        LoanStatus.PENDING -> WarmAmber
        LoanStatus.APPROVED -> PrimaryGreen
        LoanStatus.PICKED_UP -> Color(0xFF26648E)
        LoanStatus.RETURNED -> Color(0xFF556052)
        LoanStatus.CANCELLED -> MaterialTheme.colorScheme.error
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("loan_card_${loan.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Status bar & title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = statusColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = loan.status.label,
                        color = statusColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                IconButton(
                    onClick = onOpenChat,
                    modifier = Modifier.testTag("loan_chat_btn_${loan.id}")
                ) {
                    Icon(
                        Icons.Outlined.Chat,
                        contentDescription = "Open Chat",
                        tint = PrimaryGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Item Title & Category
            Row(verticalAlignment = Alignment.CenterVertically) {
                CategoryIcon(loan.item.category, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = loan.item.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Counterparty info
            Row(verticalAlignment = Alignment.CenterVertically) {
                val counterpartyName = if (loan.isBorrowerPerspective) loan.lenderName else loan.borrowerName
                UserAvatar(name = counterpartyName, size = 26)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (loan.isBorrowerPerspective) "Lent by $counterpartyName" else "Borrower: $counterpartyName",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Dates info
            Surface(
                color = SurfaceVariantLight.copy(alpha = 0.5f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "Pickup: ${loan.pickupDateTime}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Return: ${loan.returnDateTime}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Unlocked Address Notice (if Approved or In Use)
            if (loan.exactAddress != null && (loan.status == LoanStatus.APPROVED || loan.status == LoanStatus.PICKED_UP)) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = PrimaryContainerGreen.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.LockOpen,
                            contentDescription = null,
                            tint = PrimaryGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Exact Pickup Address (Unlocked)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnPrimaryContainerGreen
                            )
                            Text(
                                text = loan.exactAddress,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            if (loan.pickupInstructions != null) {
                                Text(
                                    text = loan.pickupInstructions,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Engine Buttons depending on state & perspective
            when (loan.status) {
                LoanStatus.PENDING -> {
                    if (loan.isBorrowerPerspective) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Awaiting owner response...",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            OutlinedButton(
                                onClick = onDecline,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                            ) {
                                Text("Cancel Request", fontSize = 12.sp)
                            }
                        }
                    } else {
                        // Lender perspective: approve or decline
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = onDecline,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Decline")
                            }
                            Button(
                                onClick = onApprove,
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Approve")
                            }
                        }
                    }
                }

                LoanStatus.APPROVED -> {
                    Button(
                        onClick = onHandover,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("confirm_handover_button")
                    ) {
                        Icon(Icons.Default.Handshake, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Confirm Pickup Handover")
                    }
                }

                LoanStatus.PICKED_UP -> {
                    OutlinedButton(
                        onClick = onReturn,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("confirm_return_button")
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Confirm Item Returned Safe")
                    }
                }

                LoanStatus.RETURNED -> {
                    if (loan.reviewRating == null) {
                        Button(
                            onClick = onOpenReview,
                            colors = ButtonDefaults.buttonColors(containerColor = WarmAmber),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("leave_review_button")
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Leave Neighbor Review (★ 5.0)")
                        }
                    } else {
                        Surface(
                            color = PrimaryContainerGreen.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = PrimaryGreen)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Reviewed (${loan.reviewRating}★): \"${loan.reviewFeedback}\"",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                LoanStatus.CANCELLED -> {
                    Text(
                        text = "This exchange was cancelled.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun ReviewDialog(
    loan: LoanExchange,
    onDismiss: () -> Unit,
    onSubmit: (Int, String, List<String>) -> Unit
) {
    var rating by remember { mutableStateOf(5) }
    var comment by remember { mutableStateOf("Great experience! Item was in excellent shape and pickup was seamless.") }
    val selectedTags = remember { mutableStateListOf("Careful with item", "Super friendly") }

    val availableTags = listOf(
        "Super friendly",
        "Careful with item",
        "Clean on return",
        "Punctual pickup",
        "Great communicator"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Rate Your Experience", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Help build trust for ${if (loan.isBorrowerPerspective) loan.lenderName else loan.borrowerName}:",
                    fontSize = 13.sp
                )

                // Star selector
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    (1..5).forEach { star ->
                        IconButton(onClick = { rating = star }) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = "$star stars",
                                tint = if (star <= rating) StarGold else MaterialTheme.colorScheme.outlineVariant,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }

                // Quick tags
                Text("Select Highlights:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(availableTags) { tag ->
                        val isSelected = selectedTags.contains(tag)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                if (isSelected) selectedTags.remove(tag) else selectedTags.add(tag)
                            },
                            label = { Text(tag, fontSize = 11.sp) }
                        )
                    }
                }

                // Written review
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Public neighbor review") },
                    maxLines = 3,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(rating, comment, selectedTags.toList()) },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                modifier = Modifier.testTag("submit_review_button")
            ) {
                Text("Submit Review")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
