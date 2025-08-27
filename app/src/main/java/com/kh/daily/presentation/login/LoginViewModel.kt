package com.kh.daily.presentation.login

import androidx.lifecycle.ViewModel
import com.kh.daily.data.local.LoginRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LoginViewModel (private val authRepository: LoginRepository ): ViewModel() {

    private val _isLoading =  MutableStateFlow(false)
    var _isPopupVisible = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    val isPopupVisible: StateFlow<Boolean> = _isPopupVisible

    fun login(email: String, password: String, onResult: (Boolean,String?) -> Unit){
        _isLoading.value = true

        authRepository.loginUser(email, password){ success, message ->

            _isLoading.value = success
            _isPopupVisible.value = success
            onResult(success,message)

        }
    }


    fun signUp(email: String, password: String, onResult: (Boolean,String?) -> Unit){
        _isLoading.value = true

        authRepository.signUpUser(email, password){ success, message ->

            _isLoading.value = success
            _isPopupVisible.value = success
            onResult(success,message)

        }
    }

    fun restPassWord(oobCode: String, newPassword:String ,onResult: (Boolean,String?) -> Unit) {
        _isLoading.value = true
        authRepository.resetPassword(oobCode, newPassword){success, message ->
            _isLoading.value = false
            onResult(success, message)
        }
    }

    fun passwordResetEmail(email: String ,onResult: (Boolean,String?) -> Unit) {
        _isLoading.value = true
        authRepository.sendPasswordResetEmail(email){success, message ->
            _isLoading.value = false
            onResult(success, message)
        }
    }

}