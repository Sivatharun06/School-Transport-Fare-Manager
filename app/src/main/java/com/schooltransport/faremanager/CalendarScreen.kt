
package com.schooltransport.faremanager

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun CalendarScreen(store: TransportStore) {

    val today = remember { Calendar.getInstance() }

    var displayedMonth by remember {
        mutableIntStateOf(today.get(Calendar.MONTH))
    }

    var displayedYear by remember {
        mutableIntStateOf(today.get(Calendar.YEAR))
    }

    var selectedDate by remember {
        mutableStateOf(formatDate(today))
    }

    LaunchedEffect(Unit) {
        store.loadAll()
    }

    val monthCalendar = remember(displayedMonth, displayedYear) {
        Calendar.getInstance().apply {
            set(Calendar.YEAR, displayedYear)
            set(Calendar.MONTH, displayedMonth)
            set(Calendar.DAY_OF_MONTH, 1)
        }
    }

    val monthName = remember(displayedMonth, displayedYear) {
        SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)
            .format(monthCalendar.time)
    }

    val firstDayOffset =
        (monthCalendar.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY + 7) % 7

    val daysInMonth =
        monthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)

    val calendarCells = buildList<Int?> {
        repeat(firstDayOffset) {
            add(null)
        }

        for (day in 1..daysInMonth) {
            add(day)
        }

        while (size % 7 != 0) {
            add(null)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            "Calendar & Attendance",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(16.dp))

        // CALENDAR

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp)
        ) {

            Column(
                modifier = Modifier.padding(12.dp)
            ) {

                // MONTH NAVIGATION

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    TextButton(
                        onClick = {

                            val c = Calendar.getInstance().apply {
                                set(displayedYear, displayedMonth, 1)
                                add(Calendar.MONTH, -1)
                            }

                            displayedMonth = c.get(Calendar.MONTH)
                            displayedYear = c.get(Calendar.YEAR)

                            selectedDate = formatDate(c)
                        }
                    ) {
                        Text("‹ Previous")
                    }

                    Text(
                        monthName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    TextButton(
                        onClick = {

                            val c = Calendar.getInstance().apply {
                                set(displayedYear, displayedMonth, 1)
                                add(Calendar.MONTH, 1)
                            }

                            displayedMonth = c.get(Calendar.MONTH)
                            displayedYear = c.get(Calendar.YEAR)

                            selectedDate = formatDate(c)
                        }
                    ) {
                        Text("Next ›")
                    }
                }

                Spacer(Modifier.height(12.dp))

                // WEEK DAYS

                val weekDays = listOf(
                    "Sun", "Mon", "Tue", "Wed",
                    "Thu", "Fri", "Sat"
                )

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    weekDays.forEach { day ->

                        Text(
                            day,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                // CALENDAR DATES

                calendarCells.chunked(7).forEach { week ->

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {

                        week.forEach { day ->

                            if (day == null) {

                                Box(
                                    Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                )

                            } else {

                                val dateCalendar =
                                    Calendar.getInstance().apply {
                                        set(
                                            displayedYear,
                                            displayedMonth,
                                            day
                                        )
                                    }

                                val dateKey = formatDate(dateCalendar)

                                val isSelected =
                                    dateKey == selectedDate

                                val isToday =
                                    dateKey == formatDate(today)

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                        .background(
                                            color = if (isSelected) {
                                                MaterialTheme.colorScheme.primary
                                            } else {
                                                MaterialTheme.colorScheme.surfaceVariant
                                            },
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .clickable {
                                            selectedDate = dateKey
                                        },
                                    contentAlignment = Alignment.Center
                                ) {

                                    Text(
                                        day.toString(),
                                        color = if (isSelected) {
                                            MaterialTheme.colorScheme.onPrimary
                                        } else {
                                            MaterialTheme.colorScheme.onSurface
                                        },
                                        fontWeight = if (isToday || isSelected) {
                                            FontWeight.Bold
                                        } else {
                                            FontWeight.Normal
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(5.dp))
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ATTENDANCE

        Text(
            "Attendance – $selectedDate",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(8.dp))

        if (store.isLoading && store.children.isEmpty()) {

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

        } else if (store.children.isEmpty()) {

            Text("Add children first to mark attendance.")

        } else {

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {

                items(
                    items = store.children,
                    key = { it.id }
                ) { child ->

                    val existingTrip = store.trips.firstOrNull {
                        it.childId == child.id &&
                            it.date == selectedDate
                    }

                    var saving by remember(child.id, selectedDate) {
                        mutableStateOf(false)
                    }

                    // RESET BUTTON STATE AFTER SAVE OR ERROR

                    LaunchedEffect(
                        store.trips,
                        store.errorMessage,
                        child.id,
                        selectedDate
                    ) {
                        saving = false
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    ) {

                        Column(
                            modifier = Modifier.padding(14.dp)
                        ) {

                            Text(
                                child.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            if (
                                child.fromLocation.isNotBlank() ||
                                child.toLocation.isNotBlank()
                            ) {

                                Text(
                                    "${child.fromLocation} → ${child.toLocation}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            Spacer(Modifier.height(10.dp))

                            // MORNING AND RETURN BUTTONS

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {

                                AttendanceButton(
                                    label = "Morning",
                                    fare = existingTrip?.morningFare
                                        ?: child.morningFare,
                                    checked = existingTrip?.morningDone == true,
                                    enabled = !saving,
                                    modifier = Modifier.weight(1f)
                                ) {

                                    val newMorning =
                                        existingTrip?.morningDone != true

                                    val newReturn =
                                        existingTrip?.returnDone ?: false

                                    saveAttendance(
                                        store = store,
                                        child = child,
                                        existingTrip = existingTrip,
                                        date = selectedDate,
                                        morningDone = newMorning,
                                        returnDone = newReturn,
                                        onSaving = {
                                            saving = it
                                        }
                                    )
                                }

                                AttendanceButton(
                                    label = "Return",
                                    fare = existingTrip?.returnFare
                                        ?: child.returnFare,
                                    checked = existingTrip?.returnDone == true,
                                    enabled = !saving,
                                    modifier = Modifier.weight(1f)
                                ) {

                                    val newMorning =
                                        existingTrip?.morningDone ?: false

                                    val newReturn =
                                        existingTrip?.returnDone != true

                                    saveAttendance(
                                        store = store,
                                        child = child,
                                        existingTrip = existingTrip,
                                        date = selectedDate,
                                        morningDone = newMorning,
                                        returnDone = newReturn,
                                        onSaving = {
                                            saving = it
                                        }
                                    )
                                }
                            }

                            Spacer(Modifier.height(8.dp))

                            Text(
                                "Daily fare: Rs. ${
                                    String.format(
                                        Locale.US,
                                        "%.2f",
                                        existingTrip?.totalFare ?: 0.0
                                    )
                                }",
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        store.errorMessage?.let {

            Text(
                it,
                color = MaterialTheme.colorScheme.error
            )
        }

        store.successMessage?.let {

            Text(
                it,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// SAVE ATTENDANCE

private fun saveAttendance(
    store: TransportStore,
    child: TransportChild,
    existingTrip: TransportTrip?,
    date: String,
    morningDone: Boolean,
    returnDone: Boolean,
    onSaving: (Boolean) -> Unit
) {

    onSaving(true)

    // DELETE WHEN BOTH TRIPS ARE UNCHECKED

    if (!morningDone && !returnDone) {

        if (existingTrip != null) {
            store.deleteTrip(existingTrip.id)
        } else {
            onSaving(false)
        }

        return
    }

    // PRESERVE HISTORICAL FARES

    val trip = TransportTrip(

        id = existingTrip?.id ?: "",

        childId = child.id,

        childName = child.name,

        date = date,

        time = existingTrip?.time ?: "",

        fromLocation = existingTrip?.fromLocation
            ?: child.fromLocation,

        toLocation = existingTrip?.toLocation
            ?: child.toLocation,

        morningDone = morningDone,

        returnDone = returnDone,

        morningFare = existingTrip?.morningFare
            ?: child.morningFare,

        returnFare = existingTrip?.returnFare
            ?: child.returnFare,

        note = existingTrip?.note ?: "",

        createdAt = existingTrip?.createdAt
            ?: System.currentTimeMillis()
    )

    store.saveTrip(trip)
}

// ATTENDANCE BUTTON

@Composable
private fun AttendanceButton(
    label: String,
    fare: Double,
    checked: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (checked) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.secondaryContainer
            },
            contentColor = if (checked) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSecondaryContainer
            }
        ),
        shape = RoundedCornerShape(12.dp)
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                if (checked) "✓ $label" else label
            )

            Text(
                "Rs. ${fare.toInt()}",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

// DATE FORMAT

private fun formatDate(calendar: Calendar): String {

    return SimpleDateFormat(
        "yyyy-MM-dd",
        Locale.US
    ).format(calendar.time)
}
