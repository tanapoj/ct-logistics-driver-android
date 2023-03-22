package com.scgexpress.backoffice.android.ui.delivery.ofd.detail.item

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.google.android.material.snackbar.Snackbar
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.aws.DeveloperAuthenticationProvider
import com.scgexpress.backoffice.android.common.Const
import com.scgexpress.backoffice.android.common.Const.PARAMS_MANIFEST_BOOKING_INFO
import com.scgexpress.backoffice.android.common.Const.PARAMS_MANIFEST_COMPLETED
import com.scgexpress.backoffice.android.common.Const.PARAMS_MANIFEST_FILTER_ALL
import com.scgexpress.backoffice.android.common.Const.PARAMS_MANIFEST_FILTER_BOOKING
import com.scgexpress.backoffice.android.common.Const.PARAMS_MANIFEST_FILTER_TRACKING
import com.scgexpress.backoffice.android.common.Const.PARAMS_MANIFEST_ID
import com.scgexpress.backoffice.android.common.Const.PARAMS_MANIFEST_IN_PROGRESS
import com.scgexpress.backoffice.android.common.Const.PARAMS_MANIFEST_ITEM_LIST
import com.scgexpress.backoffice.android.common.Const.PARAMS_MANIFEST_TAB_TYPE
import com.scgexpress.backoffice.android.common.Const.PARAMS_TRACKING_ID
import com.scgexpress.backoffice.android.common.Utils
import com.scgexpress.backoffice.android.preference.LoginPreference
import com.scgexpress.backoffice.android.repository.LoginRepository
import com.scgexpress.backoffice.android.ui.delivery.ofd.detail.OfdDetailActivity
import com.scgexpress.backoffice.android.ui.delivery.ofd.detail.OfdDetailViewModel
import com.scgexpress.backoffice.android.ui.delivery.ofd.detail.booking.BookingDetailsActivity
import com.scgexpress.backoffice.android.ui.delivery.ofd.detail.item.dragged.OfdDetailItemsDraggableActivity
import com.scgexpress.backoffice.android.ui.delivery.ofd.detail.search.OfdItemSearchActivity
import com.scgexpress.backoffice.android.ui.delivery.ofd.detail.tracking.TrackingDetailsActivity
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.fragment_ofd_detail.*
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class OfdDetailItemsFragment : Fragment(), HasSupportFragmentInjector {

    companion object {
        private lateinit var manifestId: String

        fun newInstance(id: String, type: String):
                OfdDetailItemsFragment = OfdDetailItemsFragment().apply {
            manifestId = id
            val args = Bundle()
            args.putString(PARAMS_MANIFEST_TAB_TYPE, type)
            this.arguments = args
        }
    }

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var loginRepository: LoginRepository

    @Inject
    lateinit var loginPreference: LoginPreference

    private lateinit var mContext: OfdDetailActivity
    private lateinit var rootView: View
    private lateinit var viewModel: OfdDetailViewModel

    private val adapter: OfdDetailItemsAdapter by lazy {
        OfdDetailItemsAdapter(viewModel)
    }

    private val s3Client by lazy {
        val developerProvider =
            DeveloperAuthenticationProvider(loginRepository, loginPreference, Const.AWS_POOL_ID, Regions.AP_SOUTHEAST_1)
        val credentialsProvider = CognitoCachingCredentialsProvider(
            mContext,
            developerProvider,
            Regions.AP_SOUTHEAST_1
        )
        AmazonS3Client(credentialsProvider)
    }

    private val transferUtility by lazy {
        TransferUtility.builder()
            .context(mContext)
            .awsConfiguration(AWSMobileClient.getInstance().configuration)
            .s3Client(s3Client)
            .build()
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

        if (context is OfdDetailActivity) {
            mContext = context
        } else {
            throw IllegalStateException("This fragment must be use in conjunction with " + OfdDetailActivity::class.java.simpleName)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(OfdDetailViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_ofd_detail, container, false)
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
        loadData()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_search -> {
                val intent = Intent(mContext, OfdItemSearchActivity::class.java)
                    .putExtra(PARAMS_MANIFEST_ID, mContext.getOfdManifestBarcode())
                    .putExtra(PARAMS_MANIFEST_ITEM_LIST, viewModel.getData())
                startActivity(intent)
                true
            }
            R.id.menu_ofd -> {
                viewModel.filterData(PARAMS_MANIFEST_FILTER_TRACKING)
                true
            }
            R.id.menu_booking -> {
                viewModel.filterData(PARAMS_MANIFEST_FILTER_BOOKING)
                true
            }
            R.id.menu_all -> {
                viewModel.filterData(PARAMS_MANIFEST_FILTER_ALL)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadData() {
        viewModel.requestItem(manifestId)
    }

    private fun initRecyclerView() {
        rvItems.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvItems.adapter = adapter
    }

    private fun observeData() {
        viewModel.dataInProgress.observe(this, Observer { it ->
            if (it == null) return@Observer
            //STOPSHIP fix this : incomplete API
            if (arguments!!.getString(PARAMS_MANIFEST_TAB_TYPE) == PARAMS_MANIFEST_IN_PROGRESS) {
                adapter.data = it
                rvItems.smoothScrollToPosition(0)
                mContext.updatePageTitle(0, it.size)
            }
        })

        viewModel.dataCompleted.observe(this, Observer { it ->
            if (it == null) return@Observer
            //STOPSHIP fix this : incomplete API
            if (arguments!!.getString(PARAMS_MANIFEST_TAB_TYPE) == PARAMS_MANIFEST_COMPLETED) {
                adapter.data = it
                rvItems.smoothScrollToPosition(0)
                mContext.updatePageTitle(1, it.size)
            }
        })

        viewModel.dataFilterInProgress.observe(this, Observer { it ->
            if (it == null) return@Observer
            //STOPSHIP fix this : incomplete API
            if (arguments!!.getString(PARAMS_MANIFEST_TAB_TYPE) == PARAMS_MANIFEST_IN_PROGRESS) {
                adapter.data = it
                rvItems.smoothScrollToPosition(0)
                mContext.updatePageTitle(0, it.size)
            }
        })

        viewModel.dataFilterCompleted.observe(this, Observer { it ->
            if (it == null) return@Observer
            //STOPSHIP fix this : incomplete API
            if (arguments!!.getString(PARAMS_MANIFEST_TAB_TYPE) == PARAMS_MANIFEST_COMPLETED) {
                adapter.data = it
                rvItems.smoothScrollToPosition(0)
                mContext.updatePageTitle(1, it.size)
            }
        })

        viewModel.dataTrackingPosition.observe(this, Observer { it ->
            if (it == null) return@Observer
        })

        viewModel.itemTrackingClick.observe(this, Observer {
            if (it == null) return@Observer
            if (it.trackingNumber == null || it.id == null) {
                viewModel.showSnackbar(getString(R.string.sentence_data_not_found))
                return@Observer
            }

            val intent = Intent(context, TrackingDetailsActivity::class.java)
                .putExtra(PARAMS_MANIFEST_ID, manifestId)
                .putExtra(PARAMS_TRACKING_ID, it.trackingNumber!!)
            startActivity(intent)
        })

        viewModel.itemLongClick.observe(this, Observer {
            if (it == null) return@Observer
            if (it.item.size > 1) {
                val intent = Intent(context, OfdDetailItemsDraggableActivity::class.java)
                    .putExtra(PARAMS_MANIFEST_ID, manifestId)
                    .putExtra(PARAMS_MANIFEST_ITEM_LIST, it)
                startActivity(intent)
            }
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
        srlItems.setOnRefreshListener { viewModel.refresh(manifestId) }
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
                    viewModel.upPhoto(transferUtility, path, trackingNo)
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(mContext, "Failed!", Toast.LENGTH_SHORT).show()
                }

            }

        } else if (requestCode == Const.REQUEST_TAKE_PHOTO) {
            try {
                val thumbnail = data!!.extras!!.get("data") as Bitmap
                val path = viewModel.saveImage(thumbnail)
                viewModel.upPhoto(transferUtility, path, trackingNo)
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }
}