package com.example.myyolo

import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface DetectionService {
    @Multipart
    @POST("/detect")
    suspend fun detectImage(@Part file: MultipartBody.Part): retrofit2.Response<okhttp3.ResponseBody>
}