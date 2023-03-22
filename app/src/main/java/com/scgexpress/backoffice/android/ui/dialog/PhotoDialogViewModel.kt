package com.scgexpress.backoffice.android.ui.dialog

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.common.FileUploader
import com.scgexpress.backoffice.android.common.Utils
import com.scgexpress.backoffice.android.di.RxThreadScheduler
import com.scgexpress.backoffice.android.di.glide.GlideApp
import com.scgexpress.backoffice.android.model.PhotoStored
import com.scgexpress.backoffice.android.model.User
import com.scgexpress.backoffice.android.model.pickup.PickupTask
import com.scgexpress.backoffice.android.preference.LoginPreference
import com.scgexpress.backoffice.android.repository.LoginRepository
import com.scgexpress.backoffice.android.viewmodel.RxAndroidViewModel
import java.io.File
import javax.inject.Inject

class PhotoDialogViewModel @Inject constructor(
    application: Application,
    private val rxThreadScheduler: RxThreadScheduler,
    private val repo: LoginRepository,
    private val loginPreference: LoginPreference
) : RxAndroidViewModel(application) {

    private val context: Context
        get() = getApplication()

    val user: User
        get() = Utils.convertStringToUser(loginPreference.loginUser!!)

    private var _data: MutableLiveData<PickupTask> = MutableLiveData()
    val data: LiveData<PickupTask>
        get() = _data

    fun loadPhoto(photo: PhotoStored, imageView: ImageView) {
        if (displayPhotoByFilePath(photo, imageView)) return
        if (photo.url.isNullOrBlank()) {
            displayPhotoByUrl(null, imageView)
            return
        }

        val fileUploader = FileUploader(context, repo, loginPreference)
        fileUploader.downloadBitmap(
            photo.url
        )
            .subscribeOn(rxThreadScheduler.io())
            .observeOn(rxThreadScheduler.ui())
            .subscribe({
                if (it == null) return@subscribe
                displayPhotoByUrl(it, imageView)
            }) {
            }.also {
                addDisposable(it)
            }
    }

    private fun displayPhotoByFilePath(item: PhotoStored, imageView: ImageView): Boolean {
        val mImagePlaceholder: Drawable? =
            ContextCompat.getDrawable(imageView.context, R.drawable.ic_placeholder)

        if (!item.filePath.isNullOrBlank()) {
            GlideApp.with(imageView.context)
                .load(Uri.fromFile(File(item.filePath)))
                .placeholder(mImagePlaceholder)
                .centerCrop()
                .into(imageView)
            return true
        }
        return false
    }

    private fun displayPhotoByUrl(bitmap: Bitmap?, imageView: ImageView) {
        val mImagePlaceholder: Drawable? =
            ContextCompat.getDrawable(imageView.context, R.drawable.ic_placeholder)

        GlideApp.with(imageView.context)
            .load(bitmap)
            .placeholder(mImagePlaceholder)
            .centerCrop()
            .into(imageView)
    }
}