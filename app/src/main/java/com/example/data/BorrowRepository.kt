package com.example.data

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BorrowRepository {

    private val _userProfile = MutableStateFlow(
        UserProfile(
            name = "Alex Morgan",
            neighborhood = "Maplewood Heights",
            trustScore = 98,
            verifications = listOf("ID Verified", "Phone Verified", "Neighbor Vouched", "Community Host"),
            itemsLentCount = 14,
            itemsBorrowedCount = 8,
            memberSince = "March 2024"
        )
    )
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _items = MutableStateFlow<List<BorrowItem>>(emptyList())
    val items: StateFlow<List<BorrowItem>> = _items.asStateFlow()

    private val _loans = MutableStateFlow<List<LoanExchange>>(emptyList())
    val loans: StateFlow<List<LoanExchange>> = _loans.asStateFlow()

    private val _chatMessages = MutableStateFlow<Map<String, List<ChatMessage>>>(emptyMap())
    val chatMessages: StateFlow<Map<String, List<ChatMessage>>> = _chatMessages.asStateFlow()

    private val _isOffline = MutableStateFlow(false)
    val isOffline: StateFlow<Boolean> = _isOffline.asStateFlow()

    private val _reports = MutableStateFlow<List<Pair<String, String>>>(emptyList())

    init {
        seedInitialData()
    }

    private fun seedInitialData() {
        val lenderMarcus = Lender(
            id = "u1",
            name = "Marcus K.",
            rating = 4.95,
            reviewsCount = 28,
            badges = listOf("ID Verified", "Super Lender", "Top Rated"),
            responseTime = "< 10 mins",
            neighborhood = "Maplewood North"
        )
        val lenderElena = Lender(
            id = "u2",
            name = "Elena R.",
            rating = 4.88,
            reviewsCount = 19,
            badges = listOf("ID Verified", "Neighbor Favorite"),
            responseTime = "< 30 mins",
            neighborhood = "Oak Ridge Quadrant"
        )
        val lenderDavid = Lender(
            id = "u3",
            name = "David W.",
            rating = 5.0,
            reviewsCount = 14,
            badges = listOf("ID Verified", "Punctual Host"),
            responseTime = "< 15 mins",
            neighborhood = "Westwood Cross"
        )
        val lenderSofia = Lender(
            id = "u4",
            name = "Sofia M.",
            rating = 4.92,
            reviewsCount = 31,
            badges = listOf("ID Verified", "Super Lender"),
            responseTime = "< 5 mins",
            neighborhood = "Highland Green"
        )

        val seedItems = listOf(
            BorrowItem(
                id = "bb-101",
                title = "DeWalt 20V Cordless Drill + 30-pc Bit Set",
                category = ItemCategory.TOOLS,
                distanceKm = 0.4,
                neighborhood = "Maplewood North",
                crossStreets = "Near Lincoln Elementary & 3rd",
                owner = lenderMarcus,
                description = "High torque brushless drill with 2 lithium batteries, high-speed charger, and drill/driver bits in a rugged hard case. Ideal for hanging art, assembling furniture, and light carpentry.",
                rules = listOf(
                    "Return clean, wiped down, and fully charged",
                    "Keep all bits sorted in original organizer",
                    "Indoor and dry-weather use only"
                ),
                depositRequired = false,
                depositAmount = 0,
                condition = "Like New",
                estimatedValue = "$180",
                maxDays = 3,
                preferredTimes = "Flexible; porch pickup box available 8 AM - 9 PM"
            ),
            BorrowItem(
                id = "bb-102",
                title = "Coleman 4-Person Sundome Waterproof Camping Tent",
                category = ItemCategory.OUTDOOR,
                distanceKm = 1.2,
                neighborhood = "Oak Ridge Quadrant",
                crossStreets = "Near Highland Park West",
                owner = lenderElena,
                description = "Quick 10-minute setup dome tent with WeatherTec system, rainfly, ground tarp, and all 8 stakes. Fits a queen airbed comfortably.",
                rules = listOf(
                    "Never pack or fold while damp or wet",
                    "Sweep out dirt and grass before packing",
                    "Return all 8 stakes in their separate pouch"
                ),
                depositRequired = true,
                depositAmount = 25,
                condition = "Excellent condition",
                estimatedValue = "$110",
                maxDays = 5,
                preferredTimes = "Weekdays after 5 PM, weekends anytime"
            ),
            BorrowItem(
                id = "bb-103",
                title = "Nordic Ware Cast Aluminum Bundt Cake Pan",
                category = ItemCategory.KITCHEN,
                distanceKm = 0.7,
                neighborhood = "Westwood Cross",
                crossStreets = "4th & Elm Crossing",
                owner = lenderDavid,
                description = "Heavy-cast aluminum 10-cup elegant fluted Bundt pan with premium non-stick coating. Bakes sharp, gorgeous decorative cakes.",
                rules = listOf(
                    "Strictly hand-wash with soft sponge (no dishwasher)",
                    "Do NOT use metal forks or knives on coating",
                    "Grease thoroughly before pouring batter"
                ),
                depositRequired = false,
                depositAmount = 0,
                condition = "Pristine",
                estimatedValue = "$45",
                maxDays = 2,
                preferredTimes = "Morning 9-11 AM or Evening 6-8 PM"
            ),
            BorrowItem(
                id = "bb-104",
                title = "Bosch 1600 PSI Electric Pressure Washer",
                category = ItemCategory.LAWN,
                distanceKm = 0.9,
                neighborhood = "Maplewood North",
                crossStreets = "Near 6th Ave & Pine",
                owner = lenderMarcus,
                description = "Compact lightweight electric pressure washer with variable fan spray nozzle and 20ft hose. Fantastic for driveways, siding, and patio furniture.",
                rules = listOf(
                    "Bring your own standard garden hose",
                    "Drain all water completely before packing away",
                    "Do not run motor without water flowing"
                ),
                depositRequired = true,
                depositAmount = 30,
                condition = "Great working order",
                estimatedValue = "$160",
                maxDays = 2,
                preferredTimes = "Weekends preferred"
            ),
            BorrowItem(
                id = "bb-105",
                title = "Little Giant 17ft Multi-Position Folding Ladder",
                category = ItemCategory.TOOLS,
                distanceKm = 1.8,
                neighborhood = "Highland Green",
                crossStreets = "Near Sycamore Circle",
                owner = lenderSofia,
                description = "Heavy duty aluminum ladder that converts to A-frame, extension ladder, 90-degree stair ladder, and scaffold trestle. Rated 300 lbs.",
                rules = listOf(
                    "Always lock dual-pin hinges securely before climbing",
                    "Wipe off paint drips immediately if using for painting",
                    "Carrying requires large SUV or roof rack"
                ),
                depositRequired = false,
                depositAmount = 0,
                condition = "Rugged & Reliable",
                estimatedValue = "$240",
                maxDays = 3,
                preferredTimes = "Evenings after 6 PM"
            ),
            BorrowItem(
                id = "bb-106",
                title = "Cuisinart 2-Quart Automatic Ice Cream Maker",
                category = ItemCategory.KITCHEN,
                distanceKm = 1.5,
                neighborhood = "Oak Ridge Quadrant",
                crossStreets = "Near Maple Library",
                owner = lenderElena,
                description = "Fully automatic frozen yogurt, sorbet, and ice cream maker. Includes double-insulated freezer bowl and recipe book.",
                rules = listOf(
                    "Hand-wash paddle and bowl only",
                    "Freeze bowl 24 hours prior to churning",
                    "Return clean and dry"
                ),
                depositRequired = false,
                depositAmount = 0,
                condition = "Excellent",
                estimatedValue = "$70",
                maxDays = 4,
                preferredTimes = "Flexible pickup"
            ),
            BorrowItem(
                id = "bb-107",
                title = "Yakima SkyBox 16 Carbonite Roof Cargo Box",
                category = ItemCategory.OUTDOOR,
                distanceKm = 2.4,
                neighborhood = "Highland Green",
                crossStreets = "Near Cedar & 8th",
                owner = lenderSofia,
                description = "Aerodynamic roof carrier box with quick-dial mounting hardware and SKS lock system. Includes 2 keys. Fits crossbars easily.",
                rules = listOf(
                    "Ensure vehicle has sturdy crossbars installed",
                    "Maximum 110 lbs internal weight",
                    "Return keys attached to key tag"
                ),
                depositRequired = true,
                depositAmount = 50,
                condition = "Great condition",
                estimatedValue = "$550",
                maxDays = 7,
                preferredTimes = "Saturday mornings 9-11 AM"
            )
        )
        _items.value = seedItems

        // Initial loans covering key states
        val seedLoans = listOf(
            LoanExchange(
                id = "loan-01",
                item = seedItems[0], // DeWalt Drill
                borrowerName = "Alex Morgan",
                lenderName = "Marcus K.",
                pickupDateTime = "Tomorrow, 10:00 AM",
                returnDateTime = "Sunday, 5:00 PM",
                note = "Hanging floating shelves in my living room. Will take great care of it!",
                status = LoanStatus.APPROVED,
                exactAddress = "742 Evergreen Terrace (Rear garden gate)",
                pickupInstructions = "Drill kit is in the weatherproof deck storage box. Code is 4821.",
                agreedToTerms = true,
                dueInText = "2 days, 6 hours",
                isBorrowerPerspective = true
            ),
            LoanExchange(
                id = "loan-02",
                item = seedItems[1], // Tent
                borrowerName = "Alex Morgan",
                lenderName = "Elena R.",
                pickupDateTime = "Friday, 4:00 PM",
                returnDateTime = "Monday, 11:00 AM",
                note = "Weekend family camping at Pine Lake. Will pack completely dry!",
                status = LoanStatus.PENDING,
                agreedToTerms = true,
                dueInText = "Pending Approval",
                isBorrowerPerspective = true
            ),
            LoanExchange(
                id = "loan-03",
                item = seedItems[2], // Bundt Pan
                borrowerName = "Sarah Jenkins",
                lenderName = "Alex Morgan",
                pickupDateTime = "Yesterday, 2:00 PM",
                returnDateTime = "Today, 6:00 PM",
                note = "Baking a harvest pumpkin cake for my daughter's birthday!",
                status = LoanStatus.PICKED_UP,
                exactAddress = "318 Maple Street",
                pickupInstructions = "Handed over safely yesterday afternoon.",
                agreedToTerms = true,
                dueInText = "Due in 4 hours",
                isBorrowerPerspective = false // Lender perspective!
            ),
            LoanExchange(
                id = "loan-04",
                item = seedItems[3], // Pressure Washer
                borrowerName = "Alex Morgan",
                lenderName = "Marcus K.",
                pickupDateTime = "Last Weekend",
                returnDateTime = "Monday, 6:00 PM",
                note = "Cleaned our front driveway and walkway. Worked like a charm!",
                status = LoanStatus.RETURNED,
                exactAddress = "742 Evergreen Terrace",
                agreedToTerms = true,
                dueInText = "Completed",
                isBorrowerPerspective = true
            )
        )
        _loans.value = seedLoans

        // Seed chat messages
        val initialChats = mutableMapOf<String, List<ChatMessage>>()
        initialChats["loan-01"] = listOf(
            ChatMessage(
                id = "m1",
                loanId = "loan-01",
                senderName = "Alex Morgan",
                isFromCurrentUser = true,
                text = "Hi Marcus! Requesting to borrow your drill tomorrow morning for hanging shelves.",
                timestamp = "Yesterday, 3:12 PM"
            ),
            ChatMessage(
                id = "m2",
                loanId = "loan-01",
                senderName = "BorrowBuddy System",
                isFromCurrentUser = false,
                text = "Marcus approved your request! Approximate location has been unlocked.",
                timestamp = "Yesterday, 3:25 PM",
                isSystemNotification = true
            ),
            ChatMessage(
                id = "m3",
                loanId = "loan-01",
                senderName = "Marcus K.",
                isFromCurrentUser = false,
                text = "Hey Alex! All set. I've placed the drill in the weatherproof deck box behind the garden gate. Enjoy your DIY project!",
                timestamp = "Yesterday, 3:26 PM"
            ),
            ChatMessage(
                id = "m4",
                loanId = "loan-01",
                senderName = "BorrowBuddy System",
                isFromCurrentUser = false,
                text = "Exact pickup address: 742 Evergreen Terrace. Lockbox code: 4821",
                timestamp = "Yesterday, 3:26 PM",
                isSystemNotification = true,
                hasAddressUnlock = true
            )
        )

        initialChats["loan-02"] = listOf(
            ChatMessage(
                id = "m21",
                loanId = "loan-02",
                senderName = "Alex Morgan",
                isFromCurrentUser = true,
                text = "Hi Elena! Excited to take our kids camping at Pine Lake. Will make sure the tent is completely dry before repacking.",
                timestamp = "Today, 10:15 AM"
            ),
            ChatMessage(
                id = "m22",
                loanId = "loan-02",
                senderName = "BorrowBuddy System",
                isFromCurrentUser = false,
                text = "Request sent to Elena. She usually responds in under 30 minutes.",
                timestamp = "Today, 10:15 AM",
                isSystemNotification = true
            )
        )

        initialChats["loan-03"] = listOf(
            ChatMessage(
                id = "m31",
                loanId = "loan-03",
                senderName = "Sarah Jenkins",
                isFromCurrentUser = false,
                text = "Thank you so much Alex! Handover was super easy. Cake smells amazing already!",
                timestamp = "Yesterday, 5:40 PM"
            ),
            ChatMessage(
                id = "m32",
                loanId = "loan-03",
                senderName = "Alex Morgan",
                isFromCurrentUser = true,
                text = "You're very welcome Sarah! Can't wait to hear how the birthday cake turns out.",
                timestamp = "Yesterday, 6:01 PM"
            )
        )

        _chatMessages.value = initialChats
    }

    fun searchAndFilter(
        query: String,
        category: ItemCategory,
        maxDistanceKm: Double,
        depositOnly: Boolean?,
        sortBy: String
    ): List<BorrowItem> {
        return _items.value.filter { item ->
            val matchesQuery = query.isBlank() ||
                    item.title.contains(query, ignoreCase = true) ||
                    item.description.contains(query, ignoreCase = true) ||
                    item.neighborhood.contains(query, ignoreCase = true)
            val matchesCategory = category == ItemCategory.ALL || item.category == category
            val matchesDistance = item.distanceKm <= maxDistanceKm
            val matchesDeposit = when (depositOnly) {
                true -> item.depositRequired
                false -> !item.depositRequired
                null -> true
            }
            matchesQuery && matchesCategory && matchesDistance && matchesDeposit
        }.let { list ->
            when (sortBy) {
                "Distance" -> list.sortedBy { it.distanceKm }
                "Rating" -> list.sortedByDescending { it.owner.rating }
                "Value" -> list.sortedByDescending { it.depositAmount }
                else -> list
            }
        }
    }

    fun addItem(
        title: String,
        category: ItemCategory,
        description: String,
        rules: List<String>,
        depositRequired: Boolean,
        depositAmount: Int,
        condition: String,
        preferredTimes: String
    ): BorrowItem {
        val user = _userProfile.value
        val newItem = BorrowItem(
            id = "bb-${System.currentTimeMillis() % 10000}",
            title = title,
            category = category,
            distanceKm = 0.1, // Local to the user
            neighborhood = user.neighborhood,
            crossStreets = "Near Community Park (Within 500m)",
            owner = Lender(
                id = "current_user",
                name = user.name,
                rating = 5.0,
                reviewsCount = user.itemsLentCount,
                badges = user.verifications,
                responseTime = "< 5 mins",
                neighborhood = user.neighborhood
            ),
            description = description,
            rules = if (rules.isEmpty()) listOf("Return clean and on time") else rules,
            depositRequired = depositRequired,
            depositAmount = depositAmount,
            condition = condition,
            preferredTimes = preferredTimes
        )
        _items.value = listOf(newItem) + _items.value
        _userProfile.value = user.copy(itemsLentCount = user.itemsLentCount + 1)
        return newItem
    }

    fun requestBorrow(
        item: BorrowItem,
        pickupDateTime: String,
        returnDateTime: String,
        note: String
    ): LoanExchange {
        val user = _userProfile.value
        val loanId = "loan-${System.currentTimeMillis() % 10000}"
        val newLoan = LoanExchange(
            id = loanId,
            item = item,
            borrowerName = user.name,
            lenderName = item.owner.name,
            pickupDateTime = pickupDateTime,
            returnDateTime = returnDateTime,
            note = note,
            status = LoanStatus.PENDING,
            isBorrowerPerspective = true
        )
        _loans.value = listOf(newLoan) + _loans.value

        // Seed initial message
        val msgs = listOf(
            ChatMessage(
                id = "m-${System.currentTimeMillis()}",
                loanId = loanId,
                senderName = user.name,
                isFromCurrentUser = true,
                text = "Hi ${item.owner.name}! I would love to borrow your '${item.title}'. Pickup proposed: $pickupDateTime, Return: $returnDateTime. Note: $note",
                timestamp = "Just now"
            ),
            ChatMessage(
                id = "m-${System.currentTimeMillis() + 1}",
                loanId = loanId,
                senderName = "BorrowBuddy System",
                isFromCurrentUser = false,
                text = "Borrow request sent! You will be notified as soon as ${item.owner.name} reviews it.",
                timestamp = "Just now",
                isSystemNotification = true
            )
        )
        val currentChats = _chatMessages.value.toMutableMap()
        currentChats[loanId] = msgs
        _chatMessages.value = currentChats

        return newLoan
    }

    fun approveLoan(loanId: String) {
        val updated = _loans.value.map { loan ->
            if (loan.id == loanId) {
                loan.copy(
                    status = LoanStatus.APPROVED,
                    exactAddress = "318 Maple Street (Rear garden box)",
                    pickupInstructions = "Available for pickup! Call or message if needed."
                )
            } else loan
        }
        _loans.value = updated
        addSystemChatMessage(loanId, "Request approved! The pickup coordinates and instructions are now available.")
    }

    fun declineLoan(loanId: String) {
        val updated = _loans.value.map { loan ->
            if (loan.id == loanId) loan.copy(status = LoanStatus.CANCELLED) else loan
        }
        _loans.value = updated
        addSystemChatMessage(loanId, "This request was declined or cancelled.")
    }

    fun confirmHandover(loanId: String) {
        val updated = _loans.value.map { loan ->
            if (loan.id == loanId) loan.copy(status = LoanStatus.PICKED_UP) else loan
        }
        _loans.value = updated
        addSystemChatMessage(loanId, "Item marked as picked up! Please handle with care and return by scheduled time.")
    }

    fun confirmReturn(loanId: String) {
        val updated = _loans.value.map { loan ->
            if (loan.id == loanId) loan.copy(status = LoanStatus.RETURNED) else loan
        }
        _loans.value = updated
        addSystemChatMessage(loanId, "Item safely returned! Loan completed. Please take a moment to leave a review.")
    }

    fun submitReview(loanId: String, rating: Int, feedback: String, tags: List<String>) {
        val updated = _loans.value.map { loan ->
            if (loan.id == loanId) {
                loan.copy(reviewRating = rating, reviewFeedback = feedback, reviewTags = tags)
            } else loan
        }
        _loans.value = updated
        addSystemChatMessage(loanId, "Review submitted (★ $rating). Thank you for keeping our neighborhood trusting and helpful!")
    }

    fun sendMessage(loanId: String, text: String) {
        val user = _userProfile.value
        val newMsg = ChatMessage(
            id = "msg-${System.currentTimeMillis()}",
            loanId = loanId,
            senderName = user.name,
            isFromCurrentUser = true,
            text = text,
            timestamp = "Just now"
        )
        val currentChats = _chatMessages.value.toMutableMap()
        val list = (currentChats[loanId] ?: emptyList()) + newMsg
        currentChats[loanId] = list
        _chatMessages.value = currentChats
    }

    private fun addSystemChatMessage(loanId: String, text: String) {
        val sysMsg = ChatMessage(
            id = "sys-${System.currentTimeMillis()}",
            loanId = loanId,
            senderName = "BorrowBuddy System",
            isFromCurrentUser = false,
            text = text,
            timestamp = "Just now",
            isSystemNotification = true
        )
        val currentChats = _chatMessages.value.toMutableMap()
        val list = (currentChats[loanId] ?: emptyList()) + sysMsg
        currentChats[loanId] = list
        _chatMessages.value = currentChats
    }

    fun toggleOfflineMode() {
        _isOffline.value = !_isOffline.value
    }

    fun reportItem(itemId: String, reason: String) {
        _reports.value = _reports.value + (itemId to reason)
    }

    fun updateNeighborhood(newNeighborhood: String) {
        _userProfile.value = _userProfile.value.copy(neighborhood = newNeighborhood)
    }
}
