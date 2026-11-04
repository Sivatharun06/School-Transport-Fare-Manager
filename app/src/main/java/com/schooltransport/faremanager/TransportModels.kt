
package com.schooltransport.faremanager

import com.google.firebase.firestore.Exclude

data class FarePlan(
    val id: String = "",
    val name: String = "",
    val morningFare: Double = 400.0,
    val returnFare: Double = 400.0,
    val createdAt: Long = System.currentTimeMillis()
)

data class TransportChild(
    val id: String = "",
    val name: String = "",
    val age: Int = 0,
    val parentName: String = "",
    val phone: String = "",
    val fromLocation: String = "",
    val toLocation: String = "",
    val farePlanId: String = "",
    val farePlanName: String = "",
    val morningFare: Double = 400.0,
    val returnFare: Double = 400.0,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

data class TransportTrip(
    val id: String = "",
    val childId: String = "",
    val childName: String = "",
    val date: String = "",
    val time: String = "",
    val fromLocation: String = "",
    val toLocation: String = "",
    val morningDone: Boolean = false,
    val returnDone: Boolean = false,
    val morningFare: Double = 400.0,
    val returnFare: Double = 400.0,
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    @get:Exclude
    val totalFare: Double
        get() = (if (morningDone) morningFare else 0.0) +
                (if (returnDone) returnFare else 0.0)
}

data class TransportPayment(
    val id: String = "",
    val childId: String = "",
    val childName: String = "",
    val amount: Double = 0.0,
    val paymentDate: String = "",
    val monthKey: String = "",
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
