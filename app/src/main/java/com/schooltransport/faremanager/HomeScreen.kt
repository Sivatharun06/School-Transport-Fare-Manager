
package com.schooltransport.faremanager

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun HomeScreen(
    store: TransportStore,
    onOpenFarePlans: () -> Unit
) {
    var selectedMonth by remember {
        mutableStateOf(
            SimpleDateFormat("yyyy-MM", Locale.getDefault())
                .format(Calendar.getInstance().time)
        )
    }

    LaunchedEffect(Unit) {
        store.loadAll()
    }

    val summary = store.monthlySummary(selectedMonth)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    "Dashboard",
                    style = MaterialTheme.typography.headlineSmall
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

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Monthly Overview",
                    style = MaterialTheme.typography.titleMedium
                )

                Text("Month: $selectedMonth")

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total Fare")
                    Text("Rs. ${summary.totalFare}")
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Paid")
                    Text("Rs. ${summary.paidAmount}")
                }

                HorizontalDivider()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Balance",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        "Rs. ${summary.balance}",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                if (summary.overpaidAmount > 0) {
                    Text("Overpaid: Rs. ${summary.overpaidAmount}")
                }
            }
        }

        Card(Modifier.fillMaxWidth()) {
            Column(
                Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Children",
                    style = MaterialTheme.typography.titleMedium
                )
                Text("Registered: ${store.children.size}")
                Text("Active: ${store.children.count { it.isActive }}")
            }
        }

        Card(Modifier.fillMaxWidth()) {
            Column(
                Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Payment Configurations",
                    style = MaterialTheme.typography.titleMedium
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

        Card(Modifier.fillMaxWidth()) {
            Column(
                Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Quick Information",
                    style = MaterialTheme.typography.titleMedium
                )
                Text("Recorded trips: ${store.trips.size}")
                Text("Payment records: ${store.payments.size}")
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
}
