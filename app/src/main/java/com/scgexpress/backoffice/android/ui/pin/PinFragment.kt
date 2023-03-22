package com.scgexpress.backoffice.android.ui.pin

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.scgexpress.backoffice.android.BuildConfig
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.config.FirebaseRemoteConfig
import com.scgexpress.backoffice.android.ui.login.LoginActivity
import com.scgexpress.backoffice.android.ui.login.PinViewModel
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.fragment_pin.*
import timber.log.Timber
import javax.inject.Inject


class PinFragment : Fragment(), HasSupportFragmentInjector {

    companion object {
        fun newInstance(): PinFragment {
            return PinFragment().also {
                it.arguments = Bundle()
            }
        }
    }

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var remoteConfig: FirebaseRemoteConfig

    private lateinit var viewModel: PinViewModel
    private lateinit var rootView: View


    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentInjector
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)

        if (context is PinActivity) {
        } else {
            throw IllegalStateException("This fragment must be use in conjunction with " + PinActivity::class.java.simpleName)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(PinViewModel::class.java)

        if (viewModel.isLogin()) {
            startLoginActivity()
        }
    }

    override fun onResume() {
        super.onResume()
        checkLatestVersion()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_pin, container, false)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initButton()
        observeData()

        viewModel.init()
    }

    private fun initButton() {
        for ((numPad, value) in getNumPads()) {
            numPad.setOnClickListener {
                viewModel.addPinNumber(value)
            }
        }

        btnBackspace.setOnClickListener {
            viewModel.removeLastedPinNumber()
        }
    }

    private fun getNumPads(): Map<View, Int> {
        return mapOf(
                btn0 to 0,
                btn1 to 1,
                btn2 to 2,
                btn3 to 3,
                btn4 to 4,
                btn5 to 5,
                btn6 to 6,
                btn7 to 7,
                btn8 to 8,
                btn9 to 9
        )
    }

    private fun observeData() {
        viewModel.pinCode.observe(this, Observer {
            setIndicator(it?.length ?: 0)
        })
        viewModel.snackbar.observe(this, Observer { it ->
            if (it != null) {
                it.getContentIfNotHandled()?.let {
                    // Only proceed if the event has never been handled
                    Snackbar.make(rootView, it, Snackbar.LENGTH_SHORT).show()
                }
            }
        })
        viewModel.pass.observe(this, Observer {
            if (it == true) {
                startLoginActivity()
            }
        })
    }

    private fun setIndicator(n: Int) {

        val outline = R.drawable.ic_pin_circle_outline
        val fill = R.drawable.ic_pin_circle_fill

        ivIndicator1.setImageResource(if (n >= 1) fill else outline)
        ivIndicator2.setImageResource(if (n >= 2) fill else outline)
        ivIndicator3.setImageResource(if (n >= 3) fill else outline)
        ivIndicator4.setImageResource(if (n >= 4) fill else outline)
    }


    private fun checkLatestVersion() {
        remoteConfig.latestVersion.observe(this, Observer { _ ->
            val latestVersion = remoteConfig.latestVersion.value
            val currentVersion = BuildConfig.VERSION_CODE

            Timber.i("latestVersion: $latestVersion and currentVersion: $currentVersion")

            if (latestVersion != currentVersion) {
                val packageName = remoteConfig.downloadUrl.value ?: ""
                AlertDialog.Builder(context!!).apply {
                    setCancelable(false)
                    setTitle(resources.getString(R.string.dialog_app_version_title))
                    setMessage(resources.getString(R.string.dialog_app_version_message))
                    setPositiveButton(resources.getString(R.string.dialog_app_version_ok)) { dialog, which ->
                        startPlayStore(packageName)
                    }
                }.run {
                    create()
                }.also {
                    it.show()
                }
            }
        })
    }

    private fun startPlayStore(appPackageName: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
        } catch (ex: android.content.ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
        }

    }

    private fun startLoginActivity() {
        startActivity(Intent(activity, LoginActivity::class.java))
        activity!!.finish()
    }
}
