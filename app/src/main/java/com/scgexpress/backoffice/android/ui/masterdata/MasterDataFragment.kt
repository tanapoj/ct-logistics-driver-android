package com.scgexpress.backoffice.android.ui.masterdata

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.aws.DeveloperAuthenticationProvider
import com.scgexpress.backoffice.android.common.Const
import com.scgexpress.backoffice.android.preference.LoginPreference
import com.scgexpress.backoffice.android.repository.LoginRepository
import com.scgexpress.backoffice.android.ui.menu.MenuActivity
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.fragment_masterdata.*
import javax.inject.Inject

class MasterDataFragment : Fragment(), HasSupportFragmentInjector {

    companion object {
        private lateinit var viewModel: MasterDataViewModel

        fun newInstance(vm: MasterDataViewModel):
                MasterDataFragment = MasterDataFragment().apply {
            val args = Bundle()
            //args.putString(KEY_ID, id)
            this.arguments = args
            viewModel = vm
        }

        private const val KEY_ID: String = "id"
    }

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>
    private lateinit var rootView: View

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentInjector
    }

    @Inject
    lateinit var loginRepository: LoginRepository

    @Inject
    lateinit var loginPreference: LoginPreference

    private lateinit var mContext: Context


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
        val developerProvider =
            DeveloperAuthenticationProvider(loginRepository, loginPreference, Const.AWS_POOL_ID, Regions.AP_SOUTHEAST_1)
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

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)

        if (context is MasterDataActivity) {
            mContext = context
        } else {
            throw IllegalStateException("This fragment must be use in conjunction with " + MasterDataActivity::class.java.simpleName)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_masterdata, container, false)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.progressValue.observe(this, Observer { percent ->
            percent?.let {
                if (it < 0) {
                    pbLoading.isIndeterminate = true
                } else {
                    pbLoading.isIndeterminate = false
                    setProgress(it)
                }
            }
        })

        viewModel.status.observe(this, Observer {
            tvStatus.text = it
        })

        viewModel.statusDetail.observe(this, Observer {
            tvStatusDetail.text = it
        })

        viewModel.done.observe(this, Observer {
            if (it == true) {
                pbLoading.isIndeterminate = true
                tvStatus.text = resources.getString(R.string.master_data_update_state_up_to_date)
                //Handler().postDelayed({
                    startMenuActivity()
                //}, 1_000)
            }
        })

        viewModel.dialogErrorMessage.observe(this, Observer {
            showDialogErrorMessage(it)
        })

        Handler().postDelayed({
            startUpdateMasterData()
        }, 500)
    }

    private fun startUpdateMasterData() {
        viewModel.loadMasterDataJsonFile(s3Client, activity!!.filesDir.canonicalPath)
    }

    private fun setProgress(percent: Int) {
        //Log.i("-master", "$percent %")
        pbLoading.progress = percent
    }

    private fun startMenuActivity() {
        activity?.apply {
            startActivity(Intent(activity, MenuActivity::class.java))
            finish()
        }
    }


    private fun showDialogErrorMessage(msg: String) {
        AlertDialog.Builder(context!!).apply {
            setMessage(msg)
            setPositiveButton("Ok") { dialog, _ ->
                dialog.dismiss()
                activity?.finish()
            }
        }.run {
            create()
        }.also {
            it.show()
        }
    }

}