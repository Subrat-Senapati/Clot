package com.example.internshipminiproject.Screen.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.internshipminiproject.data.model.Address
import com.example.internshipminiproject.viewmodel.AddressViewModel


@Composable
fun EditAddressScreen(
    navController: NavController,
    addressId: String,
    addressViewModel: AddressViewModel = hiltViewModel()
) {
    val addresses by addressViewModel.addresses.observeAsState()
    val selectedAddress = addresses?.find { it.addressId == addressId }

    LaunchedEffect(Unit) {
        addressViewModel.fetchUserAddresses()
    }

    if (selectedAddress != null) {
        EditAddressForm(
            navController = navController,
            address = selectedAddress
        )
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAddressForm(
    navController: NavController,
    address: Address,
    addressViewModel: AddressViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf(address.name) }
    var streetAddress by remember { mutableStateOf(address.street) }
    var city by remember { mutableStateOf(address.city) }
    var state by remember { mutableStateOf(address.state) }
    var zipCode by remember { mutableStateOf(address.zipCode) }
    var phoneNumber by remember { mutableStateOf(address.phoneNumber) }

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Edit Address",
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
            OutlinedTextField(value = streetAddress, onValueChange = { streetAddress = it }, label = { Text("Street Address") })
            OutlinedTextField(value = city, onValueChange = { city = it }, label = { Text("City") })

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = state,
                    onValueChange = { state = it },
                    label = { Text("State") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = zipCode,
                    onValueChange = {
                        if (it.length <= 6) zipCode = it
                    },
                    label = { Text("Zip Code") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            OutlinedTextField(value = phoneNumber, onValueChange = { phoneNumber = it }, label = { Text("Phone Number") })

            Button(
                onClick = {
                    val updatedAddress = address.copy(
                        name = name,
                        street = streetAddress,
                        city = city,
                        state = state,
                        zipCode = zipCode,
                        phoneNumber = phoneNumber
                    )
                    addressViewModel.updateAddress(
                        updatedAddress,
                        onSuccess = {
                            Toast.makeText(context, "Address updated", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        },
                        onFailure = {
                            Toast.makeText(context, "Failed to update", Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank() && streetAddress.isNotBlank() && city.isNotBlank()
                        && state.isNotBlank() && zipCode.length >= 4
            ) {
                Text("Update Address")
            }
        }
    }
}
