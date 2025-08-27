package com.kh.daily.data.local

import android.util.Log
import com.google.firebase.auth.FirebaseAuth


class LoginRepository {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun loginUser(email : String , password : String , onResult: (Boolean, String?) -> Unit){

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                    val user = firebaseAuth.currentUser
                    Log.d("SignIn", "Sign in successful: ${user?.email}")
                } else {
                    onResult(false, task.exception?.message)
                    Log.e("SignIn", "Sign in failed: ${task.exception?.message}")

                }
            }

    }

    fun signUpUser(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    fun resetPassword(oobCode: String, newPassword: String, onResult: (Boolean,String) -> Unit) {
        firebaseAuth.verifyPasswordResetCode(oobCode)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true,"Password reset successful!")
                } else {
                    onResult(false,task.exception?.message ?: "Error resetting password")
                }
            }

    }

    fun sendPasswordResetEmail(email: String, onResult: (Boolean,String) -> Unit) {
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true,"Email reset successful!")
                } else {
                    task.exception?.message?.let { onResult(false, it) }
                }
            }
    }

    fun sendResetPasswordVaiEmail() {

    }


}