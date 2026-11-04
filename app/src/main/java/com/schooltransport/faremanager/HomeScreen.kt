
package com.schooltransport.faremanager

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun HomeScreen(store: TransportStore) {

    val today = remember { Calendar.getInstance() }

    var month by remember {
        mutableIntStateOf(today.get(Calendar.MONTH))
    }

    var year by remember {
        mutableIntStateOf(today.get(Calendar.YEAR))
    }

    val monthKey = remember(month, year) {
        String.format(Locale.US, "%04d-%02d", year, month + 1)
    }

    val monthName = remember(month, year) {
        SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)
            .format(
                Calendar.getInstance().apply {
                    set(year, month, 1)
                }.time
            )
    }

    LaunchedEffect(Unit) {
        store.loadAll()
    }

    val summary = store.monthlySummary(monthKey)

    val activeChildren = store.children.filter {
        it.isActive
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            "Dashboard",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            "School Transport Fare Manager",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(16.dp))

        // MONTH SELECTOR

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = {
                        val c = Calendar.getInstance().apply {
                            set(year, month, 1)
                            add(Calendar.MONTH, -1)
                        }
                        month = c.get(Calendar.MONTH)
                        year = c.get(Calendar.YEAR)
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
                        val c = Calendar.getInstance().apply {
                            set(year, month, 1)
                            add(Calendar.MONTH, 1)
                        }
                        month = c.get(Calendar.MONTH)
                        year = c.get(Calendar.YEAR)
                    }
                ) {
                    Text("Next ›")
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        if (store.isLoading && store.children.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {

            // SUMMARY CARDS

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SummaryCard(
                    title = "Total Fare",
                    amount = summary.totalFare,
                    modifier = Modifier.weight(1f)
                )

                SummaryCard(
                    title = "Paid Amount",
                    amount = summary.paidAmount,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SummaryCard(
                    title = "Balance",
                    amount = summary.balance,
                    modifier = Modifier.weight(1f)
                )

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Text(
                            "Children",
                            style = MaterialTheme.typography.labelMedium
                        )

                        Spacer(Modifier.height(6.dp))

                        Text(
                            activeChildren.size.toString(),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            "Active children",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // ATTENDANCE SUMMARY

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Monthly Activity",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(8.dp))

                    Text("Attendance records: ${summary.tripCount}")

                    val morningCount = store.trips.count {
                        it.date.startsWith("$monthKey-") &&
                        it.morningDone
                    }

                    val returnCount = store.trips.count {
                        it.date.startsWith("$monthKey-") &&
                        it.returnDone
                    }

                    Text("Morning trips: $morningCount")
                    Text("Return trips: $returnCount")
                }
            }

            Spacer(Modifier.height(16.dp))

            // CHILDREN MONTHLY SUMMARY

            Text(
                "Children's Fare Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            if (activeChildren.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "No children registered yet.",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(
                        items = activeChildren,
                        key = { it.id }
                    ) { child ->

                        val childSummary =
                            store.childMonthlySummary(child.id, monthKey)

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp)
                            ) {
                                Text(
                                    child.name,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Spacer(Modifier.height(6.dp))

                                DashboardAmountRow(
                                    "Total Fare",
                                    childSummary.totalFare
                                )

                                DashboardAmountRow(
                                    "Paid",
                                    childSummary.paidAmount
                                )

                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 6.dp)
                                )

                                DashboardAmountRow(
                                    "Balance",
                                    childSummary.balance,
                                    bold = true
                                )

                                Text(
                                    "${childSummary.tripCount} attendance records",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        store.errorMessage?.let {
            Text(
                it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
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
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Text(
                title,
                style = MaterialTheme.typography.labelMedium
            )

            Spacer(Modifier.height(6.dp))

            Text(
                "Rs. ${"%.2f".format(Locale.US, amount)}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun DashboardAmountRow(
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
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal
        )

        Text(
            "Rs. ${"%.2f".format(Locale.US, amount)}",
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Medium
        )
    }
}
