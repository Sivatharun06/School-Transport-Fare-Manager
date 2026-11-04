
package com.schooltransport.faremanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.schooltransport.faremanager.ui.theme.SchoolTransportTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SchoolTransportTheme {
                SchoolTransportApp()
            }
        }
    }
}

private val DeepPurple = Color(0xFF241044)
private val Purple = Color(0xFF51247A)
private val Gold = Color(0xFFFFC857)

@Composable
fun SchoolTransportApp() {

    val auth = remember {
        FirebaseAuth.getInstance()
    }

    var isAuthenticated by remember {
        mutableStateOf(auth.currentUser != null)
    }

    var showLogin by remember {
        mutableStateOf(false)
    }

    DisposableEffect(auth) {

        val authListener =
            FirebaseAuth.AuthStateListener { firebaseAuth ->

                isAuthenticated =
                    firebaseAuth.currentUser != null

                if (firebaseAuth.currentUser == null) {
                    showLogin = false
                }
            }

        auth.addAuthStateListener(authListener)

        onDispose {
            auth.removeAuthStateListener(authListener)
        }
    }

    when {

        isAuthenticated -> {
            key(auth.currentUser?.uid) {
                DashboardScreen(
                    onLogout = {
                        auth.signOut()
                    }
                )
            }
        }

        showLogin -> {
            AuthScreen(
                onAuthenticated = {
                    isAuthenticated = true
                }
            )
        }

        else -> {
            SplashScreen(
                onGetStarted = {
                    showLogin = true
                }
            )
        }
    }
}

@Composable
private fun SplashScreen(
    onGetStarted: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        DeepPurple,
                        Purple,
                        DeepPurple
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {

            Text(
                "🚌",
                fontSize = 76.sp
            )

            Spacer(Modifier.height(20.dp))

            Text(
                "School Transport",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                "Fare Manager",
                color = Gold,
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(18.dp))

            Text(
                "Manage school transport fares easily",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = onGetStarted,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Gold,
                    contentColor = DeepPurple
                )
            ) {
                Text(
                    "Get Started",
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                "Version 1.0",
                color = Gold,
                fontSize = 12.sp
            )
        }
    }
}
