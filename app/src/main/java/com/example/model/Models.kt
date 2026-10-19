package com.example.model

enum class ItemCategory(val displayName: String, val iconName: String) {
    ALL("All", "Category"),
    TOOLS("Tools & DIY", "Build"),
    OUTDOOR("Outdoor & Camping", "Camp"),
    KITCHEN("Kitchen & Party", "Restaurant"),
    LAWN("Lawn & Garden", "Yard"),
    BABY("Baby & Kids", "Child"),
    HOBBY("Games & Hobbies", "Casino")
}

data class Lender(
    val id: String,
    val name: String,
    val rating: Double,
    val reviewsCount: Int,
    val badges: List<String>,
    val memberSince: String = "2024",
    val responseTime: String = "< 15 mins",
    val neighborhood: String = "Maplewood Heights"
)

data class BorrowItem(
    val id: String,
    val title: String,
    val category: ItemCategory,
    val distanceKm: Double,
    val neighborhood: String,
    val crossStreets: String,
    val owner: Lender,
    val description: String,
    val rules: List<String>,
    val depositRequired: Boolean = false,
    val depositAmount: Int = 0,
    val condition: String = "Excellent condition",
    val estimatedValue: String = "$120",
    val maxDays: Int = 3,
    val preferredTimes: String = "Evenings (after 5 PM) or weekends",
    val isAvailable: Boolean = true
) {
    val distanceDisplay: String
        get() = "${distanceKm} km away"
}

enum class LoanStatus(val label: String) {
    PENDING("Pending Approval"),
    APPROVED("Confirmed & Ready"),
    PICKED_UP("Picked Up / In Use"),
    RETURNED("Returned Safely"),
    CANCELLED("Cancelled")
}

data class LoanExchange(
    val id: String,
    val item: BorrowItem,
    val borrowerName: String,
    val lenderName: String,
    val pickupDateTime: String,
    val returnDateTime: String,
    val note: String,
    val status: LoanStatus,
    val exactAddress: String? = null,
    val pickupInstructions: String? = null,
    val agreedToTerms: Boolean = true,
    val dueInText: String = "1 day, 4 hours",
    val reviewRating: Int? = null,
    val reviewFeedback: String? = null,
    val reviewTags: List<String> = emptyList(),
    val isBorrowerPerspective: Boolean = true
)

data class ChatMessage(
    val id: String,
    val loanId: String,
    val senderName: String,
    val isFromCurrentUser: Boolean,
    val text: String,
    val timestamp: String,
    val isSystemNotification: Boolean = false,
    val hasAddressUnlock: Boolean = false
)

data class UserProfile(
    val name: String,
    val neighborhood: String,
    val trustScore: Int,
    val verifications: List<String>,
    val itemsLentCount: Int,
    val itemsBorrowedCount: Int,
    val memberSince: String,
    val isIdVerified: Boolean = true
)
