package com.example.internshipminiproject.Screen.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.internshipminiproject.Screen.auth.AgeDropdown
import com.example.internshipminiproject.Screen.auth.GenderSegmentedButton
import com.example.internshipminiproject.data.datastore.UserPreference
import com.example.internshipminiproject.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfile(navController: NavController, userViewModel: UserViewModel = hiltViewModel()) {
    val userData by userViewModel.user.observeAsState()
    val context = LocalContext.current
    val preference = remember { UserPreference(context) }

    var firstname by remember { mutableStateOf("") }
    var lastname by remember { mutableStateOf("") }
    var selectedGender = remember { mutableStateOf("Men") }
    var selectedAgeRange = remember { mutableStateOf<String?>(null) }

    // Populate state from userData once fetched
    LaunchedEffect(userData) {
        userData?.let {
            firstname = it.firstname ?: ""
            lastname = it.lastname ?: ""
            selectedGender.value = it.gender ?: "Men"
            selectedAgeRange.value = it.agerange
        }
    }

    // Fetch user ID and user data
    LaunchedEffect(Unit) {
        val userId = preference.getUserId()
        if (!userId.isNullOrBlank()) {
            userViewModel.fetchCurrentUserData(userId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(end = 56.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Edit Profile",
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 1
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.padding(horizontal = 10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondary)
                                .padding(6.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSecondary,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp, 40.dp)
                .background(MaterialTheme.colorScheme.background)
        ) {
            OutlinedTextField(
                value = firstname,
                onValueChange = { firstname = it },
                label = { Text("Firstname", color = MaterialTheme.colorScheme.onBackground) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(4.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    unfocusedContainerColor = MaterialTheme.colorScheme.secondary,
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = lastname,
                onValueChange = { lastname = it },
                label = { Text("Lastname", color = MaterialTheme.colorScheme.onBackground) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(4.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    unfocusedContainerColor = MaterialTheme.colorScheme.secondary,
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text("Gender", fontSize = 18.sp, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(10.dp))
            GenderSegmentedButton(selectedGender)

            Spacer(modifier = Modifier.height(15.dp))

            Text("How old are you?", fontSize = 18.sp, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(20.dp))
            AgeDropdown(selectedCategory = selectedAgeRange)

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    userViewModel.updateUserProfile(
                        firstname = firstname,
                        lastname = lastname,
                        gender = selectedGender.value,
                        ageRange = selectedAgeRange.value.toString(),
                        onSuccess = {
                            Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        },
                        onFailure = {
                            Toast.makeText(context, "Failed to update", Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
            ) {
                Text("Save Profile", color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.padding(8.dp))
            }
        }
    }
}
