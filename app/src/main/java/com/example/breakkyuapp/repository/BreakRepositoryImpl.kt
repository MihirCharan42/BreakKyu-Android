package com.example.breakkyuapp.repository

import android.content.res.Resources.NotFoundException
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.Query
import java.lang.Exception
import java.sql.Time
import javax.inject.Inject

class BreakRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
) : BreakRepository {
    override fun getBreak(): Resource<Task<QuerySnapshot>> {
        val res = firestore.collection("breaks").orderBy("time", Query.Direction.DESCENDING).get()
        return Resource.Success(res)
    }

    override fun addBreak(
        firebaseUser: FirebaseUser, fromTime: Timestamp, note: String
    ): Resource<Task<DocumentReference>> {
        var breakObj = hashMapOf(
            "userName" to firebaseUser.displayName,
            "userId" to firebaseUser.uid,
            "time" to fromTime,
            "note" to note
        )

        val res = firestore.collection("breaks").add(breakObj)
        return Resource.Success(res)
    }

    override suspend fun deleteBreak(
        firebaseUser: FirebaseUser, toTime: Timestamp, fromTime: Timestamp, id: String): Resource<Task<DocumentReference>> {
        Log.e("id", id)
        var breakObj = hashMapOf(
            "userName" to firebaseUser.displayName,
            "userId" to firebaseUser.uid,
            "toTime" to toTime,
            "fromTime" to fromTime,
            "note" to ""
        )
        val delete =
            firestore.collection("breaks").document(id).delete().addOnCompleteListener {
                Log.d("Delete", "Worked")
            }.addOnFailureListener {
                Log.e("Delete", "Error ${it.message}")
            }

        val res = firestore.collection("breaks-completed").add(breakObj)
        return Resource.Success(res)
    }

}