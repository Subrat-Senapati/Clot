package com.example.internshipminiproject.Screen.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.internshipminiproject.viewmodel.UserViewModel

@Composable
fun OnboardingScreen(
    id: String,
    navController: NavController,
    userViewModel: UserViewModel = viewModel(),
) {

    var selectedGender = remember { mutableStateOf("Men") }
    var selectedAgeRange = remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    Scaffold(
        bottomBar = {
            Button(
                onClick = {
                    userViewModel.updateUserAdditionalData(
                        userId = id,
                        gender = selectedGender.value,
                        agerange = selectedAgeRange.value ?: "18 - 35"
                    ) { success, error ->
                        if (success) {
                            navController.navigate("HomepageScreen")
                        } else {
                            Toast.makeText(
                                context,
                                error ?: "Error saving data",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp, 50.dp),
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = "Finish",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp, 110.dp)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Text(
                text = "Tell us About yourself",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(50.dp))

            Text(
                text = "Who do you shop for?",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(20.dp))

            GenderSegmentedButton(selectedGender)

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "How old are you?",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(20.dp))

            AgeDropdown(selectedAgeRange)
        }
    }
}

@Composable
fun GenderSegmentedButton(selected: MutableState<String>) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick = { selected.value = "Men" },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selected.value == "Men") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
            ),
            modifier = Modifier.width(150.dp)
        ) {
            Text(
                text = "Men",
                color = if (selected.value == "Men") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier.padding(0.dp, 5.dp)
            )
        }

        Button(
            onClick = { selected.value = "Women" },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selected.value == "Women") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
            ),
            modifier = Modifier.width(150.dp)
        ) {
            Text(
                text = "Women",
                color = if (selected.value == "Women") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier.padding(0.dp, 5.dp)
            )
        }
    }
}

@Composable
fun AgeDropdown(selectedCategory: MutableState<String?>) {
    var expanded by remember { mutableStateOf(false) }

    val categories = listOf(
        "Less than 18",
        "18 - 35",
        "35 - 60",
        "60+"
    )

    Box {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedCategory.value ?: "Age Range",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colorScheme.onSecondary
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown Arrow",
                    tint = MaterialTheme.colorScheme.onSecondary
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category, color = MaterialTheme.colorScheme.onSecondary) },
                    onClick = {
                        selectedCategory.value = category
                        expanded = false
                    }
                )
            }
        }
    }
}