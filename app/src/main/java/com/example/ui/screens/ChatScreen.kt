package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.example.model.ChatMessage
import com.example.model.LoanExchange
import com.example.model.LoanStatus
import com.example.ui.BorrowViewModel
import com.example.ui.components.CategoryIcon
import com.example.ui.components.UserAvatar
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    loanId: String,
    viewModel: BorrowViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val loans by viewModel.allLoans.collectAsState()
    val loan = loans.firstOrNull { it.id == loanId }
    val chatMap by viewModel.repository.chatMessages.collectAsState()
    val messages = chatMap[loanId] ?: emptyList()

    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val quickPhrases = listOf(
        "On my way now!",
        "Arrived at the pickup spot!",
        "Running about 10 mins late",
        "Item looks great, thanks!"
    )

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (loan != null) {
                        Column {
                            Text(
                                text = if (loan.isBorrowerPerspective) loan.lenderName else loan.borrowerName,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = loan.item.title,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        Text("Exchange Chat")
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("chat_back_button")
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.openReportDialog() }) {
                        Icon(Icons.Outlined.Shield, contentDescription = "Safety Center", tint = PrimaryGreen)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .navigationBarsPadding()
                ) {
                    // Quick phrases
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(bottom = 6.dp)
                    ) {
                        items(quickPhrases) { phrase ->
                            SuggestionChip(
                                onClick = {
                                    viewModel.sendChatMessage(loanId, phrase)
                                },
                                label = { Text(phrase, fontSize = 11.sp) }
                            )
                        }
                    }

                    // Input Row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            placeholder = { Text("Message about pickup or return...") },
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("chat_message_input"),
                            maxLines = 3
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                if (inputText.isNotBlank()) {
                                    viewModel.sendChatMessage(loanId, inputText)
                                    inputText = ""
                                }
                            },
                            colors = IconButtonDefaults.filledIconButtonColors(containerColor = PrimaryGreen),
                            modifier = Modifier.testTag("chat_send_button")
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Sticky Loan Status Banner
            if (loan != null) {
                Surface(
                    color = PrimaryContainerGreen.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CategoryIcon(loan.item.category, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Status: ${loan.status.label}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnPrimaryContainerGreen
                            )
                        }

                        if (loan.exactAddress != null) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Navigation, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Directions", fontSize = 12.sp, color = PrimaryGreen, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Chat Messages List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(messages, key = { it.id }) { msg ->
                    ChatBubble(msg = msg)
                }
            }
        }
    }
}

@Composable
fun ChatBubble(msg: ChatMessage) {
    if (msg.isSystemNotification) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                color = if (msg.hasAddressUnlock) PrimaryContainerGreen else SurfaceVariantLight,
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        if (msg.hasAddressUnlock) Icons.Default.LockOpen else Icons.Default.Info,
                        contentDescription = null,
                        tint = if (msg.hasAddressUnlock) PrimaryGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = msg.text,
                        fontSize = 12.sp,
                        fontWeight = if (msg.hasAddressUnlock) FontWeight.Bold else FontWeight.Medium,
                        color = if (msg.hasAddressUnlock) OnPrimaryContainerGreen else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Text(
                text = msg.timestamp,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (msg.isFromCurrentUser) Arrangement.End else Arrangement.Start
        ) {
            if (!msg.isFromCurrentUser) {
                UserAvatar(name = msg.senderName, size = 28)
                Spacer(modifier = Modifier.width(8.dp))
            }

            Column(
                horizontalAlignment = if (msg.isFromCurrentUser) Alignment.End else Alignment.Start
            ) {
                Surface(
                    color = if (msg.isFromCurrentUser) PrimaryGreen else MaterialTheme.colorScheme.surface,
                    contentColor = if (msg.isFromCurrentUser) Color.White else MaterialTheme.colorScheme.onSurface,
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (msg.isFromCurrentUser) 16.dp else 4.dp,
                        bottomEnd = if (msg.isFromCurrentUser) 4.dp else 16.dp
                    ),
                    shadowElevation = 1.dp
                ) {
                    Text(
                        text = msg.text,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        lineHeight = 18.sp
                    )
                }
                Text(
                    text = msg.timestamp,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(top = 2.dp, start = 4.dp, end = 4.dp)
                )
            }
        }
    }
}
