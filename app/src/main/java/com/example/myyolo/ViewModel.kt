import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myyolo.RetrofitClient
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class DetectionViewModel : ViewModel() {
    fun detectImage(file: File, onResult: (result: okhttp3.ResponseBody?) -> Unit) {
        viewModelScope.launch {
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
            val response = RetrofitClient.detectionService.detectImage(body)
            if (response.isSuccessful) {
                onResult(response.body())
            } else {
                onResult(null)
            }
        }
    }
}