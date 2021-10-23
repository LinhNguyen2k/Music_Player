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
import com.example.music_player.Player
import com.example.music_player.R
import com.example.music_player.json.Song
import com.example.music_player.json.formatDuration
import com.squareup.picasso.Picasso

class MusicFavoriteAdapter(private var listMusic : ArrayList<Song>, private val context : Context) :
    RecyclerView.Adapter<MusicFavoriteAdapter.ViewHolder>() {
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
//        if (Player.isChekOnline && listMusic[position].isCheck){
            holder.tv_songNameMusic.text = item.title
        val linkImg  = listMusic[position].thumbnail.removeRange(34,48)
            Picasso.with(context).load(linkImg).into(holder.img)
            holder.tv_songNameAlbum.text = item.artists_names
            holder.duration.text =formatDuration(listMusic[position].duration)
//        } else {
//            holder.tv_songNameMusic.text = item.title
//            val imageArt = getImage(listMusic[position].thumbnail)
//            if (imageArt != null) {
//                BitmapFactory.decodeByteArray(imageArt, 0, imageArt.size)
//                holder.img.setImageBitmap(BitmapFactory.decodeByteArray(imageArt, 0, imageArt!!.size))
//            }
//            holder.tv_songNameAlbum.text = item.artists_names
//            holder.duration.text =formatDurations(listMusic[position].duration)
//        }

//        holder.tv_songNameAlbum.text = item.album
//        holder.duration.text = formatDuration(listMusic[position].duration)
//        Glide.with(context)
//            .load(listMusic[position].artUri)
//            .apply(RequestOptions().placeholder(R.mipmap.music_player).centerCrop())
//            .into(holder.img)


        holder.itemView.setOnClickListener {
            Player.isChekOnline = true
            val intent  = Intent(context, Player::class.java).addFlags(FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("idSongs", listMusic[position].id)
            intent.putExtra("typeSongs", listMusic[position].type)
            intent.putExtra("index", position)
            intent.putExtra("class", "MusicFavoriteAdapter")
            ContextCompat.startActivity(context, intent, null)
        }

    }

    override fun getItemCount(): Int {
        return listMusic.size
    }

}