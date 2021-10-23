package com.example.music_player.adapter

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.music_player.MainActivity
import com.example.music_player.Player
import com.example.music_player.R
import com.example.music_player.json.Album
import com.example.music_player.json.Artist
import com.example.music_player.json.Song
import com.example.music_player.json.formatDuration
import com.squareup.picasso.Picasso

class MusicSearchAdapter(
    private var listMusic: ArrayList<com.example.music_player.JsonSearch.Song>,
    private val context: Context,
) :
    RecyclerView.Adapter<MusicSearchAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tv_songNameMusic: TextView = view.findViewById(R.id.tv_songNameMusic)
        var tv_songNameAlbum: TextView = view.findViewById(R.id.tv_songNameAlbum)
        var duration: TextView = view.findViewById(R.id.tv_songDuration)
        var img: ImageView = view.findViewById(R.id.imageMV)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.custom_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listMusic[position]
        holder.tv_songNameMusic.text = item.name
        var linkImg = "https://photo-resize-zmp3.zadn.vn/w94_r1x1_jpeg/" + listMusic[position].thumb
        Picasso.with(context).load(linkImg).into(holder.img)
        holder.tv_songNameAlbum.text = item.artist
        holder.duration.text = formatDuration(listMusic[position].duration.toLong())
//        holder.tv_songNameAlbum.text = item.album
//        holder.duration.text = formatDuration(listMusic[position].duration)
//        Glide.with(context)
//            .load(listMusic[position].artUri)
//            .apply(RequestOptions().placeholder(R.mipmap.music_player).centerCrop())
//            .into(holder.img)


        holder.itemView.setOnClickListener {
            val intent = Intent(context, Player::class.java).addFlags(FLAG_ACTIVITY_NEW_TASK)
            Player.isChekOnline = true

            for (i in 0 until MainActivity.listSearch.size - 1) {
                var name = MainActivity.listSearch[i].artist
                var id = MainActivity.listSearch[i].id

                var img =
                    "https://photo-resize-zmp3.zadn.vn/w94_r1x1_jpeg/${MainActivity.listSearch[i].thumb}"
               var title = MainActivity.listSearch[i].name
               var duration = MainActivity.listSearch[i].duration
                Player.musicListSearch.add(Song(Album(),
                    Artist(), emptyList(),name,"",-1, duration.toLong(),id,false,false,"",
                    "","","","","","",-1,"","",img,title,-1,"",true))
            }
//            Player.musicListSearch.add(song)
            intent.putExtra("id", listMusic[position].id)
            intent.putExtra("index", position)
            intent.putExtra("class", "MusicSearchAdapter")
            ContextCompat.startActivity(context, intent, null)
        }

    }

    override fun getItemCount(): Int {
        return listMusic.size
    }

}