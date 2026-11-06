
package com.schooltransport.faremanager

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun DashboardScreen(onLogout: () -> Unit) {

    val store = remember { TransportStore() }

    var selectedTab by remember { mutableIntStateOf(0) }
    var showFarePlans by remember { mutableStateOf(false) }
    var showTrips by remember { mutableStateOf(false) }
    var showProfile by remember { mutableStateOf(false) }

    val titles = listOf("Home", "Children", "Calendar", "Payments", "Settings")

    val icons = listOf(
        Icons.Default.Home,
        Icons.Default.People,
        Icons.Default.CalendarMonth,
        Icons.Default.Payments,
        Icons.Default.Settings
    )

    val isSubPage = showFarePlans || showTrips || showProfile
    val isMainPage = !isSubPage

    val currentTitle = when {
        showFarePlans -> "Payment Configurations"
        showTrips -> "Trips"
        else -> "Profile"
    }

    val currentSubtitle = when {
        showFarePlans -> "Manage your transport fare plans"
        showTrips -> "Manage your transport trips"
        else -> "Manage your account details"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {

        if (isMainPage && selectedTab != 0) {
            Column(
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 10.dp,
                    bottom = 8.dp
                )
            ) {
                PremiumAppHeader(
                    onProfileClick = { showProfile = true }
                )
            }
        }

        if (isSubPage) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            showFarePlans = false
                            showTrips = false
                            showProfile = false
                        }
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }

                    Column {
                        Text(
                            currentTitle,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            currentSubtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when {
                showFarePlans -> FarePlansScreen(store)
                showTrips -> TripsScreen(store)
                showProfile -> ProfileScreen()

                else -> when (selectedTab) {
                    0 -> HomeScreen(
                        store = store,
                        onOpenFarePlans = { showFarePlans = true },
                        onOpenTrips = { showTrips = true },
                        onOpenProfile = { showProfile = true }
                    )

                    1 -> ChildrenScreen(store)
                    2 -> CalendarScreen(store)
                    3 -> PaymentsScreen(store)

                    4 -> SettingsScreen(
                        onLogout = {
                            showFarePlans = false
                            showTrips = false
                            showProfile = false
                            selectedTab = 0
                            onLogout()
                        },
                        onOpenFarePlans = { showFarePlans = true }
                    )
                }
            }
        }

        if (!isSubPage) {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                tonalElevation = 8.dp
            ) {
                titles.forEachIndexed { index, title ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = {
                            Icon(
                                imageVector = icons[index],
                                contentDescription = title
                            )
                        },
                        label = {
                            Text(
                                title,
                                fontWeight = if (selectedTab == index) {
                                    FontWeight.Bold
                                } else {
                                    FontWeight.Medium
                                }
                            )
                        },
                        alwaysShowLabel = true,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    }
}
