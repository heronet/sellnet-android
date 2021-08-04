package com.heronet.sellnet.ui.screen.authentication

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.heronet.sellnet.ui.OutlinedErrorTextField
import com.heronet.sellnet.viewmodel.AuthViewModel


@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel
) {
    authViewModel.getLocations()
    Surface(color = MaterialTheme.colors.background) {
        var email by remember { mutableStateOf("") }
        var name by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var city by remember { mutableStateOf("") }
        var cityDropdownEnabled by remember { mutableStateOf(false) }
        var division by remember { mutableStateOf("") }
        var divisionDropdownEnabled by remember { mutableStateOf(false) }
        var password by remember { mutableStateOf("") }
        var passwordVisibility by remember { mutableStateOf(false) }
        val isLoading by remember { authViewModel.isLoading }
        val isLocationsLoading by remember { authViewModel.isLocationsLoading }
        val locations by remember { authViewModel.locations }
        val registerErrorMessages by remember { authViewModel.registerErrorMessages }
        var canSubmit by remember { mutableStateOf(false) }
        val scrollState = rememberScrollState()
        val focusManager = LocalFocusManager.current

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .verticalScroll(scrollState)
        ) {
            OutlinedErrorTextField(
                value = name,
                placeholder = { Text(text = "Your Name") },
                label = { Text(text = "Name") },
                onValueChange = { name = it },
                enabled = !isLoading,
                singleLine = true,
                keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Down) }),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedErrorTextField(
                value = email,
                placeholder = { Text(text = "Your Email") },
                label = { Text(text = "Email") },
                onValueChange = { email = it },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
                isError = registerErrorMessages.containsKey("Email"),
                singleLine = true,
                errorText = registerErrorMessages["Email"],
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Down) })
            )
            OutlinedErrorTextField(
                value = phone,
                placeholder = { Text(text = "Your Phone Number") },
                label = { Text(text = "Phone") },
                onValueChange = { value ->
                    if (value.length <= 11) {
                        phone = value.filter { it.isDigit() }
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                enabled = !isLoading,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = registerErrorMessages.containsKey("Phone"),
                errorText = registerErrorMessages["Phone"],
                keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Down) })
            )
            OutlinedErrorTextField(
                value = password,
                placeholder = { Text(text = "Your Password") },
                visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                label = { Text(text = "Password") },
                onValueChange = { password = it },
                enabled = !isLoading,
                singleLine = true,
                isError = registerErrorMessages.containsKey("Password"),
                errorText = registerErrorMessages["Password"],
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                trailingIcon = {
                    val image =
                        if (passwordVisibility) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisibility = !passwordVisibility }
                    ) {
                        Icon(imageVector = image, null)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            if (city.isNotBlank()) {
                OutlinedErrorTextField(
                    value = city,
                    label = { Text(text = "City") },
                    onValueChange = { },
                    enabled = false,
                    singleLine = true,
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            DropdownMenu(
                expanded = cityDropdownEnabled,
                onDismissRequest = { cityDropdownEnabled = false }) {
                locations!!.cities.forEach {
                    DropdownMenuItem(onClick = { city = it; cityDropdownEnabled = false }) {
                        Text(text = it)
                    }
                }
            }
            if (division.isNotBlank()) {
                OutlinedErrorTextField(
                    value = division,
                    label = { Text(text = "Division") },
                    onValueChange = { },
                    enabled = false,
                    singleLine = true,
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            DropdownMenu(
                expanded = divisionDropdownEnabled,
                onDismissRequest = { divisionDropdownEnabled = false }) {
                locations!!.divisions.forEach {
                    DropdownMenuItem(onClick = { division = it; divisionDropdownEnabled = false }) {
                        Text(text = it)
                    }
                }
            }
            Spacer(modifier = Modifier.padding(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { cityDropdownEnabled = !cityDropdownEnabled },
                    enabled = !isLocationsLoading && !isLoading,
                    modifier = Modifier.fillMaxWidth(0.5f)
                ) {
                    Text(text = "Select City")
                }

                Button(
                    onClick = { divisionDropdownEnabled = !divisionDropdownEnabled },
                    enabled = !isLocationsLoading && !isLoading,
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .fillMaxWidth()
                ) {
                    Text(text = "Select Division")
                }
            }
            Spacer(modifier = Modifier.padding(4.dp))
            Button(
                onClick = {
                    if (canSubmit) {
                        authViewModel.registerUser(name, email, password, phone, city, division)
                    }
                },
                enabled = !isLoading && !isLocationsLoading && canSubmit,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (!isLoading)
                    Text(text = "Register")
                else
                    Text(text = "Please Wait...")
            }
        }
        if (email.isNotBlank() && password.isNotBlank() && name.isNotBlank() && phone.isNotBlank() && city.isNotBlank() && division.isNotBlank())
            canSubmit = true
    }
}