package com.android.tv.reference.details

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.app.DetailsSupportFragmentBackgroundController
import androidx.leanback.widget.*
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.android.tv.reference.R
import com.android.tv.reference.browse.VideoCardPresenter
import com.android.tv.reference.repository.FileVideoRepository
import com.android.tv.reference.shared.datamodel.Episode
import com.android.tv.reference.shared.datamodel.Video
import com.android.tv.reference.shared.datamodel.VideoType
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.coroutines.launch
import timber.log.Timber


const val VIDEO_ACTION_PLAY = 1L

class MovieDetailsFragment : DetailsSupportFragment(), OnItemViewClickedListener {
    private lateinit var rowsAdapter: ArrayObjectAdapter
    private lateinit var video: Video
    private val backgroundController = DetailsSupportFragmentBackgroundController(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        video = requireArguments().getParcelable("video")!!

        setupBackground()

        lifecycleScope.launch {
            buildDetails()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        onItemViewClickedListener = this
        return view
    }

    private fun setupBackground () {
        val fullSizeBackground = video.backgroundImageUri.replace("w500", "original")

        Picasso.get().load(fullSizeBackground).placeholder(R.drawable.image_placeholder)
            .error(R.drawable.image_placeholder).into(object : Target {
                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                    Timber.e(e, "Failed to load background ${video.backgroundImageUri}")
                }

                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                    backgroundController.coverBitmap = bitmap
                    backgroundController.enableParallax()
                }

                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
            })
    }

    private suspend fun buildDetails() {
        val videoRepository = FileVideoRepository(requireActivity().application)

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

        // Display series episodes by season
        if (video.videoType == VideoType.SERIES) {
            val episodes = videoRepository.getEpisodes(video.id)
            val seasons = episodes.groupBy { it.season }.toSortedMap()

            seasons.forEach {
                val seasonHeader = HeaderItem(0, "Season ${it.key}")
                val seasonRowAdapter = ArrayObjectAdapter(EpisodeCardPresenter())
                seasonRowAdapter.addAll(0, it.value)
                rowsAdapter.add(ListRow(seasonHeader, seasonRowAdapter))
            }
        }

        // Add a Related items row
        val listRowAdapter = ArrayObjectAdapter(VideoCardPresenter()).apply {
            add(video)
            add(video)
            add(video)
        }
        val header = HeaderItem(1, "Related Items")
        rowsAdapter.add(ListRow(header, listRowAdapter))

        adapter = rowsAdapter
    }

    override fun onItemClicked(
        itemViewHolder: Presenter.ViewHolder?,
        item: Any?,
        rowViewHolder: RowPresenter.ViewHolder?,
        row: Row?
    ) {
        if (item is Action) {
            if (item.id == VIDEO_ACTION_PLAY) {
                findNavController().navigate(
                    MovieDetailsFragmentDirections.actionMovieDetailsFragmentToPlaybackFragment(video.toPlayableMedia())
                )
            } else {
                Timber.w("action clicked but not handled ${item.id}")
            }
        } else if (item is Episode) {
            findNavController().navigate(
                MovieDetailsFragmentDirections.actionMovieDetailsFragmentToPlaybackFragment(item.toPlayableMedia(video))
            )
        } else if (item is Video) {
            findNavController().navigate(
                MovieDetailsFragmentDirections.actionMovieDetailsFragmentToMovieDetailsFragment(item)
            )
        } else {
            Timber.i("Unhandled click on item $item")
        }
    }
}