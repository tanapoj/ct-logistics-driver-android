package com.scgexpress.backoffice.android.ui.delivery.detail

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
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.common.*
import com.scgexpress.backoffice.android.common.Const.PARAMS_DELIVERY_TASK_COMPLETED
import com.scgexpress.backoffice.android.common.Const.PARAMS_DELIVERY_TASK_IN_PROGRESS
import com.scgexpress.backoffice.android.common.Const.PARAMS_DELIVERY_TASK_RETENTION
import com.scgexpress.backoffice.android.common.Const.PARAMS_DIALOG_RE_STATUS
import com.scgexpress.backoffice.android.common.Const.REQUEST_CHOOSING_IMAGE
import com.scgexpress.backoffice.android.common.Const.REQUEST_READ_EXTERNAL_STORAGE
import com.scgexpress.backoffice.android.common.Const.REQUEST_TAKE_PHOTO
import com.scgexpress.backoffice.android.model.PhotoStored
import com.scgexpress.backoffice.android.model.PhotoTitle
import com.scgexpress.backoffice.android.model.delivery.DeliveryTask
import com.scgexpress.backoffice.android.preference.LoginPreference
import com.scgexpress.backoffice.android.repository.LoginRepository
import com.scgexpress.backoffice.android.ui.delivery.ofd.sent.signature.SignatureActivity
import com.scgexpress.backoffice.android.ui.dialog.PhotoDialogViewModel
import com.scgexpress.backoffice.android.ui.dialog.PhotoSelectDialogFragment
import com.scgexpress.backoffice.android.ui.dialog.RejectStatusDialogFragment
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.fragment_pickup_details.btnPhone
import kotlinx.android.synthetic.main.fragment_pickup_details.containerButton
import kotlinx.android.synthetic.main.fragment_pickup_details.txtEmail
import kotlinx.android.synthetic.main.fragment_pickup_details.txtOrderDate
import kotlinx.android.synthetic.main.fragment_pickup_details.txtPhone
import kotlinx.android.synthetic.main.fragment_pickup_details.txtRemark
import kotlinx.android.synthetic.main.fragment_pickup_details.txtSender
import kotlinx.android.synthetic.main.fragment_pickup_details.txtStatus
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

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var mContext: Context
    private lateinit var rootView: View

    private var trackingNo: String? = null

    private val viewModelPhoto by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(PhotoDialogViewModel::class.java)
    }

    private var isPhotoAction = false

    private lateinit var mListener: PhotoSelectDialogFragment.OnOptionSelectedListener

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentInjector
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)

        if (context is TrackingDetailsActivity) {
            mContext = context
            mListener = context
        } else {
            throw IllegalStateException("This fragment must be use in conjunction with " + TrackingDetailsActivity::class.java.simpleName)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
            startActivity(
                Intent(context, SignatureActivity::class.java)
                    .putExtra(Const.PARAMS_MANIFEST_ID, viewModel.manifestID)
                    .putExtra(Const.PARAMS_MANIFEST_SENT_LIST, it)
            )
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
                    activity!!.showAlertMessage(it)
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

        /*viewModelRetentionReason.getLocationHelper(context!!).observe(this, Observer<Location> { location ->
            if (location == null) return@Observer
            viewModelRetentionReason.latitude = location.latitude
            viewModelRetentionReason.longitude = location.longitude
        })*/
    }

    private fun mapData(data: DeliveryTask) {
        trackingNo = viewModel.trackingID
        edtTrackingNo.text = trackingNo!!.toTrackingId()
        val orderCode = "Order code : $trackingNo"
        txtOrderCode.text = orderCode

        txtOrderDate.text = "(${data.deliveryAt.toDateTimeFormat()})"

        txtStatus.text = data.deliveryStatus

        if (data.deliveryStatus == PARAMS_DELIVERY_TASK_IN_PROGRESS || data.deliveryStatus == PARAMS_DELIVERY_TASK_RETENTION) {
            containerButton.visibility = View.VISIBLE
        } else {
            containerButton.visibility = View.GONE
        }

        if (data.deliveryStatus == PARAMS_DELIVERY_TASK_COMPLETED) {
            txtSigner.text = data.recipientSignedName

            vDivider7.visibility = View.VISIBLE
            imgSignature.visibility = View.VISIBLE
            txtSigner.visibility = View.VISIBLE

            if (data.recipientSignatureImgUrl!!.isNotEmpty() || data.recipientSignatureImgPath!!.isNotEmpty())
                viewModelPhoto.loadPhoto(
                    PhotoStored(
                        data.recipientSignatureImgUrl,
                        data.recipientSignatureImgPath
                    ), imgSignature
                )
            else
                imgSignature.visibility = View.GONE
        } else {
            vDivider7.visibility = View.GONE
            imgSignature.visibility = View.GONE
            txtSigner.visibility = View.GONE
        }

        txtRecipient.text = data.recipientName
        txtPhone.text = data.recipientTel
        val recipientAddress = listOfNotNull(
            data.recipientLocation.address, data.recipientLocation.zipcode
        ).joinToString(", ")
        txtEmail.text = recipientAddress

        if (trackingNo!!.startsWith("11")) {
            txtCod.text = Utils.setCurrencyFormat(data.codAmount!!.toDouble())
        } else {
            cvCodAmount.visibility = View.GONE
        }

        txtRemark.text = data.remark

        txtSender.text = "Sender : ${data.senderName}"
        /*val senderAddress = listOfNotNull(
            data.sen, data.senderDistrict,
            data.senderProvince, data.senderZipcode
        ).joinToString(", ")
        txtSenderAddress.text = senderAddress*/
        txtSenderPhone.text = data.senderTel

        if (data.productImgUrl!!.isNotEmpty() || data.productImgPath!!.isNotEmpty())
            viewModelPhoto.loadPhoto(
                PhotoStored(
                    data.productImgUrl,
                    data.productImgPath
                ), imgPhoto
            )

        btnPhone.setOnClickListener {
            if (!viewModel.checkLastClickTime()) return@setOnClickListener
            Utils.phoneCall(this.activity!!, data.recipientTel)
        }

        btnSenderPhone.setOnClickListener {
            if (!viewModel.checkLastClickTime()) return@setOnClickListener
            Utils.phoneCall(this.activity!!, data.senderTel)
        }

        imgPhoto.setOnClickListener {
            /*if (!viewModelRetentionReason.checkLastClickTime()) return@setOnClickListener
            if (viewModelRetentionReason.recipientBitmap != null) {
                val fragment = PhotoExpandDialogFragment.newInstance(viewModelRetentionReason.recipientBitmap!!)
                fragment.show(childFragmentManager, "PHOTO_EXPAND_DIALOG")
            }*/
        }

        imgSignature.setOnClickListener {
            /*if (!viewModelRetentionReason.checkLastClickTime()) return@setOnClickListener
            if (viewModelRetentionReason.signatureBitmap != null) {
                val fragment = PhotoExpandDialogFragment.newInstance(viewModelRetentionReason.signatureBitmap!!)
                fragment.show(childFragmentManager, "PHOTO_EXPAND_DIALOG")
            }*/
        }

        txtService.text = "Normal - Doc40"
        txtReverse.text = "รอรับ"
        txtShipper.text = "Shipper code : 99999 - Shipper name : Shippop"
        txtStatus.text = "Status (Update 2019-02-19 17:28)"
    }

    private fun initButton() {
        btnAddPhoto.setOnClickListener {
            if (!viewModel.checkLastClickTime()) return@setOnClickListener
            showPictureDialog("https://brandinside.asia/wp-content/uploads/2017/01/scg-express-4.jpg")
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

    private fun showPictureDialog(url: String) {
        val pictureDialog = AlertDialog.Builder(mContext)
        pictureDialog.setTitle(getString(R.string.photo_select_method_title))
        val pictureDialogItems =
            arrayOf(getString(R.string.photo_select_method_camera), getString(R.string.view_photo))
        pictureDialog.setItems(
            pictureDialogItems
        ) { _, which ->
            when (which) {
                0 -> takePhotoFromCamera()
                1 -> choosePhotoFromGallery(url)
            }
        }
        pictureDialog.show()
    }

    private fun choosePhotoFromGallery(url: String) {
        mListener.onStatusSelected(PhotoTitle("", PhotoStored(url, "")))
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
                ), REQUEST_READ_EXTERNAL_STORAGE
            )
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
                    val bitmap =
                        MediaStore.Images.Media.getBitmap(mContext.contentResolver, contentURI)
                    val path = viewModel.saveImage(bitmap)
                    //viewModelRetentionReason.upPhoto(transferUtility, imgPhoto, path, this.trackingNo!!)
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
                //viewModelRetentionReason.upPhoto(transferUtility, imgPhoto, path, this.trackingNo!!)
                //Toast.makeText(mContext, "Image Saved!", Toast.LENGTH_SHORT).show()
            } catch (ex: Exception) {
                Timber.e(ex)
            }
        }
    }
}
