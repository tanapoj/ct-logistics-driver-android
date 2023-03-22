package com.scgexpress.backoffice.android.ui.delivery.ofd.sent.signature


import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.github.gcacace.signaturepad.views.SignaturePad
import com.google.android.material.snackbar.Snackbar
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.aws.DeveloperAuthenticationProvider
import com.scgexpress.backoffice.android.common.Const
import com.scgexpress.backoffice.android.common.Const.REQUEST_READ_EXTERNAL_STORAGE
import com.scgexpress.backoffice.android.preference.LoginPreference
import com.scgexpress.backoffice.android.repository.LoginRepository
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.fragment_signature.*
import timber.log.Timber
import javax.inject.Inject


class SignatureFragment : Fragment(), HasSupportFragmentInjector {

    companion object {
        private lateinit var viewModel: SignatureViewModel

        fun newInstance(vm: SignatureViewModel): SignatureFragment {
            viewModel = vm
            return SignatureFragment().also {
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
    private var signatureBitmap: Bitmap? = null

    private val transferUtility by lazy {
        val developerProvider = DeveloperAuthenticationProvider(loginRepository, loginPreference, Const.AWS_POOL_ID, Regions.AP_SOUTHEAST_1)
        val credentialsProvider = CognitoCachingCredentialsProvider(
                mContext,
                developerProvider,
                Regions.AP_SOUTHEAST_1
        )
        //val credentials = BasicAWSCredentials(Const.AWS_ACCESS_KEY, Const.AWS_SECRET_KEY)
        val s3Client = AmazonS3Client(credentialsProvider)
        TransferUtility.builder()
                .context(mContext)
                .awsConfiguration(AWSMobileClient.getInstance().configuration)
                .s3Client(s3Client)
                .build()
    }


    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentInjector
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)

        if (context is SignatureActivity) {
            mContext = context
        } else {
            throw IllegalStateException("This fragment must be use in conjunction with " + SignatureActivity::class.java.simpleName)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_signature, container, false)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        isPremise()
        initButton()
        observeData()
    }

    private fun isPremise(): Boolean {
        if (ActivityCompat.checkSelfPermission(activity!!,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity!!, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_READ_EXTERNAL_STORAGE)
            return false
        }
        return true
    }

    private fun observeData() {
        viewModel.snackbar.observe(this, Observer { it ->
            if (it != null) {
                it.getContentIfNotHandled()?.let {
                    // Only proceed if the event has never been handled
                    Snackbar.make(rootView, it, Snackbar.LENGTH_SHORT).show()
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

        viewModel.signer.observe(this, Observer { it ->
            if (it == null) return@Observer
            edtSigner.setText(it)
            edtSigner.setSelection(edtSigner.text.length)
        })

        viewModel.getLocationHelper(context!!).observe(this, Observer<Location> { location ->
            if (location == null) return@Observer
            viewModel.latitude = location.latitude
            viewModel.longitude = location.longitude
        })
    }

    private fun initButton() {
        btnAccept.setOnClickListener {
            if (!viewModel.checkLastClickTime()) return@setOnClickListener
            if (signatureBitmap != null && edtSigner.text.toString() != "" && isPremise()) {
                val path = viewModel.saveImage(signatureBitmap!!)
                viewModel.upPhoto(transferUtility, btnAccept, path, edtSigner.text.toString())
            } else viewModel.showSnackbar(getString(R.string.sentence_signature_please_sign_customer_name))
        }

        signaturePad.setOnSignedListener(object : SignaturePad.OnSignedListener {
            override fun onStartSigning() {
                //Event triggered when the pad is touched
            }

            override fun onSigned() {
                //Event triggered when the pad is signed
                signatureBitmap = signaturePad.signatureBitmap
                Timber.d("signatureBitmap : $signatureBitmap")
            }

            override fun onClear() {
                //Event triggered when the pad is cleared
            }
        })

        btnClear.setOnClickListener {
            signatureBitmap = null
            signaturePad.clear()
        }
    }
}
