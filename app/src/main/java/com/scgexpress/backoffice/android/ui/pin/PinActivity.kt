package com.scgexpress.backoffice.android.ui.pin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.scgexpress.backoffice.android.BuildConfig
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.base.BaseActivity
import com.scgexpress.backoffice.android.config.FirebaseRemoteConfig
import dagger.android.AndroidInjection
import timber.log.Timber
import javax.inject.Inject


class PinActivity : BaseActivity() {

    override var allowEmergencyMasterDataUpdateDialog = false

    @Inject
    lateinit var remoteConfig: FirebaseRemoteConfig

    private val mFragment by lazy {
        PinFragment.newInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_pin)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.clContainer, mFragment)
                    .commit()
        }

    }

    override fun onResume() {
        super.onResume()
        checkLatestVersion()
    }

    private fun checkLatestVersion() {
        remoteConfig.latestVersion.observe(this, Observer { _ ->
            val latestVersion = remoteConfig.latestVersion.value
            val currentVersion = BuildConfig.VERSION_CODE

            Timber.i("latestVersion: ${latestVersion} and currentVersion: ${currentVersion}")

            if (latestVersion != currentVersion) {
                val packageName = remoteConfig.downloadUrl.value ?: ""
                AlertDialog.Builder(this).apply {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mFragment.onActivityResult(requestCode, resultCode, data)
    }

}
