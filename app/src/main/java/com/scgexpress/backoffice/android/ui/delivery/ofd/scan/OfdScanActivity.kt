package com.scgexpress.backoffice.android.ui.delivery.ofd.scan

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.ViewModelProviders
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.base.BaseActivity
import com.scgexpress.backoffice.android.common.Const.PARAMS_MANIFEST
import com.scgexpress.backoffice.android.model.Manifest
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_ofd_scan.*

class OfdScanActivity : BaseActivity() {

    private val mFragment by lazy {
        OfdScanFragment.newInstance(viewModel)
    }

    private val viewModel: OfdScanViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(OfdScanViewModel::class.java)
    }

    private var manifest: Manifest? = Manifest()

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ofd_scan)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.clContainer, mFragment)
                .commit()
        }

        initActionBar()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mFragment.onActivityResult(requestCode, resultCode, data)
    }

    private fun initActionBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Scan OFD"
        }
    }
}

