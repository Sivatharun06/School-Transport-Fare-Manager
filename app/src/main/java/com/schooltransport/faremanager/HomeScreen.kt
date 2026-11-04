
package com.schooltransport.faremanager

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun HomeScreen(
    store: TransportStore,
    onOpenFarePlans: () -> Unit
) {

    val today = remember { Calendar.getInstance() }

    var selectedMonth by remember {
        mutableIntStateOf(today.get(Calendar.MONTH))
    }

    var selectedYear by remember {
        mutableIntStateOf(today.get(Calendar.YEAR))
    }

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

            IconButton(onClick = onOpenFarePlans) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "Payment Configurations"
                )
            }
        }

        // MONTHLY OVERVIEW

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    "Monthly Overview",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = {
                            val calendar = Calendar.getInstance().apply {
                                set(selectedYear, selectedMonth, 1)
                                add(Calendar.MONTH, -1)
                            }

                            selectedMonth = calendar.get(Calendar.MONTH)
                            selectedYear = calendar.get(Calendar.YEAR)
                        }
                    ) {
                        Text("‹ Previous")
                    }

                    Text(
                        monthName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    TextButton(
                        onClick = {
                            val calendar = Calendar.getInstance().apply {
                                set(selectedYear, selectedMonth, 1)
                                add(Calendar.MONTH, 1)
                            }

                            selectedMonth = calendar.get(Calendar.MONTH)
                            selectedYear = calendar.get(Calendar.YEAR)
                        }
                    ) {
                        Text("Next ›")
                    }
                }

                OverviewRow(
                    "Total Fare",
                    summary.totalFare
                )

                OverviewRow(
                    "Paid Amount",
                    summary.paidAmount
                )

                HorizontalDivider()

                OverviewRow(
                    "Balance",
                    summary.balance,
                    bold = true
                )

                if (summary.overpaid > 0) {
                    OverviewRow(
                        "Overpaid",
                        summary.overpaid,
                        bold = true
                    )
                }

                Text(
                    "${summary.tripCount} attendance records",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

        // CHILDREN

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Children",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                OverviewRow(
                    "Registered",
                    store.children.size.toDouble()
                )

                OverviewRow(
                    "Active",
                    store.children.count { it.isActive }.toDouble()
                )
            }
        }

        // PAYMENT CONFIGURATIONS

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Payment Configurations",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "Create and manage different morning and return fare plans."
                )

                Text(
                    "Available plans: ${store.farePlans.size}"
                )

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
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Quick Information",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text("Recorded trips: ${store.trips.size}")
                Text("Payment records: ${store.payments.size}")
            }
        }

        // MESSAGES

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
}

@Composable
private fun OverviewRow(
    label: String,
    amount: Double,
    bold: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            fontWeight = if (bold) {
                FontWeight.Bold
            } else {
                FontWeight.Normal
            }
        )

        Text(
            "Rs. ${"%.2f".format(Locale.US, amount)}",
            fontWeight = if (bold) {
                FontWeight.Bold
            } else {
                FontWeight.Medium
            }
        )
    }
}
