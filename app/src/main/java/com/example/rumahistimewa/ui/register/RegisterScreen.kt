package com.example.rumahistimewa.ui.register

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onRegisterSuccess: () -> Unit,
    onBackToLogin: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("customer") }

    val state by viewModel.registerState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Register",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nama Lengkap") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text("Daftar sebagai:")

        Row {
            RadioButton(
                selected = role == "customer",
                onClick = { role = "customer" }
            )
            Text("Customer")

            Spacer(modifier = Modifier.width(16.dp))

            RadioButton(
                selected = role == "owner",
                onClick = { role = "owner" }
            )
            Text("Owner")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.register(name, email, password, role)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onBackToLogin) {
            Text("Sudah punya akun? Login")
        }

        state?.let {
            if (it == "success") {
                onRegisterSuccess()
            }
        }
    }
}
