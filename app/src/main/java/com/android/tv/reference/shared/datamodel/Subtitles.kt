package com.android.tv.reference.shared.datamodel

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
class Subtitles(
    val id: Int,
    val lang: String
): Parcelable {}