
package com.schooltransport.faremanager

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private val DashboardPurple = Color(0xFF241044)
private val DashboardGold = Color(0xFFFFC857)

private data class DashboardTab(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun DashboardScreen(onLogout: () -> Unit) {

    val store = remember { TransportStore() }
    var selectedTab by remember { mutableIntStateOf(0) }

    val tabs = listOf(
        DashboardTab("Home", Icons.Default.Home),
        DashboardTab("Children", Icons.Default.People),
        DashboardTab("Calendar", Icons.Default.CalendarMonth),
        DashboardTab("Trips", Icons.Default.DirectionsCar),
        DashboardTab("Payments", Icons.Default.Payments),
        DashboardTab("Settings", Icons.Default.Settings)
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = DashboardPurple,
                contentColor = Color.White
            ) {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title
                            )
                        },
                        label = {
                            Text(tab.title)
                        },
                        alwaysShowLabel = true,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DashboardPurple,
                            selectedTextColor = DashboardGold,
                            indicatorColor = DashboardGold,
                            unselectedIconColor = Color.White.copy(alpha = 0.7f),
                            unselectedTextColor = Color.White.copy(alpha = 0.7f)
                        )
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DashboardPurple)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        "School Transport",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Fare Manager",
                        color = DashboardGold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Text(
                    tabs[selectedTab].title,
                    color = DashboardGold,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                when (selectedTab) {
                    0 -> HomeScreen(store)
                    1 -> ChildrenScreen(store)
                    2 -> CalendarScreen(store)
                    3 -> TripsScreen(store)
                    4 -> PaymentsScreen(store)
                    5 -> SettingsScreen(onLogout)
                }
            }
        }
    }
}
