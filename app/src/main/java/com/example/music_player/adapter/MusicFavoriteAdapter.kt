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
import com.example.music_player.FavoriteActivity
import com.example.music_player.OfflineActivity
import com.example.music_player.Player
import com.example.music_player.R
import com.example.music_player.json.Song
import com.example.music_player.json.formatDuration
import com.example.music_player.model.Music
import com.example.music_player.model.formatDurations
import com.example.music_player.model.getImage
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
        if ( listMusic[position].isCheck){
            holder.tv_songNameMusic.text = item.title
        val linkImg  = listMusic[position].thumbnail.removeRange(34,48)
            Picasso.with(context).load(linkImg).into(holder.img)
            holder.tv_songNameAlbum.text = item.artists_names
            holder.duration.text =formatDuration(listMusic[position].duration)
        } else {
            holder.tv_songNameMusic.text = item.title
            val imageArt = getImage(listMusic[position].rank_status)
            if (imageArt != null) {
                BitmapFactory.decodeByteArray(imageArt, 0, imageArt.size)
                holder.img.setImageBitmap(BitmapFactory.decodeByteArray(imageArt, 0, imageArt!!.size))
            }
            holder.tv_songNameAlbum.text = item.artists_names
            holder.duration.text =formatDurations(listMusic[position].duration)
        }

//        holder.tv_songNameAlbum.text = item.album
//        holder.duration.text = formatDuration(listMusic[position].duration)
//        Glide.with(context)
//            .load(listMusic[position].artUri)
//            .apply(RequestOptions().placeholder(R.mipmap.music_player).centerCrop())
//            .into(holder.img)


        holder.itemView.setOnClickListener {

            if (listMusic[position].isCheck){
                Player.isChekOnline = true
                val intent  = Intent(context, Player::class.java).addFlags(FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra("idSongs", listMusic[position].id)
                intent.putExtra("typeSongs", listMusic[position].type)
                intent.putExtra("index", position)
                intent.putExtra("class", "MusicFavoriteAdapter")
                ContextCompat.startActivity(context, intent, null)
            }else {
                Player.isChekOnline = false
                val intent  = Intent(context, Player::class.java).addFlags(FLAG_ACTIVITY_NEW_TASK)
                val name = listMusic[position].artists_names
                val title = listMusic[position].title
                val id = listMusic[position].id
                val thumbnail = listMusic[position].thumbnail
                val duration = listMusic[position].duration
                val path = listMusic[position].rank_status
                OfflineActivity.MusicList.add(Music(id,title,"",name,duration,path,thumbnail,false))
                intent.putExtra("idSongs", listMusic[position].id)
                intent.putExtra("typeSongs", listMusic[position].type)
                intent.putExtra("index", position)
                intent.putExtra("class", "MusicFavoriteAdapter")
                OfflineActivity.MusicList.clear()
                while (OfflineActivity.MusicList.size <= FavoriteActivity.favoriteList.size){
                    OfflineActivity.MusicList.add(Music(id,title,"",name,duration,path,thumbnail,false))
                }
                ContextCompat.startActivity(context, intent, null)
            }

        }

    }

    override fun getItemCount(): Int {
        return listMusic.size
    }

}