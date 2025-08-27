package com.kh.daily.utils.login

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.kh.daily.R

@Composable
fun InputPasswordTextField(
    value : String,
    onValueChange: (String) -> Unit,
    label :  @Composable (() -> Unit)? = null,
    modifier : Modifier = Modifier,
    placeholder: @Composable (() -> Unit)? = null,
    isDisable: Boolean
    ){
    var passwordVisible by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        placeholder = placeholder,
        modifier = modifier,
        singleLine = true,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val icon = if (passwordVisible)  Icons.Filled.Visibility else Icons.Filled.VisibilityOff
            val description = if (passwordVisible) "Hide password" else "Show password"
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(imageVector = icon, contentDescription = description)
            }
        },
        colors = TextFieldDefaults.colors(
            disabledTextColor = if (isDisable) Color.Gray else colorResource(id = R.color.colorPrimary),
            cursorColor = colorResource(id = R.color.colorPrimary),
            errorCursorColor = Color.Red,
            errorLeadingIconColor = Color.Red,
            errorTrailingIconColor = Color.Red,
            focusedLabelColor = colorResource(id = R.color.colorPrimary),
            unfocusedLabelColor = Color.Gray,
            unfocusedIndicatorColor = Color.Gray,
            focusedIndicatorColor = colorResource(id = R.color.colorPrimary),
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White,
            focusedPlaceholderColor = Color.Gray,
            focusedTextColor = Color.Gray,
            unfocusedTextColor = Color.Gray
        ),
        shape = RoundedCornerShape(12.dp)
    )

}