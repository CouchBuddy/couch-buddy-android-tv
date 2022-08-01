/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.tv.reference.repository

import android.app.Application
import com.android.tv.reference.shared.datamodel.Video
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

/**
 * VideoRepository implementation to read video data from a file saved on /res/raw
 */
class FileVideoRepository(override val application: Application) : VideoRepository {
    companion object {
        const val BASE_URL = "http://192.168.129.9/api/"
    }

    private val service = Retrofit.Builder()
        .baseUrl(FileVideoRepository.BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        .create(FileVideoRepository.LibraryService::class.java)

    suspend fun loadData() {
        _allVideos = service.getLibrary()
    }

    // Underscore name to allow lazy loading since "getAllVideos" matches the getter name otherwise
    private var _allVideos: List<Video> = emptyList()

    override fun getAllVideos(): List<Video> {
        return _allVideos
    }

    override fun getVideoById(id: String): Video? {
        return getAllVideos()
            .firstOrNull { it.id == id }
    }

    override fun getVideoByVideoUri(uri: String): Video? {
        return getAllVideos()
            .firstOrNull { it.videoUri == uri }
    }

    override fun getAllVideosFromSeries(seriesUri: String): List<Video> {
        return getAllVideos().filter { it.seriesUri == seriesUri }
    }

    private interface LibraryService {
        @GET("library")
        suspend fun getLibrary(): List<Video>
    }
}
