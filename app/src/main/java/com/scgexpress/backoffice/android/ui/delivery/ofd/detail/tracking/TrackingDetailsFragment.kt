package com.scgexpress.backoffice.android.ui.delivery.ofd.detail.tracking


import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.google.android.material.snackbar.Snackbar
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.aws.DeveloperAuthenticationProvider
import com.scgexpress.backoffice.android.common.*
import com.scgexpress.backoffice.android.common.Const.PARAMS_DIALOG_RE_STATUS
import com.scgexpress.backoffice.android.common.Const.REQUEST_CHOOSING_IMAGE
import com.scgexpress.backoffice.android.common.Const.REQUEST_READ_EXTERNAL_STORAGE
import com.scgexpress.backoffice.android.common.Const.REQUEST_TAKE_PHOTO
import com.scgexpress.backoffice.android.constant.ParcelStatus
import com.scgexpress.backoffice.android.model.MasterParcelModel
import com.scgexpress.backoffice.android.model.TrackingInfo
import com.scgexpress.backoffice.android.preference.LoginPreference
import com.scgexpress.backoffice.android.repository.LoginRepository
import com.scgexpress.backoffice.android.ui.delivery.ofd.detail.tracking.TrackingDetailsViewModel.Companion.IMAGE_RECIPIENT
import com.scgexpress.backoffice.android.ui.delivery.ofd.detail.tracking.TrackingDetailsViewModel.Companion.IMAGE_SIGNATURE
import com.scgexpress.backoffice.android.ui.delivery.ofd.sent.signature.SignatureActivity
import com.scgexpress.backoffice.android.ui.dialog.RejectStatusDialogFragment
import com.scgexpress.backoffice.android.ui.photo.PhotoExpandDialogFragment
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.fragment_tracking_details.*
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject


class TrackingDetailsFragment : Fragment(), HasSupportFragmentInjector {

    companion object {
        private lateinit var viewModel: TrackingDetailsViewModel
        fun newInstance(vm: TrackingDetailsViewModel): TrackingDetailsFragment {
            viewModel = vm
            return TrackingDetailsFragment().also {
                it.arguments = Bundle()
            }
        }
    }

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var loginRepository: LoginRepository

    @Inject
    lateinit var loginPreference: LoginPreference

    private lateinit var mContext: Context
    private lateinit var rootView: View

    private var trackingNo: String? = null

    private val s3Client by lazy {
        val developerProvider = DeveloperAuthenticationProvider(loginRepository, loginPreference, Const.AWS_POOL_ID, Regions.AP_SOUTHEAST_1)
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

    private var isPhotoAction = false

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentInjector
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)

