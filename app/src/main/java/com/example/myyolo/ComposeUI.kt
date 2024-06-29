import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

@Composable
fun DetectionScreen(viewModel: DetectionViewModel = viewModel()) {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var resultImageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = { launcher.launch("image/*") }) {
            Text("Select Image")
        }

        Spacer(modifier = Modifier.height(16.dp))

        imageUri?.let { uri ->
            Image(
                painter = rememberImagePainter(data = uri),
                contentDescription = "Selected Image",
                modifier = Modifier.size(200.dp)
            )

            Button(onClick = {
                val inputStream = context.contentResolver.openInputStream(uri)
                if (inputStream != null) {
                    val file = createTempFile(context)
                    saveInputStreamToFile(inputStream, file)
                    viewModel.detectImage(file) { responseBody ->
                        if (responseBody != null) {
                            val resultFile = saveResponseBodyToFile(responseBody, context)
                            resultImageUri = Uri.fromFile(resultFile)
                        } else {
                            Toast.makeText(context, "Error detecting image", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Error opening image", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text("Detect")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        resultImageUri?.let { uri ->
            Image(
                painter = rememberImagePainter(data = uri),
                contentDescription = "Result Image",
                modifier = Modifier.size(200.dp)
            )
        }
    }
}

// Function to create a temporary file
fun createTempFile(context: android.content.Context): File {
    return File.createTempFile("temp_image", ".jpg", context.cacheDir)
}

// Function to save input stream to a file
fun saveInputStreamToFile(inputStream: InputStream, file: File) {
    val outputStream = FileOutputStream(file)
    inputStream.use { input ->
        outputStream.use { output ->
            input.copyTo(output)
        }
    }
}

// Function to save response body to a file
fun saveResponseBodyToFile(responseBody: okhttp3.ResponseBody, context: android.content.Context): File {
    val file = File.createTempFile("predicted_image", ".jpg", context.cacheDir)
    val outputStream = FileOutputStream(file)
    outputStream.use { output ->
        responseBody.byteStream().use { input ->
            input.copyTo(output)
        }
    }
    return file
}