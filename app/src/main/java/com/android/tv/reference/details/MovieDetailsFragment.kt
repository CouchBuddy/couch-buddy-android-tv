package com.android.tv.reference.details

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.widget.*
import androidx.navigation.fragment.findNavController
import com.android.tv.reference.R
import com.android.tv.reference.browse.BrowseFragmentDirections
import com.android.tv.reference.shared.datamodel.Video
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target

const val VIDEO_ACTION_PLAY = 1L

class MovieDetailsFragment : DetailsSupportFragment(), OnItemViewClickedListener {
    private lateinit var rowsAdapter: ArrayObjectAdapter
    private lateinit var video: Video

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        video = requireArguments().getParcelable<Video>("video")!!
        buildDetails()
    }

    private fun buildDetails() {
        val selector = ClassPresenterSelector().apply {
            // Attach your media item details presenter to the row presenter:
            FullWidthDetailsOverviewRowPresenter(DetailsDescriptionPresenter()).also {
                addClassPresenter(DetailsOverviewRow::class.java, it)
            }
            addClassPresenter(ListRow::class.java, ListRowPresenter())
        }
        rowsAdapter = ArrayObjectAdapter(selector)

        val detailsOverview = DetailsOverviewRow(video).apply {
            if (video.thumbnailUri.isNotEmpty()) {
                Picasso.get().load(video.thumbnailUri).placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.image_placeholder).into(object : Target {
                        override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                            TODO("not implemented")
                        }

                        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                            // loaded bitmap is here (bitmap)

                            setImageBitmap(requireContext(), bitmap)
                        }

                        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}

                    })
            }
            val videoActionsAdapter = ArrayObjectAdapter()
            videoActionsAdapter.add(Action(VIDEO_ACTION_PLAY, "Play"))

            actionsAdapter = videoActionsAdapter
        }
        rowsAdapter.add(detailsOverview)

        // Add a Related items row
        val listRowAdapter = ArrayObjectAdapter().apply {
            add(video)
            add(video)
            add(video)
        }
        val header = HeaderItem(0, "Related Items")
        rowsAdapter.add(ListRow(header, listRowAdapter))

        adapter = rowsAdapter
    }

    override fun onItemClicked(
        itemViewHolder: Presenter.ViewHolder?,
        item: Any?,
        rowViewHolder: RowPresenter.ViewHolder?,
        row: Row?
    ) {
        if (item !is Action) return

        val action = item as Action

        if (action.id == VIDEO_ACTION_PLAY) {
            findNavController().navigate(
                BrowseFragmentDirections.actionBrowseFragmentToPlaybackFragment(video)
            )
        }
    }
}