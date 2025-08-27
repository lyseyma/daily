package com.kh.daily.presentation.login

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.kh.daily.R.*
import com.kh.daily.utils.login.InputPasswordTextField
import com.kh.daily.utils.popup.ShowPopup
import com.kh.daily.utils.login.InputTextField

@SuppressLint("ResourceAsColor")
@Composable
fun LoginScreen (navController: NavHostController,
                 viewModel: LoginViewModel,
                 screenType:String,
                 intent: Intent
    ){
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var confirmPass by remember { mutableStateOf("") }
    var gmailCode by remember { mutableStateOf("") }
    val isError = email.isEmpty() && password.isEmpty()
    val context = LocalContext.current
    val isLoading = viewModel.isLoading.collectAsState()
    val isPopupVisible = viewModel.isPopupVisible.collectAsState()
    var clearEmail by remember { mutableStateOf(false) }
    val isClearEmail: Boolean = clearEmail
    val signUpScreen = "signup_screen" == screenType
    val loginScreen = "login_screen" == screenType
    val forgotScreen = "forgot_screen" == screenType
    var messageError by remember { mutableStateOf("") }
    val isConfirmPass = password.chars() == confirmPass.chars()
    val activity = context as? ComponentActivity
    val oobCode = intent.data?.getQueryParameter("oobCode")
    val isOobCode = forgotScreen && oobCode != null



    val errorBorderColor by animateColorAsState(
        targetValue = colorResource(id = color.colorPrimary),
        label = ""
    )

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    painter = painterResource(id = drawable.ic_check_foreground), // Replace with your icon
                    contentDescription = "Logo Icon",
                    tint = colorResource(id = color.colorPrimary),
                    modifier = Modifier
                        .size(200.dp)
                        .padding(top = 80.dp)
                )
            }

            // Input Fields
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 50.dp, top = 80.dp)
            ) {
                if (loginScreen || signUpScreen){
                    InputTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            clearEmail = it.isEmpty()
                        },
                        label = { Text("Email") },
                        placeholder = { Text("Enter your email") },
                        isClearEmail = isClearEmail,
                        trailingIcon = {
                            IconButton(onClick = {
                                email = "" // Clear the text
                                clearEmail = true
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Clear,
                                    contentDescription = "Clear text"
                                )
                            }
                        },
                        errorBorderColor = errorBorderColor
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (signUpScreen){
                        InputTextField(
                            value = fullName,
                            onValueChange = {
                                fullName = it
                                clearEmail = it.isEmpty()
                            },
                            label = { Text("Full Name") },
                            placeholder = { Text("Enter your full name") },
                            isClearEmail = isClearEmail,
                            trailingIcon = {
                                IconButton(onClick = {
                                    fullName = "" // Clear the text
                                    clearEmail = true
                                }) {
                                    Icon(
                                        imageVector = Icons.Filled.Clear,
                                        contentDescription = "Clear text"
                                    )
                                }
                            },
                            errorBorderColor = errorBorderColor
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                }

//                if (forgotScreen){
//                    InputTextField(
//                        value = gmailCode,
//                        onValueChange = {
//                            gmailCode = it
//                        },
//                        label = { Text("G-mail code") },
//                        placeholder = { Text("Enter your G-mail code") },
//                        isClearEmail = isClearEmail,
//                        errorBorderColor = errorBorderColor
//                    )
//
//                    Spacer(modifier = Modifier.height(16.dp))
//                }
                InputPasswordTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(if(loginScreen) "Password" else "New password") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(if (!isOobCode) 1f else 0.5f),
                    placeholder = { Text(if (loginScreen) "Enter your password" else "Enter your new password") },
                    isDisable = !isOobCode
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (signUpScreen || forgotScreen){
                    InputPasswordTextField(
                        value = confirmPass,
                        onValueChange = { confirmPass = it },
                        label = { Text("Confirm Password") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(if (!isOobCode) 1f else 0.5f),
                        placeholder = { Text("Enter your Confirm password") },
                        isDisable = !isOobCode
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }


                if (loginScreen){
                    Text(
                        text = "Forgot Password?",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier
                            .align(Alignment.End)
                            .clickable {
                                if (email.isEmpty()) {
                                    viewModel._isPopupVisible.value = true
                                    messageError =
                                        "Please input email before reset password 🥰🥰🥰🥰🥰"
                                } else {
                                    viewModel.passwordResetEmail(email) { success, message ->
                                        if (success) {
                                            viewModel._isPopupVisible.value = false
                                            Toast
                                                .makeText(
                                                    context,
                                                    "😍 Password reset Successful!",
                                                    Toast.LENGTH_SHORT
                                                )
                                                .show()
                                            messageError = " 🥰🥰🥰🥰🥰 oobCode$oobCode"
                                            navController.navigate("forgot_screen")
                                        } else {
                                            viewModel._isPopupVisible.value = true
                                            Toast
                                                .makeText(
                                                    context,
                                                    "😥 Password reset Failed",
                                                    Toast.LENGTH_SHORT
                                                )
                                                .show()
                                            messageError = message ?: "Invalid email😛"
                                        }
                                    }
                                }


                            },
                        textDecoration = TextDecoration.Underline
                    )
                }

            }

            // Sign In Button and Footer
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Button(
                    onClick = {
                        /* TODO: Implement Login Logic */
//                        if  (isError)  return@Button
                        when(screenType){
                            "login_screen" -> {
                                if (isError){
                                    viewModel._isPopupVisible.value = true
                                    messageError = "Input email or password"
                                }
                               else viewModel.login(email, password) { success, message ->
                                    if (success) {
                                        viewModel._isPopupVisible.value = false
                                        Toast.makeText(context, "😍 Login Successful!", Toast.LENGTH_SHORT).show()
                                        navController.navigate("taskList_screen"){
                                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                        }
                                    } else {
                                        viewModel._isPopupVisible.value = true
                                        Toast.makeText(context,  "😥 Login Failed", Toast.LENGTH_SHORT).show()
                                        messageError = message ?: "Invalid email or password 😛"
                                    }

                                }
                            }
                            "signup_screen" -> {
                                if (isConfirmPass){
                                    viewModel._isPopupVisible.value = true
                                    messageError = "Password is not match 😥😥😪"
                                }else{
                                    viewModel.signUp(email, password) { success, message ->

                                        if (success) {
                                            viewModel._isPopupVisible.value = false
                                            Toast.makeText(context, "😍 SignUp Successful!", Toast.LENGTH_SHORT).show()
                                        } else {
                                            viewModel._isPopupVisible.value = true
                                            Toast.makeText(context, "😥 SignUp Failed$message", Toast.LENGTH_SHORT).show()

                                        }

                                    }
                                }

                            }
                            "forgot_screen" -> {

                                Log.d("TAG", "LoginScreen oobCode : $oobCode " )
                                if (password == confirmPass && oobCode != null){

                                    viewModel.restPassWord(oobCode, confirmPass){ success, message ->
                                        if (success) {
                                            viewModel._isPopupVisible.value = false
                                            Toast.makeText(context, "😍 Password reset Successful!", Toast.LENGTH_SHORT).show()
                                            navController.navigate("login_screen")
                                        } else {
                                            viewModel._isPopupVisible.value = true
                                            Toast.makeText(context, "😥 SignUp Failed$message", Toast.LENGTH_SHORT).show()
                                            messageError ="😓😓  Password reset Failed$message"
                                        }
                                    }

                                }else{
                                    viewModel._isPopupVisible.value = true
                                    messageError = "Passwords do not match or oobCode is missing!"
                                }

                            }
                            else -> Toast.makeText(context,  "No data q(≧▽≦q) ", Toast.LENGTH_SHORT).show()
                        }

                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                                            colorResource(id = color.colorPrimary),
                                            colorResource(id = color.colorPrimary),
                                            Color.Gray,
                                            Color.Gray),
                    enabled = !isOobCode
                ) {
                    Text(
                        text = when(screenType){
                            "login_screen" -> "SIGN IN"
                            "signup_screen" -> "SIGN UP"
                            "forgot_screen" -> "CHANGE PASSWORD"
                            else -> "SIGN IN"
                        },
                        fontSize = 16.sp,
                        color = Color.White)
                }


                Spacer(modifier = Modifier.height(16.dp))
                if (signUpScreen || loginScreen){
                    Row {
                        Text(text = if (loginScreen) "Don't have an account?" else "Have an account?",
                            color = Color.Gray,
                            fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text =if (loginScreen) "Sign up" else "Log in",
                            fontSize = 14.sp,
                            color = colorResource(id = color.colorPrimary),
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.clickable { /* TODO: Navigate to Sign Up */
                                navController.navigate("signup_screen")
                            }
                        )
                    }
                }

            }

            ShowPopup(
                isShowDialog = isPopupVisible,
                showPopup = viewModel._isPopupVisible,
                message = messageError
            )

        }

    }
}




