
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private val WeekendRed = Color(0xFFD94848)
private val WeekendGreen = Color(0xFF236B43)
private val WeekdayPurple = Color(0xFFECE2FA)
private val WeekdayGold = Color(0xFFFFD34E)
private val DeepPurpleText = Color(0xFF382064)

private data class CalendarDateItem(
    val date: String,
    val dateLabel: String,
    val dayLabel: String,
    val isWeekend: Boolean
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

    LaunchedEffect(store.children, store.selectedCalendarChildId) {
        if (
            store.children.isNotEmpty() &&
            store.selectedCalendarChildId.isBlank()
        ) {
            store.selectCalendarChild(store.children.first().id)
        }
    }

    val selectedChild = store.children.firstOrNull {
        it.id == store.selectedCalendarChildId
    } ?: store.children.firstOrNull()

    val monthCalendar = remember(displayedMonth, displayedYear) {
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

    val monthKey = remember(displayedMonth, displayedYear) {
        String.format(
            Locale.US,
            "%04d-%02d",
            displayedYear,
            displayedMonth + 1
        )
    }

    val monthName = remember(displayedMonth, displayedYear) {
        SimpleDateFormat(
            "MMMM yyyy",
            Locale.getDefault()
        ).format(monthCalendar.time)
    }

    val dateItems = remember(displayedMonth, displayedYear) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, displayedYear)
            set(Calendar.MONTH, displayedMonth)
            set(Calendar.DAY_OF_MONTH, 1)
        }

        val totalDays =
            calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val labelFormat = SimpleDateFormat("d MMM", Locale.getDefault())
        val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())

        (1..totalDays).map { day ->
            calendar.set(Calendar.DAY_OF_MONTH, day)

            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

            CalendarDateItem(
                date = dateFormat.format(calendar.time),
                dateLabel = labelFormat.format(calendar.time),
                dayLabel = dayFormat.format(calendar.time),
                isWeekend = dayOfWeek == Calendar.SATURDAY ||
                    dayOfWeek == Calendar.SUNDAY
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

    val morningCount = childTrips.count { it.morningDone }
    val returnCount = childTrips.count { it.returnDone }

    LaunchedEffect(
        store.trips,
        store.errorMessage,
        store.successMessage
    ) {
        pendingDates.clear()
    }

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

    fun toggleAttendance(date: String, isMorning: Boolean) {
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(3.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { changeMonth(-1) }) {
                    Icon(
                        Icons.Default.ChevronLeft,
                        contentDescription = "Previous month",
                        tint = DeepPurpleText
                    )
                }

                Text(
                    text = monthName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = DeepPurpleText
                )

                IconButton(onClick = { changeMonth(1) }) {
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = "Next month",
                        tint = DeepPurpleText
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Box {
            OutlinedButton(
                onClick = { childMenuExpanded = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = selectedChild?.name ?: "Select Children",
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = "Select child"
                )
            }

            DropdownMenu(
                expanded = childMenuExpanded,
                onDismissRequest = { childMenuExpanded = false },
                modifier = Modifier.fillMaxWidth(0.85f)
            ) {
                store.children.forEach { child ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                child.name,
                                fontWeight = if (
                                    child.id == selectedChild?.id
                                ) FontWeight.Bold else FontWeight.Normal
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

                    val morningDone = trip?.morningDone ?: false
                    val returnDone = trip?.returnDone ?: false

                    val dailyTotal =
                        (if (morningDone) {
                            trip?.morningFare ?: selectedChild.morningFare
                        } else 0.0) +
                        (if (returnDone) {
                            trip?.returnFare ?: selectedChild.returnFare
                        } else 0.0)

                    val pendingKey = "${selectedChild.id}_${item.date}"
                    val rowPending =
                        pendingKey in pendingDates || store.isLoading


                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 8.dp,
                                    vertical = 10.dp
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {

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
                                    color = if (item.isWeekend) {
                                        WeekendRed
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            }

                            
Button(
    onClick = {
        toggleAttendance(item.date, true)
    },
    enabled = !rowPending,
    modifier = Modifier
        .weight(1f)
        .height(52.dp),
    contentPadding = PaddingValues(
        horizontal = 2.dp,
        vertical = 4.dp
    ),
    shape = RoundedCornerShape(12.dp),
    colors = ButtonDefaults.buttonColors(
        containerColor = Color.White,
        contentColor = Color(0xFF702FC5),
        disabledContainerColor = Color(0xFFF5F2F8),
        disabledContentColor = Color(0xFF9B83B8)
    ),
    elevation = ButtonDefaults.buttonElevation(
        defaultElevation = 3.dp,
        pressedElevation = 1.dp,
        disabledElevation = 0.dp
    )
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
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

Button(
    onClick = {
        toggleAttendance(item.date, false)
    },
    enabled = !rowPending,
    modifier = Modifier
        .weight(1f)
        .height(52.dp),
    contentPadding = PaddingValues(
        horizontal = 2.dp,
        vertical = 4.dp
    ),
    shape = RoundedCornerShape(12.dp),
    colors = ButtonDefaults.buttonColors(
        containerColor = Color.White,
        contentColor = Color(0xFF702FC5),
        disabledContainerColor = Color(0xFFF5F2F8),
        disabledContentColor = Color(0xFF9B83B8)
    ),
    elevation = ButtonDefaults.buttonElevation(
        defaultElevation = 3.dp,
        pressedElevation = 1.dp,
        disabledElevation = 0.dp
    )
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
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

                    }
                }

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
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp)
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
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp)
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
