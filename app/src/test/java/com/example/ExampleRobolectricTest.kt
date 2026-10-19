package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.BorrowRepository
import com.example.location.GpsLocationManager
import com.example.model.ItemCategory
import com.example.model.LoanStatus
import com.example.model.UserGpsLocation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("BorrowBuddy", appName)
    }

    @Test
    fun `repository provides initial items and loans`() {
        val repo = BorrowRepository()
        val items = repo.items.value
        assertTrue("Repository should have seeded items", items.isNotEmpty())
        val drill = items.find { it.title.contains("Drill") }
        assertNotNull("Drill should be present in seeded items", drill)

        val loans = repo.loans.value
        assertTrue("Repository should have active loan states", loans.isNotEmpty())
    }

    @Test
    fun `nearby providers can be retrieved based on distance`() {
        val repo = BorrowRepository()
        val nearby = repo.getNearbyProviders(maxDistanceKm = 10.0)
        assertTrue("Should find nearby providers within 10 km", nearby.isNotEmpty())
        val firstProvider = nearby.first()
        assertTrue("Provider distance should be non-negative", firstProvider.second >= 0.0)
    }

    @Test
    fun `gps location update recomputes item distances`() {
        val repo = BorrowRepository()
        val initialDrill = repo.items.value.first()
        val initialDist = initialDrill.distanceKm

        // Simulate moving to new GPS coordinates
        val newGps = UserGpsLocation(
            latitude = 37.7800,
            longitude = -122.4150,
            accuracyMeters = 5f,
            neighborhoodName = "Downtown Hub",
            isRealGpsFix = true
        )
        repo.updateUserGpsLocation(newGps)

        val updatedDrill = repo.items.value.first { it.id == initialDrill.id }
        assertNotNull(updatedDrill)
        assertEquals("Downtown Hub", repo.userProfile.value.neighborhood)
    }

    @Test
    fun `borrow request creates pending loan`() {
        val repo = BorrowRepository()
        val item = repo.items.value.first()
        val newLoan = repo.requestBorrow(item, "Tomorrow 10 AM", "Sunday 5 PM", "Testing borrow")

        assertEquals(LoanStatus.PENDING, newLoan.status)
        assertEquals(item.id, newLoan.item.id)
    }

    @Test
    fun `approve loan unlocks pickup address`() {
        val repo = BorrowRepository()
        val item = repo.items.value.first()
        val newLoan = repo.requestBorrow(item, "Tomorrow 10 AM", "Sunday 5 PM", "DIY project")
        repo.approveLoan(newLoan.id)

        val updated = repo.loans.value.first { it.id == newLoan.id }
        assertEquals(LoanStatus.APPROVED, updated.status)
        assertNotNull(updated.exactAddress)
    }
}
