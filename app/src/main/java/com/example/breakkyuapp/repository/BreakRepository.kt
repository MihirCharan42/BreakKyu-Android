package com.example.breakkyuapp.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot


interface BreakRepository {

    fun getBreak() : Resource<Task<QuerySnapshot>>

    fun addBreak(firebaseUser: FirebaseUser, fromTime: Timestamp, note: String) : Resource<Task<DocumentReference>>

    suspend fun deleteBreak(firebaseUser: FirebaseUser, toTime: Timestamp, fromTime: Timestamp, id: String) : Resource<Task<DocumentReference>>
}