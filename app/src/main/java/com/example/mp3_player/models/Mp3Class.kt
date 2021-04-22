package com.example.mp3_player.models

import android.net.Uri
import java.io.Serializable

class Mp3Class:Serializable {
    var uri:Uri?=null
    var name: String? = null
    var artist:String?=null
    var duration:String?=null
    var albumId:Long? = null


    constructor(uri:Uri, name: String?, artist: String?, duration: String?, albumId: Long?) {
        this.uri = uri
        this.name = name
        this.artist = artist
        this.duration = duration
        this.albumId = albumId
    }

    constructor()
}