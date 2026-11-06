
package com.schooltransport.faremanager

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.util.Calendar
import java.util.Locale

val PremiumRoyalPurple = Color(0xFF32105F)
val PremiumDeepPurple = Color(0xFF241044)
val PremiumPurple = Color(0xFF55258A)
val PremiumGold = Color(0xFFFFD45A)
val PremiumCream = Color(0xFFFFF8E8)

@Composable
fun PremiumAppHeader(
    onProfileClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                10.dp,
                RoundedCornerShape(25.dp),
                ambientColor = PremiumDeepPurple.copy(alpha = 0.25f),
                spotColor = PremiumDeepPurple.copy(alpha = 0.35f)
            ),
        shape = RoundedCornerShape(25.dp),
        color = PremiumRoyalPurple
    ) {
        Row(
            modifier = Modifier
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            PremiumDeepPurple,
                            PremiumRoyalPurple,
                            PremiumPurple
                        )
                    )
                )
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 21.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    "School Transport",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    "Fare Manager",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PremiumGold
                )
            }

            Box(
                modifier = Modifier
                    .size(58.dp)
                    .shadow(5.dp, CircleShape)
                    .clip(CircleShape)
                    .background(PremiumCream)
                    .border(2.dp, PremiumGold, CircleShape)
                    .clickable(onClick = onProfileClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.AccountCircle,
                    contentDescription = "Open Profile",
                    modifier = Modifier.size(39.dp),
                    tint = PremiumRoyalPurple
                )
            }
        }
    }
}

@Composable
fun PremiumMonthSelector(
    month: Int,
    year: Int,
    onMonthChange: (Int, Int) -> Unit
) {
    var showPicker by remember { mutableStateOf(false) }

    val monthNames = remember {
        listOf(
            "January", "February", "March",
            "April", "May", "June",
            "July", "August", "September",
            "October", "November", "December"
        )
    }

    val monthLabel = "${monthNames[month]} $year"

    fun moveMonth(direction: Int) {
        val calendar = Calendar.getInstance().apply {
            set(year, month, 1)
            add(Calendar.MONTH, direction)
        }
        onMonthChange(
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.YEAR)
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.5.dp,
                PremiumGold,
                RoundedCornerShape(22.dp)
            ),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = PremiumRoyalPurple
        ),
        elevation = CardDefaults.cardElevation(5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(43.dp)
                    .shadow(3.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable { moveMonth(-1) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.ChevronLeft,
                    contentDescription = "Previous month",
                    tint = PremiumRoyalPurple,
                    modifier = Modifier.size(29.dp)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { showPicker = true }
                    .padding(horizontal = 5.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Icon(
                    Icons.Default.CalendarMonth,
                    contentDescription = "Choose month",
                    tint = PremiumGold,
                    modifier = Modifier.size(27.dp)
                )
                Text(
                    monthLabel,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            Box(
                modifier = Modifier
                    .size(43.dp)
                    .shadow(3.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable { moveMonth(1) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = "Next month",
                    tint = PremiumRoyalPurple,
                    modifier = Modifier.size(29.dp)
                )
            }
        }
    }

    if (showPicker) {
        var pickerYear by remember(year) { mutableIntStateOf(year) }

        AlertDialog(
            onDismissRequest = { showPicker = false },
            containerColor = PremiumDeepPurple,
            shape = RoundedCornerShape(25.dp),
            title = {
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(
                        "Select Month",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        pickerYear.toString(),
                        color = PremiumGold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { pickerYear-- }) {
                            Icon(
                                Icons.Default.ChevronLeft,
                                contentDescription = "Previous year",
                                tint = PremiumGold
                            )
                            Text("Year", color = PremiumGold)
                        }

                        TextButton(onClick = { pickerYear++ }) {
                            Text("Year", color = PremiumGold)
                            Icon(
                                Icons.Default.ChevronRight,
                                contentDescription = "Next year",
                                tint = PremiumGold
                            )
                        }
                    }

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(9.dp),
                        userScrollEnabled = false
                    ) {
                        itemsIndexed(monthNames) { index, name ->
                            val selected = index == month && pickerYear == year

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (selected) PremiumGold else PremiumPurple
                                    )
                                    .border(
                                        1.dp,
                                        PremiumGold.copy(
                                            alpha = if (selected) 1f else 0.55f
                                        ),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        onMonthChange(index, pickerYear)
                                        showPicker = false
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    name.take(3),
                                    color = if (selected) {
                                        PremiumDeepPurple
                                    } else {
                                        Color.White
                                    },
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showPicker = false }) {
                    Text("Close", color = PremiumGold)
                }
            }
        )
    }
}
