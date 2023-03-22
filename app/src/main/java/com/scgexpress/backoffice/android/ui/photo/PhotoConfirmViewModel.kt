package com.scgexpress.backoffice.android.ui.photo

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.common.Const
import com.scgexpress.backoffice.android.common.FileHelper
import com.scgexpress.backoffice.android.common.Utils
import com.scgexpress.backoffice.android.di.glide.GlideApp
import com.scgexpress.backoffice.android.ui.pickup.main.PickupMainViewModel
import com.scgexpress.backoffice.android.viewmodel.RxAndroidViewModel
import javax.inject.Inject

class PhotoConfirmViewModel@Inject constructor(application: Application) : RxAndroidViewModel(application)  {

    private val context: Context
        get() = getApplication()

    //photo
    private val _photoBitmap = MutableLiveData<Bitmap?>()
    val photoBitmap: LiveData<Bitmap?>
        get() = _photoBitmap

    private val fileHelper = FileHelper.helper

    private val mImagePlaceholder: Drawable? =
        ContextCompat.getDrawable(application, R.drawable.ic_placeholder)

    fun savePhoto(bitmap: Bitmap) {
        val filename = "${Utils.getCurrentDateInMills()}"
        fileHelper.saveImageJpeg(context, Const.DIRECTORY_PICKUP, filename, bitmap)
            ?.let { savedFile ->
                _photoBitmap.value = bitmap
                PickupMainViewModel.DataHolder.senderPhoto = savedFile
            }
    }

    fun upLoadImage(){

    }

    internal fun setImage(view: ImageView, url: Any) {
        GlideApp.with(view.context)
            .load(url)
            .placeholder(mImagePlaceholder)
            .centerCrop()
            .into(view)
    }
}