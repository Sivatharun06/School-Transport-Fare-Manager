
package com.schooltransport.faremanager

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import java.util.Date
import java.util.Locale

@Composable
fun TripsScreen(store: TransportStore) {

    var showDialog by remember { mutableStateOf(false) }
    var editingTrip by remember { mutableStateOf<TransportTrip?>(null) }
    var tripToDelete by remember { mutableStateOf<TransportTrip?>(null) }

    LaunchedEffect(Unit) {
        store.loadAll()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column {
                Text(
                    "Trips",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Text("${store.trips.size} trip records")
            }

            Button(
                onClick = {
                    editingTrip = null
                    showDialog = true
                },
                enabled = store.children.isNotEmpty()
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null
                )

                Spacer(Modifier.width(4.dp))

                Text("Add Trip")
            }
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

        if (store.children.isEmpty()) {
            Text("Add a child first to create a trip.")
        }

        if (store.isLoading && store.trips.isEmpty()) {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

        } else if (store.trips.isEmpty()) {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No trip records yet.")
            }

        } else {

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {

                items(
                    items = store.trips,
                    key = { it.id }
                ) { trip ->

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
                                    trip.childName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                Text("Date: ${trip.date}")

                                if (trip.time.isNotBlank()) {
                                    Text("Time: ${trip.time}")
                                }

                                if (
                                    trip.fromLocation.isNotBlank() ||
                                    trip.toLocation.isNotBlank()
                                ) {
                                    Text(
                                        "${trip.fromLocation} → ${trip.toLocation}"
                                    )
                                }

                                Text(
                                    "Morning: ${
                                        if (trip.morningDone) "Done" else "Not done"
                                    }"
                                )

                                Text(
                                    "Return: ${
                                        if (trip.returnDone) "Done" else "Not done"
                                    }"
                                )

                                Text(
                                    "Morning fare: Rs. ${trip.morningFare.toInt()}"
                                )

                                Text(
                                    "Return fare: Rs. ${trip.returnFare.toInt()}"
                                )

                                Text(
                                    "Total: Rs. ${trip.totalFare.toInt()}",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                if (trip.note.isNotBlank()) {
                                    Text("Note: ${trip.note}")
                                }
                            }

                            Column {

                                IconButton(
                                    onClick = {
                                        editingTrip = trip
                                        showDialog = true
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "Edit trip"
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        tripToDelete = trip
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete trip",
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

    // ADD / EDIT DIALOG

    if (showDialog) {

        TripFormDialog(
            children = store.children,
            existingTrip = editingTrip,
            onDismiss = {
                showDialog = false
                editingTrip = null
            },
            onSave = { trip ->
                store.saveTrip(trip)
                showDialog = false
                editingTrip = null
            }
        )
    }

    // DELETE CONFIRMATION

    tripToDelete?.let { trip ->

        AlertDialog(
            onDismissRequest = {
                tripToDelete = null
            },
            title = {
                Text("Delete Trip?")
            },
            text = {
                Text(
                    "Delete ${trip.childName}'s trip on ${trip.date}?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        store.deleteTrip(trip.id)
                        tripToDelete = null
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
                        tripToDelete = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

// TRIP FORM DIALOG

@Composable
private fun TripFormDialog(
    children: List<TransportChild>,
    existingTrip: TransportTrip?,
    onDismiss: () -> Unit,
    onSave: (TransportTrip) -> Unit
) {

    var selectedChild by remember(existingTrip, children) {
        mutableStateOf(
            children.firstOrNull {
                it.id == existingTrip?.childId
            } ?: if (existingTrip == null) {
                children.firstOrNull()
            } else {
                null
            }
        )
    }

    var date by remember(existingTrip) {
        mutableStateOf(
            existingTrip?.date ?: SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.US
            ).format(Date())
        )
    }

    var time by remember(existingTrip) {
        mutableStateOf(
            existingTrip?.time ?: SimpleDateFormat(
                "HH:mm",
                Locale.US
            ).format(Date())
        )
    }

    var fromLocation by remember(existingTrip, children) {
        mutableStateOf(
            existingTrip?.fromLocation
                ?: children.firstOrNull {
                    it.id == existingTrip?.childId
                }?.fromLocation
                ?: children.firstOrNull()?.fromLocation.orEmpty()
        )
    }

    var toLocation by remember(existingTrip, children) {
        mutableStateOf(
            existingTrip?.toLocation
                ?: children.firstOrNull {
                    it.id == existingTrip?.childId
                }?.toLocation
                ?: children.firstOrNull()?.toLocation.orEmpty()
        )
    }

    var morningDone by remember(existingTrip) {
        mutableStateOf(existingTrip?.morningDone ?: true)
    }

    var returnDone by remember(existingTrip) {
        mutableStateOf(existingTrip?.returnDone ?: false)
    }

    var morningFare by remember(existingTrip, children) {
        mutableStateOf(
            existingTrip?.morningFare?.toString()
                ?: children.firstOrNull {
                    it.id == existingTrip?.childId
                }?.morningFare?.toString()
                ?: children.firstOrNull()?.morningFare?.toString()
                ?: "400"
        )
    }

    var returnFare by remember(existingTrip, children) {
        mutableStateOf(
            existingTrip?.returnFare?.toString()
                ?: children.firstOrNull {
                    it.id == existingTrip?.childId
                }?.returnFare?.toString()
                ?: children.firstOrNull()?.returnFare?.toString()
                ?: "400"
        )
    }

    var note by remember(existingTrip) {
        mutableStateOf(existingTrip?.note ?: "")
    }

    var showChildPicker by remember {
        mutableStateOf(false)
    }

    var error by remember {
        mutableStateOf("")
    }

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text(
                if (existingTrip == null) "Add Trip"
                else "Edit Trip"
            )
        },

        text = {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                // CHILD SELECTION

                OutlinedButton(
                    onClick = {
                        showChildPicker = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(selectedChild?.name ?: "Select Child")
                }

                // DATE

                OutlinedTextField(
                    value = date,
                    onValueChange = {
                        date = it
                        error = ""
                    },
                    label = {
                        Text("Date (YYYY-MM-DD)")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // TIME

                OutlinedTextField(
                    value = time,
                    onValueChange = {
                        time = it
                        error = ""
                    },
                    label = {
                        Text("Time (HH:mm)")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // ROUTE

                OutlinedTextField(
                    value = fromLocation,
                    onValueChange = {
                        fromLocation = it
                    },
                    label = {
                        Text("From")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = toLocation,
                    onValueChange = {
                        toLocation = it
                    },
                    label = {
                        Text("To")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                HorizontalDivider()

                // MORNING

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = morningDone,
                        onCheckedChange = {
                            morningDone = it
                        }
                    )

                    Text("Morning completed")
                }

                OutlinedTextField(
                    value = morningFare,
                    onValueChange = {
                        morningFare = it
                        error = ""
                    },
                    label = {
                        Text("Morning fare (Rs.)")
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // RETURN

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = returnDone,
                        onCheckedChange = {
                            returnDone = it
                        }
                    )

                    Text("Return completed")
                }

                OutlinedTextField(
                    value = returnFare,
                    onValueChange = {
                        returnFare = it
                        error = ""
                    },
                    label = {
                        Text("Return fare (Rs.)")
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // NOTE

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

                    val morning = morningFare.toDoubleOrNull()
                    val returning = returnFare.toDoubleOrNull()

                    when {

                        selectedChild == null ->
                            error = "Select a child"

                        !isValidTripDate(date) ->
                            error = "Enter a valid date (YYYY-MM-DD)"

                        !time.matches(
                            Regex("^([01]\\d|2[0-3]):[0-5]\\d$")
                        ) ->
                            error = "Enter time as HH:mm"

                        morning == null ||
                            returning == null ||
                            !morning.isFinite() ||
                            !returning.isFinite() ||
                            morning < 0 ||
                            returning < 0 ->
                            error = "Enter valid fare amounts"

                        !morningDone && !returnDone ->
                            error = "Select at least one completed trip"

                        else -> {

                            val child = selectedChild!!

                            onSave(
                                TransportTrip(
                                    id = existingTrip?.id ?: "",
                                    childId = child.id,
                                    childName = child.name,
                                    date = date,
                                    time = time,
                                    fromLocation = fromLocation.trim(),
                                    toLocation = toLocation.trim(),
                                    morningDone = morningDone,
                                    returnDone = returnDone,
                                    morningFare = morning,
                                    returnFare = returning,
                                    note = note.trim(),
                                    createdAt = existingTrip?.createdAt
                                        ?: System.currentTimeMillis()
                                )
                            )
                        }
                    }
                }
            ) {
                Text("Save Trip")
            }
        },

        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )

    // CHILD PICKER

    if (showChildPicker) {

        AlertDialog(
            onDismissRequest = {
                showChildPicker = false
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

                                val isSameChild =
                                    child.id == existingTrip?.childId

                                selectedChild = child

                                // Same child: preserve historical route and fares.
                                // Different child: use selected child's current details.

                                if (!isSameChild) {

                                    fromLocation = child.fromLocation
                                    toLocation = child.toLocation

                                    morningFare =
                                        child.morningFare.toString()

                                    returnFare =
                                        child.returnFare.toString()
                                }

                                showChildPicker = false
                                error = ""
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
                        showChildPicker = false
                    }
                ) {
                    Text("Close")
                }
            }
        )
    }
}

// DATE VALIDATION

private fun isValidTripDate(value: String): Boolean {

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
