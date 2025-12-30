package com.example.rumahistimewa.ui.admin

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import com.example.rumahistimewa.ui.theme.RedPrimary
import com.example.rumahistimewa.data.remote.RetrofitClient
import kotlinx.coroutines.launch

@Composable
fun AdminVillaFormScreen(
    villaId: String? = null,
    onNavigate: (String) -> Unit = {},
    onLogout: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    var villaName by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var selectedImages by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var isSubmitting by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    // Logic to fetch existing data if villaId is present
    LaunchedEffect(villaId) {
        if (villaId != null) {
            isLoading = true
            try {
                val response = RetrofitClient.api.getVillaDetail(villaId)
                if (response.isSuccessful) {
                    val villa = response.body()
                    if (villa != null) {
                        villaName = villa.name
                        location = villa.location
                        description = villa.description
                        price = villa.price.toString()
                        // Loading existing photos is tricky with URIs. 
                        // For now we just allow adding NEW photos. 
                        // To properly show existing photos we would need to display URLs.
                        // Ideally we would have a separate list for existing URLs and new URIs.
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Failed to load villa details", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
    }

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

    AdminLayout(
        title = if (villaId != null) "Edit Villa" else "Add New Villa",
        onNavigate = onNavigate,
        onLogout = onLogout
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (isLoading) {
                 CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = RedPrimary)
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Back")
                        }
                    }

                    // Image Upload Section (Simply adding new images logic)
                    item {
                        Text("Photos (New Uploads)", style = MaterialTheme.typography.labelLarge)
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
                                    coil.compose.AsyncImage(
                                        model = uri,
                                        contentDescription = "Selected Image",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                    )
                                }
                            }
                        }
                    }

                    // Form Fields
                    item {
                        OutlinedTextField(
                            value = villaName,
                            onValueChange = { villaName = it },
                            label = { Text("Villa Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = location,
                            onValueChange = { location = it },
                            label = { Text("Location") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Description") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = price,
                            onValueChange = { price = it },
                            label = { Text("Price") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }

                    item {
                        Button(
                            onClick = {
                                if (villaId != null) {
                                    // Edit Mode
                                    if (villaName.isNotBlank() && location.isNotBlank()) {
                                        isSubmitting = true
                                        scope.launch {
                                            try {
                                                val namePart = createPartFromString(villaName)
                                                val locPart = createPartFromString(location)
                                                val descPart = createPartFromString(description)
                                                val pricePart = createPartFromString(price)
                                                
                                                // Handle photos if needed
                                                val photoParts = if (selectedImages.isNotEmpty()) {
                                                    selectedImages.map { prepareFilePart("photos", it) }
                                                } else null
                                                
                                                val response = RetrofitClient.api.updateVilla(
                                                    id = villaId,
                                                    name = namePart,
                                                    location = locPart,
                                                    price = pricePart,
                                                    description = descPart,
                                                    photos = photoParts
                                                )
                                                
                                                if (response.isSuccessful) {
                                                    Toast.makeText(context, "Villa Updated", Toast.LENGTH_SHORT).show()
                                                    onNavigate("admin_villas")
                                                } else {
                                                    Toast.makeText(context, "Update Failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                                                }
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                            } finally {
                                                isSubmitting = false
                                            }
                                        }
                                    }
                                } else {
                                    // Create Mode - using owner/villas endpoint as requested
                                    if (villaName.isNotBlank() && location.isNotBlank() && price.isNotBlank()) {
                                        isSubmitting = true
                                        scope.launch {
                                            try {
                                                val namePart = createPartFromString(villaName)
                                                val locPart = createPartFromString(location)
                                                val descPart = createPartFromString(description)
                                                val pricePart = createPartFromString(price)
                                                
                                                // Handle photos for create (required)
                                                val photoParts = selectedImages.map { prepareFilePart("photos", it) }
                                                
                                                if (photoParts.isEmpty()) {
                                                    Toast.makeText(context, "Please select at least one photo", Toast.LENGTH_SHORT).show()
                                                    isSubmitting = false
                                                    return@launch
                                                }

                                                val response = RetrofitClient.api.createVilla(
                                                    name = namePart,
                                                    location = locPart,
                                                    price = pricePart,
                                                    description = descPart,
                                                    photos = photoParts
                                                )
                                                
                                                if (response.isSuccessful) {
                                                    Toast.makeText(context, "Villa Created Data Saved", Toast.LENGTH_SHORT).show()
                                                    onNavigate("admin_villas")
                                                } else {
                                                    Toast.makeText(context, "Create Failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                                                }
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                            } finally {
                                                isSubmitting = false
                                            }
                                        }
                                    } else {
                                         Toast.makeText(context, "Please complete fields", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            enabled = !isSubmitting,
                            colors = ButtonDefaults.buttonColors(containerColor = RedPrimary),
                            modifier = Modifier.fillMaxWidth().height(50.dp)
                        ) {
                            if (isSubmitting) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Text(if (villaId != null) "Update Villa" else "Create Villa")
                            }
                        }
                    }
                    
                    item {
                         Spacer(modifier = Modifier.height(50.dp))
                    }
                }
            }
        }
    }
}
