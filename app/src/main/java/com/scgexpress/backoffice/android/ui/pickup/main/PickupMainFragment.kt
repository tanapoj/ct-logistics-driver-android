package com.scgexpress.backoffice.android.ui.pickup.main

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.zxing.integration.android.IntentIntegrator
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.camera.QrScannerController
import com.scgexpress.backoffice.android.common.*
import com.scgexpress.backoffice.android.common.Const.FLAG_PICKUP_BOOKING_LIST_CONTINUE_NEXT_TASK
import com.scgexpress.backoffice.android.common.Const.PARAMS_PICKUP_SEARCH_SHIPPER_CODE
import com.scgexpress.backoffice.android.common.Const.PARAMS_PICKUP_TASK_ID
import com.scgexpress.backoffice.android.common.Const.PARAMS_PICKUP_TASK_TOTAL_COUNT
import com.scgexpress.backoffice.android.common.listener.DrawableClickListener
import com.scgexpress.backoffice.android.db.entity.masterdata.TblScgPostalcode
import com.scgexpress.backoffice.android.di.glide.GlideApp
import com.scgexpress.backoffice.android.repository.pickup.ContextWrapper
import com.scgexpress.backoffice.android.repository.pickup.IdAndValue
import com.scgexpress.backoffice.android.ui.pickup.bookingList.PickupBookingListActivity
import com.scgexpress.backoffice.android.ui.pickup.summary.PickupSummaryActivity
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.fragment_pickup_main.*
import timber.log.Timber
import javax.inject.Inject

class PickupMainFragment : Fragment(), HasSupportFragmentInjector {

    companion object {
        fun newInstance(): PickupMainFragment {
            return PickupMainFragment().also {
                it.arguments = Bundle()
            }
        }
    }

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var pendingAdapter: PickupMainAdapter
    private lateinit var scannedAdapter: PickupMainAdapter

    private lateinit var rootView: View
    private lateinit var qrScanner: QrScannerController
    private val contextWrapper by lazy {
        ContextWrapper(context!!)
    }

    private lateinit var taskId: String
    private var shipperCode: String? = null
    private var serviceType: List<IdAndValue> = emptyList()
    private var sizing: List<IdAndValue> = emptyList()

    private val viewModel: PickupMainViewModel by lazy {
        ViewModelProviders.of(activity!!, viewModelFactory).get(PickupMainViewModel::class.java)
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentInjector
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)

