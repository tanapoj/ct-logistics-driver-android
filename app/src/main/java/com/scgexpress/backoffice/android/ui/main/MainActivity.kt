package com.scgexpress.backoffice.android.ui.main

import android.content.Intent
import android.os.Bundle
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.base.BaseActivity
import com.scgexpress.backoffice.android.ui.pin.PinActivity
import dagger.android.AndroidInjection

class MainActivity : BaseActivity() {

    override var allowEmergencyMasterDataUpdateDialog = false

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // start login activity
        startActivity(Intent(this, PinActivity::class.java))
//        startActivity(Intent(this, MasterDataActivity::class.java))
        finish()
    }
}
