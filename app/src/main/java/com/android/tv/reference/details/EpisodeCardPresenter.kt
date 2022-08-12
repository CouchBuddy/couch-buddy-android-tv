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
package com.android.tv.reference.details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.android.tv.reference.R
import com.android.tv.reference.databinding.PresenterEpisodeCardBinding
import com.android.tv.reference.repository.FileVideoRepository
import com.android.tv.reference.shared.datamodel.Episode
import com.squareup.picasso.Picasso

/**
 * Presents an [Episode] as an [ImageCardView] with Season and Episode info.
 */
class EpisodeCardPresenter : Presenter() {
    var apiBaseUrl: String? = null

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val context = parent.context
        val binding = PresenterEpisodeCardBinding.inflate(LayoutInflater.from(context), parent, false)

        // Set the image size ahead of time since loading can take a while.
        val resources = context.resources
        binding.root.setMainImageDimensions(
                resources.getDimensionPixelSize(R.dimen.thumbnail_card_width),
                resources.getDimensionPixelSize(R.dimen.thumbnail_card_height))

        val videoRepository = FileVideoRepository(context)
        apiBaseUrl = videoRepository.getApiBaseUrl()

        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any?) {
        checkNotNull(item)
        val episode = item as Episode
        val binding = PresenterEpisodeCardBinding.bind(viewHolder.view)
        binding.root.titleText = "Episode ${episode.episode}"

        Picasso.get().load("${apiBaseUrl}episodes/${item.id}/thumbnail").placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_placeholder).into(binding.root.mainImageView)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val binding = PresenterEpisodeCardBinding.bind(viewHolder.view)
        binding.root.mainImage = null
    }
}
