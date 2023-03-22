package com.scgexpress.backoffice.android.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.di.glide.GlideApp
import com.scgexpress.backoffice.android.model.PhotoStored
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.dialog_expand_photo.*
import javax.inject.Inject


class PhotoExpandDialogFragment : AppCompatDialogFragment(),
    HasSupportFragmentInjector {

    companion object {
        private const val KEY_PHOTO_STORED = "key_photo_stored"
        private const val KEY_PHOTO_BITMAP = "key_photo_bitmap"

        fun newInstance(photoStored: PhotoStored): PhotoExpandDialogFragment {
            val fragment = PhotoExpandDialogFragment()
            val bundle = Bundle()
            bundle.putParcelable(KEY_PHOTO_STORED, photoStored)
            fragment.arguments = bundle
            return fragment
        }
    }

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(PhotoDialogViewModel::class.java)
    }

    private var photo: Bitmap? = null
    private var photoStored: PhotoStored? = PhotoStored()

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentInjector
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogPhotoExpand)
        if (savedInstanceState == null) {
            restoreArguments(this.arguments!!)
        } else {
            restoreInstanceState(savedInstanceState)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_expand_photo, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Perform remaining operations here. No null issues.
        setupView()
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    private fun setupView() {
        if (photo != null) {
            setImage(imgView, photo!!)
        } else {
            setImage(imgView, photoStored!!)
        }
        btnClose.setOnClickListener {
            dismiss()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(KEY_PHOTO_BITMAP, photo)
        outState.putParcelable(KEY_PHOTO_STORED, photoStored)
    }

    private fun restoreInstanceState(bundle: Bundle) {
        photo = bundle.getParcelable(KEY_PHOTO_BITMAP)
        photoStored = bundle.getParcelable(KEY_PHOTO_STORED)
    }

    private fun restoreArguments(bundle: Bundle) {
        photo = bundle.getParcelable(KEY_PHOTO_BITMAP)
        photoStored = bundle.getParcelable(KEY_PHOTO_STORED)
    }

    private fun setImage(view: ImageView, image: Bitmap) {
        GlideApp.with(view.context)
            .load(image)
            .fitCenter()
            .into(view)
    }

    private fun setImage(view: ImageView, image: PhotoStored) {
        viewModel.loadPhoto(image, view)
    }
}