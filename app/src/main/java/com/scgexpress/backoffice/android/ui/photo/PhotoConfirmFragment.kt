package com.scgexpress.backoffice.android.ui.photo

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.scgexpress.backoffice.android.R
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.fragment_take_photo.*
import javax.inject.Inject
import android.provider.MediaStore
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.scgexpress.backoffice.android.common.Const
import com.scgexpress.backoffice.android.common.Const.REQUEST_TAKE_PHOTO
import com.scgexpress.backoffice.android.common.FileHelper
import timber.log.Timber

class PhotoConfirmFragment :Fragment(), HasSupportFragmentInjector {

    companion object {
        private lateinit var viewModel: PhotoConfirmViewModel
        fun newInstance(vm: PhotoConfirmViewModel): PhotoConfirmFragment {
            viewModel = vm
            return PhotoConfirmFragment().also {
                it.arguments = Bundle()
            }
        }
    }

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    private lateinit var mContext: Context
    private lateinit var rootView: View
    private var cameraPhotoUri: Uri? = null

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentInjector
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is PhotoConfirmActivity) {
            mContext = context
        } else {
            throw IllegalStateException("This fragment must be use in conjunction with " + PhotoConfirmActivity::class.java.simpleName)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        takePhotoFromCamera(REQUEST_TAKE_PHOTO)
        initButton()
        observeData()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_take_photo, container, false)
        return rootView
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TAKE_PHOTO) {
            try {
                parseActivityResultForPhoto(requestCode,resultCode)
            } catch (ex: Exception) {
                Timber.e(ex)
            }
        }
    }

    private fun initButton(){
        btnSaveImage.setOnClickListener {
            viewModel.upLoadImage()
            activity!!.finish()
        }
    }

    fun observeData(){
        viewModel.photoBitmap.observe(this, Observer { bitmap ->
            if (bitmap != null) {
                viewModel.setImage(imgResult,bitmap)
            }
        })
    }

    private fun parseActivityResultForPhoto(requestCode: Int, resultCode: Int) {

        try {
            val bitmap = when (requestCode) {
                REQUEST_TAKE_PHOTO -> cameraPhotoUri?.let { uri ->
                    val bitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver, uri)
                    bitmap
                }
                else -> return
            }

            Timber.d("parseActivityResultForPhoto() requestCode=$requestCode resultCode=$resultCode $cameraPhotoUri")

            bitmap?.let {
                when (requestCode) {
                    REQUEST_TAKE_PHOTO -> viewModel.savePhoto(
                        it
                    )
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun takePhotoFromCamera(requestCode: Int) {

        fun checkPermission() = ActivityCompat.checkSelfPermission(
            activity!!, Manifest.permission.CAMERA
        ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
            activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED

        if (checkPermission()) {
            ActivityCompat.requestPermissions(
                activity!!,
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                Const.REQUEST_READ_EXTERNAL_STORAGE
            )
        }
        if (checkPermission()) {
            return
        }

        //val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        //startActivityForResult(intent, photoId)

        activity?.apply {
            val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
            Log.i("-camera", "storageDir = $storageDir")
            val (intent, uri) = FileHelper.helper.getDispatchTakePhotoIntent(
                this, packageManager, storageDir
            )
            cameraPhotoUri = uri
            startActivityForResult(intent, requestCode)
        }
    }

//    private fun takePhotoFromCamera() {
//        if (ActivityCompat.checkSelfPermission(activity!!,
//                android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(activity!!, arrayOf(android.Manifest.permission.CAMERA,
//                android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
//                Const.REQUEST_READ_EXTERNAL_STORAGE
//            )
//        } else {
//            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//            startActivityForResult(intent, Const.REQUEST_TAKE_PHOTO)
//        }
//    }
}