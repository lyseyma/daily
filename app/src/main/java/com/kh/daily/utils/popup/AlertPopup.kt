package com.kh.daily.utils.popup

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoNotDisturbAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.kh.daily.R
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun AlertPopup(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    confirmText: String = "Confirm",
    dismissText: String = "Cancel"
    ) {
    Dialog(
        onDismissRequest = { onDismiss() }
    ){
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            tonalElevation = 8.dp,
            modifier = Modifier
                .padding(16.dp)

        ) {
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {


                Icon(
                    painter = rememberVectorPainter(Icons.Filled.DoNotDisturbAlt),
                    contentDescription = "Logo Icon",
                    tint = colorResource(id = R.color.colorPrimary),
                    modifier = Modifier
                        .size(30.dp)
                        .align(Alignment.CenterHorizontally)

                )
//
                Spacer(modifier = Modifier.height(10.dp))

                // Message
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(10.dp))
                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            text = dismissText,
                            color = colorResource(id = R.color.colorPrimary)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    TextButton(onClick = onConfirm) {
                        Text(
                            text = confirmText,
                            color = colorResource(id = R.color.colorPrimary)
                        )
                    }
                }

            }
        }

    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ShowPopup(
    isShowDialog: State<Boolean>,
    showPopup: MutableStateFlow<Boolean>,
    message: String
    ){
    if (isShowDialog.value) {
        AlertPopup(
            title = "M D",
            message = message,
            onDismiss = { showPopup.value = false },
            onConfirm = {
                showPopup.value = false
                // Handle confirmation action
            }
        )
    }
}

