
package com.schooltransport.faremanager

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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

    var paymentToEdit by remember {
        mutableStateOf<TransportPayment?>(null)
    }

    var paymentToDelete by remember {
        mutableStateOf<TransportPayment?>(null)
    }

    var showHistory by remember {
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

    val monthlyPayments = store.payments
        .filter { it.monthKey == monthKey }
        .sortedByDescending { it.paymentDate }

    val allPayments = store.payments
        .sortedWith(
            compareByDescending<TransportPayment> { it.paymentDate }
                .thenByDescending { it.createdAt }
        )

    // PAYMENT HISTORY PAGE

    if (showHistory) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                TextButton(
                    onClick = {
                        showHistory = false
                    }
                ) {
                    Text("‹ Back")
                }

                Spacer(Modifier.width(8.dp))

                Text(
                    "Payment History",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(12.dp))

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

            if (store.isLoading && allPayments.isEmpty()) {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

            } else if (allPayments.isEmpty()) {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No payments recorded yet.")
                }

            } else {

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {

                    items(
                        items = allPayments,
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
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {

                                    Text(
                                        payment.childName,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium
                                    )

                                    Text(
                                        "Date: ${payment.paymentDate}",
                                        style = MaterialTheme.typography.bodySmall
                                    )

                                    if (payment.note.isNotBlank()) {
                                        Text(
                                            payment.note,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }

                                    Text(
                                        "Rs. ${"%.2f".format(Locale.US, payment.amount)}",
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {

                                    IconButton(
                                        onClick = {
                                            paymentToEdit = payment
                                        }
                                    ) {
                                        Icon(
                                            Icons.Default.Edit,
                                            contentDescription = "Edit payment"
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            paymentToDelete = payment
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
        }

    } else {

        // MAIN PAYMENT PAGE

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            // MONTH SELECTOR - UNCHANGED

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

            Spacer(Modifier.height(14.dp))

            // PAYMENT HISTORY + ADD PAYMENT BUTTONS

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                OutlinedButton(
                    onClick = {
                        showHistory = true
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(
                        horizontal = 6.dp,
                        vertical = 12.dp
                    )
                ) {
                    Text(
                        "Payment History",
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = {
                        showPaymentDialog = true
                    },
                    enabled = store.children.isNotEmpty(),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(
                        horizontal = 6.dp,
                        vertical = 12.dp
                    )
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null
                    )

                    Spacer(Modifier.width(4.dp))

                    Text(
                        "Add Payment",
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

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

                    if (summary.overpaidAmount > 0) {
                        PaymentSummaryRow(
                            "Overpaid",
                            summary.overpaidAmount,
                            bold = true
                        )
                    }

                    Text(
                        "${summary.tripCount} attendance records",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            // CHILD-WISE SUMMARY

            Text(
                "Child-wise Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            if (store.children.isEmpty()) {

                Text("No children registered.")

            } else {

                store.children.forEach { child ->

                    val childSummary = store.childMonthlySummary(
                        child.id,
                        monthKey
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {

                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {

                            Text(
                                child.name,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall
                            )

                            PaymentSummaryRow(
                                "Total Fare",
                                childSummary.totalFare
                            )

                            PaymentSummaryRow(
                                "Paid",
                                childSummary.paidAmount
                            )

                            PaymentSummaryRow(
                                "Balance",
                                childSummary.balance,
                                bold = true
                            )

                            if (childSummary.overpaidAmount > 0) {
                                PaymentSummaryRow(
                                    "Overpaid",
                                    childSummary.overpaidAmount,
                                    bold = true
                                )
                            }
                        }
                    }
                }
            }

            store.errorMessage?.let {
                Spacer(Modifier.height(8.dp))
                Text(
                    it,
                    color = MaterialTheme.colorScheme.error
                )
            }

            store.successMessage?.let {
                Spacer(Modifier.height(8.dp))
                Text(
                    it,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.height(20.dp))
        }
    }

    // ADD PAYMENT DIALOG

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

    // EDIT PAYMENT DIALOG

    paymentToEdit?.let { payment ->

        AddPaymentDialog(
            children = store.children,
            defaultMonth = payment.monthKey,
            existingPayment = payment,
            onDismiss = {
                paymentToEdit = null
            },
            onSave = { updatedPayment ->
                store.savePayment(updatedPayment)
                paymentToEdit = null
            }
        )
    }

    // DELETE CONFIRMATION

    paymentToDelete?.let { payment ->

        AlertDialog(
            onDismissRequest = {
                paymentToDelete = null
            },
            title = {
                Text("Delete Payment?")
            },
            text = {
                Text(
                    "Delete Rs. ${"%.2f".format(Locale.US, payment.amount)} payment from ${payment.childName}?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        store.deletePayment(payment.id)
                        paymentToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        paymentToDelete = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

// PAYMENT SUMMARY ROW

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

// ADD / EDIT PAYMENT DIALOG

@Composable
private fun AddPaymentDialog(
    children: List<TransportChild>,
    defaultMonth: String,
    existingPayment: TransportPayment? = null,
    onDismiss: () -> Unit,
    onSave: (TransportPayment) -> Unit
) {

    var selectedChildId by remember(existingPayment?.id) {
        mutableStateOf(existingPayment?.childId ?: "")
    }

    val selectedChild = children.firstOrNull {
        it.id == selectedChildId
    }

    var amount by remember(existingPayment?.id) {
        mutableStateOf(
            existingPayment?.amount?.toString() ?: ""
        )
    }

    val initialDate = remember(defaultMonth, existingPayment?.id) {

        if (existingPayment != null) {

            existingPayment.paymentDate

        } else {

            val currentMonth = SimpleDateFormat(
                "yyyy-MM",
                Locale.US
            ).format(Calendar.getInstance().time)

            if (defaultMonth == currentMonth) {

                SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.US
                ).format(Calendar.getInstance().time)

            } else {
                "$defaultMonth-01"
            }
        }
    }

    var paymentDate by remember(defaultMonth, existingPayment?.id) {
        mutableStateOf(initialDate)
    }

    var note by remember(existingPayment?.id) {
        mutableStateOf(existingPayment?.note ?: "")
    }

    var showChildren by remember {
        mutableStateOf(false)
    }

    var error by remember {
        mutableStateOf("")
    }

    val isEditing = existingPayment != null

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text(
                if (isEditing) "Edit Payment" else "Add Payment"
            )
        },

        text = {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                OutlinedButton(
                    onClick = {
                        showChildren = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        selectedChild?.name
                            ?: existingPayment?.childName
                            ?: "Select Child"
                    )
                }

                OutlinedTextField(
                    value = amount,
                    onValueChange = {
                        amount = it
                        error = ""
                    },
                    label = {
                        Text("Amount (Rs.)")
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = paymentDate,
                    onValueChange = {
                        paymentDate = it
                        error = ""
                    },
                    label = {
                        Text("Payment Date (YYYY-MM-DD)")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = note,
                    onValueChange = {
                        note = it
                    },
                    label = {
                        Text("Note (Optional)")
                    },
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

                    when {

                        selectedChild == null ->
                            error = "Select a valid child"

                        parsedAmount == null ||
                            !parsedAmount.isFinite() ||
                            parsedAmount <= 0 ->
                            error = "Enter a valid payment amount"

                        !isValidPaymentDate(paymentDate) ->
                            error = "Enter a valid date (YYYY-MM-DD)"

                        else -> {

                            val child = selectedChild

                            val payment = if (existingPayment != null) {

                                existingPayment.copy(
                                    childId = child.id,
                                    childName = child.name,
                                    amount = parsedAmount,
                                    paymentDate = paymentDate,
                                    monthKey = paymentDate.take(7),
                                    note = note.trim()
                                )

                            } else {

                                TransportPayment(
                                    childId = child.id,
                                    childName = child.name,
                                    amount = parsedAmount,
                                    paymentDate = paymentDate,
                                    monthKey = paymentDate.take(7),
                                    note = note.trim()
                                )
                            }

                            onSave(payment)
                        }
                    }
                }
            ) {
                Text(
                    if (isEditing) "Update Payment"
                    else "Save Payment"
                )
            }
        },

        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )

    // CHILD SELECTION

    if (showChildren) {

        AlertDialog(
            onDismissRequest = {
                showChildren = false
            },

            title = {
                Text("Select Child")
            },

            text = {

                Column(
                    modifier = Modifier
                        .heightIn(max = 300.dp)
                        .verticalScroll(rememberScrollState())
                ) {

                    children.forEach { child ->

                        TextButton(
                            onClick = {
                                selectedChildId = child.id
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

// PAYMENT DATE VALIDATION

private fun isValidPaymentDate(value: String): Boolean {

    if (!value.matches(Regex("^\\d{4}-\\d{2}-\\d{2}$"))) {
        return false
    }

    return try {

        val format = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.US
        )

        format.isLenient = false
        format.parse(value) != null

    } catch (_: Exception) {
        false
    }
}
