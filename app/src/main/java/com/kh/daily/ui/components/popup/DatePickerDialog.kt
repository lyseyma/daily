package com.kh.daily.ui.components.popup

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kh.daily.R
import com.kh.daily.ui.components.addTask.AddTaskBottomSheet
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * Shows a Material3 DatePicker in a Dialog.
 *
 * @param initialDate: Default date string in format "d MMM yyyy", or null.
 * @param onDateSelected: Called with selected date string ("d MMM yyyy").
 * @param onDismiss: Called when dialog is dismissed.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DatePickerDialog(
    initialDate: String?,
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    // Convert initialDate to epoch millis, fallback to today
    // DatePicker expects UTC midnight for the date
    val formatter = DateTimeFormatter.ofPattern("d MMM yyyy", java.util.Locale.ENGLISH)
    val today = LocalDate.now()
    val initialLocalDate = try {
        initialDate?.let { LocalDate.parse(it, formatter) } ?: today
    } catch (e: Exception) {
        today
    }

    // Convert to UTC midnight epoch millis (DatePicker expects UTC)
    val initialMillis = initialLocalDate
        .atStartOfDay(ZoneOffset.UTC)
        .toInstant()
        .toEpochMilli()

    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(dismissOnClickOutside = true)
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = colorResource(id = R.color.colorPrimary)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Material3 DatePicker
                val datePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = initialMillis
                )
                DatePicker(
                    state = datePickerState,
                    colors = DatePickerDefaults.colors(
                        containerColor = colorResource(id = R.color.colorPrimary),
                        titleContentColor = Color.White,
                        headlineContentColor = Color.White,
                        dayContentColor = Color.White,
                        selectedDayContainerColor = Color.White,
                        selectedDayContentColor = colorResource(id = R.color.colorPrimary),
                        todayContentColor = Color.White,
                        todayDateBorderColor = Color.White,
                        weekdayContentColor = Color.White,
                        subheadContentColor = Color.White,
                        navigationContentColor = Color.White,
                        yearContentColor = Color.White,
                        selectedYearContentColor = colorResource(id = R.color.colorPrimary),
                        selectedYearContainerColor = Color.White,
                        disabledSelectedYearContentColor = Color.White,
                        currentYearContentColor = Color.White,
                        dividerColor = Color.White,

                        )
                )

                Spacer(Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(
                        onClick = { onDismiss() },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color.White
                        )
                    ) {
                        Text("Cancel")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        enabled = datePickerState.selectedDateMillis != null,
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                // Convert from UTC millis back to LocalDate
                                val date = Instant.ofEpochMilli(millis)
                                    .atZone(ZoneOffset.UTC)
                                    .toLocalDate()
                                val formattedDate = date.format(formatter)
                                onDateSelected(formattedDate)
                            } ?: onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = colorResource(id = R.color.colorPrimary)
                        )
                    ) {
                        Text("OK")
                    }
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DatePickerDialog(
        initialDate = "Date",
        onDateSelected = {},
        onDismiss = {}

    )
}