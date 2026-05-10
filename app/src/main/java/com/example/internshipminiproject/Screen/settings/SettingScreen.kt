package com.example.internshipminiproject.Screen.settings

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.internshipminiproject.Screen.homepage.BottomMenu
import com.example.internshipminiproject.data.datastore.UserPreference
import com.example.internshipminiproject.viewmodel.AuthState
import com.example.internshipminiproject.viewmodel.AuthViewModel
import com.example.internshipminiproject.viewmodel.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel()
) {
    val authState by authViewModel.authState.observeAsState()
    val userData by userViewModel.user.observeAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Fetch user ID from DataStore and load user data
    LaunchedEffect(Unit) {
        val preference = UserPreference(context)
        val userId = preference.getUserId()
        if (!userId.isNullOrBlank()) {
            userViewModel.fetchCurrentUserData(userId)
        }
    }

    // Redirect if not authenticated
    LaunchedEffect(authState) {
        if (authState is AuthState.Unauthenticated) {
            navController.navigate("SignInScreen") {
                popUpTo("SettingsScreen") { inclusive = true }
            }
        }
    }

    val settingsItems = mapOf(
        "Address" to "AddressScreen",
        "Wishlist" to "WishlistScreen",
        "Payment" to "",
        "Help" to "",
        "Support" to ""
    )


    Scaffold(
        topBar = {
            Text(
                "Settings",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 50.dp)
                    .background(MaterialTheme.colorScheme.background),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        },
        bottomBar = { BottomMenu(navController, "SettingsScreen") }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier
                        .size(65.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondary),
                )
            }

            Spacer(Modifier.height(20.dp))

            // User Info Box
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(horizontal = 20.dp, vertical = 5.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.secondary)
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = listOfNotNull(userData?.firstname, userData?.lastname)
                            .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } },
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        userData?.email ?: "Email",
                        color = Color.Gray
                    )
                    Text(
                        text = when (userData?.gender?.lowercase()) {
                            "men" -> "Male"
                            "women" -> "Female"
                            else -> "--"
                        },
                        color = Color.Gray
                    )

                }
                Text(
                    "Edit",
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable(
                        onClick = {
                            navController.navigate("EditProfile")
                        }
                    )
                )
            }

            // Settings Items
            LazyColumn {
                items(settingsItems.entries.toList()) { (label, route) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(65.dp)
                            .padding(horizontal = 20.dp, vertical = 5.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.secondary)
                            .clickable {if(route != "") navController.navigate(route) }
                            .padding(horizontal = 20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(label, color = MaterialTheme.colorScheme.onSecondary)
                        Icon(
                            Icons.Default.KeyboardArrowRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }
            }


            // Logout Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .padding(horizontal = 20.dp, vertical = 5.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Sign Out",
                    color = Color.Red,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        scope.launch {
                            UserPreference(context).clearUserId()
                            authViewModel.logout()
                        }
                    }
                )
            }
        }
    }
}
