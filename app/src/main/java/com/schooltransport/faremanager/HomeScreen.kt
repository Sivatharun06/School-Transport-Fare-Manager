
package com.schooltransport.faremanager

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun HomeScreen(
    store: TransportStore,
    onOpenFarePlans: () -> Unit,
    onOpenTrips: () -> Unit,
    onOpenProfile: () -> Unit
) {
    val today = remember { Calendar.getInstance() }

    var selectedMonth by remember {
        mutableIntStateOf(today.get(Calendar.MONTH))
    }

    var selectedYear by remember {
        mutableIntStateOf(today.get(Calendar.YEAR))
    }

    var showChildDialog by remember { mutableStateOf(false) }
    var showTripDialog by remember { mutableStateOf(false) }

    val monthKey = remember(selectedMonth, selectedYear) {
        String.format(
            Locale.US,
            "%04d-%02d",
            selectedYear,
            selectedMonth + 1
        )
    }

    val monthName = remember(selectedMonth, selectedYear) {
        SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)
            .format(
                Calendar.getInstance().apply {
                    set(selectedYear, selectedMonth, 1)
                }.time
            )
    }

    LaunchedEffect(Unit) {
        store.loadAll()
    }

    val summary = store.monthlySummary(monthKey)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        // HEADER

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Dashboard",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "Transport overview",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            IconButton(onClick = onOpenProfile) {
                Icon(
                    Icons.Default.AccountCircle,
                    contentDescription = "Profile",
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        // MONTH NAVIGATION

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = {
                        val calendar = Calendar.getInstance().apply {
                            set(selectedYear, selectedMonth, 1)
                            add(Calendar.MONTH, -1)
                        }
                        selectedMonth = calendar.get(Calendar.MONTH)
                        selectedYear = calendar.get(Calendar.YEAR)
                    }
                ) {
                    Icon(
                        Icons.Default.ChevronLeft,
                        contentDescription = "Previous month"
                    )
                }

                Text(
                    monthName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                IconButton(
                    onClick = {
                        val calendar = Calendar.getInstance().apply {
                            set(selectedYear, selectedMonth, 1)
                            add(Calendar.MONTH, 1)
                        }
                        selectedMonth = calendar.get(Calendar.MONTH)
                        selectedYear = calendar.get(Calendar.YEAR)
                    }
                ) {
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = "Next month"
                    )
                }
            }
        }

        // MONTHLY TOTAL

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Monthly Total",
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    "Rs. ${"%.2f".format(Locale.US, summary.totalFare)}",
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "${summary.tripCount} attendance records",
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        // PAID AND BALANCE

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        "Paid",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        "Rs. ${"%.2f".format(Locale.US, summary.paidAmount)}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        "Balance",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        "Rs. ${"%.2f".format(Locale.US, summary.balance)}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        if (summary.overpaidAmount > 0) {
            Text(
                "Overpaid: Rs. ${
                    "%.2f".format(Locale.US, summary.overpaidAmount)
                }",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }

        // NEW TRIP

        Button(
            onClick = {
                showTripDialog = true
            },
            enabled = store.children.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("New Trip")
        }

        // CHILDREN

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Children",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            "${store.children.size} registered",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    FilledTonalButton(
                        onClick = {
                            showChildDialog = true
                        }
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("Add Child")
                    }
                }

                if (store.children.isEmpty()) {
                    Text("No children added yet.")
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        store.children
                            .sortedBy { it.name.lowercase() }
                            .forEach { child ->

                                Column(
                                    modifier = Modifier.width(76.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(62.dp)
                                            .clip(CircleShape)
                                            .background(
                                                MaterialTheme.colorScheme.secondaryContainer
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            child.name.trim()
                                                .firstOrNull()
                                                ?.uppercaseChar()
                                                ?.toString() ?: "?",
                                            style = MaterialTheme.typography.headlineMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }

                                    Text(
                                        child.name,
                                        style = MaterialTheme.typography.labelMedium,
                                        textAlign = TextAlign.Center,
                                        maxLines = 2
                                    )

                                    Text(
                                        if (child.isActive) "Active" else "Inactive",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (child.isActive) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.error
                                        }
                                    )
                                }
                            }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Active children")
                    Text(
                        store.children.count { it.isActive }.toString(),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // PAYMENT CONFIGURATIONS

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Payment Configurations",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "Create and manage different morning and return fare plans."
                )

                Text("Available plans: ${store.farePlans.size}")

                Button(
                    onClick = onOpenFarePlans,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Manage Payment Configurations")
                }
            }
        }

        // QUICK INFORMATION

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "Quick Information",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Recorded trips")
                    Text(
                        store.trips.size.toString(),
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Payment records")
                    Text(
                        store.payments.size.toString(),
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = onOpenTrips,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("View All Trips")
                }
            }
        }

        store.errorMessage?.let { message ->
            Text(
                message,
                color = MaterialTheme.colorScheme.error
            )
        }

        store.successMessage?.let { message ->
            Text(
                message,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }

    // ADD CHILD POPUP

    if (showChildDialog) {
        ChildDialog(
            store = store,
            child = null,
            onDismiss = {
                showChildDialog = false
            },
            onSave = { child ->
                store.saveChild(child)
                showChildDialog = false
            }
        )
    }

    // NEW TRIP POPUP

    if (showTripDialog) {
        TripFormDialog(
            children = store.children,
            existingTrip = null,
            onDismiss = {
                showTripDialog = false
            },
            onSave = { trip ->
                store.saveTrip(trip)
                showTripDialog = false
            }
        )
    }
}