        if (context is TrackingDetailsActivity) {
            mContext = context
        } else {
            throw IllegalStateException("This fragment must be use in conjunction with " + TrackingDetailsActivity::class.java.simpleName)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_tracking_details, container, false)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initButton()
        observeData()
    }

    override fun onResume() {
        super.onResume()
        if (!isPhotoAction) loadData()
        isPhotoAction = false
    }

    private fun loadData() {
        viewModel.getData()
    }

    private fun observeData() {
        viewModel.data.observe(this, Observer { it ->
            if (it == null) return@Observer
            mapData(it)
        })

        viewModel.scanDataList.observe(this, Observer {
            if (it == null) return@Observer
            startActivity(Intent(context, SignatureActivity::class.java)
                    .putExtra(Const.PARAMS_MANIFEST_ID, viewModel.manifestID)
                    .putExtra(Const.PARAMS_MANIFEST_SENT_LIST, it))
        })

        viewModel.snackbar.observe(this, Observer { it ->
            if (it != null) {
                it.getContentIfNotHandled()?.let {
                    // Only proceed if the event has never been handled
                    Snackbar.make(rootView, it, Snackbar.LENGTH_SHORT).show()
                }
            }
        })

        viewModel.warning.observe(this, Observer { it ->
            if (it != null) {
                it.getContentIfNotHandled()?.let {
                    // Only proceed if the event has never been handled
                    activity!!.showWarningDialog(it)
                }
            }
        })

        viewModel.finish.observe(this, Observer { it ->
            if (it != null) {
                it.getContentIfNotHandled()?.let {
                    // Only proceed if the event has never been handled
                    if (it) {
                        activity!!.onBackPressed()
                    }
                }
            }
        })

        viewModel.codDialog.observe(this, Observer { it ->
            if (it != null) {
                it.getContentIfNotHandled()?.let {
                    // Only proceed if the event has never been handled
                    viewModel.showDialogCod(rootView.context, it)
                }
            }
        })

        viewModel.getLocationHelper(context!!).observe(this, Observer<Location> { location ->
            if (location == null) return@Observer
            viewModel.latitude = location.latitude
            viewModel.longitude = location.longitude
        })
    }

    private fun mapData(data: TrackingInfo) {
        trackingNo = viewModel.trackingID
        edtTrackingNo.setText(trackingNo!!.toTrackingId())
        val orderCode = "Order code : $trackingNo"
        txtOrderCode.text = orderCode
        if (data.dateDelivered !== null)
            txtOrderDate.text = data.dateDelivered.toDateFormat()

        for (status: MasterParcelModel in ParcelStatus.list) {
            if (status.id == data.idStatus) {
                txtStatus.text = listOfNotNull(status.name, data.ofdNotes).joinToString(" - ")
            }
        }

        if (data.idStatus == "2" || data.idStatus == "7") {
            containerButton.visibility = View.VISIBLE
        } else {
            containerButton.visibility = View.GONE
        }

        if (data.idStatus == "34") {
            txtSigner.text = data.recipientSignName

            vDivider7.visibility = View.VISIBLE
            imgSignature.visibility = View.VISIBLE
            txtSigner.visibility = View.VISIBLE

            if (data.signatureImage.isNotEmpty())
                viewModel.loadPhoto(imgSignature, s3Client, data.signatureImage, IMAGE_SIGNATURE)
            else
                imgSignature.visibility = View.GONE
        } else {
            vDivider7.visibility = View.GONE
            imgSignature.visibility = View.GONE
            txtSigner.visibility = View.GONE
        }

        txtRecipient.text = data.recipientName
        txtPhone.text = data.recipientTel
        val recipientAddress = listOfNotNull(data.recipientAddress, data.recipientDistrict,
                data.recipientProvince, data.recipientZipcode).joinToString(", ")
        txtAddress.text = recipientAddress

        if (trackingNo!!.startsWith("11")) {
            txtCod.text = Utils.setCurrencyFormat(data.codAmount.toDouble())
        } else {
            cvCodAmount.visibility = View.GONE
        }

        txtRemark.text = data.userNote

        txtSender.text = data.senderName
        txtSenderCode.text = data.senderCode
        val senderAddress = listOfNotNull(data.senderAddress, data.senderDistrict,
                data.senderProvince, data.senderZipcode).joinToString(", ")
        txtSenderAddress.text = senderAddress
        txtSenderPhone.text = data.senderTel

        if (data.recipientImage.isNotEmpty())
            viewModel.loadPhoto(imgPhoto, s3Client, data.recipientImage[data.recipientImage.lastIndex], IMAGE_RECIPIENT)

        btnPhone.setOnClickListener {
            if (!viewModel.checkLastClickTime()) return@setOnClickListener
            Utils.phoneCall(this.activity!!, data.recipientTel)
        }

        imgPhoto.setOnClickListener {
            if (!viewModel.checkLastClickTime()) return@setOnClickListener
            if (viewModel.recipientBitmap != null) {
                val fragment = PhotoExpandDialogFragment.newInstance(viewModel.recipientBitmap!!)
                fragment.show(childFragmentManager, "PHOTO_EXPAND_DIALOG")
            }
        }

        imgSignature.setOnClickListener {
            if (!viewModel.checkLastClickTime()) return@setOnClickListener
            if (viewModel.signatureBitmap != null) {
                val fragment = PhotoExpandDialogFragment.newInstance(viewModel.signatureBitmap!!)
                fragment.show(childFragmentManager, "PHOTO_EXPAND_DIALOG")
            }
        }
    }

    private fun initButton() {
        btnAddPhoto.setOnClickListener {
            if (!viewModel.checkLastClickTime()) return@setOnClickListener
            showPictureDialog()
        }

        btnDelivery.setOnClickListener {
            if (!viewModel.checkLastClickTime()) return@setOnClickListener
            viewModel.checkCod()
        }

        btnRetention.setOnClickListener {
            if (!viewModel.checkLastClickTime()) return@setOnClickListener
            viewModel.showDialogRetention(btnRetention.context, viewModel.manifestID)
        }

        btnReStatus.setOnClickListener {
            if (!viewModel.checkLastClickTime()) return@setOnClickListener
            val fragment = RejectStatusDialogFragment.newInstance()
            fragment.show(childFragmentManager, PARAMS_DIALOG_RE_STATUS)
        }
    }

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(mContext)
        pictureDialog.setTitle(getString(R.string.photo_select_method_title))
        val pictureDialogItems = arrayOf(getString(R.string.photo_select_method_gallery), getString(R.string.photo_select_method_camera))
        pictureDialog.setItems(pictureDialogItems
        ) { _, which ->
            when (which) {
                0 -> choosePhotoFromGallery()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }

    private fun choosePhotoFromGallery() {
        if (ActivityCompat.checkSelfPermission(activity!!,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity!!, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_READ_EXTERNAL_STORAGE)
        } else {
            val galleryIntent = Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

            startActivityForResult(galleryIntent, REQUEST_CHOOSING_IMAGE)
            isPhotoAction = true
        }
    }

    private fun takePhotoFromCamera() {
        if (ActivityCompat.checkSelfPermission(activity!!,
                        android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity!!, arrayOf(android.Manifest.permission.CAMERA,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_READ_EXTERNAL_STORAGE)
        } else {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, REQUEST_TAKE_PHOTO)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CHOOSING_IMAGE) {
            if (data != null) {
                val contentURI = data.data
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(mContext.contentResolver, contentURI)
                    val path = viewModel.saveImage(bitmap)
                    viewModel.upPhoto(transferUtility, imgPhoto, path, this.trackingNo!!)
                    //Toast.makeText(mContext, "Image Saved!", Toast.LENGTH_SHORT).show()
                    imgPhoto.setImageBitmap(bitmap)
                    viewModel.setImage(imgPhoto, bitmap)

                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(mContext, "Failed!", Toast.LENGTH_SHORT).show()
                }

            }

        } else if (requestCode == REQUEST_TAKE_PHOTO) {
            try {
                val thumbnail = data!!.extras!!.get("data") as Bitmap
                viewModel.setImage(imgPhoto, thumbnail)
                val path = viewModel.saveImage(thumbnail)
                viewModel.upPhoto(transferUtility, imgPhoto, path, this.trackingNo!!)
                //Toast.makeText(mContext, "Image Saved!", Toast.LENGTH_SHORT).show()
            } catch (ex: Exception) {
                Timber.e(ex)
            }
        }
    }
}
