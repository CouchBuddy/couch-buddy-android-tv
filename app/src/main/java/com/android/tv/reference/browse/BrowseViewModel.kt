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
package com.android.tv.reference.browse

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.android.tv.reference.auth.UserManager
import com.android.tv.reference.repository.VideoRepository
import com.android.tv.reference.repository.VideoRepositoryFactory
import com.android.tv.reference.shared.datamodel.Video
import com.android.tv.reference.shared.datamodel.VideoGroup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BrowseViewModel(application: Application) : AndroidViewModel(application) {
//    private var videoRepository: VideoRepository
    private val userManager = UserManager.getInstance(application.applicationContext)
    val customMenuItems = MutableLiveData<List<BrowseCustomMenu>>(listOf())
    val isSignedIn = Transformations.map(userManager.userInfo) { it != null }

    init {
    }

    suspend fun getBrowseContent (): MutableLiveData<List<VideoGroup>> {
        val browseContent = MutableLiveData<List<VideoGroup>>()
        val videoRepository = VideoRepositoryFactory.getVideoRepository(getApplication())
        browseContent.value = getVideoGroupList(videoRepository)

        return browseContent
    }

    fun getVideoGroupList(repository: VideoRepository): List<VideoGroup> {
        val videosByCategory = mutableMapOf<String, MutableList<Video>>()
        repository.getAllVideos().forEach { video ->
            val categories = (video.category?.split(",") ?: emptyList()).map { cat -> cat.trim() }
            categories.forEach {
                if (!videosByCategory.containsKey(it)) {
                    videosByCategory[it] = mutableListOf<Video>()
                }

                videosByCategory[it]!!.add(video)
            }
        }

        val videoGroupList = mutableListOf<VideoGroup>()
        videosByCategory.forEach { (k, v) ->
            videoGroupList.add(VideoGroup(k, v))
        }

        return videoGroupList
    }

    fun signOut() = viewModelScope.launch(Dispatchers.IO) {
        userManager.signOut()
    }
}
