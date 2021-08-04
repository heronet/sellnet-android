package com.heronet.sellnet.ui.screen.authentication

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.navigation.NavController
import com.heronet.sellnet.ui.OutlinedErrorTextField
import com.heronet.sellnet.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    Surface(color = MaterialTheme.colors.background) {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var passwordVisibility by remember { mutableStateOf(false) }
        val isLoading by remember { authViewModel.isLoading }
        val authErrorText by remember { authViewModel.errorText }
        val focusManager = LocalFocusManager.current

        Column(modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)) {
            OutlinedErrorTextField(
                value = email,
                placeholder = { Text(text =  "Your Email") },
                label = { Text(text = "Email") },
                onValueChange = {email = it},
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
                isError = authErrorText.contains("Email"),
                errorText = if (authErrorText.contains("Email")) authErrorText else null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                keyboardActions = KeyboardActions(onDone = { focusManager.moveFocus(FocusDirection.Down) })
            )
            OutlinedErrorTextField(
                value = password,
                placeholder = { Text(text =  "Your Password") },
                visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                label = { Text(text = "Password") },
                onValueChange = {password = it},
                enabled = !isLoading,
                isError = authErrorText.contains("Password"),
                errorText = if (authErrorText.contains("Password")) authErrorText else null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                trailingIcon = {
                    val image = if (passwordVisibility) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisibility = !passwordVisibility }
                    ) {
                        Icon(imageVector  = image, null)
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.padding(4.dp))
            Row {
                Button(
                    onClick = {
                        if(email.isNotBlank() && password.isNotBlank())
                            authViewModel.loginUser(email, password)
                    },
                    enabled = !isLoading
                ) {
                    if (!isLoading)
                        Text(text = "Login")
                    else
                        Text(text = "Please Wait...")
                }
                Button(
                    onClick = {
                        navController.navigate("register")
                    },
                    enabled = !isLoading,
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Text(text = "Or, Register")
                }
            }
        }
    }


}