package com.example.mp3_player.fragments

import android.Manifest
import android.app.AlertDialog
import android.content.ContentUris
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.mp3_player.R
import com.example.mp3_player.adapters.Mp3Adapters
import com.example.mp3_player.databinding.FragmentListMusicBinding
import com.example.mp3_player.models.ListMusic
import com.example.mp3_player.models.Mp3Class
import com.github.florent37.runtimepermission.kotlin.askPermission
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import java.util.concurrent.TimeUnit


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ListMusicFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ListMusicFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    lateinit var fragmentListMusicBinding: FragmentListMusicBinding
    lateinit var root:View
     var mp3Adapters:Mp3Adapters?=null
    private var requestCode = 1
    var count = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentListMusicBinding = FragmentListMusicBinding.inflate(inflater,container,false)
        root = fragmentListMusicBinding.root
        askPermission(Manifest.permission.READ_EXTERNAL_STORAGE) {
            ListMusic.listMusic = getAllMusicFun()
            mp3Adapters = Mp3Adapters(root.context,ListMusic.listMusic!!,object:Mp3Adapters.OnItemCLick{
                override fun onClick(mp3Class: Mp3Class, position: Int, image: Bitmap) {
                    var bundle = Bundle()
                    bundle.putSerializable("mp3",mp3Class)
                    bundle.putInt("position",position)
                    findNavController().navigate(R.id.fragmentsFragment,bundle)
                }
            })
            fragmentListMusicBinding.rv.adapter = mp3Adapters
        }.onDeclined {
            if (it.hasDenied()) {
                AlertDialog.Builder(root.context).setMessage("Iltimos dostup bering yo`qsa sizga yordam bera olmayman")
                        .setCancelable(false)
                        .setPositiveButton("Ok") { dialog, which ->
                            it.askAgain()
                        }.setNegativeButton("No") { dialog, which ->
                            dialog.dismiss()
                        }.show()
            }
            if (it.hasForeverDenied()) {
                AlertDialog.Builder(root.context).setMessage("Iltimos dostup bering yo`qsa sizga yordam bera olmayman")
                        .setCancelable(false)
                        .setPositiveButton("Ok") { dialog, which ->
                            it.goToSettings()
                        }.show()
            }
        }
        return root
    }





    fun getAllMusicFun():ArrayList<Mp3Class>{
        var listMusic = ArrayList<Mp3Class>()
        var collection = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        var projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM_ID
        )
        var selection = "${MediaStore.Audio.Media.IS_MUSIC}=?"
        var selectionArgs = arrayOf("1")
        var cursor: Cursor = activity?.contentResolver!!.query(
                collection,
                projection,
                selection,
                selectionArgs,
                null
        )!!
        if(cursor.moveToFirst()){
            var id = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            var artist = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            var title = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            var duration = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            var albumId = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            do {
                var id = cursor.getLong(id)
                var artist = cursor.getString(artist)
                var title = cursor.getString(title)
                var duration = cursor.getInt(duration)
                var albumID = cursor.getLong(albumId)
                var format: String? = null
                if (duration != null) {
                    format = String.format(
                        "%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(duration.toLong()),
                        TimeUnit.MILLISECONDS.toSeconds(duration.toLong()) -
                                TimeUnit.MINUTES.toSeconds(
                                    TimeUnit.MILLISECONDS.toMinutes(
                                        duration.toLong()
                                    )
                                )
                    )
                } else {
                    format = "--/--"
                }

                var contentUri:Uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,id)
                listMusic.add(Mp3Class(contentUri,title,artist,format,albumID))
            }while (cursor.moveToNext())
        }
        return listMusic
    }


//    fun getAllMusic(context: Context):ArrayList<Mp3Class>{
//        var listMusic = ArrayList<Mp3Class>()
//
//            var uri:Uri =MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
//            val projection = arrayOf(
//                MediaStore.Audio.AudioColumns.DATA,
//                MediaStore.Audio.AudioColumns.TITLE,
//                MediaStore.Audio.AudioColumns.ARTIST,
//                MediaStore.Audio.AudioColumns.DURATION
//            )
//            val cursor = context.contentResolver.query(
//                uri,
//                projection,
//                null,
//                null,
//                null
//            )
//            if (cursor != null) {
//                while (cursor.moveToNext()) {
//                    val mp3Class = Mp3Class()
//
//                    val path = cursor.getString(0)
//                    val name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
//                    val artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
//                    val duration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
//

//                    mp3Class.name = name.toString()
//                    mp3Class.artist = artist.toString()
//                    mp3Class.path = path.toString()
//                    mp3Class.duration = format
//                    Log.d("AAAA", "name: $name, artist: $artist, duration: $format")
//                    listMusic.add(mp3Class)
//                }
//                cursor.close()
//            }
//        return listMusic
//    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ListMusicFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ListMusicFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}