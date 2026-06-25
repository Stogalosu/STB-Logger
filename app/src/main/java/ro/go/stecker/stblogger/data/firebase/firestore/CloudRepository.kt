package ro.go.stecker.stblogger.data.firebase.firestore

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObjects
import ro.go.stecker.stblogger.data.database.entities.Line
import ro.go.stecker.stblogger.data.database.entities.Path
import ro.go.stecker.stblogger.data.database.entities.Stop

interface CloudRepo {
    fun getStopDatabase(onSuccess: (List<Stop>) -> Unit)
    fun getLineDatabase(onSuccess: (List<Line>) -> Unit)
    fun getPathDatabase(onSuccess: (List<Path>) -> Unit)
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

    override fun getPathDatabase(onSuccess: (List<Path>) -> Unit) {
        db.collection("paths")
            .get()
            .addOnSuccessListener {snapshot ->
                snapshot.documents.sortBy { it.reference.id }
                val list = snapshot.toObjects<Path>().toMutableList()
                onSuccess(list)
            }
    }
}