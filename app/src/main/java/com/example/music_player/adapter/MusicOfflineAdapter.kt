package com.example.music_player.adapter

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.music_player.Player
import com.example.music_player.R
import com.example.music_player.json.formatDuration
import com.example.music_player.model.Music
import com.example.music_player.model.formatDurations
import com.example.music_player.model.getImage

class MusicOfflineAdapter(private var listMusic : ArrayList<Music>, private val context : Context) :
    RecyclerView.Adapter<MusicOfflineAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tv_songNameMusic : TextView = view.findViewById(R.id.tv_songNameMusic)
        var tv_songNameAlbum : TextView = view.findViewById(R.id.tv_songNameAlbum)
        var duration : TextView = view.findViewById(R.id.tv_songDuration)
        var img : ImageView = view.findViewById(R.id.imageMV)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.custom_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listMusic[position]
        holder.tv_songNameMusic.text = item.title
        holder.tv_songNameAlbum.text = item.artist

        if (formatDurations(listMusic[position].duration) == "00:00"){
            holder.duration.text = formatDuration(listMusic[position].duration)
        }else{
            holder.duration.text = formatDurations(listMusic[position].duration)
        }
        val imageArt = getImage(listMusic[position].path)
        if (imageArt != null) {
            BitmapFactory.decodeByteArray(imageArt, 0, imageArt.size)
            holder.img.setImageBitmap(BitmapFactory.decodeByteArray(imageArt, 0, imageArt!!.size))
        }

        holder.itemView.setOnClickListener {
            val intent  = Intent(context, Player::class.java).addFlags(FLAG_ACTIVITY_NEW_TASK)
            Player.isChekOnline = false
            intent.putExtra("index", position)
            intent.putExtra("class", "MusicOfflineAdapter")
            ContextCompat.startActivity(context, intent, null)
        }

    }

    override fun getItemCount(): Int {
        return listMusic.size
    }

}