package com.musicplayer.ui.player

import android.content.Context
import android.content.SharedPreferences
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.musicplayer.databinding.FragmentPlayerBinding
import com.musicplayer.models.Music

class PlayerFragment : Fragment() {

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!
    private lateinit var playerViewModel: PlayerViewModel
    private var music: Music? = null
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var audioManager: AudioManager
    private var currentVolume = 50

    companion object {
        private const val SHARED_PREF_NAME = "volume"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            music = it.getParcelable<Music>("music")
        }

        activity?.let {
            sharedPreferences = it.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            editor = sharedPreferences.edit()
            currentVolume = sharedPreferences.getInt(SHARED_PREF_NAME, 50)
        }
    }

    override fun onStart() {
        super.onStart()
        currentVolume = sharedPreferences.getInt(SHARED_PREF_NAME, 50)
        binding.sbVolume.progress = sharedPreferences.getInt(SHARED_PREF_NAME, 50)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        registerEvents()
        listenEvents()
    }

    private fun init() {
        mediaPlayer = MediaPlayer.create(requireActivity(), Uri.parse(music?.url))
        activity?.let {
            audioManager = it.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0)
        }

        binding.sbVolume.progress = currentVolume
        mediaPlayer.setVolume(getFloat(currentVolume), getFloat(currentVolume))
        initialiseSeekBar()
        mediaPlayer.start()
        playerViewModel = ViewModelProvider(this)[PlayerViewModel::class.java]
        playerViewModel.setContext(requireContext())
        music?.let {
            playerViewModel.isFavorite(it.mid)
        }
    }

    private fun registerEvents() {
        binding.sbVolume.max = 100
        binding.sbVolume.progress = currentVolume
        binding.txtMusicTitle.text = music?.title
        binding.btnPlayMusic.setOnClickListener(btnPlayMusicClickListener)
        binding.btnPauseMusic.setOnClickListener(btnPauseClickListener)


        binding.btnAddToFavorites.setOnClickListener {
            music?.let {
                playerViewModel.addToFavorite(it)
            }
        }

        binding.btnRemoveFromFavorites.setOnClickListener {
            music?.let {
                playerViewModel.removeFromFavorite(it.mid)
            }
        }

        binding.sbDuration.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) mediaPlayer.seekTo(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        binding.sbVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0)
                currentVolume = progress
                mediaPlayer.setVolume(getFloat(currentVolume), getFloat(currentVolume))
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                currentVolume = seekBar.progress
                mediaPlayer.setVolume(getFloat(currentVolume), getFloat(currentVolume))
                putVolumeToSharedPref(currentVolume)
            }
        })

        binding.customToolbar.btnBack.setOnClickListener { findNavController().popBackStack() }
    }

    private fun listenEvents() {
        playerViewModel.isFavorite.observe(viewLifecycleOwner) {
            if (it) {
                binding.btnAddToFavorites.visibility = View.GONE
                binding.btnRemoveFromFavorites.visibility = View.VISIBLE
            } else {
                binding.btnAddToFavorites.visibility = View.VISIBLE
                binding.btnRemoveFromFavorites.visibility = View.GONE
            }
        }
    }

    private val btnPlayMusicClickListener = View.OnClickListener {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
            binding.btnPlayMusic.visibility = View.GONE
            binding.btnPauseMusic.visibility = View.VISIBLE
        }
    }

    private val btnPauseClickListener = View.OnClickListener {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            binding.btnPlayMusic.visibility = View.VISIBLE
            binding.btnPauseMusic.visibility = View.GONE
        }
    }

    private fun initialiseSeekBar() {
        binding.sbDuration.max = mediaPlayer.duration

        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                try {
                    binding.sbDuration.progress = mediaPlayer.currentPosition
                    handler.postDelayed(this, 1000)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }, 1000)
    }

    private fun putVolumeToSharedPref(volume: Int) {
        editor.putInt("volume", volume)
        editor.commit()
    }

    private fun getFloat(currentVolume: Int): Float {
        return currentVolume.toFloat() / 100
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        mediaPlayer.stop()
    }


}