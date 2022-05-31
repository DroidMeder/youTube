package kg.geekteck.youtube

import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.util.SparseArray
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YtFile
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.MergingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import kg.geekteck.youtube.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private var currentWindow = 0
    private var playBackPosition = 0L
    private var downloadUrl: String? = null
    private var isFullScreen = false
    private  var playWhenReady = true
    private lateinit var binding: ActivityMainBinding
    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        println("%%%%%%%%%%%%% $requestedOrientation")

        player = ExoPlayer.Builder(this)
            .setSeekBackIncrementMs(5000)
            .setSeekForwardIncrementMs(5000)
            .build()

        if (!isFullScreen) {
            binding.viewPlayer.findViewById<ImageView>(R.id.bt_fullscreen_exit).visibility =
                View.GONE
            binding.viewPlayer.findViewById<ImageView>(R.id.bt_back).visibility = View.GONE
            binding.viewPlayer.findViewById<ImageView>(R.id.bt_fullscreen).visibility = View.VISIBLE
        } else {
            binding.viewPlayer.findViewById<ImageView>(R.id.bt_fullscreen_exit).visibility =
                View.VISIBLE
            binding.viewPlayer.findViewById<ImageView>(R.id.bt_fullscreen).visibility = View.GONE
            binding.viewPlayer.findViewById<ImageView>(R.id.bt_back).visibility = View.VISIBLE
        }


        binding.viewPlayer.findViewById<ImageView>(R.id.bt_fullscreen).setOnClickListener {
            if (!isFullScreen) {
                binding.viewPlayer.findViewById<ImageView>(R.id.bt_fullscreen_exit).visibility =
                    View.VISIBLE
                binding.viewPlayer.findViewById<ImageView>(R.id.bt_fullscreen).visibility =
                    View.GONE
                binding.viewPlayer.findViewById<ImageView>(R.id.bt_back).visibility = View.VISIBLE
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

            } else {
                binding.viewPlayer.findViewById<ImageView>(R.id.bt_fullscreen_exit).visibility =
                    View.VISIBLE
                binding.viewPlayer.findViewById<ImageView>(R.id.bt_fullscreen).visibility =
                    View.GONE
                binding.viewPlayer.findViewById<ImageView>(R.id.bt_back).visibility = View.VISIBLE
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
            isFullScreen = !isFullScreen
        }

        binding.viewPlayer.findViewById<ImageView>(R.id.bt_fullscreen_exit).setOnClickListener {
            if (isFullScreen) {
                binding.viewPlayer.findViewById<ImageView>(R.id.bt_fullscreen_exit).visibility =
                    View.GONE
                binding.viewPlayer.findViewById<ImageView>(R.id.bt_fullscreen).visibility =
                    View.VISIBLE
                binding.viewPlayer.findViewById<ImageView>(R.id.bt_back).visibility = View.GONE
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

            } else {
                binding.viewPlayer.findViewById<ImageView>(R.id.bt_fullscreen_exit).visibility =
                    View.VISIBLE
                binding.viewPlayer.findViewById<ImageView>(R.id.bt_fullscreen).visibility =
                    View.GONE
                binding.viewPlayer.findViewById<ImageView>(R.id.bt_back).visibility = View.VISIBLE
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
            isFullScreen = !isFullScreen
        }

        binding.viewPlayer.player = player
        binding.viewPlayer.keepScreenOn = true
        player!!.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    ExoPlayer.STATE_BUFFERING -> binding.progressBar.visibility = View.VISIBLE
                    ExoPlayer.STATE_READY -> {
                        binding.progressBar.visibility = View.GONE
                        playWhenReady =true
                    }
                    Player.STATE_ENDED -> {
                        binding.progressBar.visibility = View.GONE
                    }
                    Player.STATE_IDLE -> {
                        TODO()
                    }
                }
            }
           /* override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> binding.progressBar.visibility = View.VISIBLE
                    Player.STATE_READY -> binding.progressBar.visibility = View.GONE
                }
            }*/
        })
        val videoUri = Uri.parse("https://www.rmp-streaming.com/media/big-buck-bunny-360p.mp4")
        youTubePlay(videoUri)


    }

    private fun youTubePlay(videoUri: Uri){
        val youtubeLink = "https://www.youtube.com/watch?v=l_ZNbRAzEhU"

        object : YouTubeExtractor(this) {
            override fun onExtractionComplete(ytFiles: SparseArray<YtFile>?, vMeta: VideoMeta?) {


                if (ytFiles != null) {
                    val videoTag = 137
                    val audioTag = 140
                    val audioSource = ProgressiveMediaSource.Factory(DefaultHttpDataSource.Factory())
                        .createMediaSource(MediaItem.fromUri(ytFiles[audioTag].url))
                    val visdeoSource = ProgressiveMediaSource.Factory(DefaultHttpDataSource.Factory())
                        .createMediaSource(MediaItem.fromUri(ytFiles[videoTag].url))
                    player?.setMediaSource(MergingMediaSource(
                        true,
                        visdeoSource,
                        audioSource),
                        true
                    )

                    val mediaItem = downloadUrl?.let { MediaItem.fromUri(it) }
                    if (mediaItem != null) {
                        player?.setMediaItem(mediaItem)
                    }
                    player?.prepare()
                    player?.play()
                    /*player.prepare()
                    player.seekTo(currentWindow, playBackPosition)*/
                }
            }


        }.extract(youtubeLink)
    }

}