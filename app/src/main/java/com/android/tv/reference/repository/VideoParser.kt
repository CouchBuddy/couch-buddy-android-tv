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

import com.android.tv.reference.shared.datamodel.ApiResponse
import com.android.tv.reference.shared.datamodel.Video
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

object VideoParser {

    fun loadVideosFromJson(jsonString: String): List<Video> {
        val moshi = Moshi.Builder().build()
        val type = Types.newParameterizedType(ApiResponse::class.java, Video::class.java)
        val adapter = moshi.adapter<ApiResponse<Video>>(type)
        return adapter.fromJson(jsonString)!!.content
    }

    fun findVideoFromJson(jsonString: String, videoId: String): Video? {
        val videosList = loadVideosFromJson(jsonString)
        for (video in videosList) {
            if (video.id.toString() == videoId) {
                return video
            }
        }
        return null
    }
}
