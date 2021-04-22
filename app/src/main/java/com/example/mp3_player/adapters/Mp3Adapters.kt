package com.example.mp3_player.adapters

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.mp3_player.R
import com.example.mp3_player.databinding.ItemMusicBinding
import com.example.mp3_player.models.Mp3Class
import java.io.FileDescriptor
import java.lang.Exception

class Mp3Adapters(var context: Context,var data:ArrayList<Mp3Class>,var onItemClick:OnItemCLick):RecyclerView.Adapter<Mp3Adapters.Vh>() {
    inner class Vh(var itemMusicBinding: ItemMusicBinding):RecyclerView.ViewHolder(itemMusicBinding.root){
        fun onBind(mp3Class: Mp3Class,position: Int){
            itemMusicBinding.image1.setImageURI(mp3Class.uri)

            itemMusicBinding.name.text = mp3Class.name
            itemMusicBinding.artist.text=mp3Class.artist
            itemMusicBinding.duration.text=mp3Class.duration.toString()
            var albumart = getAlbumart(mp3Class.albumId!!)
            itemMusicBinding.image1.setImageBitmap(albumart)
            itemMusicBinding.root.setOnClickListener {
                if (onItemClick!=null){
                    onItemClick!!.onClick(mp3Class,position,albumart)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
    return Vh(ItemMusicBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(data!![position],position)
    }

    override fun getItemCount(): Int {
        return data!!.size
    }
    interface OnItemCLick{
        fun onClick(mp3Class: Mp3Class,position: Int,image:Bitmap)
    }

    fun getAlbumart(album_id:Long):Bitmap{
        var bm:Bitmap?=null
        try {
            var sArtworkUri = Uri.parse("content://media/external/audio/albumart")
            var uri =  ContentUris.withAppendedId(sArtworkUri,album_id)
            var pfd:ParcelFileDescriptor = context.contentResolver.openFileDescriptor(uri,"r")!!
            if (pfd!=null){
                var fd:FileDescriptor = pfd.fileDescriptor
                bm = BitmapFactory.decodeFileDescriptor(fd)
            }
        }catch (e:Exception){
            bm=BitmapFactory.decodeResource(
                context.resources,
                R.drawable.mmmm
            )
        }
        return bm!!
    }
}