
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
    val tripCount: Int = 0,
    val overpaidAmount: Double = 0.0
)

class TransportStore {

    private val repository = TransportRepository()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    var farePlans by mutableStateOf<List<FarePlan>>(emptyList())
        private set

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
        farePlans = repository.getFarePlans()
            .sortedBy { it.name.lowercase() }

        children = repository.getChildren()
            .sortedBy { it.name.lowercase() }

        trips = repository.getTrips()
            .sortedWith(
                compareByDescending<TransportTrip> { it.date }
                    .thenByDescending { it.time }
            )

        payments = repository.getPayments()
            .sortedByDescending { it.paymentDate }
    }

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

    // FARE PLANS

    fun saveFarePlan(plan: FarePlan) {
        if (plan.name.isBlank()) {
            errorMessage = "Payment method name is required"
            return
        }

        if (plan.morningFare < 0 || plan.returnFare < 0) {
            errorMessage = "Fare amounts cannot be negative"
            return
        }

        execute(
            {
                repository.saveFarePlan(plan)

                // Update currently assigned children.
                // Existing trip records are deliberately untouched.
                if (plan.id.isNotBlank()) {
                    val assignedChildren = children.filter {
                        it.farePlanId == plan.id
                    }

                    assignedChildren.forEach { child ->
                        repository.saveChild(
                            child.copy(
                                farePlanName = plan.name.trim(),
                                morningFare = plan.morningFare,
                                returnFare = plan.returnFare
                            )
                        )
                    }
                }
            },
            "Payment configuration saved successfully"
        )
    }

    fun deleteFarePlan(id: String) {
        val isAssigned = children.any {
            it.farePlanId == id
        }

        if (isAssigned) {
            errorMessage =
                "This payment method is assigned to a child. Change the child's plan first."
            return
        }

        execute(
            { repository.deleteFarePlan(id) },
            "Payment configuration deleted successfully"
        )
    }

    fun findFarePlan(id: String): FarePlan? {
        return farePlans.find { it.id == id }
    }

    // CHILDREN

    fun saveChild(child: TransportChild) {
        if (child.name.isBlank()) {
            errorMessage = "Child name is required"
            return
        }

        if (child.age < 0) {
            errorMessage = "Age cannot be negative"
            return
        }

        val selectedPlan = farePlans.firstOrNull {
            it.id == child.farePlanId
        }

        if (child.farePlanId.isNotBlank() && selectedPlan == null) {
            errorMessage = "Selected payment configuration was not found"
            return
        }

        // A new child must have a fare plan.
        if (child.id.isBlank() && selectedPlan == null) {
            errorMessage = "Please select a payment configuration"
            return
        }

        val childToSave = if (selectedPlan != null) {
            child.copy(
                farePlanName = selectedPlan.name,
                morningFare = selectedPlan.morningFare,
                returnFare = selectedPlan.returnFare
            )
        } else {
            // Existing legacy child without a plan:
            // preserve the previously stored fares.
            child
        }

        execute(
            { repository.saveChild(childToSave) },
            "Child saved successfully"
        )
    }

    fun deleteChild(id: String) {
        execute(
            { repository.deleteChild(id) },
            "Child deleted successfully"
        )
    }

    fun findChild(id: String): TransportChild? {
        return children.find { it.id == id }
    }

    // TRIPS

    fun saveTrip(trip: TransportTrip) {
        if (trip.childId.isBlank()) {
            errorMessage = "Please select a child"
            return
        }

        if (trip.date.isBlank()) {
            errorMessage = "Trip date is required"
            return
        }

        if (!trip.morningDone && !trip.returnDone) {
            errorMessage = "Select at least one completed trip"
            return
        }

        if (trip.morningFare < 0 || trip.returnFare < 0) {
            errorMessage = "Fare cannot be negative"
            return
        }

        execute(
            { repository.saveTrip(trip) },
            "Trip saved successfully"
        )
    }

    fun deleteTrip(id: String) {
        execute(
            { repository.deleteTrip(id) },
            "Trip deleted successfully"
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
        if (payment.childId.isBlank()) {
            errorMessage = "Please select a child"
            return
        }

        if (payment.amount <= 0) {
            errorMessage = "Payment amount must be greater than zero"
            return
        }

        if (payment.paymentDate.isBlank()) {
            errorMessage = "Payment date is required"
            return
        }

        val normalizedPayment = payment.copy(
            monthKey = payment.monthKey.ifBlank {
                payment.paymentDate.take(7)
            }
        )

        execute(
            { repository.savePayment(normalizedPayment) },
            "Payment saved successfully"
        )
    }

    fun deletePayment(id: String) {
        execute(
            { repository.deletePayment(id) },
            "Payment deleted successfully"
        )
    }

    // MONTHLY SUMMARY

    fun monthlySummary(monthKey: String): MonthlySummary {
        val monthTrips = trips.filter {
            it.date.startsWith("$monthKey-")
        }

        val monthPayments = payments.filter {
            it.monthKey == monthKey
        }

        return createSummary(
            totalFare = monthTrips.sumOf { it.totalFare },
            paidAmount = monthPayments.sumOf { it.amount },
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

        return createSummary(
            totalFare = childTrips.sumOf { it.totalFare },
            paidAmount = childPayments.sumOf { it.amount },
            tripCount = childTrips.size
        )
    }

    private fun createSummary(
        totalFare: Double,
        paidAmount: Double,
        tripCount: Int
    ): MonthlySummary {
        val balance = (totalFare - paidAmount).coerceAtLeast(0.0)
        val overpaid = (paidAmount - totalFare).coerceAtLeast(0.0)

        return MonthlySummary(
            totalFare = totalFare,
            paidAmount = paidAmount,
            balance = balance,
            tripCount = tripCount,
            overpaidAmount = overpaid
        )
    }

    fun clearMessages() {
        errorMessage = null
        successMessage = null
    }
}
