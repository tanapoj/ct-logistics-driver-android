package com.scgexpress.backoffice.android.ui.delivery.ofd.retention

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.integration.android.IntentIntegrator
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.camera.QrScannerController
import com.scgexpress.backoffice.android.common.Const
import com.scgexpress.backoffice.android.common.listener.DrawableClickListener
import com.scgexpress.backoffice.android.common.showAlertMessage
import com.scgexpress.backoffice.android.common.trimTrackingCode
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.fragment_ofd_cant_sent.*
import timber.log.Timber
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 *
 */
class OfdRetentionFragment : Fragment(), HasSupportFragmentInjector {

    companion object {
        private lateinit var viewModel: OfdRetentionViewModel

        fun newInstance(vm: OfdRetentionViewModel, trackingID: String): OfdRetentionFragment {
            viewModel = vm
            return OfdRetentionFragment().also {
                val args = Bundle()
                args.putString(Const.PARAMS_TRACKING_ID, trackingID)
                it.arguments = args
            }
        }
    }

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var rootView: View

    private val adapter: OfdRetentionAdapter by lazy {
        OfdRetentionAdapter(viewModel)
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentInjector
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)

        if (context is OfdRetentionActivity) {
        } else {
            throw IllegalStateException("This fragment must be use in conjunction with " + OfdRetentionActivity::class.java.simpleName)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_ofd_cant_sent, container, false)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initQrScanner()
        initRecyclerView()
        initButton()
        observeData()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        IntentIntegrator.parseActivityResult(requestCode, resultCode, data)?.let { it ->
            it.contents?.let {
                //edtScanTracking.setText(it)
                //checkExistTracking(it)
            }
        }
    }

    private fun initRecyclerView() {
        rvItems.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvItems.adapter = adapter

        edtScanTracking.requestFocus()
    }

    private fun observeData() {

        viewModel.items.observe(this, Observer {
            adapter.data = it
            Timber.d("delivery item scanned: $it")
        })

        viewModel.countStatus.observe(this, Observer {
            tvCountStatus.text = "$it"
        })

        viewModel.snackbar.observe(this, Observer { e ->
            e.letContentIfNotHandled {
                // Only proceed if the event has never been handled
                Snackbar.make(rootView, it, Snackbar.LENGTH_SHORT).show()
            }

        })

        viewModel.trackingCode.observe(this, Observer { tracking ->
            tracking.letContentIfNotHandled {
                edtScanTracking.setText(it)
            }
        })

        viewModel.warning.observe(this, Observer { e ->
            e.letContentIfNotHandled {
                activity!!.showAlertMessage(it)
            }

        })

        viewModel.confirmRemoveTracking.observe(this, Observer { e ->
            e.letContentIfNotHandled { trackingCode ->
                AlertDialog.Builder(activity!!).apply {
                    setMessage(
                        resources.getString(
                            R.string.sentence_ofd_confirm_remove_tracking,
                            trackingCode
                        )
                    )
                    setPositiveButton(resources.getString(R.string.confirm)) { _, _ ->
                        viewModel.removeTrackingCode(trackingCode, true)
                    }
                    setNegativeButton(resources.getString(R.string.cancel)) { _, _ ->

                    }
                    setCancelable(false)
                }.run {
                    create()
                }.also {
                    it.show()
                }
            }
        })
    }

    private fun initButton() {
        qrScanner.scanCode.observe(this, Observer {
            edtScanTracking.setText(it.trimTrackingCode())
            viewModel.setTrackingCode(it)
            stopQrScanner()
        })

        qrScanner.imageSize.observe(this, Observer { (width, height) ->
            linearLayoutBeautyContent.layoutParams.height = -((height - txQrReader.height) / 2)
        })

        edtScanTracking.apply {
            setOnTouchListener(object :
                DrawableClickListener.RightDrawableClickListener(edtScanTracking) {
                override fun onDrawableClick(): Boolean {
                    startQrScanner()
                    return true
                }
            })

            setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                    viewModel.setTrackingCode(edtScanTracking.text.toString())
                    return@OnKeyListener true
                }
                false
            })
        }

        ivIconCloseCamera.setOnClickListener {
            stopQrScanner()
        }

        btnDone.setOnClickListener {
            if (adapter.data.size > 1) {

            } else
                viewModel.showWarning(getString(R.string.sentence_please_scan_your_tracking))

        }
    }

    //QR Scanner

    private val REQUEST_CODE_PERMISSIONS = 10
    private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
    private lateinit var qrScanner: QrScannerController

    private fun  initQrScanner() {

        activity!!.let {
            qrScanner = QrScannerController(it, this, txQrReader)
            txQrReader.setOnLongClickListener {
                IntentIntegrator(activity).run {
                    initiateScan()
                }
                true
            }
        }

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

    private fun startQrScanner() {
        AnimationUtils.loadAnimation(activity!!, R.anim.blink_interval).run {
            vBarcodeRedLine.startAnimation(this)
        }
        edtScanTracking.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        sectionCamera.visibility = View.VISIBLE
        qrScanner.startCamera()
    }

    private fun stopQrScanner() {
        edtScanTracking.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_qr_code_24dp, 0)
        sectionCamera.visibility = View.GONE
        qrScanner.stopCamera()
    }
}
