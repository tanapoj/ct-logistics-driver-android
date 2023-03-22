package com.scgexpress.backoffice.android.base

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.scgexpress.backoffice.android.common.Utils
import com.scgexpress.backoffice.android.preference.LoginPreference
import com.scgexpress.backoffice.android.preference.LoginPreference.Companion.LOGIN_DEFAULT_VALUE
import com.scgexpress.backoffice.android.preference.LoginPreference.Companion.LOGIN_DEFAULT_VALUE_TIMESTAMP
import com.scgexpress.backoffice.android.preferrence.MasterDataPreference
import com.scgexpress.backoffice.android.ui.masterdata.forceupdate.ForceUpdateActivity
import com.scgexpress.backoffice.android.ui.pin.PinActivity
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import java.util.concurrent.TimeUnit
import javax.inject.Inject


abstract class BaseActivity : AppCompatActivity(), HasSupportFragmentInjector {

    open var allowEmergencyMasterDataUpdateDialog = true

    companion object {
        val DISCONNECT_TIMEOUT = TimeUnit.HOURS.toSeconds(7)
    }

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var loginPreference: LoginPreference

    @Inject
    lateinit var masterDataPreference: MasterDataPreference

    private val disconnectHandler = Handler()
    private val disconnectRunnable = Runnable {
        clearLoginStatus()
        logout()
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = fragmentInjector

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        checkActiveStatus()
        setActiveStatus()
        resetDisconnectTimer()
    }

    public override fun onPause() {
        super.onPause()
        checkActiveStatus()
        setActiveStatus()
        resetDisconnectTimer()

    }

    override fun onStart() {
        super.onStart()
        observeMasterDataForceUpdate()
    }

    public override fun onStop() {
        super.onStop()
        stopDisconnectTimer()
    }

    private fun resetDisconnectTimer() {
        disconnectHandler.removeCallbacks(disconnectRunnable)
        disconnectHandler.postDelayed(disconnectRunnable, TimeUnit.SECONDS.toMillis(DISCONNECT_TIMEOUT))
    }

    private fun stopDisconnectTimer() {
        disconnectHandler.removeCallbacks(disconnectRunnable)
    }

    private fun setActiveStatus() {
        loginPreference.activeTimestamp = Utils.getCurrentTimestamp()
    }

    private fun clearLoginStatus() {
        loginPreference.loginUser = LOGIN_DEFAULT_VALUE
        loginPreference.activeTimestamp = LOGIN_DEFAULT_VALUE_TIMESTAMP
    }

    private fun logout() {
        val intent = Intent(this, PinActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }

    private fun checkActiveStatus() {
        val lastActive: Long = if (loginPreference.activeTimestamp != null) {
            loginPreference.activeTimestamp!!
        } else 0
        if (Utils.getCurrentTimestamp() - lastActive > TimeUnit.SECONDS.toMillis(DISCONNECT_TIMEOUT)) {
            clearLoginStatus()
            logout()
        }
    }

    private fun observeMasterDataForceUpdate() {
        if (!allowEmergencyMasterDataUpdateDialog) {
            return
        }
//        Log.i("-masterdata", "BaseActiviry:: lastver=${masterDataPreference.lastestVersion} noticetime=${masterDataPreference.noticeTime}")
//        Log.i("-masterdata", "BaseActiviry:: current=${Utils.getCurrentTimestamp()} current-noticeTime=${Utils.getCurrentTimestamp() - masterDataPreference.noticeTime}")
//        Log.i("-masterdata", "BaseActiviry:: hasNotice()=${masterDataPreference.hasNotice()} isExpire()=${masterDataPreference.isExpire()}")
        if (masterDataPreference.isExpire() && masterDataPreference.hasNotice()) {
            val intent = Intent(this, ForceUpdateActivity::class.java)
            startActivity(intent)
        }
    }
}