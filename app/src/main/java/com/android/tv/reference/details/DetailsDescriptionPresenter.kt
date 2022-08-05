package com.android.tv.reference.details

import androidx.leanback.widget.AbstractDetailsDescriptionPresenter
import com.android.tv.reference.shared.datamodel.Video
import com.android.tv.reference.shared.datamodel.VideoType

class DetailsDescriptionPresenter : AbstractDetailsDescriptionPresenter() {

    override fun onBindDescription(viewHolder: ViewHolder, itemData: Any) {
        val video = itemData as Video

        viewHolder.apply {
            title.text = video.name
            body.text = video.description
            if (video.videoType == VideoType.MOVIE) {
                subtitle.text = "${video.year} ${video.duration().toMinutes()} mins"
            } else {
                subtitle.text = "${video.year} - TV Series"
            }
        }
    }
}