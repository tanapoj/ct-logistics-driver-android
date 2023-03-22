package com.scgexpress.backoffice.android.ui.delivery.ofd.detail.search


import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.common.Const
import com.scgexpress.backoffice.android.common.Const.PARAMS_MANIFEST_BOOKING_INFO
import com.scgexpress.backoffice.android.common.Const.PARAMS_MANIFEST_ID
import com.scgexpress.backoffice.android.common.Const.PARAMS_TRACKING_ID
import com.scgexpress.backoffice.android.common.Utils
import com.scgexpress.backoffice.android.ui.delivery.ofd.detail.OfdDetailActivity
import com.scgexpress.backoffice.android.ui.delivery.ofd.detail.booking.BookingDetailsActivity
import com.scgexpress.backoffice.android.ui.delivery.ofd.detail.tracking.TrackingDetailsActivity
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.fragment_ofd_item_search.*
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class OfdItemSearchFragment : Fragment(), HasSupportFragmentInjector {

    companion object {
        private lateinit var viewModel: OfdItemSearchViewModel

        fun newInstance(vm: OfdItemSearchViewModel):
                OfdItemSearchFragment = OfdItemSearchFragment().apply {
            viewModel = vm
            this.arguments = Bundle()
        }
    }

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    private lateinit var mContext: OfdItemSearchActivity
    private lateinit var rootView: View

    private val adapter: OfdItemSearchAdapter by lazy {
        OfdItemSearchAdapter(viewModel)
    }

    private val refreshObserver =
        Observer<Boolean> {
            srlItems.isRefreshing = it!!
        }

    private var trackingNo = ""

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentInjector
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)

        if (context is OfdItemSearchActivity) {
            mContext = context
        } else {
            throw IllegalStateException("This fragment must be use in conjunction with " + OfdDetailActivity::class.java.simpleName)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_ofd_item_search, container, false)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initRecyclerView()
        initRefreshLayout()
        observeData()
    }

    override fun onResume() {
        super.onResume()
    }

    private fun initRecyclerView() {
        rvItems.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvItems.adapter = adapter
    }

    private fun observeData() {
        viewModel.dataSearch.observe(this, Observer { it ->
            if (it == null) return@Observer
            adapter.data = it
            rvItems.smoothScrollToPosition(0)
        })

        viewModel.itemTrackingClick.observe(this, Observer {
            if (it == null) return@Observer
            if (it.trackingNumber == null) {
                viewModel.showSnackbar(getString(R.string.sentence_data_not_found))
                return@Observer
            }
            val intent = Intent(context, TrackingDetailsActivity::class.java)
                .putExtra(PARAMS_MANIFEST_ID, viewModel.manifestBarcode.value)
                .putExtra(PARAMS_TRACKING_ID, it.trackingNumber!!)
            startActivity(intent)
        })

        viewModel.itemBookingClick.observe(this, Observer {
            if (it == null) return@Observer
            val intent = Intent(context, BookingDetailsActivity::class.java)
                .putExtra(PARAMS_MANIFEST_BOOKING_INFO, it)
            startActivity(intent)
        })

        viewModel.phoneCall.observe(this, Observer {
            if (it == null) return@Observer
            Utils.phoneCall(this.activity!!, it)
        })

        viewModel.photo.observe(this, Observer {
            if (it == null) return@Observer
            trackingNo = it
            showPictureDialog()
        })

        viewModel.snackbar.observe(this, Observer { it ->
            if (it != null) {
                it.getContentIfNotHandled()?.let {
                    // Only proceed if the event has never been handled
                    Snackbar.make(rootView, it, Snackbar.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun initRefreshLayout() {
        srlItems.setColorSchemeColors(ContextCompat.getColor(context!!, R.color.colorAccent))
        srlItems.setOnRefreshListener { viewModel.refresh() }
        viewModel.refreshing
            .observe(this, refreshObserver)
    }

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(mContext)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Gallery", "Camera")
        pictureDialog.setItems(
            pictureDialogItems
        ) { _, which ->
            when (which) {
                0 -> choosePhotoFromGallery()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }

    private fun choosePhotoFromGallery() {
        if (ActivityCompat.checkSelfPermission(
                activity!!,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity!!, arrayOf(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), Const.REQUEST_READ_EXTERNAL_STORAGE
            )
        } else {
            val galleryIntent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )

            startActivityForResult(galleryIntent, Const.REQUEST_CHOOSING_IMAGE)
        }
    }

    private fun takePhotoFromCamera() {
        if (ActivityCompat.checkSelfPermission(
                activity!!,
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity!!, arrayOf(
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), Const.REQUEST_READ_EXTERNAL_STORAGE
            )
        } else {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, Const.REQUEST_TAKE_PHOTO)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Const.REQUEST_CHOOSING_IMAGE) {
            if (data != null) {
                val contentURI = data.data
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(mContext.contentResolver, contentURI)
                    val path = viewModel.saveImage(bitmap)
                    viewModel.upPhoto(path, this.trackingNo)
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(mContext, "Failed!", Toast.LENGTH_SHORT).show()
                }

            }

        } else if (requestCode == Const.REQUEST_TAKE_PHOTO) {
            try {
                val thumbnail = data!!.extras!!.get("data") as Bitmap
                val path = viewModel.saveImage(thumbnail)
                viewModel.upPhoto(path, this.trackingNo)
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }
}
