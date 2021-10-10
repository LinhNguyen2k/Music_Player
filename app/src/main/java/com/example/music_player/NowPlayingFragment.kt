package com.example.music_player

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.music_player.databinding.FragmentNowPlayingBinding
import com.example.music_player.model.setSongPosition


class NowPlayingFragment : Fragment() {
    companion object {
        lateinit var binding: FragmentNowPlayingBinding
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_now_playing, container, false)
        binding = FragmentNowPlayingBinding.bind(view)
        binding.root.visibility = View.INVISIBLE

        binding.btnPlayPause.setOnClickListener {
            if (Player.isPlaying) {
                pauseMusic()
            } else {
                playMusic()
            }
        }
        binding.btnNextSongs.setOnClickListener {
            setSongPosition(check = true)
            Player.musicService!!.createMusicPlayer()
            Glide.with(this)
                .load(Player.musicListPlayer[Player.songPosition].img)
                .apply(RequestOptions().placeholder(R.mipmap.music_player).centerCrop())
                .into(binding.imgSong)
            Player.musicService!!.showNotification(R.drawable.ic_baseline_pause_24)
            binding.tvSongNameFragment.text = Player.musicListPlayer[Player.songPosition].title
            playMusic()
        }
        binding.root.setOnClickListener {
            val intent  = Intent(requireContext(), Player::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("index", Player.songPosition)
            intent.putExtra("class", "NowPlaying")
            ContextCompat.startActivity(requireContext(), intent, null)
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        if (Player.musicService != null) {
            binding.root.visibility = View.VISIBLE
            binding.tvSongNameFragment.isSelected = true
            Glide.with(this)
                .load(Player.musicListPlayer[Player.songPosition].img)
                .apply(RequestOptions().placeholder(R.mipmap.music_player).centerCrop())
                .into(binding.imgSong)
            binding.tvSongNameFragment.text = Player.musicListPlayer[Player.songPosition].title
            if (Player.isPlaying) {
                binding.btnPlayPause.setIconResource(R.drawable.ic_baseline_pause_24)
            } else {
                binding.btnPlayPause.setIconResource(R.drawable.ic_baseline_play_arrow_24)
            }
        }
    }

    private fun playMusic() {
        Player.musicService!!.mediaPlayer!!.start()
        binding.btnPlayPause.setIconResource(R.drawable.ic_baseline_pause_24)
        Player.musicService!!.showNotification(R.drawable.ic_baseline_pause_24)
        Player.binding.btnNextRight.setIconResource(R.drawable.ic_baseline_pause_24)
        Player.isPlaying = true
    }

    private fun pauseMusic() {
        Player.musicService!!.mediaPlayer!!.pause()
        binding.btnPlayPause.setIconResource(R.drawable.ic_baseline_play_arrow_24)
        Player.musicService!!.showNotification(R.drawable.ic_baseline_play_arrow_24)
        Player.binding.btnNextRight.setIconResource(R.drawable.ic_baseline_play_arrow_24)
        Player.isPlaying = false
    }


}