
package com.schooltransport.faremanager

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.text.SimpleDateFormat
import java.util.Locale

data class MonthlySummary(
    val totalFare: Double = 0.0,
    val paidAmount: Double = 0.0,
    val balance: Double = 0.0,
    val tripCount: Int = 0,
    val overpaidAmount: Double = 0.0
)

class TransportStore {

    private val repository = TransportRepository()

    private val scope =
        CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val operationMutex = Mutex()

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

    // CALENDAR CHILD SELECTION

    var selectedCalendarChildId by mutableStateOf("")
        private set

    fun selectCalendarChild(childId: String) {
        selectedCalendarChildId = childId
    }

    // LOAD DATA

    fun loadAll() {
        scope.launch {
            operationMutex.withLock {
                isLoading = true
                errorMessage = null

                try {
                    refreshData()
                } catch (e: Exception) {
                    errorMessage =
                        e.message ?: "Unable to load data"
                } finally {
                    isLoading = false
                }
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

        // Keep the previously selected child if it still exists.
        if (
            selectedCalendarChildId.isNotBlank() &&
            children.none { it.id == selectedCalendarChildId }
        ) {
            selectedCalendarChildId = ""
        }
    }

    // COMMON FIREBASE OPERATION

    private fun execute(
        operation: suspend () -> Unit,
        message: String
    ) {
        scope.launch {
            operationMutex.withLock {

                isLoading = true
                errorMessage = null
                successMessage = null

                try {
                    operation()
                    refreshData()
                    successMessage = message

                } catch (e: Exception) {
                    errorMessage =
                        e.message ?: "Something went wrong"

                } finally {
                    isLoading = false
                }
            }
        }
    }

    // FARE PLANS

    fun saveFarePlan(plan: FarePlan) {

        val cleanName = plan.name.trim()

        if (cleanName.isBlank()) {
            errorMessage =
                "Payment method name is required"
            return
        }

        if (
            !plan.morningFare.isFinite() ||
            !plan.returnFare.isFinite() ||
            plan.morningFare < 0 ||
            plan.returnFare < 0
        ) {
            errorMessage =
                "Enter valid non-negative fare amounts"
            return
        }

        val cleanPlan = plan.copy(name = cleanName)

        execute(
            {
                repository.saveFarePlan(cleanPlan)

                if (cleanPlan.id.isNotBlank()) {

                    val assignedChildren =
                        children.filter {
                            it.farePlanId == cleanPlan.id
                        }

                    assignedChildren.forEach { child ->

                        repository.saveChild(
                            child.copy(
                                farePlanName = cleanPlan.name,
                                morningFare = cleanPlan.morningFare,
                                returnFare = cleanPlan.returnFare
                            )
                        )
                    }
                }
            },
            "Payment configuration saved successfully"
        )
    }

    fun deleteFarePlan(id: String) {

        if (id.isBlank()) {
            errorMessage =
                "Invalid payment configuration"
            return
        }

        execute(
            {
                val isAssigned = children.any {
                    it.farePlanId == id
                }

                if (isAssigned) {
                    throw IllegalStateException(
                        "This payment method is assigned to a child. Change the child's plan first."
                    )
                }

                repository.deleteFarePlan(id)
            },
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

        if (
            child.farePlanId.isNotBlank() &&
            selectedPlan == null
        ) {
            errorMessage =
                "Selected payment configuration was not found"
            return
        }

        if (child.id.isBlank() && selectedPlan == null) {
            errorMessage =
                "Please select a payment configuration"
            return
        }

        val childToSave = if (selectedPlan != null) {

            child.copy(
                farePlanName = selectedPlan.name,
                morningFare = selectedPlan.morningFare,
                returnFare = selectedPlan.returnFare
            )

        } else {
            child
        }

        if (
            !childToSave.morningFare.isFinite() ||
            !childToSave.returnFare.isFinite() ||
            childToSave.morningFare < 0 ||
            childToSave.returnFare < 0
        ) {
            errorMessage = "Enter valid child fare amounts"
            return
        }

        execute(
            {
                repository.saveChild(childToSave)
            },
            "Child saved successfully"
        )
    }

    fun deleteChild(id: String) {

        if (id.isBlank()) {
            errorMessage = "Invalid child ID"
            return
        }

        execute(
            {
                repository.deleteChild(id)
            },
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

        if (!isValidDate(trip.date)) {
            errorMessage = "Enter a valid trip date"
            return
        }

        if (!trip.morningDone && !trip.returnDone) {
            errorMessage =
                "Select at least one completed trip"
            return
        }

        if (
            !trip.morningFare.isFinite() ||
            !trip.returnFare.isFinite() ||
            trip.morningFare < 0 ||
            trip.returnFare < 0
        ) {
            errorMessage = "Enter valid fare amounts"
            return
        }

        execute(
            {
                repository.saveTrip(trip)
            },
            "Trip saved successfully"
        )
    }

    fun deleteTrip(id: String) {

        if (id.isBlank()) {
            errorMessage = "Invalid trip ID"
            return
        }

        execute(
            {
                repository.deleteTrip(id)
            },
            "Trip deleted successfully"
        )
    }

    fun tripsForDate(date: String): List<TransportTrip> {
        return trips.filter {
            it.date == date
        }
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

        if (
            !payment.amount.isFinite() ||
            payment.amount <= 0
        ) {
            errorMessage =
                "Payment amount must be a valid number greater than zero"
            return
        }

        if (!isValidDate(payment.paymentDate)) {
            errorMessage = "Enter a valid payment date"
            return
        }

        val normalizedPayment = payment.copy(
            monthKey = payment.paymentDate.take(7)
        )

        execute(
            {
                repository.savePayment(normalizedPayment)
            },
            "Payment saved successfully"
        )
    }

    fun deletePayment(id: String) {

        if (id.isBlank()) {
            errorMessage = "Invalid payment ID"
            return
        }

        execute(
            {
                repository.deletePayment(id)
            },
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
            totalFare = monthTrips.sumOf {
                it.totalFare
            },
            paidAmount = monthPayments.sumOf {
                it.amount
            },
            tripCount = monthTrips.size
        )
    }

    fun childMonthlySummary(
        childId: String,
        monthKey: String
    ): MonthlySummary {

        val childTrips = tripsForChild(
            childId,
            monthKey
        )

        val childPayments = payments.filter {
            it.childId == childId &&
                it.monthKey == monthKey
        }

        return createSummary(
            totalFare = childTrips.sumOf {
                it.totalFare
            },
            paidAmount = childPayments.sumOf {
                it.amount
            },
            tripCount = childTrips.size
        )
    }

    private fun createSummary(
        totalFare: Double,
        paidAmount: Double,
        tripCount: Int
    ): MonthlySummary {

        val balance =
            (totalFare - paidAmount).coerceAtLeast(0.0)

        val overpaid =
            (paidAmount - totalFare).coerceAtLeast(0.0)

        return MonthlySummary(
            totalFare = totalFare,
            paidAmount = paidAmount,
            balance = balance,
            tripCount = tripCount,
            overpaidAmount = overpaid
        )
    }

    // DATE VALIDATION

    private fun isValidDate(value: String): Boolean {

        if (
            !value.matches(
                Regex("^\\d{4}-\\d{2}-\\d{2}$")
            )
        ) {
            return false
        }

        return try {

            val format = SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.US
            )

            format.isLenient = false
            format.parse(value) != null

        } catch (_: Exception) {
            false
        }
    }

    // CLEAR MESSAGES

    fun clearMessages() {
        errorMessage = null
        successMessage = null
    }
}
