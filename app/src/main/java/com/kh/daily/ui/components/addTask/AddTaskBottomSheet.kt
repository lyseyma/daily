package com.kh.daily.ui.components.addTask

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kh.daily.R
import com.kh.daily.ui.components.popup.DatePickerDialog
import com.kh.daily.ui.theme.MDailyTheme

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskBottomSheet(
    title: String,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    selectedDate: String,
    onSelectedDateChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = colorResource(id = R.color.colorPrimary), // Coral/salmon background color from design
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color.White) },
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .padding(bottom = 32.dp), // Extra padding for bottom sheet
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.Start,

            ) {
            // Header with diamond icon and title
            Row(
                modifier = Modifier.padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Diamond icon
                Text(
                    text = "◇",
                    color = colorResource(id = R.color.colorPrimary),
                    fontSize = 16.sp,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "ADD TODO",
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.colorPrimary),
                    fontSize = 16.sp
                )
            }

            // Title input field
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                placeholder = {
                    Text(
                        "Title",
                        color = Color.White.copy(alpha = 0.7f)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White,
                    focusedPlaceholderColor = Color.White.copy(alpha = 0.7f),
                    unfocusedPlaceholderColor = Color.White.copy(alpha = 0.7f)
                ),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )

            // Description input field (larger)
            OutlinedTextField(
                value = description,
                onValueChange = onDescriptionChange,
                placeholder = {
                    Text(
                        "Description",
                        color = Color.White.copy(alpha = 0.7f)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White,
                    focusedPlaceholderColor = Color.White.copy(alpha = 0.7f),
                    unfocusedPlaceholderColor = Color.White.copy(alpha = 0.7f)
                ),
                shape = RoundedCornerShape(8.dp),
                maxLines = 6
            )

            // Deadline (Optional) field
            OutlinedTextField(
                value = selectedDate,
                onValueChange = { onSelectedDateChange },
                placeholder = {
                    Text(
                        "Deadline (Optional)",
                        color = Color.White.copy(alpha = 0.7f)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White,
                    focusedPlaceholderColor = Color.White.copy(alpha = 0.7f),
                    unfocusedPlaceholderColor = Color.White.copy(alpha = 0.7f)
                ),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                trailingIcon = {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = "Select Date",
                        tint = Color.White.copy(alpha = 0.7f),

                        modifier = Modifier
                            .clickable {
                                showBottomSheet = true
                            }
                    )
                },

                )

            // Add Image (Optional) field
            OutlinedTextField(
                value = "",
                onValueChange = { /* Handle image input */ },
                placeholder = {
                    Text(
                        "Add Image (Optional)",
                        color = Color.White.copy(alpha = 0.7f)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White,
                    focusedPlaceholderColor = Color.White.copy(alpha = 0.7f),
                    unfocusedPlaceholderColor = Color.White.copy(alpha = 0.7f)
                ),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                trailingIcon = {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add Image",
                        tint = Color.White.copy(alpha = 0.7f)
                    )
                }
            )

            // ADD TODO button
            Button(
                onClick = onConfirm,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = colorResource(id = R.color.colorPrimary)
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text(
                    "ADD TODO",
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = colorResource(R.color.colorPrimary)
                )
            }
        }
    }

    // DatePicker Dialog
    if (showBottomSheet) {
        DatePickerDialog(
            initialDate = selectedDate,
            onDateSelected = { date ->
                onSelectedDateChange(date)
                showBottomSheet = false
            },
            onDismiss = {
                showBottomSheet = false
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MDailyTheme {
    }
}