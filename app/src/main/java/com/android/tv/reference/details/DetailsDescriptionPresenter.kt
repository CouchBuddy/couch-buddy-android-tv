package com.android.tv.reference.details

import androidx.leanback.widget.AbstractDetailsDescriptionPresenter
import com.android.tv.reference.shared.datamodel.Video

class DetailsDescriptionPresenter : AbstractDetailsDescriptionPresenter() {

    override fun onBindDescription(viewHolder: AbstractDetailsDescriptionPresenter.ViewHolder, itemData: Any) {
        val video = itemData as Video
        // In a production app, the itemData object contains the information
        // needed to display details for the media item:
        // viewHolder.title.text = details.shortTitle

        // Here we provide static data for testing purposes:
        viewHolder.apply {
            title.text = video.name
            subtitle.text = "${video.year} - Runtime ${video.duration().toString()}"
            body.text = video.description
        }
    }
}