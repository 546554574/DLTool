package com.toune.dltools.dialog

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.toune.dltools.R
import com.yanzhenjie.album.AlbumFile
import com.yanzhenjie.album.AlbumLoader

class MediaLoader : AlbumLoader {
    override fun load(imageView: ImageView, albumFile: AlbumFile) {
        load(imageView, albumFile.path)
    }

    override fun load(imageView: ImageView, url: String) {
        Glide.with(imageView.context)
            .load(url)
            .apply(
                RequestOptions()
                    .error(R.drawable.logo)
                    .placeholder(R.drawable.logo)
            )
            .into(imageView)
    }
}