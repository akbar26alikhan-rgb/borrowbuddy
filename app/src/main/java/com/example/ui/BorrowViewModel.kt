package com.example.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.BorrowRepository
import com.example.location.GpsLocationManager
import com.example.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class UiFilterState(
    val query: String = "",
    val category: ItemCategory = ItemCategory.ALL,
    val maxDistanceKm: Double = 5.0,
    val depositOnly: Boolean? = null,
    val sortBy: String = "Distance",
    val showProvidersOnly: Boolean = false
)

sealed class ScreenDestination {
    object Explore : ScreenDestination()
    object Loans : ScreenDestination()
    object Messages : ScreenDestination()
    object Profile : ScreenDestination()
    object NearbyProviders : ScreenDestination()
    data class ItemDetail(val itemId: String) : ScreenDestination()
    data class ProviderDetail(val providerId: String) : ScreenDestination()
    data class ChatThread(val loanId: String) : ScreenDestination()
    object AddListing : ScreenDestination()
}

class BorrowViewModel(
    val repository: BorrowRepository = BorrowRepository()
) : ViewModel() {

    private var gpsManager: GpsLocationManager? = null

    private val _currentScreen = MutableStateFlow<ScreenDestination>(ScreenDestination.Explore)
    val currentScreen: StateFlow<ScreenDestination> = _currentScreen.asStateFlow()

    private val _filterState = MutableStateFlow(UiFilterState())
    val filterState: StateFlow<UiFilterState> = _filterState.asStateFlow()

    private val _showFilterSheet = MutableStateFlow(false)
    val showFilterSheet: StateFlow<Boolean> = _showFilterSheet.asStateFlow()

    private val _showRequestSheet = MutableStateFlow(false)
    val showRequestSheet: StateFlow<Boolean> = _showRequestSheet.asStateFlow()

    private val _showReportDialog = MutableStateFlow(false)
    val showReportDialog: StateFlow<Boolean> = _showReportDialog.asStateFlow()

    private val _showReviewDialog = MutableStateFlow<LoanExchange?>(null)
    val showReviewDialog: StateFlow<LoanExchange?> = _showReviewDialog.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    private val _isLocating = MutableStateFlow(false)
    val isLocating: StateFlow<Boolean> = _isLocating.asStateFlow()

    val userProfile = repository.userProfile
    val userGpsLocation = repository.userGpsLocation
    val isOffline = repository.isOffline
    val allLoans = repository.loans

    val filteredItems: StateFlow<List<BorrowItem>> = combine(
        repository.items,
        _filterState
    ) { items, filter ->
        repository.searchAndFilter(
            query = filter.query,
            category = filter.category,
            maxDistanceKm = filter.maxDistanceKm,
            depositOnly = filter.depositOnly,
            sortBy = filter.sortBy
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val nearbyProviders: StateFlow<List<Pair<Lender, Double>>> = combine(
        repository.items,
        _filterState
    ) { _, filter ->
        repository.getNearbyProviders(filter.maxDistanceKm)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun initGps(context: Context) {
        if (gpsManager == null) {
            gpsManager = GpsLocationManager(context.applicationContext)
        }
    }

    fun requestGpsLocation(context: Context) {
        initGps(context)
        _isLocating.value = true
        gpsManager?.requestGpsLocation { gpsLoc ->
            _isLocating.value = false
            repository.updateUserGpsLocation(gpsLoc)
            _userMessage.value = "GPS Acquired: ${gpsLoc.neighborhoodName} (±${gpsLoc.accuracyMeters.toInt()}m)"
        } ?: run {
            _isLocating.value = false
        }
    }

    fun navigateTo(destination: ScreenDestination) {
        _currentScreen.value = destination
    }

    fun onBack() {
        val current = _currentScreen.value
        if (current is ScreenDestination.ItemDetail ||
            current is ScreenDestination.ChatThread ||
            current is ScreenDestination.AddListing ||
            current is ScreenDestination.NearbyProviders ||
            current is ScreenDestination.ProviderDetail
        ) {
            _currentScreen.value = ScreenDestination.Explore
        }
    }

    fun setQuery(q: String) {
        _filterState.value = _filterState.value.copy(query = q)
    }

    fun selectCategory(cat: ItemCategory) {
        _filterState.value = _filterState.value.copy(category = cat)
    }

    fun setMaxDistance(dist: Double) {
        _filterState.value = _filterState.value.copy(maxDistanceKm = dist)
    }

    fun setDepositFilter(depositOnly: Boolean?) {
        _filterState.value = _filterState.value.copy(depositOnly = depositOnly)
    }

    fun setSortBy(sortBy: String) {
        _filterState.value = _filterState.value.copy(sortBy = sortBy)
    }

    fun setShowProvidersOnly(showOnly: Boolean) {
        _filterState.value = _filterState.value.copy(showProvidersOnly = showOnly)
    }

    fun openFilterSheet() {
        _showFilterSheet.value = true
    }

    fun closeFilterSheet() {
        _showFilterSheet.value = false
    }

    fun resetFilters() {
        _filterState.value = UiFilterState(query = _filterState.value.query)
    }

    fun openRequestSheet() {
        _showRequestSheet.value = true
    }

    fun closeRequestSheet() {
        _showRequestSheet.value = false
    }

    fun openReportDialog() {
        _showReportDialog.value = true
    }

    fun closeReportDialog() {
        _showReportDialog.value = false
    }

    fun openReviewDialog(loan: LoanExchange) {
        _showReviewDialog.value = loan
    }

    fun closeReviewDialog() {
        _showReviewDialog.value = null
    }

    fun submitBorrowRequest(
        item: BorrowItem,
        pickupDateTime: String,
        returnDateTime: String,
        note: String
    ) {
        repository.requestBorrow(item, pickupDateTime, returnDateTime, note)
        closeRequestSheet()
        _userMessage.value = "Borrow request sent to ${item.owner.name}!"
        _currentScreen.value = ScreenDestination.Loans
    }

    fun createListing(
        title: String,
        category: ItemCategory,
        description: String,
        rules: List<String>,
        depositRequired: Boolean,
        depositAmount: Int,
        condition: String,
        preferredTimes: String
    ) {
        repository.addItem(
            title = title,
            category = category,
            description = description,
            rules = rules,
            depositRequired = depositRequired,
            depositAmount = depositAmount,
            condition = condition,
            preferredTimes = preferredTimes
        )
        _userMessage.value = "New listing '${title}' shared with neighbors!"
        _currentScreen.value = ScreenDestination.Explore
    }

    fun approveLoan(loanId: String) {
        repository.approveLoan(loanId)
        _userMessage.value = "Request approved! Exact pickup instructions unlocked."
    }

    fun declineLoan(loanId: String) {
        repository.declineLoan(loanId)
        _userMessage.value = "Request declined."
    }

    fun confirmHandover(loanId: String) {
        repository.confirmHandover(loanId)
        _userMessage.value = "Item marked as picked up! In use."
    }

    fun confirmReturn(loanId: String) {
        repository.confirmReturn(loanId)
        _userMessage.value = "Item marked as returned safe!"
    }

    fun submitReview(loanId: String, rating: Int, feedback: String, tags: List<String>) {
        repository.submitReview(loanId, rating, feedback, tags)
        closeReviewDialog()
        _userMessage.value = "Review submitted. Thank you for building neighborhood trust!"
    }

    fun sendChatMessage(loanId: String, text: String) {
        if (text.isNotBlank()) {
            repository.sendMessage(loanId, text.trim())
        }
    }

    fun toggleOffline() {
        repository.toggleOfflineMode()
        val offline = repository.isOffline.value
        _userMessage.value = if (offline) "Offline mode enabled (viewing cached items)" else "Back online! Live sync active."
    }

    fun reportItem(itemId: String, reason: String) {
        repository.reportItem(itemId, reason)
        closeReportDialog()
        _userMessage.value = "Report received. Our community safety team will review it."
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }

    fun changeNeighborhood(newNeighborhood: String) {
        repository.updateNeighborhood(newNeighborhood)
        _userMessage.value = "Neighborhood updated to $newNeighborhood"
    }
}
