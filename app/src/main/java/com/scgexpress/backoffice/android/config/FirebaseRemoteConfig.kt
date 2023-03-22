package com.scgexpress.backoffice.android.config

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.scgexpress.backoffice.android.BuildConfig
import com.scgexpress.backoffice.android.R

class FirebaseRemoteConfig {

    val LATEST_VERSION = "latest_app_version"
    val DOWNLOAD_URL = "latest_app_download_url"
    val PIN_CODE = "pin_code"

    val remoteConfig by lazy {
        FirebaseRemoteConfig.getInstance().apply {
            setConfigSettings(
                FirebaseRemoteConfigSettings
                    .Builder()
                    .setDeveloperModeEnabled(BuildConfig.DEBUG)
                    .build()
            )
            setDefaults(R.xml.remote_config_defaults)
        }
    }

    private val _latestVersion = MutableLiveData<Int>()
    val latestVersion: LiveData<Int>
        get() = _latestVersion

    private val _downloadUrl = MutableLiveData<String>()
    val downloadUrl: LiveData<String>
        get() = _downloadUrl

    private val _pinCode = MutableLiveData<String>()
    val pinCode: LiveData<String>
        get() = _pinCode

    fun init() {
        remoteConfig.activateFetched()
        remoteConfig.fetch(0).addOnCompleteListener {
            remoteConfig.getString(LATEST_VERSION).toIntOrNull()?.let { value ->
                _latestVersion.value = value
            }
            remoteConfig.getString(DOWNLOAD_URL)?.let { value ->
                _downloadUrl.value = value
            }
            remoteConfig.getString(PIN_CODE)?.let { value ->
                _pinCode.value = value
            }
        }
    }
}