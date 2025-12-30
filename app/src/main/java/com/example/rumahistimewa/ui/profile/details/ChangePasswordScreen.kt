package com.example.rumahistimewa.ui.profile.details

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rumahistimewa.data.model.ChangePasswordRequest
import com.example.rumahistimewa.data.remote.RetrofitClient
import com.example.rumahistimewa.data.repository.ProfileRepository
import com.example.rumahistimewa.ui.profile.ProfileViewModel
import com.example.rumahistimewa.ui.theme.RedPrimary
import com.example.rumahistimewa.ui.theme.RedSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(onBackClick: () -> Unit) {
    val repository = ProfileRepository(RetrofitClient.api)
    val viewModel: ProfileViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return ProfileViewModel(repository) as T
            }
        }
    )

    val passwordChangeState by viewModel.passwordChangeState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }

    LaunchedEffect(passwordChangeState) {
        if (passwordChangeState == "Success") {
            onBackClick()
            viewModel.resetPasswordChangeState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Renew Password", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = RedPrimary)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            if (error != null) {
                Text(text = error!!, color = Color.Red, modifier = Modifier.padding(bottom = 8.dp))
            }
            if (passwordChangeState != null && passwordChangeState != "Success") {
                 Text(text = passwordChangeState!!, color = Color.Red, modifier = Modifier.padding(bottom = 8.dp))
            }


            OutlinedTextField(
                value = oldPassword,
                onValueChange = { oldPassword = it },
                label = { Text("Current Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("New Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { 
                    viewModel.changePassword(
                        ChangePasswordRequest(
                            currentPassword = oldPassword,
                            newPassword = newPassword
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = RedSecondary),
                enabled = !isLoading
            ) {
                 if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Update Password")
                }
            }
        }
    }
}
