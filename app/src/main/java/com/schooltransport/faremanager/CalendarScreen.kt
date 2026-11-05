
package com.schooltransport.faremanager

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private data class CalendarDateItem(
    val date: String,
    val dateLabel: String,
    val dayLabel: String
)

@Composable
fun CalendarScreen(store: TransportStore) {

    val today = remember { Calendar.getInstance() }

    var displayedMonth by remember {
        mutableIntStateOf(today.get(Calendar.MONTH))
    }

    var displayedYear by remember {
        mutableIntStateOf(today.get(Calendar.YEAR))
    }

    var childMenuExpanded by remember {
        mutableStateOf(false)
    }

    val pendingDates = remember {
        mutableStateListOf<String>()
    }

    LaunchedEffect(Unit) {
        store.loadAll()
    }

    // SELECTED CHILD

    LaunchedEffect(
        store.children,
        store.selectedCalendarChildId
    ) {
        if (
            store.children.isNotEmpty() &&
            store.selectedCalendarChildId.isBlank()
        ) {
            store.selectCalendarChild(
                store.children.first().id
            )
        }
    }

    val selectedChild = store.children.firstOrNull {
        it.id == store.selectedCalendarChildId
    } ?: store.children.firstOrNull()

    // MONTH INFORMATION

    val monthCalendar = remember(
        displayedMonth,
        displayedYear
    ) {
        Calendar.getInstance().apply {
            set(Calendar.YEAR, displayedYear)
            set(Calendar.MONTH, displayedMonth)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }

    val monthKey = remember(
        displayedMonth,
        displayedYear
    ) {
        String.format(
            Locale.US,
            "%04d-%02d",
            displayedYear,
            displayedMonth + 1
        )
    }

    val monthName = remember(
        displayedMonth,
        displayedYear
    ) {
        SimpleDateFormat(
            "MMMM yyyy",
            Locale.getDefault()
        ).format(monthCalendar.time)
    }

    // GENERATE ALL DATES

    val dateItems = remember(
        displayedMonth,
        displayedYear
    ) {

        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, displayedYear)
            set(Calendar.MONTH, displayedMonth)
            set(Calendar.DAY_OF_MONTH, 1)
        }

        val totalDays =
            calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        val dateFormat = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.US
        )

        val labelFormat = SimpleDateFormat(
            "d MMM",
            Locale.getDefault()
        )

        val dayFormat = SimpleDateFormat(
            "EEEE",
            Locale.getDefault()
        )

        (1..totalDays).map { day ->

            calendar.set(Calendar.DAY_OF_MONTH, day)

            CalendarDateItem(
                date = dateFormat.format(calendar.time),
                dateLabel = labelFormat.format(calendar.time),
                dayLabel = dayFormat.format(calendar.time)
            )
        }
    }

    val childTrips = remember(
        store.trips,
        selectedChild?.id,
        monthKey
    ) {
        selectedChild?.let {
            store.tripsForChild(it.id, monthKey)
        } ?: emptyList()
    }

    val monthlySummary = remember(
        store.trips,
        store.payments,
        selectedChild?.id,
        monthKey
    ) {
        selectedChild?.let {
            store.childMonthlySummary(it.id, monthKey)
        } ?: MonthlySummary()
    }

    val morningCount = childTrips.count {
        it.morningDone
    }

    val returnCount = childTrips.count {
        it.returnDone
    }

    // CLEAR PENDING ROWS AFTER STORE UPDATE

    LaunchedEffect(
        store.trips,
        store.errorMessage,
        store.successMessage
    ) {
        pendingDates.clear()
    }

    // MONTH NAVIGATION

    fun changeMonth(direction: Int) {

        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, displayedYear)
            set(Calendar.MONTH, displayedMonth)
            set(Calendar.DAY_OF_MONTH, 1)
            add(Calendar.MONTH, direction)
        }

        displayedMonth = calendar.get(Calendar.MONTH)
        displayedYear = calendar.get(Calendar.YEAR)
    }

    // ATTENDANCE TOGGLE

    fun toggleAttendance(
        date: String,
        isMorning: Boolean
    ) {

        val child = selectedChild ?: return

        val pendingKey = "${child.id}_$date"

        if (pendingKey in pendingDates) return

        val existingTrip = store.trips.firstOrNull {
            it.childId == child.id && it.date == date
        }

        val newMorningDone = if (isMorning) {
            !(existingTrip?.morningDone ?: false)
        } else {
            existingTrip?.morningDone ?: false
        }

        val newReturnDone = if (!isMorning) {
            !(existingTrip?.returnDone ?: false)
        } else {
            existingTrip?.returnDone ?: false
        }

        pendingDates.add(pendingKey)

        if (!newMorningDone && !newReturnDone) {

            if (existingTrip != null) {
                store.deleteTrip(existingTrip.id)
            } else {
                pendingDates.remove(pendingKey)
            }

            return
        }

        val updatedTrip = TransportTrip(
            id = existingTrip?.id ?: "",
            childId = child.id,
            childName = child.name,
            date = date,
            time = existingTrip?.time ?: "",
            fromLocation = existingTrip?.fromLocation
                ?: child.fromLocation,
            toLocation = existingTrip?.toLocation
                ?: child.toLocation,
            morningDone = newMorningDone,
            returnDone = newReturnDone,
            morningFare = existingTrip?.morningFare
                ?: child.morningFare,
            returnFare = existingTrip?.returnFare
                ?: child.returnFare,
            note = existingTrip?.note ?: "",
            createdAt = existingTrip?.createdAt
                ?: System.currentTimeMillis()
        )

        store.saveTrip(updatedTrip)
    }

    // SCREEN

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {

        Spacer(modifier = Modifier.height(12.dp))

        // MONTH SELECTOR

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 3.dp
            )
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                IconButton(
                    onClick = { changeMonth(-1) }
                ) {
                    Icon(
                        Icons.Default.ChevronLeft,
                        contentDescription = "Previous month"
                    )
                }

                Text(
                    text = monthName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                IconButton(
                    onClick = { changeMonth(1) }
                ) {
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = "Next month"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // CHILD SELECTOR

        Box {

            OutlinedButton(
                onClick = {
                    childMenuExpanded = true
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {

                Text(
                    text = selectedChild?.name
                        ?: "Select Children",
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = "Select child"
                )
            }

            DropdownMenu(
                expanded = childMenuExpanded,
                onDismissRequest = {
                    childMenuExpanded = false
                },
                modifier = Modifier.fillMaxWidth(0.85f)
            ) {

                store.children.forEach { child ->

                    DropdownMenuItem(
                        text = {
                            Text(
                                child.name,
                                fontWeight = if (
                                    child.id == selectedChild?.id
                                ) {
                                    FontWeight.Bold
                                } else {
                                    FontWeight.Normal
                                }
                            )
                        },
                        onClick = {
                            store.selectCalendarChild(child.id)
                            childMenuExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ATTENDANCE LIST

        if (selectedChild == null) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Please add a child first",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

        } else {

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                items(
                    items = dateItems,
                    key = { it.date }
                ) { item ->

                    val trip = childTrips.firstOrNull {
                        it.date == item.date
                    }

                    val morningDone =
                        trip?.morningDone ?: false

                    val returnDone =
                        trip?.returnDone ?: false

                    val dailyTotal =
                        (if (morningDone) {
                            trip?.morningFare ?: selectedChild.morningFare
                        } else {
                            0.0
                        }) +
                        (if (returnDone) {
                            trip?.returnFare ?: selectedChild.returnFare
                        } else {
                            0.0
                        })

                    val pendingKey =
                        "${selectedChild.id}_${item.date}"

                    val rowPending =
                        pendingKey in pendingDates ||
                            store.isLoading

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor =
                                MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 1.dp
                        )
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 8.dp,
                                    vertical = 10.dp
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement =
                                Arrangement.spacedBy(5.dp)
                        ) {

                            // DATE AND DAY

                            Column(
                                modifier = Modifier.weight(1.05f)
                            ) {

                                Text(
                                    text = item.dateLabel,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Text(
                                    text = item.dayLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // MORNING BUTTON

                            Button(
                                onClick = {
                                    toggleAttendance(
                                        item.date,
                                        true
                                    )
                                },
                                enabled = !rowPending,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp),
                                contentPadding = PaddingValues(
                                    horizontal = 2.dp,
                                    vertical = 4.dp
                                ),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (morningDone) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant
                                    },
                                    contentColor = if (morningDone) {
                                        MaterialTheme.colorScheme.onPrimary
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            ) {

                                Column(
                                    horizontalAlignment =
                                        Alignment.CenterHorizontally
                                ) {

                                    Text(
                                        "Morning",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Text(
                                        "Rs. ${"%.0f".format(Locale.US, trip?.morningFare ?: selectedChild.morningFare)}",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }

                            // RETURN BUTTON

                            Button(
                                onClick = {
                                    toggleAttendance(
                                        item.date,
                                        false
                                    )
                                },
                                enabled = !rowPending,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp),
                                contentPadding = PaddingValues(
                                    horizontal = 2.dp,
                                    vertical = 4.dp
                                ),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (returnDone) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant
                                    },
                                    contentColor = if (returnDone) {
                                        MaterialTheme.colorScheme.onPrimary
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            ) {

                                Column(
                                    horizontalAlignment =
                                        Alignment.CenterHorizontally
                                ) {

                                    Text(
                                        "Return",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Text(
                                        "Rs. ${"%.0f".format(Locale.US, trip?.returnFare ?: selectedChild.returnFare)}",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }

                            // DAILY TOTAL

                            Column(
                                modifier = Modifier.weight(0.9f),
                                horizontalAlignment = Alignment.End
                            ) {

                                Text(
                                    "Total",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Text(
                                    text = "Rs. ${"%.0f".format(Locale.US, dailyTotal)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // MONTHLY SUMMARY

                item {

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Monthly Summary",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        SummaryCard(
                            title = "Total",
                            amount = monthlySummary.totalFare,
                            modifier = Modifier.weight(1f)
                        )

                        SummaryCard(
                            title = "Paid",
                            amount = monthlySummary.paidAmount,
                            modifier = Modifier.weight(1f)
                        )

                        SummaryCard(
                            title = "Balance",
                            amount = monthlySummary.balance,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Monthly Trips",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        TripCountCard(
                            title = "Morning Trips",
                            count = morningCount,
                            modifier = Modifier.weight(1f)
                        )

                        TripCountCard(
                            title = "Return Trips",
                            count = returnCount,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(
    title: String,
    amount: Double,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "Rs. ${"%.0f".format(Locale.US, amount)}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun TripCountCard(
    title: String,
    count: Int,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