        if (context is PickupMainActivity) {
        } else {
            throw IllegalStateException("This fragment must be use in conjunction with " + PickupMainActivity::class.java.simpleName)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_pickup_main, container, false)
        return rootView
    }

    override fun onStart() {
        super.onStart()
        viewModel.onStart()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //        activity?.apply {
        //            val ids = intent.getStringArrayExtra("task_ids")
        //            Timber.d("pickup main ids: [${ids.joinToString(",")}]")
        //        }

        activity!!.let {
            qrScanner = QrScannerController(it, this, txQrReader)
            txQrReader.setOnLongClickListener {
                IntentIntegrator(activity).run {
                    initiateScan()
                }
                true
            }
        }

        initIntent()
        initQrScanner()
        initSpinner()
        bindEventListener()
        loadData()
        observeData()
        initMemoryState()
        initRecyclerView()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        parseActivityResultForPhoto(requestCode, resultCode, data)

        IntentIntegrator.parseActivityResult(requestCode, resultCode, data)?.let { it ->
            it.contents?.let {
                etTrackingCode.setText(it)
                stopQrScanner()
            }
        }
    }

    private fun initRecyclerView() {
        pendingAdapter = PickupMainAdapter(viewModel, PickupMainAdapter.ScanStatus.Pending)
        rvTrackingPending.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvTrackingPending.adapter = pendingAdapter

        scannedAdapter = PickupMainAdapter(viewModel, PickupMainAdapter.ScanStatus.Scanned)
        rvTrackingScanned.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvTrackingScanned.adapter = scannedAdapter
    }

    private fun initIntent() {
        activity!!.intent!!.apply {
            shipperCode = getStringExtra(PARAMS_PICKUP_SEARCH_SHIPPER_CODE)
            taskId = getStringExtra(PARAMS_PICKUP_TASK_ID) ?: ""
            //        ?: throw IllegalArgumentException("intent PARAMS_PICKUP_TASK_ID missing")
            viewModel.setTaskIdAndCustomerCode(taskId, shipperCode.toString())
            Timber.d("F.initIntent taskId=$taskId, shipperCode=$shipperCode")
        }

        shipperCode?.let {
            tvShipperCode.text = it
            setStatusCount(0, 0, 0, 0)
        }
    }

    private fun initMemoryState() {
        if (!swRemember.isChecked) {
            viewModel.loadServiceType()()
        }
    }

    private fun initSpinner() {

        setServiceSpinner(emptyList())
        spnServiceType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                parent?.let {
                    val (serviceId, _) = serviceType[position]
                    viewModel.setService(serviceId)()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        setSizeSpinner(emptyList())
        spnSize.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                parent?.let {
                    if (sizing.isNotEmpty()) {
                        val (sizeId, _) = sizing[position]
                        viewModel.setSize(sizeId)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setServiceSpinner(list: List<IdAndValue>) {

        if (serviceType == list) {
            return
        }
        serviceType = list

        fun toStrings(sizeData: List<IdAndValue>) = sizeData.map { item -> item.second }

        spnServiceType.adapter = object : ArrayAdapter<String>(
            spnServiceType.context, R.layout.support_simple_spinner_dropdown_item, toStrings(serviceType)
        ) {}
    }

    private fun setSizeSpinner(list: List<IdAndValue>) {

        if (sizing.drop(1) == list) {
            return
        }
        sizing = listOf(IdAndValue(0, getString(R.string.choose_sizes))) + list

        Timber.d("render size spn")

        fun toStrings(sizeData: List<IdAndValue>) = sizeData.map { item -> item.second }

        spnSize.adapter = object : ArrayAdapter<String>(
            spnSize.context, R.layout.support_simple_spinner_dropdown_item, toStrings(sizing)
        ) {
            override fun isEnabled(position: Int): Boolean {
                return if (position == 0) false else super.isEnabled(position)
            }

            override fun getDropDownView(
                position: Int, convertView: View?, parent: ViewGroup
            ): View {
                val tv = super.getDropDownView(position, convertView, parent) as TextView
                if (position == 0) {
                    tv.setTextColor(Color.GRAY)
                } else {
                    tv.setTextColor(Color.BLACK)
                }
                return tv
            }
        }
    }

    private fun bindEventListener() {
        etTrackingCode.setOnTouchListener(object :
            DrawableClickListener.RightDrawableClickListener(etTrackingCode) {
            override fun onDrawableClick(): Boolean {
                startQrScanner()
                return true
            }
        })

        etTrackingCode.addTextChangedListener(object : OnTextChange() {
            override fun afterTextChanged(et: Editable?) {
                viewModel.setTrackingCode(et.toString().trim())
            }
        })

        swRemember.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setRemember(isChecked)
        }

        etZipCode.addTextChangedListener(object : OnTextChange() {
            override fun afterTextChanged(et: Editable?) {
                val zipCode = et.toString().trim()
                if (zipCode.length == 5) {
                    viewModel.setZipCode(zipCode)
                }
            }
        })

        etCod.addTextChangedListener(object : OnTextChange() {
            override fun afterTextChanged(et: Editable?) {
                viewModel.setCodAmount(et.toString().trim().toDoubleOrNull() ?: .0)
            }
        })

        cbCarton.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setHasCarton(isChecked)
        }

        ivIconCloseCamera.setOnClickListener {
            stopQrScanner()
        }

        btnRegister.setOnClickListener {
            viewModel.onRegister()
        }

        //TODO: for test only, remove when done
        btnRegister.setOnLongClickListener {
            Intent(activity, PickupBookingListActivity::class.java).apply {
                putExtra(Const.FLAG_PICKUP_BOOKING_LIST_CONTINUE_NEXT_TASK, true)
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            }.also {
                startActivity(it)
            }
            true
        }

        btnSummary.setOnClickListener {
            Intent(activity!!, PickupSummaryActivity::class.java).apply {
                putExtra(PARAMS_PICKUP_TASK_ID, taskId)
                putExtra(PARAMS_PICKUP_TASK_TOTAL_COUNT, viewModel.task.totalCount)
            }.also {
                startActivity(it)
            }
        }

        vSenderPhoto.setOnClickListener {
            //takePhotoSender()
            showPictureDialog(PhotoFor.SENDER)

        }
        vRecipientPhoto.setOnClickListener {
            //takePhotoRecipient()
            showPictureDialog(PhotoFor.RECIPIENT)
        }

        viewModel.onFinish.observe(this, Observer {
            if (it == true) {
                finish()
            }
        })
    }

    private fun loadData() {
        viewModel.loadData()
    }

    private fun observeData() {

        viewModel.shipperCode.observe(this, Observer {
            tvShipperCode.text = when {
                it.isNullOrBlank() -> resources.getString(
                    R.string.pickup_title_shipper_code, shipperCode
                )
                else -> resources.getString(R.string.pickup_title_shipper_code, it)
            }
        })

        viewModel.bookingCode.observe(this, Observer {
            when {
                it.isNullOrBlank() -> {
                    tvBookingCode.visibility = View.GONE
                    tvBookingCode.text = ""
                }
                else -> {
                    tvBookingCode.visibility = View.VISIBLE
                    tvBookingCode.text = it
                }
            }
        })

        viewModel.statusCount.observe(this, Observer {
            val (fromBooking, newTracking, total) = it ?: return@Observer
            setStatusCount(viewModel.task.pickupedCount, fromBooking, newTracking, total)
        })

        //goto
        viewModel.gotoSummary.observe(this, Observer {
            if (it == null) return@Observer
            it.getContentIfNotHandled()?.let {
                Intent(activity!!, PickupMainActivity::class.java).run {
                    startActivity(this)
                }
            }
        })

        //render input
        viewModel.spinnerList.observe(this, Observer { (type, list) ->
            when (type) {
                PickupMainViewModel.SpinnerType.ServiceType -> setServiceSpinner(list)
                PickupMainViewModel.SpinnerType.Sizing -> setSizeSpinner(list)
            }
        })
        viewModel.spinnerSelectIndex.observe(this, Observer { (serviceId, sizeId) ->
            if (serviceId != null) {
                val serviceIndex = serviceType.indexOfFirst { (id, _) -> id == serviceId }
                Timber.d("::set UI serviceIndex: ($serviceIndex) $serviceId of $serviceType")
                if (serviceIndex >= 0) spnServiceType.setSelection(serviceIndex)
            }
            if (sizeId != null) {
                val sizeIndex = sizing.indexOfFirst { (id, _) -> id == sizeId }
                Timber.d("::set UI sizeId: ($sizeIndex) $sizeId of $sizing")
                if (sizeIndex >= 0) spnSize.setSelection(sizeIndex)
            }
            //            if (serviceId == null) return@Observer
            //            val serviceIndex = serviceType.indexOfFirst { (id, _) -> id == serviceId }
            //            Timber.d("serviceIndex: $serviceIndex, serviceId: $serviceId")
            //            if (serviceIndex < 0) return@Observer
            //            Timber.d("::set UI serviceIndex: $serviceIndex")
            //            spnServiceType.setSelection(serviceIndex)
            //            viewModelRetentionReason.setService(serviceId).subscribe({
            //                if (sizeId == null) return@subscribe
            //                val sizeIndex = sizing.indexOfFirst { (id, _) -> id == sizeId }
            //                Timber.d("::set UI sizeId: $sizeId")
            //                spnSize.setSelection(sizeIndex)
            //                viewModelRetentionReason.setSize(sizeId)
            //            }, {
            //                it.printStackTrace()
            //            })
        })

        viewModel.trackingCode.observe(this, Observer {
            if (it == null) return@Observer
            etTrackingCode.setText(it)
        })

        viewModel.isTrackingCodeOk.observe(this, Observer {
            if (it == true) {
                containerFee.visibility = View.VISIBLE
                tvAddress.visibility = View.VISIBLE
                etZipCode.visibility = View.VISIBLE
            } else {
                containerFee.visibility = View.GONE
                tvAddress.visibility = View.GONE
                etZipCode.visibility = View.GONE
            }
        })

        viewModel.postalCodeList.observe(this, Observer {
            if (it == null) return@Observer
            showDialogMultiPostalCodeSelector(it)
        })

        viewModel.assortCodeText.observe(this, Observer {
            if (it == null) {
                tvAddress.visibility = View.GONE
            } else {
                tvAddress.visibility = View.VISIBLE
                tvAddress.text = it
            }
        })

        viewModel.codAmount.observe(this, Observer {
            if (it == null) return@Observer
            etCod.setText(it)
        })

        viewModel.remember.observe(this, Observer {
            if (it == null) return@Observer
            swRemember.isChecked = it
        })

        //editText
        viewModel.zipCode.observe(this, Observer {
            if (it == null) return@Observer
            etZipCode.setText(it)
        })

        //information
        viewModel.totalFee.observe(this, Observer {
            if (it == null) return@Observer
            tvTotalFee.text = if (it > 0) it.toCurrencyFormatWithUnit() else "-"
            vFeeLine.visibility =
                if (viewModel.deliveryFee.value ?: .0 > .0 || viewModel.codFee.value ?: .0 > .0 || viewModel.cartonFee.value ?: .0 > .0) View.VISIBLE else View.GONE
        })

        viewModel.deliveryFee.observe(this, Observer {
            showFeeOrGone(tvDeliveryCostTitle, tvDeliveryFee, it)
        })

        viewModel.codFee.observe(this, Observer {
            showFeeOrGone(tvCodFeeTitle, tvCodFee, it)
        })

        viewModel.cartonFee.observe(this, Observer {
            showFeeOrGone(tvCartonTitle, tvCartonFee, it)
        })

        viewModel.hasCod.observe(this, Observer {
            val display = if (it == true) View.VISIBLE else View.GONE
            containerCod.visibility = display
            etCod.visibility = display
        })

        viewModel.hasCarton.observe(this, Observer {
            containerCarton.visibility = if (it == true) View.VISIBLE else View.GONE
        })

        viewModel.setHasCarton.observe(this, Observer {
            cbCarton.isChecked = it == true
        })

        //tracking list
        viewModel.pendingTrackingList.observe(this, Observer {
            if (it == null) {
                containerPendingTrackingList.visibility = View.GONE
                return@Observer
            }
            pendingAdapter.data = it
            pendingAdapter.notifyDataSetChanged()
        })
        viewModel.scannedTrackingList.observe(this, Observer {
            if (it == null) {
                containerScanningTrackingList.visibility = View.GONE
                return@Observer
            }
            else{
                containerScanningTrackingList.visibility = View.VISIBLE
            }
            scannedAdapter.data = it
            scannedAdapter.notifyDataSetChanged()
            //            Timber.d("scannedAdapter.data = ${it.size} ${scannedAdapter.data.map { it.tracking }}")
        })

        //dialog

        viewModel.dialogConfirmAddExtraTracking.observe(this, Observer { e ->
            e?.getContentIfNotHandled()?.let {
                showDialogExtraTracking(it)
            }
        })

        viewModel.dialogAlertMessage.observe(this, Observer { e ->
            e?.getContentIfNotHandled()?.let {
                showDialogAlert(it)
            }
        })

        //photo

        viewModel.photoBitmapSender.observe(this, Observer { bitmap ->
            if (bitmap != null) {
                ivPhotoSender.setImage(bitmap)
                ivPhotoSender.imageTintList = null
            } else {
                ivPhotoSender.clearImage()
            }
        })

        viewModel.photoBitmapRecipient.observe(this, Observer { bitmap ->
            if (bitmap != null) {
                ivPhotoRecipient.setImage(bitmap)
                ivPhotoRecipient.imageTintList = null
            } else {
                ivPhotoRecipient.clearImage()
            }
        })

        viewModel.enableRegister.observe(this, Observer {
            btnRegister.isEnabled = it == true
        })

        viewModel.enableSeeSummary.observe(this, Observer {
            btnSummary.isEnabled = it == true
        })

        //QR
        qrScanner.scanCode.observe(this, Observer {
            etTrackingCode.setText(it.trimTrackingCode())
            stopQrScanner()
        })

        qrScanner.imageSize.observe(this, Observer { (width, height) ->
            //Timber.d("image size camera change: $width x $height - ${txQrReader.height} = ${-((height - txQrReader.height) / 2)}")
            linearLayoutBeautyContent.layoutParams.height = -((height - txQrReader.height) / 2)
        })
    }

    private fun startQrScanner() {
        AnimationUtils.loadAnimation(activity!!, R.anim.blink_interval).run {
            vBarcodeRedLine.startAnimation(this)
        }
        etTrackingCode.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        sectionCamera.visibility = View.VISIBLE
        qrScanner.startCamera()
    }

    private fun stopQrScanner() {
        etTrackingCode.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_qr_code_24dp, 0)
        sectionCamera.visibility = View.GONE
        qrScanner.stopCamera()
    }


    private fun setStatusCount(initScanned: Int, fromBooking: Int, newTracking: Int, total: Int) {
        val scanned = initScanned + fromBooking + newTracking

        Timber.d("F.setStatusCount(scanned=$scanned fromBooking=$fromBooking  newTracking=$newTracking)")

        tvCountStatus.text = when {
            total > 0 -> context?.resources?.getString(
                R.string.pickup_title_amount_to_total, scanned, total
            )
            else -> "$scanned"
        }

        when {
            total > 0 -> View.VISIBLE
            else -> View.GONE
        }.also {
            containerCountStatusBar.visibility = it
            v1.visibility = it
        }

        tvFromBookingCount.text = "$fromBooking"
        tvNewTrackingCount.text = "$newTracking"
    }

    private fun showDialogMultiPostalCodeSelector(postals: List<TblScgPostalcode>) {

        if (postals.size == 1) {
            showDialogRemotePostalCodeAlert(postals.first())
            return
        }

        AlertDialog.Builder(activity!!).apply {
            val items = postals.map { it ->
                "${it.postalCode}, ${it.province}, ${it.district}"
            }.toTypedArray()
            setTitle(resources.getString(R.string.dialog_remote_postalcode_selector))
            setSingleChoiceItems(items, 0) { dialog, position ->
                showDialogRemotePostalCodeAlert(postals[position])
                dialog.cancel()
            }
            setNegativeButton(resources.getString(R.string.dialog_remote_postalcode_cancel)) { _, _ ->
                etZipCode.setText("")
            }
        }.run {
            create()
        }.also {
            it.show()
        }
    }

    private fun showDialogRemotePostalCodeAlert(postalCode: TblScgPostalcode) {

        val id = postalCode.id
        val includeExtraCharge = postalCode.hasExtraCharge()

        if (!postalCode.isRemoteArea()) {
            viewModel.setPostalId(id)
            return
        }

        AlertDialog.Builder(activity!!).apply {
            setTitle(resources.getString(R.string.dialog_remote_postalcode_alert))
            if (includeExtraCharge) {
                setMessage(resources.getString(R.string.dialog_remote_postalcode_extra_desc))
            } else {
                setMessage(resources.getString(R.string.dialog_remote_postalcode_desc))
            }

            setPositiveButton(resources.getString(R.string.dialog_remote_postalcode_ok)) { _, _ ->
                viewModel.setPostalId(id)
            }
            setNegativeButton(resources.getString(R.string.dialog_remote_postalcode_cancel)) { _, _ ->
                viewModel.setPostalId(null)
            }
        }.run {
            create()
        }.also {
            it.show()
        }
    }

    private fun showDialogExtraTracking(trackingCode: String) {
        AlertDialog.Builder(activity!!).apply {
            setTitle(resources.getString(R.string.dialog_confirm_extra_tracking, trackingCode))
            setPositiveButton(resources.getString(R.string.dialog_confirm_extra_tracking_ok)) { _, _ ->
                viewModel.setAllowExtraTrackingCode(true)
                viewModel.onRegister()
            }
            setNegativeButton(resources.getString(R.string.dialog_confirm_extra_tracking_cancel)) { _, _ ->
                viewModel.setTrackingCode("")
            }
        }.run {
            create()
        }.also {
            it.show()
        }
    }

    private fun showDialogAlert(msg: String) {
        AlertDialog.Builder(activity!!).apply {
            setTitle(msg)
            setPositiveButton(resources.getString(R.string.ok)) { _, _ -> }
        }.run {
            create()
        }.also {
            it.show()
        }
    }

    private fun showFeeOrGone(title: TextView, value: TextView, fee: Double?) {
        if (fee != null && fee >= 0) {
            value.text = fee.toCurrencyFormatWithUnit()
            value.visibility = View.VISIBLE
            title.visibility = View.VISIBLE
        } else {
            value.visibility = View.GONE
            title.visibility = View.GONE
        }
    }

    //Photo

    enum class PhotoFor {
        SENDER, RECIPIENT
    }

    private val REQUEST_TAKE_PHOTO_SENDER = 10001
    private val REQUEST_TAKE_PHOTO_RECIPIENT = 10002

    private val REQUEST_SELECT_PHOTO_SENDER = 20001
    private val REQUEST_SELECT_PHOTO_RECIPIENT = 20002

    private val mImagePlaceholder: Drawable by lazy {
        ContextCompat.getDrawable(activity!!, R.drawable.ic_placeholder)!!
    }


    private fun showPictureDialog(photoFor: PhotoFor) {
        AlertDialog.Builder(activity!!).apply {
            setTitle(getString(R.string.photo_select_method_title))

            val pictureDialogItems = arrayOf(
                getString(R.string.photo_select_method_gallery),
                getString(R.string.photo_select_method_camera)
            )
            setItems(pictureDialogItems) { _, which ->
                when {
                    which == 0 && photoFor == PhotoFor.SENDER -> selectPhotoFromGallery(
                        REQUEST_SELECT_PHOTO_SENDER
                    )
                    which == 0 && photoFor == PhotoFor.RECIPIENT -> selectPhotoFromGallery(
                        REQUEST_SELECT_PHOTO_RECIPIENT
                    )
                    which == 1 && photoFor == PhotoFor.SENDER -> takePhotoFromCamera(
                        REQUEST_TAKE_PHOTO_SENDER
                    )
                    which == 1 && photoFor == PhotoFor.RECIPIENT -> takePhotoFromCamera(
                        REQUEST_TAKE_PHOTO_RECIPIENT
                    )
                }
            }
        }.run {
            create()
        }.also {
            it.show()
        }
    }

    private fun selectPhotoFromGallery(requestCode: Int) {
        if (ActivityCompat.checkSelfPermission(
                activity!!, Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity!!, arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), Const.REQUEST_READ_EXTERNAL_STORAGE
            )
        } else {
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, requestCode)
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

    private var cameraPhotoUri: Uri? = null

    private fun parseActivityResultForPhoto(requestCode: Int, resultCode: Int, data: Intent?) {

        try {
            val bitmap = when (requestCode) {
                REQUEST_TAKE_PHOTO_SENDER, REQUEST_TAKE_PHOTO_RECIPIENT -> cameraPhotoUri?.let { uri ->
                    val bitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver, uri)
                    bitmap
                }
                REQUEST_SELECT_PHOTO_SENDER, REQUEST_SELECT_PHOTO_RECIPIENT -> data?.let {
                    val contentURI = it.data
                    val bitmap = MediaStore.Images.Media.getBitmap(
                        activity!!.contentResolver, contentURI
                    )
                    bitmap
                }
                else -> return
            }

            Timber.d("parseActivityResultForPhoto() requestCode=$requestCode resultCode=$resultCode data=$data $cameraPhotoUri")

            bitmap?.let {
                when (requestCode) {
                    REQUEST_TAKE_PHOTO_SENDER, REQUEST_SELECT_PHOTO_SENDER -> viewModel.setSenderPhoto(
                        it
                    )
                    REQUEST_TAKE_PHOTO_RECIPIENT, REQUEST_SELECT_PHOTO_RECIPIENT -> viewModel.setRecipientPhoto(
                        it
                    )
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun ImageView.setImage(url: Any) {
        GlideApp.with(context).load(url).placeholder(mImagePlaceholder).centerCrop().into(this)
    }

    private fun ImageView.clearImage() {
        setImageResource(R.drawable.ic_baseline_image_24dp)
        imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context!!, R.color.grayLight))
    }


    //QR

    private val REQUEST_CODE_PERMISSIONS = 10
    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

    private fun initQrScanner() {
        if (allPermissionsGranted()) {
            stopQrScanner()
        } else {
            ActivityCompat.requestPermissions(
                activity!!, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            activity!!.baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private abstract class OnTextChange : TextWatcher {

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    }

    private fun finish() {

        if (viewModel.taskIdList == null) {
            activity!!.finish()
        } else {
            Intent(activity, PickupBookingListActivity::class.java).apply {
                putExtra(PARAMS_PICKUP_SEARCH_SHIPPER_CODE, shipperCode)
                putExtra(FLAG_PICKUP_BOOKING_LIST_CONTINUE_NEXT_TASK, false)
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            }.also {
                startActivity(it)
            }
        }
    }

    fun onBackPressed() {
        viewModel.onFinish()
    }
}
