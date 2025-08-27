package com.kh.daily.utils.login

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.kh.daily.R

@Composable
fun InputTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label:  @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    isClearEmail: Boolean,
    visualTransformation:VisualTransformation = VisualTransformation.None,
    trailingIcon:  @Composable (() -> Unit)? = null,
    errorBorderColor: Color
    ) {

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = label,
            placeholder = placeholder,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            visualTransformation = if (isClearEmail) VisualTransformation.None else VisualTransformation.None,
            trailingIcon = trailingIcon,
            colors = TextFieldDefaults.colors(
                disabledTextColor = colorResource(id = R.color.colorPrimary),
                cursorColor = colorResource(id = R.color.colorPrimary),
                errorCursorColor = Color.Red,
                errorLeadingIconColor = Color.Red,
                errorTrailingIconColor = Color.Red,
                focusedLabelColor = colorResource(id = R.color.colorPrimary),
                unfocusedLabelColor = Color.Gray,
                unfocusedIndicatorColor = errorBorderColor,
                focusedIndicatorColor = errorBorderColor,
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                focusedPlaceholderColor = Color.Gray,
                focusedTextColor = Color.Gray,
                unfocusedTextColor = Color.Gray
            ),
            shape = RoundedCornerShape(12.dp)
        )

}
