package com.example.music_player.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.music_player.R
import com.example.music_player.activity.Player
import com.example.music_player.model.json.Song
import com.example.music_player.model.json.formatDuration
import com.squareup.picasso.Picasso

class MusicListAdapter(private val listMusic : ArrayList<Song>, private val context : Context) :
    RecyclerView.Adapter<MusicListAdapter.ViewHolder>() {
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
        val linkImg  = listMusic[position].thumbnail.removeRange(34,48)
        Picasso.with(context).load(linkImg).into(holder.img)
        holder.tv_songNameAlbum.text = item.artists_names
        holder.duration.text = formatDuration(listMusic[position].duration)

        holder.itemView.setOnClickListener {
            val intent  = Intent(context, Player::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            Player.isChekOnline = true
            intent.putExtra("idSongs", listMusic[position].id)
            intent.putExtra("typeSongs", listMusic[position].type)
            intent.putExtra("index", position)
            intent.putExtra("class", "MusicListAdapter")
            ContextCompat.startActivity(context, intent, null)
        }

    }

    override fun getItemCount(): Int {
        return listMusic.size
    }

}