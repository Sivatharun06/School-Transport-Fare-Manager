
package com.schooltransport.faremanager

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val TripPurple = Color(0xFF6033AD)
private val TripSoftPurple = Color(0xFFF0E9FC)
private val TripGold = Color(0xFFFFC928)
private val TripGreen = Color(0xFF236B43)

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
            .background(Color(0xFFF8F6FC))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Trip Records",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF382064)
                )
                Text(
                    "${store.trips.size} trip records",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            Button(
                onClick = {
                    editingTrip = null
                    showDialog = true
                },
                enabled = store.children.isNotEmpty(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TripPurple,
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(5.dp))
                Text("Add Trip", fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(Modifier.height(16.dp))

        store.errorMessage?.let {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFEAEA)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    it,
                    modifier = Modifier.padding(12.dp),
                    color = Color(0xFFB3261E)
                )
            }
            Spacer(Modifier.height(8.dp))
        }

        store.successMessage?.let {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE5F5E9)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    it,
                    modifier = Modifier.padding(12.dp),
                    color = TripGreen
                )
            }
            Spacer(Modifier.height(8.dp))
        }

        if (store.children.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = TripSoftPurple
                )
            ) {
                Text(
                    "Add a child first to create a trip.",
                    modifier = Modifier.padding(16.dp),
                    color = TripPurple,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        if (store.isLoading && store.trips.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = TripPurple)
            }
        } else if (store.trips.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.DirectionsBus,
                            contentDescription = null,
                            tint = TripPurple,
                            modifier = Modifier.size(42.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "No trip records yet",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF382064)
                        )
                        Text(
                            "Your trip records will appear here.",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(
                    items = store.trips,
                    key = { it.id }
                ) { trip ->

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 2.dp
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .background(
                                            TripSoftPurple,
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.DirectionsBus,
                                        contentDescription = null,
                                        tint = TripPurple,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Spacer(Modifier.width(12.dp))

                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        trip.childName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF382064)
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.CalendarToday,
                                            contentDescription = null,
                                            tint = Color.Gray,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(Modifier.width(5.dp))
                                        Text(
                                            trip.date,
                                            color = Color.Gray,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        if (trip.time.isNotBlank()) {
                                            Spacer(Modifier.width(10.dp))
                                            Icon(
                                                Icons.Default.AccessTime,
                                                contentDescription = null,
                                                tint = Color.Gray,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(Modifier.width(4.dp))
                                            Text(
                                                trip.time,
                                                color = Color.Gray,
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
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
                                            contentDescription = "Edit trip",
                                            tint = TripPurple
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
                                            tint = Color(0xFFD94848)
                                        )
                                    }
                                }
                            }

                            if (
                                trip.fromLocation.isNotBlank() ||
                                trip.toLocation.isNotBlank()
                            ) {
                                Spacer(Modifier.height(10.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = TripPurple,
                                        modifier = Modifier.size(17.dp)
                                    )
                                    Spacer(Modifier.width(5.dp))
                                    Text(
                                        "${trip.fromLocation} → ${trip.toLocation}",
                                        color = Color(0xFF55515D),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }

                            Spacer(Modifier.height(12.dp))
                            HorizontalDivider(color = Color(0xFFEDE8F4))
                            Spacer(Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                TripStatusCard(
                                    title = "Morning",
                                    completed = trip.morningDone,
                                    fare = trip.morningFare
                                )
                                TripStatusCard(
                                    title = "Return",
                                    completed = trip.returnDone,
                                    fare = trip.returnFare
                                )
                            }

                            Spacer(Modifier.height(12.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        TripSoftPurple,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Total Fare",
                                    color = Color(0xFF382064),
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    "Rs. ${trip.totalFare.toInt()}",
                                    color = TripPurple,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }

                            if (trip.note.isNotBlank()) {
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    "Note: ${trip.note}",
                                    color = Color.Gray,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }

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

    tripToDelete?.let { trip ->
        AlertDialog(
            onDismissRequest = {
                tripToDelete = null
            },
            shape = RoundedCornerShape(22.dp),
            title = {
                Text(
                    "Delete Trip?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("Delete ${trip.childName}'s trip on ${trip.date}?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        store.deleteTrip(trip.id)
                        tripToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD94848)
                    ),
                    shape = RoundedCornerShape(12.dp)
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
                    Text("Cancel", color = TripPurple)
                }
            }
        )
    }
}

@Composable
private fun TripStatusCard(
    title: String,
    completed: Boolean,
    fare: Double
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .background(
                if (completed) Color(0xFFFFF5D6) else Color(0xFFF5F3F7),
                RoundedCornerShape(12.dp)
            )
            .padding(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        if (completed) TripGreen else Color.Gray,
                        CircleShape
                    )
            )
            Spacer(Modifier.width(6.dp))
            Text(
                title,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF382064),
                style = MaterialTheme.typography.bodySmall
            )
        }
        Spacer(Modifier.height(5.dp))
        Text(
            if (completed) "Completed" else "Not done",
            color = if (completed) TripGreen else Color.Gray,
            style = MaterialTheme.typography.labelSmall
        )
        Text(
            "Rs. ${fare.toInt()}",
            color = Color(0xFF382064),
            fontWeight = FontWeight.Bold
        )
    }
}

// PUBLIC TRIP FORM DIALOG – REUSED ON HOME AND TRIPS PAGE

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripFormDialog(
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
        shape = RoundedCornerShape(22.dp),
        title = {
            Text(
                if (existingTrip == null) "Add Trip" else "Edit Trip",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF382064)
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

                OutlinedButton(
                    onClick = {
                        showChildPicker = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(selectedChild?.name ?: "Select Child")
                }

                OutlinedTextField(
                    value = date,
                    onValueChange = {
                        date = it
                        error = ""
                    },
                    label = { Text("Date (YYYY-MM-DD)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = time,
                    onValueChange = {
                        time = it
                        error = ""
                    },
                    label = { Text("Time (HH:mm)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = fromLocation,
                    onValueChange = { fromLocation = it },
                    label = { Text("From") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = toLocation,
                    onValueChange = { toLocation = it },
                    label = { Text("To") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                HorizontalDivider()

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = morningDone,
                        onCheckedChange = { morningDone = it }
                    )
                    Text("Morning completed")
                }

                OutlinedTextField(
                    value = morningFare,
                    onValueChange = {
                        morningFare = it
                        error = ""
                    },
                    label = { Text("Morning fare (Rs.)") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = returnDone,
                        onCheckedChange = { returnDone = it }
                    )
                    Text("Return completed")
                }

                OutlinedTextField(
                    value = returnFare,
                    onValueChange = {
                        returnFare = it
                        error = ""
                    },
                    label = { Text("Return fare (Rs.)") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
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
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = TripPurple
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save Trip")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TripPurple)
            }
        }
    )

    if (showChildPicker) {
        AlertDialog(
            onDismissRequest = {
                showChildPicker = false
            },
            shape = RoundedCornerShape(22.dp),
            title = {
                Text("Select Child", fontWeight = FontWeight.Bold)
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

                                if (!isSameChild) {
                                    fromLocation = child.fromLocation
                                    toLocation = child.toLocation
                                    morningFare = child.morningFare.toString()
                                    returnFare = child.returnFare.toString()
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
                    Text("Close", color = TripPurple)
                }
            }
        )
    }
}

private fun isValidTripDate(value: String): Boolean {

    if (!value.matches(Regex("^\\d{4}-\\d{2}-\\d{2}$"))) {
        return false
    }

    return try {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        format.isLenient = false
        format.parse(value) != null
    } catch (_: Exception) {
        false
    }
}
