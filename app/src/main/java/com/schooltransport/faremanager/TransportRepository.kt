
package com.schooltransport.faremanager

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class TransportRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // USER-SPECIFIC COLLECTION

    private fun userCollection(name: String) =
        db.collection("users")
            .document(
                auth.currentUser?.uid
                    ?.takeIf { it.isNotBlank() }
                    ?: throw IllegalStateException("Please login first")
            )
            .collection(name)

    // FARE PLANS

    suspend fun getFarePlans(): List<FarePlan> {
        return userCollection("farePlans")
            .get()
            .await()
            .documents
            .mapNotNull { document ->
                document.toObject(FarePlan::class.java)
                    ?.copy(id = document.id)
            }
    }

    suspend fun saveFarePlan(plan: FarePlan) {
        val collection = userCollection("farePlans")

        val document = if (plan.id.isBlank()) {
            collection.document()
        } else {
            collection.document(plan.id)
        }

        document.set(
            plan.copy(id = document.id)
        ).await()
    }

    suspend fun deleteFarePlan(id: String) {
        require(id.isNotBlank()) {
            "Invalid payment configuration ID"
        }

        userCollection("farePlans")
            .document(id)
            .delete()
            .await()
    }

    // CHILDREN

    suspend fun getChildren(): List<TransportChild> {
        return userCollection("children")
            .get()
            .await()
            .documents
            .mapNotNull { document ->
                document.toObject(TransportChild::class.java)
                    ?.copy(id = document.id)
            }
    }

    suspend fun saveChild(child: TransportChild) {
        val collection = userCollection("children")

        val document = if (child.id.isBlank()) {
            collection.document()
        } else {
            collection.document(child.id)
        }

        document.set(
            child.copy(id = document.id)
        ).await()
    }

    suspend fun deleteChild(id: String) {
        require(id.isNotBlank()) {
            "Invalid child ID"
        }

        userCollection("children")
            .document(id)
            .delete()
            .await()
    }

    // TRIPS

    suspend fun getTrips(): List<TransportTrip> {
        return userCollection("trips")
            .get()
            .await()
            .documents
            .mapNotNull { document ->
                document.toObject(TransportTrip::class.java)
                    ?.copy(id = document.id)
            }
    }

    suspend fun saveTrip(trip: TransportTrip) {
        val collection = userCollection("trips")

        val document = if (trip.id.isBlank()) {
            collection.document()
        } else {
            collection.document(trip.id)
        }

        document.set(
            trip.copy(id = document.id)
        ).await()
    }

    suspend fun deleteTrip(id: String) {
        require(id.isNotBlank()) {
            "Invalid trip ID"
        }

        userCollection("trips")
            .document(id)
            .delete()
            .await()
    }

    // PAYMENTS

    suspend fun getPayments(): List<TransportPayment> {
        return userCollection("payments")
            .get()
            .await()
            .documents
            .mapNotNull { document ->
                document.toObject(TransportPayment::class.java)
                    ?.copy(id = document.id)
            }
    }

    suspend fun savePayment(payment: TransportPayment) {
        val collection = userCollection("payments")

        val document = if (payment.id.isBlank()) {
            collection.document()
        } else {
            collection.document(payment.id)
        }

        document.set(
            payment.copy(id = document.id)
        ).await()
    }

    suspend fun deletePayment(id: String) {
        require(id.isNotBlank()) {
            "Invalid payment ID"
        }

        userCollection("payments")
            .document(id)
            .delete()
            .await()
    }
}
