package com.schooltransport.faremanager

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private val CalendarDeepPurple = Color(0xFF32145F)
private val CalendarPurple = Color(0xFF54258D)
private val CalendarMidPurple = Color(0xFF7040A8)
private val CalendarGold = Color(0xFFFFD84D)
private val CalendarGoldBorder = Color(0xFFE7B92F)
private val CalendarSoftPurple = Color(0xFFECE2FA)
private val CalendarPurpleText = Color(0xFF382064)

private val CalendarSoftRuby = Color(0xFFF7E6EC)
private val CalendarRuby = Color(0xFF8F1D46)
private val CalendarMidRuby = Color(0xFFB7355D)

private val CalendarWhite = Color.White

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
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        CalendarDeepPurple,
                        CalendarPurple,
                        Color(0xFF452078)
                    )
                )
            )
            .padding(horizontal = 14.dp)
    ) {

        Spacer(modifier = Modifier.height(12.dp))

        PremiumMonthSelector(
            month = displayedMonth,
            year = displayedYear,
            onMonthChange = { newMonth, newYear ->
                displayedMonth = newMonth
                displayedYear = newYear
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // CHILD SELECTOR

        Box {
            OutlinedButton(
                onClick = { childMenuExpanded = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = CalendarWhite,
                    contentColor = CalendarPurpleText
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    CalendarGold
                )
            ) {
                Text(
                    text = selectedChild?.name ?: "Select Children",
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Bold
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
                    color = CalendarWhite,
                    fontWeight = FontWeight.Medium
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

                    val rowBackground = if (item.isWeekend) {
                        CalendarSoftRuby
                    } else {
                        CalendarSoftPurple
                    }

                    val rowBorder = if (item.isWeekend) {
                        CalendarRuby
                    } else {
                        CalendarGoldBorder
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                1.dp,
                                rowBorder,
                                RoundedCornerShape(16.dp)
                            ),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = rowBackground
                        ),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 7.dp,
                                    vertical = 9.dp
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {

                            // DATE AND DAY

                            Column(
                                modifier = Modifier.weight(1.05f)
                            ) {
                                Text(
                                    text = item.dateLabel,
                                    fontWeight = FontWeight.Bold,
                                    color = if (item.isWeekend) {
                                        CalendarRuby
                                    } else {
                                        CalendarPurpleText
                                    },
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Text(
                                    text = item.dayLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (item.isWeekend) {
                                        CalendarRuby
                                    } else {
                                        CalendarPurpleText
                                    }
                                )
                            }

                            // MORNING BUTTON

                            AttendanceButton(
                                title = "Morning",
                                fare = trip?.morningFare
                                    ?: selectedChild.morningFare,
                                selected = morningDone,
                                weekend = item.isWeekend,
                                enabled = !rowPending,
                                modifier = Modifier.weight(1f)
                            ) {
                                toggleAttendance(item.date, true)
                            }

                            // RETURN BUTTON

                            AttendanceButton(
                                title = "Return",
                                fare = trip?.returnFare
                                    ?: selectedChild.returnFare,
                                selected = returnDone,
                                weekend = item.isWeekend,
                                enabled = !rowPending,
                                modifier = Modifier.weight(1f)
                            ) {
                                toggleAttendance(item.date, false)
                            }

                            // DAILY TOTAL

                            Column(
                                modifier = Modifier
                                    .weight(0.9f)
                                    .clip(RoundedCornerShape(11.dp))
                                    .background(
                                        if (item.isWeekend) {
                                            CalendarMidRuby
                                        } else {
                                            CalendarMidPurple
                                        }
                                    )
                                    .padding(
                                        horizontal = 4.dp,
                                        vertical = 9.dp
                                    ),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    "Total",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CalendarWhite,
                                    fontWeight = FontWeight.Medium
                                )

                                Text(
                                    text = "Rs. ${
                                        "%.0f".format(
                                            Locale.US,
                                            dailyTotal
                                        )
                                    }",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = CalendarWhite
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
                        fontWeight = FontWeight.Bold,
                        color = CalendarGold
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
                        fontWeight = FontWeight.Bold,
                        color = CalendarGold
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
private fun AttendanceButton(
    title: String,
    fare: Double,
    selected: Boolean,
    weekend: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(11.dp)

    val backgroundBrush = when {
        selected && weekend -> Brush.verticalGradient(
            listOf(
                Color(0xFFB7355D),
                CalendarRuby
            )
        )

        selected && !weekend -> Brush.verticalGradient(
            listOf(
                Color(0xFFFFE77A),
                CalendarGold
            )
        )

        weekend -> Brush.verticalGradient(
            listOf(
                CalendarSoftRuby,
                Color(0xFFF2D5DF)
            )
        )

        else -> Brush.verticalGradient(
            listOf(
                CalendarWhite,
                Color(0xFFFFFCF3)
            )
        )
    }

    val borderColor = when {
        selected && weekend -> CalendarRuby
        selected && !weekend -> CalendarGoldBorder
        weekend -> CalendarRuby
        else -> CalendarGoldBorder
    }

    val textColor = when {
        selected && weekend -> CalendarWhite
        selected && !weekend -> CalendarPurpleText
        weekend -> CalendarRuby
        else -> CalendarPurpleText
    }

    Column(
        modifier = modifier
            .height(52.dp)
            .clip(shape)
            .background(backgroundBrush)
            .border(1.2.dp, borderColor, shape)
            .clickable(
                enabled = enabled,
                onClick = onClick
            )
            .padding(horizontal = 2.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = textColor
        )

        Text(
            text = "Rs. ${
                "%.0f".format(Locale.US, fare)
            }",
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}

@Composable
private fun SummaryCard(
    title: String,
    amount: Double,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .border(
                1.dp,
                CalendarGold,
                RoundedCornerShape(15.dp)
            ),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(
            containerColor = CalendarMidPurple
        ),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 14.dp,
                    horizontal = 5.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                title,
                style = MaterialTheme.typography.labelMedium,
                color = CalendarWhite,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "Rs. ${
                    "%.0f".format(Locale.US, amount)
                }",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = CalendarGold
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
        modifier = modifier
            .border(
                1.dp,
                CalendarGold,
                RoundedCornerShape(15.dp)
            ),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(
            containerColor = CalendarMidPurple
        ),
        elevation = CardDefaults.cardElevation(3.dp)
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
                color = CalendarWhite
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = CalendarGold
            )
        }
    }
}
