package com.example.breakkyuapp.repository

import com.example.breakkyuapp.utils.await
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import java.lang.Exception
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {
    override val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    override suspend fun login(email: String, password: String): Resource<FirebaseUser> {
        return try {
            val res = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Resource.Success(res.user!!)
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }

    override suspend fun signup(
        name: String,
        email: String,
        password: String
    ): Resource<FirebaseUser> {
        return try {
            val res = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            res?.user?.updateProfile(
                UserProfileChangeRequest.Builder().setDisplayName(name).build()
            )
            Resource.Success(res.user!!)
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }

    override fun logout() {
        firebaseAuth.signOut()
    }

    override fun isLoggedIn(): FirebaseUser {
        return currentUser!!
    }
}