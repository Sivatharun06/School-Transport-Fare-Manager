
package com.schooltransport.faremanager

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun PaymentsScreen(store: TransportStore) {

    val today = remember { Calendar.getInstance() }

    var month by remember {
        mutableIntStateOf(today.get(Calendar.MONTH))
    }

    var year by remember {
        mutableIntStateOf(today.get(Calendar.YEAR))
    }

    var showPaymentDialog by remember {
        mutableStateOf(false)
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

    val monthlyPayments = store.payments.filter {
        it.monthKey == monthKey
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            "Payments",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
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
                style = MaterialTheme.typography.titleLarge,
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

        Spacer(Modifier.height(8.dp))

        // MONTHLY SUMMARY

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "Monthly Summary",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                PaymentSummaryRow(
                    "Total Fare",
                    summary.totalFare
                )

                PaymentSummaryRow(
                    "Paid Amount",
                    summary.paidAmount
                )

                HorizontalDivider()

                PaymentSummaryRow(
                    "Balance",
                    summary.balance,
                    bold = true
                )

                Text(
                    "${summary.tripCount} attendance records",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Payment History",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = {
                    showPaymentDialog = true
                },
                enabled = store.children.isNotEmpty()
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text("Add Payment")
            }
        }

        Spacer(Modifier.height(8.dp))

        store.errorMessage?.let {
            Text(
                it,
                color = MaterialTheme.colorScheme.error
            )
        }

        if (store.isLoading && store.payments.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (monthlyPayments.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No payments recorded for this month.")
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(
                    items = monthlyPayments,
                    key = { it.id }
                ) { payment ->

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    payment.childName,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Text(
                                    payment.paymentDate,
                                    style = MaterialTheme.typography.bodySmall
                                )

                                if (payment.note.isNotBlank()) {
                                    Text(
                                        payment.note,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }

                                Text(
                                    "Rs. ${payment.amount}",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            IconButton(
                                onClick = {
                                    store.deletePayment(payment.id)
                                }
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete payment",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showPaymentDialog) {
        AddPaymentDialog(
            children = store.children,
            defaultMonth = monthKey,
            onDismiss = {
                showPaymentDialog = false
            },
            onSave = { payment ->
                store.savePayment(payment)
                showPaymentDialog = false
            }
        )
    }
}

@Composable
private fun PaymentSummaryRow(
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

@Composable
private fun AddPaymentDialog(
    children: List<TransportChild>,
    defaultMonth: String,
    onDismiss: () -> Unit,
    onSave: (TransportPayment) -> Unit
) {
    var selectedChild by remember {
        mutableStateOf<TransportChild?>(null)
    }

    var amount by remember {
        mutableStateOf("")
    }

    var paymentDate by remember {
        mutableStateOf(
            SimpleDateFormat("yyyy-MM-dd", Locale.US)
                .format(Calendar.getInstance().time)
        )
    }

    var note by remember {
        mutableStateOf("")
    }

    var showChildren by remember {
        mutableStateOf(false)
    }

    var error by remember {
        mutableStateOf("")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Add Payment")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        showChildren = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        selectedChild?.name ?: "Select Child"
                    )
                }

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount (Rs.)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = paymentDate,
                    onValueChange = { paymentDate = it },
                    label = { Text("Payment Date (YYYY-MM-DD)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                if (error.isNotBlank()) {
                    Text(
                        error,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsedAmount = amount.toDoubleOrNull()

                    if (selectedChild == null) {
                        error = "Select a child"
                    } else if (parsedAmount == null || parsedAmount <= 0) {
                        error = "Enter a valid payment amount"
                    } else if (!paymentDate.matches(
                            Regex("\\d{4}-\\d{2}-\\d{2}")
                        )
                    ) {
                        error = "Use date format YYYY-MM-DD"
                    } else {
                        val child = selectedChild!!

                        onSave(
                            TransportPayment(
                                childId = child.id,
                                childName = child.name,
                                amount = parsedAmount,
                                paymentDate = paymentDate,
                                monthKey = paymentDate.take(7),
                                note = note.trim()
                            )
                        )
                    }
                }
            ) {
                Text("Save Payment")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )

    if (showChildren) {
        AlertDialog(
            onDismissRequest = {
                showChildren = false
            },
            title = {
                Text("Select Child")
            },
            text = {
                Column {
                    children.forEach { child ->
                        TextButton(
                            onClick = {
                                selectedChild = child
                                showChildren = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(child.name)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showChildren = false
                    }
                ) {
                    Text("Close")
                }
            }
        )
    }
}
