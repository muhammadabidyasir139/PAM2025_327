package com.example.rumahistimewa.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onSuccess: (String) -> Unit,
    onRegisterClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val state by viewModel.loginState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text("Login", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.login(email, password) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }

        LaunchedEffect(state) {
            state?.let {
                if (it != "error") {
                    onSuccess(it)
                }
            }
        }
        TextButton(
            onClick = { onRegisterClick() }
        ) {
            Text("Belum punya akun? Register")
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        TextButton(
            onClick = { onSuccess("admin") }
        ) {
             Text("Login as Admin (Test)", color = MaterialTheme.colorScheme.secondary)
        }

    }
}
