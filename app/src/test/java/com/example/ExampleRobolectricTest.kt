package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.BorrowRepository
import com.example.model.ItemCategory
import com.example.model.LoanStatus
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
