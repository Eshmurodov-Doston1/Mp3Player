package com.example.mp3_player

import android.annotation.SuppressLint
import android.content.ContentUris
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.ParcelFileDescriptor
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.navigation.fragment.findNavController
import com.example.mp3_player.databinding.FragmentFragmentsBinding
import com.example.mp3_player.models.ListMusic
import com.example.mp3_player.models.Mp3Class
import java.io.FileDescriptor
import java.lang.Exception
import java.util.concurrent.TimeUnit

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentsFragment : Fragment() {
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
    lateinit var fragmentFragmentsBinding:FragmentFragmentsBinding
    lateinit var root:View
    private var mediaPlayer: MediaPlayer?=null
    lateinit var handler: Handler
    lateinit var listMusics:ArrayList<Mp3Class>
    lateinit var musicFile:Mp3Class
    var currentPosition=0
    var position=0
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
       fragmentFragmentsBinding = FragmentFragmentsBinding.inflate(inflater,container,false)
        root = fragmentFragmentsBinding.root
        var mp3Class = arguments?.getSerializable("mp3")
        position = arguments?.getInt("position",-1)!!
        handler = Handler(Looper.myLooper()!!)
            loadData()
            loadDataToView()
            playMusic(currentPosition)
            playClick()
            replay30Click()
            forward30Click()
            nextClick()
            previousClick()
            menuClick()
            seekBarChanging()
            mediaEnded()
        return root
    }

    private fun mediaEnded() {
        mediaPlayer!!.setOnCompletionListener {
            fragmentFragmentsBinding.play1.setImageResource(R.drawable.play)
        }
    }
    private fun seekBarChanging() {
        fragmentFragmentsBinding.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (p2) {
                    mediaPlayer?.seekTo(p1)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })
    }

    private fun menuClick() {
        fragmentFragmentsBinding.menu.setOnClickListener {
            findNavController().popBackStack()
        }
    }
    private fun previousClick() {
        fragmentFragmentsBinding.right.setOnClickListener {
            if (mediaPlayer!!.isPlaying) {
                mediaPlayer!!.stop()
                mediaPlayer!!.release()
                mediaPlayer = null
                currentPosition++
                playMusic(currentPosition)
                loadDataToView()
            } else {
                mediaPlayer = null
                currentPosition++
                playMusic(currentPosition)
                loadDataToView()
            }

        }
    }
    private fun nextClick() {
        fragmentFragmentsBinding.left.setOnClickListener {
            if (mediaPlayer!!.isPlaying) {
                mediaPlayer!!.stop()
                mediaPlayer = null
                currentPosition--
                playMusic(currentPosition)
                loadDataToView()
            } else {
                mediaPlayer = null
                currentPosition--
                playMusic(currentPosition)
                loadDataToView()
            }
        }
    }


    private fun forward30Click() {
        fragmentFragmentsBinding.second2.setOnClickListener {
            if (mediaPlayer!!.isPlaying) {
                mediaPlayer?.seekTo(mediaPlayer?.currentPosition?.plus(3000)!!)
            } else {
                mediaPlayer?.seekTo(mediaPlayer?.currentPosition?.plus(3000)!!)
                mediaPlayer?.start()
                fragmentFragmentsBinding.play1.setImageResource(R.drawable.paouse)
            }
        }
    }
    private fun replay30Click() {
        fragmentFragmentsBinding.second.setOnClickListener {
            if (mediaPlayer!!.isPlaying) {
                mediaPlayer!!.seekTo(mediaPlayer?.currentPosition?.minus(3000)!!)
            } else {
                mediaPlayer!!.seekTo(mediaPlayer?.currentPosition?.minus(3000)!!)
                mediaPlayer!!.start()
                fragmentFragmentsBinding.play1.setImageResource(R.drawable.paouse)
            }
        }
    }

    private fun playClick() {
        fragmentFragmentsBinding.play1.setOnClickListener {
            if (mediaPlayer != null) {
                if (mediaPlayer?.isPlaying!!) {
                    mediaPlayer?.pause()
                    fragmentFragmentsBinding.play1.setImageResource(R.drawable.play)
                } else {
                    mediaPlayer?.start()
                    fragmentFragmentsBinding.play1.setImageResource(R.drawable.paouse)
                }
            } else {
                playMusic(currentPosition)
            }
        }
    }


    private fun playMusic(position: Int) {
        if (position < listMusics!!.size && position >= 0) {
            if (mediaPlayer == null) {
//                Toast.makeText(root.context, "${listMusics!![position].path}", Toast.LENGTH_SHORT).show()
//                Toast.makeText(root.context, "${Uri.parse(listMusics!![position].path)}", Toast.LENGTH_SHORT).show()
                mediaPlayer = MediaPlayer.create(
                    fragmentFragmentsBinding.root.context,
                    listMusics!![position].uri
                )
                //mediaPlayer!!.setDataSource()
                mediaPlayer?.start()
                fragmentFragmentsBinding.play1.setImageResource(R.drawable.paouse)
                fragmentFragmentsBinding.seekbar.max = mediaPlayer?.duration!!
                handler.postDelayed(runnable, 100)
            }
        } else {
            currentPosition = 0
            playMusic(currentPosition)
        }
    }


    private var runnable = object : Runnable {
        override fun run() {
            if (mediaPlayer != null) {
                fragmentFragmentsBinding.seekbar.progress = mediaPlayer!!.currentPosition
                handler.postDelayed(this, 100)
                setProgress(mediaPlayer!!.currentPosition)
            }
        }

    }

    private fun setProgress(duration: Int) {
        val format = String.format(
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(duration.toLong()),
            TimeUnit.MILLISECONDS.toSeconds(duration.toLong()) -
                    TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(
                            duration.toLong()
                        )
                    )
        )
        fragmentFragmentsBinding.govTime.text = format
    }

        private fun loadData() {
        listMusics = ArrayList()
        listMusics = ListMusic.listMusic!!
        musicFile = listMusics[position!!]
        currentPosition = position as Int
    }

    @SuppressLint("SetTextI18n")
    private fun loadDataToView() {
        if (currentPosition < listMusics!!.size && currentPosition>=0) {
            fragmentFragmentsBinding.number.text = "${currentPosition + 1}/${listMusics!!.size}"
            fragmentFragmentsBinding.mp3Name1.text = listMusics!![currentPosition].name
            fragmentFragmentsBinding.mp3Artist.text = listMusics!![currentPosition].artist
            fragmentFragmentsBinding.viewPager.setImageBitmap(getAlbumart(listMusics[currentPosition].albumId!!))
            fragmentFragmentsBinding.imageMp3View.setImageBitmap(getAlbumart(listMusics[currentPosition].albumId!!))
            fragmentFragmentsBinding.timeMp3.text = listMusics!![currentPosition].duration!!
        }
    }
    fun getAlbumart(album_id:Long): Bitmap {
        var bm: Bitmap?=null
        try {
            var sArtworkUri = Uri.parse("content://media/external/audio/albumart")
            var uri =  ContentUris.withAppendedId(sArtworkUri,album_id)
            var pfd: ParcelFileDescriptor = root.context?.contentResolver!!.openFileDescriptor(uri,"r")!!
            if (pfd!=null){
                var fd: FileDescriptor = pfd.fileDescriptor
                bm = BitmapFactory.decodeFileDescriptor(fd)
            }
        }catch (e: Exception){
            bm= BitmapFactory.decodeResource(
                root.context.resources,
                R.drawable.mmmm
            )
        }
        return bm!!
    }

    private fun realiseMp() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer!!.stop()
                mediaPlayer == null
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        realiseMp()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                FragmentsFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}