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
package com.android.tv.reference.shared.datamodel

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import timber.log.Timber
import java.time.Duration

fun getTmdbImageUrl (path: String): String {
    return "http://image.tmdb.org/t/p/w500${path}"
}

/**
 * Represents a video that can be played in the app
 */
@Parcelize
@JsonClass(generateAdapter = true)
class Video(
//    val id: String,
    @Json(name="title") val name: String,
    @Json(name="plot") val description: String,
//    val uri: String,
//    val videoUri: String,
    @Json(name="poster") val thumbnailUri: String,
    @Json(name = "backdrop") val backgroundImageUri: String,
    @Json(name="genre") val category: String? = "",
    @Json(name="type") val videoType: VideoType,
    // The duration is specified in the ISO 8601 format as 'PT00H00M'. For more information on the
    // format, refer - https://en.wikipedia.org/wiki/ISO_8601.
    val duration: String? = "PT00H00M",
    // The series, season and episode information is picked from the JSON feed that stores the
    // catalog. For consistency and proper formatting of the JSON, the fields for series, season and
    // episode data have been defined as empty strings for content types that are not TV Episodes.
    val seriesUri: String = "",
    val seasonUri: String = "",
    val episodeNumber: String = "",
    val seasonNumber: String = "",

//    @Json(name="backdrop") val _backdrop: String = "",
//    @Json(name="poster") val _poster: String = "",
    val part: Int = 0,
    val actors: String? = null,
    val country: String? = null,
    val director: String? = null,
//    val genre: String = "",
    val imdbId: String = "",
    val language: String = "en",
//    val plot: String = "",
    val rated: String? = null,
    val resolution: Int? = null,
    val runtime: Int? = null,
//    val title: String,
//    val type: VideoType,
    val vote: Float? = null,
    val watched: Float? = null,
    val writer: String? = null,
    val year: Int? = 0,
    val id: Int,
//    val createdAt: Date;
//    updatedAt?: Date;
) : Parcelable {
    val uri get() = "http://192.168.129.9:3000/api/watch/m${id}"
    val videoUri get() = "http://192.168.129.9:3000/api/watch/m${id}"

    override fun toString(): String {
        return "Video(name='$name')"
    }

    /**
     * Helper method to convert the ISO-8601 formatted duration into a representation that supports
     * operations, for example, calculating the time remaining in a [Video] based on a given
     * position.
     */
    fun duration(): Duration {
        return Duration.parse(duration)
    }

    /**
     * The user has "finished" a video if the end credits start OR an approximation based on the
     * content length. We do not have metadata that contains the timestamp for when credits appear
     * in a video so we are using an approximation.
     */
    fun isAfterEndCreditsPosition(positionMillis: Long): Boolean {
        // Skip calculation for the common case of starting from 0ms.
        if (positionMillis <= 0) {
            return false
        }
        val durationMillis = duration().toMillis() * VIDEO_COMPLETED_DURATION_MAX_PERCENTAGE
        val isAfterEndCreditsPosition = positionMillis >= durationMillis
        Timber.v(
            "Has video Ended? %s, duration: %s, durationMillis: %s, positionMillis: %s",
            isAfterEndCreditsPosition,
            duration,
            durationMillis,
            positionMillis
        )
        return isAfterEndCreditsPosition
        // TODO(mayurkhin@) add metadata to check completion with video credits
    }

    companion object {
        /**
         * Threshold constant used to calculate if a Video's credits have started. Using 95% to
         * simulate the start position for when credits would appear in a video.
         */
        private const val VIDEO_COMPLETED_DURATION_MAX_PERCENTAGE = 0.95
    }
}

enum class VideoType {
    @Json(name = "clip")
    CLIP,

    @Json(name = "episode")
    EPISODE,

    @Json(name = "movie")
    MOVIE,

    @Json(name="series")
    SERIES
}
