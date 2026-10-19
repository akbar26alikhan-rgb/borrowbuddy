package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.LoanStatus
import com.example.ui.BorrowViewModel
import com.example.ui.ScreenDestination
import com.example.ui.screens.*
import com.example.ui.theme.BorrowBuddyTheme
import com.example.ui.theme.PrimaryContainerGreen
import com.example.ui.theme.PrimaryGreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BorrowBuddyTheme {
                val viewModel: BorrowViewModel = viewModel()
                BorrowBuddyApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BorrowBuddyApp(viewModel: BorrowViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val showFilterSheet by viewModel.showFilterSheet.collectAsState()
    val showRequestSheet by viewModel.showRequestSheet.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()
    val loans by viewModel.allLoans.collectAsState()
    val items by viewModel.filteredItems.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearUserMessage()
        }
    }

    // Handle back button for sub-screens
    BackHandler(
        enabled = currentScreen is ScreenDestination.ItemDetail ||
                currentScreen is ScreenDestination.ChatThread ||
                currentScreen is ScreenDestination.AddListing
    ) {
        viewModel.onBack()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            // Only show bottom navigation on primary destinations
            if (currentScreen is ScreenDestination.Explore ||
                currentScreen is ScreenDestination.Loans ||
                currentScreen is ScreenDestination.Messages ||
                currentScreen is ScreenDestination.Profile
            ) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp,
                    windowInsets = WindowInsets.navigationBars
                ) {
                    NavigationBarItem(
                        icon = {
                            Icon(
                                if (currentScreen is ScreenDestination.Explore) Icons.Filled.Explore else Icons.Outlined.Explore,
                                contentDescription = "Explore"
                            )
                        },
                        label = { Text("Explore") },
                        selected = currentScreen is ScreenDestination.Explore,
                        onClick = { viewModel.navigateTo(ScreenDestination.Explore) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryGreen,
                            indicatorColor = PrimaryContainerGreen
                        ),
                        modifier = Modifier.testTag("nav_explore")
                    )

                    NavigationBarItem(
                        icon = {
                            val activeCount = loans.count { it.status == LoanStatus.APPROVED || it.status == LoanStatus.PICKED_UP }
                            BadgedBox(
                                badge = {
                                    if (activeCount > 0) {
                                        Badge(containerColor = PrimaryGreen) { Text("$activeCount") }
                                    }
                                }
                            ) {
                                Icon(
                                    if (currentScreen is ScreenDestination.Loans) Icons.Filled.SwapHoriz else Icons.Outlined.SwapHoriz,
                                    contentDescription = "Loans"
                                )
                            }
                        },
                        label = { Text("Loans") },
                        selected = currentScreen is ScreenDestination.Loans,
                        onClick = { viewModel.navigateTo(ScreenDestination.Loans) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryGreen,
                            indicatorColor = PrimaryContainerGreen
                        ),
                        modifier = Modifier.testTag("nav_loans")
                    )

                    NavigationBarItem(
                        icon = {
                            Icon(
                                if (currentScreen is ScreenDestination.Messages) Icons.Filled.ChatBubble else Icons.Outlined.ChatBubbleOutline,
                                contentDescription = "Messages"
                            )
                        },
                        label = { Text("Messages") },
                        selected = currentScreen is ScreenDestination.Messages,
                        onClick = { viewModel.navigateTo(ScreenDestination.Messages) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryGreen,
                            indicatorColor = PrimaryContainerGreen
                        ),
                        modifier = Modifier.testTag("nav_messages")
                    )

                    NavigationBarItem(
                        icon = {
                            Icon(
                                if (currentScreen is ScreenDestination.Profile) Icons.Filled.Person else Icons.Outlined.PersonOutline,
                                contentDescription = "Profile"
                            )
                        },
                        label = { Text("Profile") },
                        selected = currentScreen is ScreenDestination.Profile,
                        onClick = { viewModel.navigateTo(ScreenDestination.Profile) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryGreen,
                            indicatorColor = PrimaryContainerGreen
                        ),
                        modifier = Modifier.testTag("nav_profile")
                    )
                }
            }
        },
        floatingActionButton = {
            if (currentScreen is ScreenDestination.Explore || currentScreen is ScreenDestination.Loans) {
                FloatingActionButton(
                    onClick = { viewModel.navigateTo(ScreenDestination.AddListing) },
                    containerColor = PrimaryGreen,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("add_listing_fab")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Lend an Item")
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val dest = currentScreen) {
                is ScreenDestination.Explore -> {
                    HomeScreen(
                        viewModel = viewModel,
                        onItemClick = { item -> viewModel.navigateTo(ScreenDestination.ItemDetail(item.id)) },
                        onOpenAddListing = { viewModel.navigateTo(ScreenDestination.AddListing) }
                    )
                }

                is ScreenDestination.ItemDetail -> {
                    val targetItem = items.firstOrNull { it.id == dest.itemId }
                        ?: viewModel.repository.items.value.firstOrNull { it.id == dest.itemId }
                    if (targetItem != null) {
                        ItemDetailScreen(
                            item = targetItem,
                            viewModel = viewModel,
                            onBack = { viewModel.navigateTo(ScreenDestination.Explore) },
                            onRequestBorrow = { viewModel.openRequestSheet() }
                        )

                        if (showRequestSheet) {
                            BorrowRequestSheet(
                                item = targetItem,
                                viewModel = viewModel,
                                onDismiss = { viewModel.closeRequestSheet() }
                            )
                        }
                    } else {
                        // Fallback if item was not found
                        viewModel.navigateTo(ScreenDestination.Explore)
                    }
                }

                is ScreenDestination.Loans -> {
                    LoansScreen(
                        viewModel = viewModel,
                        onOpenChat = { loanId -> viewModel.navigateTo(ScreenDestination.ChatThread(loanId)) }
                    )
                }

                is ScreenDestination.Messages -> {
                    // Show active conversation threads
                    LoansScreen(
                        viewModel = viewModel,
                        onOpenChat = { loanId -> viewModel.navigateTo(ScreenDestination.ChatThread(loanId)) }
                    )
                }

                is ScreenDestination.ChatThread -> {
                    ChatScreen(
                        loanId = dest.loanId,
                        viewModel = viewModel,
                        onBack = { viewModel.navigateTo(ScreenDestination.Loans) }
                    )
                }

                is ScreenDestination.AddListing -> {
                    AddListingScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateTo(ScreenDestination.Explore) }
                    )
                }

                is ScreenDestination.Profile -> {
                    ProfileScreen(viewModel = viewModel)
                }
            }
        }
    }

    // Search & Filter bottom sheet
    if (showFilterSheet) {
        FilterSheet(
            viewModel = viewModel,
            onDismiss = { viewModel.closeFilterSheet() }
        )
    }
}
