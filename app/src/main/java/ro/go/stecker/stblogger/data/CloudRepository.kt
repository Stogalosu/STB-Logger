package ro.go.stecker.stblogger.data

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObjects

interface CloudRepo {
    fun getStopDatabase(onSuccess: (List<Stop>) -> Unit)
    fun getLineDatabase(onSuccess: (List<Line>) -> Unit)
//    fun getVehicleDatabase()
}

class CloudRepository: CloudRepo {
    val db = Firebase.firestore

    override fun getStopDatabase(onSuccess: (List<Stop>) -> Unit) {
        db.collection("stops")
            .get()
            .addOnSuccessListener { onSuccess(it.toObjects<Stop>()) }
    }

    override fun getLineDatabase(onSuccess: (List<Line>) -> Unit) {
        db.collection("lines")
            .get()
            .addOnSuccessListener { onSuccess(it.toObjects<Line>()) }
    }
}