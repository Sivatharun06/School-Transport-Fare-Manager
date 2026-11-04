package com.schooltransport.faremanager

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

data class MonthlySummary(
    val totalFare: Double = 0.0,
    val paidAmount: Double = 0.0,
    val balance: Double = 0.0,
    val tripCount: Int = 0
)

class TransportStore {

    private val repository = TransportRepository()

    private val scope =
        CoroutineScope(SupervisorJob() + Dispatchers.Main)

    var children by mutableStateOf<List<TransportChild>>(emptyList())
        private set

    var trips by mutableStateOf<List<TransportTrip>>(emptyList())
        private set

    var payments by mutableStateOf<List<TransportPayment>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var successMessage by mutableStateOf<String?>(null)
        private set

    // LOAD ALL DATA

    fun loadAll() {
        scope.launch {
            isLoading = true
            errorMessage = null

            try {
                refreshData()
            } catch (e: Exception) {
                errorMessage = e.message ?: "Unable to load data"
            } finally {
                isLoading = false
            }
        }
    }

    private suspend fun refreshData() {
        children = repository.getChildren()
            .sortedBy { it.name.lowercase() }

        trips = repository.getTrips()
            .sortedByDescending { it.date }

        payments = repository.getPayments()
            .sortedByDescending { it.paymentDate }
    }

    // COMMON OPERATION HANDLER

    private fun execute(
        operation: suspend () -> Unit,
        message: String
    ) {
        scope.launch {
            isLoading = true
            errorMessage = null
            successMessage = null

            try {
                operation()
                refreshData()
                successMessage = message
            } catch (e: Exception) {
                errorMessage = e.message ?: "Something went wrong"
            } finally {
                isLoading = false
            }
        }
    }

    // CHILDREN

    fun saveChild(child: TransportChild) {
        execute(
            operation = { repository.saveChild(child) },
            message = "Child saved successfully"
        )
    }

    fun deleteChild(id: String) {
        execute(
            operation = { repository.deleteChild(id) },
            message = "Child deleted successfully"
        )
    }

    fun findChild(id: String): TransportChild? {
        return children.find { it.id == id }
    }

    // TRIPS / ATTENDANCE

    fun saveTrip(trip: TransportTrip) {
        execute(
            operation = { repository.saveTrip(trip) },
            message = "Attendance saved successfully"
        )
    }

    fun deleteTrip(id: String) {
        execute(
            operation = { repository.deleteTrip(id) },
            message = "Trip deleted successfully"
        )
    }

    fun tripsForDate(date: String): List<TransportTrip> {
        return trips.filter { it.date == date }
    }

    fun tripsForChild(
        childId: String,
        monthKey: String
    ): List<TransportTrip> {
        return trips.filter {
            it.childId == childId &&
            it.date.startsWith("$monthKey-")
        }
    }

    // PAYMENTS

    fun savePayment(payment: TransportPayment) {
        val normalizedPayment = payment.copy(
            monthKey = payment.monthKey.ifBlank {
                payment.paymentDate.take(7)
            }
        )

        execute(
            operation = {
                repository.savePayment(normalizedPayment)
            },
            message = "Payment saved successfully"
        )
    }

    fun deletePayment(id: String) {
        execute(
            operation = { repository.deletePayment(id) },
            message = "Payment deleted successfully"
        )
    }

    // MONTHLY CALCULATIONS

    fun monthlySummary(monthKey: String): MonthlySummary {

        val monthTrips = trips.filter {
            it.date.startsWith("$monthKey-")
        }

        val monthPayments = payments.filter {
            it.monthKey == monthKey
        }

        val totalFare = monthTrips.sumOf {
            it.totalFare
        }

        val paidAmount = monthPayments.sumOf {
            it.amount
        }

        return MonthlySummary(
            totalFare = totalFare,
            paidAmount = paidAmount,
            balance = totalFare - paidAmount,
            tripCount = monthTrips.size
        )
    }

    fun childMonthlySummary(
        childId: String,
        monthKey: String
    ): MonthlySummary {

        val childTrips = tripsForChild(childId, monthKey)

        val childPayments = payments.filter {
            it.childId == childId &&
            it.monthKey == monthKey
        }

        val totalFare = childTrips.sumOf {
            it.totalFare
        }

        val paidAmount = childPayments.sumOf {
            it.amount
        }

        return MonthlySummary(
            totalFare = totalFare,
            paidAmount = paidAmount,
            balance = totalFare - paidAmount,
            tripCount = childTrips.size
        )
    }

    // MESSAGE MANAGEMENT

    fun clearMessages() {
        errorMessage = null
        successMessage = null
    }
}
