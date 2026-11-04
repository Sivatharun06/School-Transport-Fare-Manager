
package com.schooltransport.faremanager

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class TransportRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun userCollection(name: String) =
        db.collection("users")
            .document(auth.currentUser?.uid
                ?: throw IllegalStateException("Please login"))
            .collection(name)

    // CHILDREN

    suspend fun getChildren(): List<TransportChild> {
        return userCollection("children")
            .get().await()
            .documents.mapNotNull {
                it.toObject(TransportChild::class.java)
                    ?.copy(id = it.id)
            }
    }

    suspend fun saveChild(child: TransportChild) {
        val ref = userCollection("children")
        if (child.id.isBlank()) {
            val doc = ref.document()
            doc.set(child.copy(id = doc.id)).await()
        } else {
            ref.document(child.id).set(child).await()
        }
    }

    suspend fun deleteChild(id: String) {
        userCollection("children").document(id).delete().await()
    }

    // TRIPS

    suspend fun getTrips(): List<TransportTrip> {
        return userCollection("trips")
            .get().await()
            .documents.mapNotNull {
                it.toObject(TransportTrip::class.java)
                    ?.copy(id = it.id)
            }
    }

    suspend fun saveTrip(trip: TransportTrip) {
        val ref = userCollection("trips")
        if (trip.id.isBlank()) {
            val doc = ref.document()
            doc.set(trip.copy(id = doc.id)).await()
        } else {
            ref.document(trip.id).set(trip).await()
        }
    }

    suspend fun deleteTrip(id: String) {
        userCollection("trips").document(id).delete().await()
    }

    // PAYMENTS

    suspend fun getPayments(): List<TransportPayment> {
        return userCollection("payments")
            .get().await()
            .documents.mapNotNull {
                it.toObject(TransportPayment::class.java)
                    ?.copy(id = it.id)
            }
    }

    suspend fun savePayment(payment: TransportPayment) {
        val ref = userCollection("payments")
        if (payment.id.isBlank()) {
            val doc = ref.document()
            doc.set(payment.copy(id = doc.id)).await()
        } else {
            ref.document(payment.id).set(payment).await()
        }
    }

    suspend fun deletePayment(id: String) {
        userCollection("payments").document(id).delete().await()
    }
}
