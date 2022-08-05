package com.android.tv.reference.shared.datamodel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.Duration

@Parcelize
class PlayableMedia(
    val id: String,
    val name: String,
    val description: String,
    val videoUri: String,
    val duration: Duration,
): Parcelable {
}