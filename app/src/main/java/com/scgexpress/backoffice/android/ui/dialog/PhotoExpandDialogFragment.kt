package com.scgexpress.backoffice.android.ui.photo

import android.app.Dialog
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatDialogFragment
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.di.glide.GlideApp
import kotlinx.android.synthetic.main.dialog_expand_photo.*


class PhotoExpandDialogFragment : AppCompatDialogFragment() {

    companion object {
        private const val KEY_PHOTO = "key_photo"

        fun newInstance(photo: Bitmap): PhotoExpandDialogFragment {
            val fragment = PhotoExpandDialogFragment()
            val bundle = Bundle()
            bundle.putParcelable(KEY_PHOTO, photo)
            fragment.arguments = bundle
            return fragment
        }
    }

    private var photo: Bitmap? = null

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
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        //dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Perform remaining operations here. No null issues.
        setupView()
    }

    private fun setupView() {
        setImage(imgView, photo!!)
        btnClose.setOnClickListener {
            dismiss()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(KEY_PHOTO, photo)
    }

    private fun restoreInstanceState(bundle: Bundle) {
        photo = bundle.getParcelable(KEY_PHOTO)
    }

    private fun restoreArguments(bundle: Bundle) {
        photo = bundle.getParcelable(KEY_PHOTO)
    }

    private fun setImage(view: ImageView, image: Any) {
        GlideApp.with(view.context)
            .load(image)
            .fitCenter()
            .into(view)
    }
}