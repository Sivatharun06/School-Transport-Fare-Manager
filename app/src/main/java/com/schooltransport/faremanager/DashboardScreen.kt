
package com.schooltransport.faremanager

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DashboardScreen(onLogout: () -> Unit) {

    val store = remember { TransportStore() }

    var selectedTab by remember {
        mutableIntStateOf(0)
    }

    var showFarePlans by remember {
        mutableStateOf(false)
    }

    val titles = listOf(
        "Home",
        "Children",
        "Calendar",
        "Trips",
        "Payments",
        "Settings"
    )

    val icons = listOf(
        Icons.Default.Home,
        Icons.Default.People,
        Icons.Default.CalendarMonth,
        Icons.Default.DirectionsCar,
        Icons.Default.Payments,
        Icons.Default.Settings
    )

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        // TOP HEADER

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 18.dp,
                    vertical = 16.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (showFarePlans) {
                IconButton(
                    onClick = {
                        showFarePlans = false
                    }
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = if (showFarePlans) {
                        "Payment Configurations"
                    } else {
                        "School Transport"
                    },
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    text = if (showFarePlans) {
                        "Manage your transport fare plans"
                    } else {
                        "Fare Manager"
                    },
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        HorizontalDivider()

        // MAIN CONTENT

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {

            if (showFarePlans) {

                FarePlansScreen(store)

            } else {

                when (selectedTab) {

                    0 -> HomeScreen(
                        store = store,
                        onOpenFarePlans = {
                            showFarePlans = true
                        }
                    )

                    1 -> ChildrenScreen(store)

                    2 -> CalendarScreen(store)

                    3 -> TripsScreen(store)

                    4 -> PaymentsScreen(store)

                    5 -> SettingsScreen(
                        onLogout = {
                            showFarePlans = false
                            selectedTab = 0
                            onLogout()
                        },
                        onOpenFarePlans = {
                            showFarePlans = true
                        }
                    )
                }
            }
        }

        // BOTTOM NAVIGATION

        if (!showFarePlans) {

            NavigationBar {

                titles.forEachIndexed { index, title ->

                    NavigationBarItem(
                        selected = selectedTab == index,

                        onClick = {
                            selectedTab = index
                        },

                        icon = {
                            Icon(
                                imageVector = icons[index],
                                contentDescription = title
                            )
                        },

                        label = {
                            Text(title)
                        },

                        alwaysShowLabel = true
                    )
                }
            }
        }
    }
}
