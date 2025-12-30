package com.example.rumahistimewa.ui.profile.submission

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.rumahistimewa.ui.theme.RedPrimary
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import android.widget.Toast
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import com.example.rumahistimewa.data.remote.RetrofitClient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VillaSubmissionScreen(
    onBackClick: () -> Unit,
    onSubmitSuccess: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var villaName by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var facilities by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var selectedImages by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var isSubmitting by remember { mutableStateOf(false) }

    // Image Picker Launcher
    val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris -> selectedImages = uris }
    )

    fun createPartFromString(value: String): RequestBody {
        val mediaType = "text/plain".toMediaTypeOrNull()
        return RequestBody.create(mediaType, value)
    }

    fun prepareFilePart(partName: String, fileUri: Uri): MultipartBody.Part {
        val resolver = context.contentResolver
        val type = resolver.getType(fileUri) ?: "image/*"
        val bytes = resolver.openInputStream(fileUri)?.use { it.readBytes() } ?: ByteArray(0)
        val mediaType = type.toMediaTypeOrNull()
        val requestFile = RequestBody.create(mediaType, bytes)
        return MultipartBody.Part.createFormData(partName, "image_${System.currentTimeMillis()}.jpg", requestFile)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Villa Submission", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = RedPrimary
                )
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    if (villaName.isNotBlank() && location.isNotBlank() && price.isNotBlank()) {
                        isSubmitting = true
                        scope.launch {
                            try {
                                val namePart = createPartFromString(villaName)
                                val locationPart = createPartFromString(location)
                                val pricePart = createPartFromString(price)
                                // Facilities might need to be part of description or separate, API definition had: name, location, price, description, photos.
                                // I will append facilities to description for now as API didn't list it explicitly, or just ignore.
                                // Actually, I'll append it to description: "Description... \nFacilities: ..."
                                val finalDesc = if (facilities.isNotBlank()) "$description\nFacilities: $facilities" else description
                                val descPart = createPartFromString(finalDesc)

                                val photoParts = selectedImages.map { uri ->
                                    prepareFilePart("photos", uri)
                                }

                                val response = RetrofitClient.api.createVilla(
                                    name = namePart,
                                    location = locationPart,
                                    price = pricePart,
                                    description = descPart,
                                    photos = photoParts
                                )

                                if (response.isSuccessful) {
                                    Toast.makeText(context, "Villa submitted successfully!", Toast.LENGTH_SHORT).show()
                                    onSubmitSuccess()
                                } else {
                                    Toast.makeText(context, "Submission failed: ${response.code()} - ${response.errorBody()?.string()}", Toast.LENGTH_LONG).show()
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                            } finally {
                                isSubmitting = false
                            }
                        }
                    } else {
                        Toast.makeText(context, "Please fill required fields (Villa Name, Location, Price)", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = !isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RedPrimary)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text("Submit Application")
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Submit your villa application", style = MaterialTheme.typography.titleMedium)
            }

            // Image Upload Section
            item {
                Text("Villa Photos", style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Button(
                            onClick = {
                                multiplePhotoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier.size(100.dp),
                            shape = MaterialTheme.shapes.medium,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add Photos", tint = Color.Black)
                        }
                    }
                    items(selectedImages) { uri ->
                        Card(
                            modifier = Modifier.size(100.dp),
                            shape = MaterialTheme.shapes.medium,
                            colors = CardDefaults.cardColors(containerColor = Color.LightGray)
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Img", color = Color.Black)
                            }
                        }
                    }
                }
                if (selectedImages.isNotEmpty()) {
                    Text("${selectedImages.size} images selected", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }

            // Form Fields
            item {
                OutlinedTextField(
                    value = villaName,
                    onValueChange = { villaName = it },
                    label = { Text("Villa Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            item {
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            item {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Villa Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }

            item {
                OutlinedTextField(
                    value = facilities,
                    onValueChange = { facilities = it },
                    label = { Text("Facilities (comma separated)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false
                )
            }

            item {
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price per Night (IDR)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }

            // Spacer to avoid button overlap if list is long
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
