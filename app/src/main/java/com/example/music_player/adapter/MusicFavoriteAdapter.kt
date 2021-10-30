package com.example.music_player.adapter

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.music_player.R
import com.example.music_player.activity.FavoriteActivity
import com.example.music_player.activity.OfflineActivity
import com.example.music_player.activity.Player
import com.example.music_player.activity.Player.Companion.listAddSongOffline
import com.example.music_player.model.Music
import com.example.music_player.model.formatDurations
import com.example.music_player.model.getImage
import com.example.music_player.model.json.Song
import com.example.music_player.model.json.formatDuration
import com.squareup.picasso.Picasso

class MusicFavoriteAdapter(private var listMusic: ArrayList<Song>, private val context: Context) :
    RecyclerView.Adapter<MusicFavoriteAdapter.ViewHolder>() {
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

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listMusic[position]
        if (listMusic[position].isCheck) {
            holder.tv_songNameMusic.text = item.title
            val linkImg = listMusic[position].thumbnail.removeRange(34, 48)
            if (isOnline(context)) {
                Picasso.with(context).load(linkImg).into(holder.img)
            } else {
                holder.img.setBackgroundResource(R.mipmap.music_player)
            }

            holder.tv_songNameAlbum.text = item.artists_names
            holder.duration.text = formatDuration(listMusic[position].duration)
        } else {
            holder.tv_songNameMusic.text = item.title
            val imageArt = getImage(listMusic[position].rank_status)

            if (imageArt != null) {
                BitmapFactory.decodeByteArray(imageArt, 0, imageArt.size)
                holder.img.setImageBitmap(BitmapFactory.decodeByteArray(imageArt,
                    0,
                    imageArt!!.size))
            } else {
                holder.img.setBackgroundResource(R.mipmap.music_player)
            }
            holder.tv_songNameAlbum.text = item.artists_names
            holder.duration.text = formatDurations(listMusic[position].duration)
        }

        holder.itemView.setOnClickListener {

            if (listMusic[position].isCheck) {
                if (!isOnline(context)) {
                    if (listMusic[position].isCheck) {
                        Toast.makeText(context,
                            "Xin bạn kiểm tra lại kết nối mạng",
                            Toast.LENGTH_LONG)
                            .show()
                    }
                } else {
                    Player.isChekOnline = true
                    val intent =
                        Intent(context, Player::class.java).addFlags(FLAG_ACTIVITY_NEW_TASK)
                    intent.putExtra("index", position)
                    intent.putExtra("class", "MusicFavoriteAdapter")
                    Player.listPhu.clear()
                    for (i in 0 until FavoriteActivity.favoriteList.size ) {
                        if (listMusic[i].isCheck) {
                            Player.listPhu.add(listMusic[i])
                        }
                    }
                    ContextCompat.startActivity(context, intent, null)
                }
            } else {
                Player.isChekOnline = false
                var count = 0
                val intent =
                    Intent(context, Player::class.java).addFlags(FLAG_ACTIVITY_NEW_TASK)

                listAddSongOffline.clear()
                for (i in 0 until FavoriteActivity.favoriteList.size ) {
                    if (!listMusic[i].isCheck) {
                        val name = listMusic[i].artists_names
                        val title = listMusic[i].title
                        val id = listMusic[i].id
                        val thumbnail = listMusic[i].thumbnail
                        val duration = listMusic[i].duration
                        val path = listMusic[i].rank_status
                            listAddSongOffline.add(listAddSongOffline.size,Music(id,
                            title,
                            "",
                            name,
                            duration,
                            path,
                            thumbnail,
                            false))
                    }

                }
                OfflineActivity.MusicList.clear()
                for (i in 0 until FavoriteActivity.favoriteList.size ) {
                    for (j in 0 until listAddSongOffline.size){
                        val name = listAddSongOffline[j].artist
                        val title = listAddSongOffline[j].title
                        val id = listAddSongOffline[j].id
                        val thumbnail = listAddSongOffline[j].artUri
                        val duration = listAddSongOffline[j].duration
                        val path = listAddSongOffline[j].path
                        if (count <= listAddSongOffline.size && count <= FavoriteActivity.favoriteList.size){
                            count++
                            OfflineActivity.MusicList.add(OfflineActivity.MusicList.size,Music(id,
                                title,
                                "",
                                name,
                                duration,
                                path,
                                thumbnail,
                                false))
                        } else if (count <= FavoriteActivity.favoriteList.size && count > listAddSongOffline.size ){
                            count++
                            OfflineActivity.MusicList.add(0,Music(id,
                                title,
                                "",
                                name,
                                duration,
                                path,
                                thumbnail,
                                false))
                        }
                    }
                }
                intent.putExtra("index", position)
                intent.putExtra("class", "MusicFavoriteAdapter")
                ContextCompat.startActivity(context, intent, null)
            }
        }

    }

    override fun getItemCount(): Int {
        return listMusic.size
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    return true
                }
            }
        }
        return false
    }
}