package com.example.quickcartadmin.viewmodel

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.example.quickcartadmin.utils.Utils
import com.example.quickcartadmin.models.Admin
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.concurrent.TimeUnit

@Suppress("NAME_SHADOWING")
class AuthViewModel : ViewModel() {

    private val _verificationId = MutableStateFlow<String?>(null)

    private val _otpSent = MutableStateFlow(false)
    val otpSent = _otpSent

    private val _issignedSuccessfully = MutableStateFlow(false)
    val issignedSuccessfully = _issignedSuccessfully

    private val _isACurrentUser = MutableStateFlow(false)
    val isACurrentUser = _isACurrentUser

    init {
        Utils.getAUthInstance().currentUser?.let {
            _isACurrentUser.value = true
        }
    }

    fun sendOTP(userNUmber: String, activity: Activity) {

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {

            }

            override fun onVerificationFailed(e: FirebaseException) {

            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken,
            ) {

                _verificationId.value = verificationId
                _otpSent.value = true

            }
        }
        val options = PhoneAuthOptions.newBuilder(Utils.getAUthInstance())
            .setPhoneNumber("+91$userNUmber") // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(activity) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

    }

    fun signInWithPhoneAuthCredential(otp: String, userNumber: String, admin: Admin) {
        val credential = PhoneAuthProvider.getCredential(_verificationId.value.toString(), otp)
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task->
            val token =task.result
            admin.adminToken= token
            Utils.getAUthInstance().signInWithCredential(credential).addOnCompleteListener { task ->
                admin.uid = Utils.getCurrentUserId()
                if (task.isSuccessful) {
                    FirebaseDatabase.getInstance().getReference("Admins").child("AdminInfo")
                        .child(admin.uid!!).setValue(admin)
                    _issignedSuccessfully.value = true

                }
            }
        }



    }


}