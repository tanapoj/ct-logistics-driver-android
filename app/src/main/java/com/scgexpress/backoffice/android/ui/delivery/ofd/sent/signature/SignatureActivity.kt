package com.scgexpress.backoffice.android.ui.delivery.ofd.sent.signature

import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.base.BaseActivity
import com.scgexpress.backoffice.android.common.Const
import com.scgexpress.backoffice.android.common.Const.PARAMS_MANIFEST_SENT_LIST
import com.scgexpress.backoffice.android.model.DeliveryOfdParcelList
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_signature.*


class SignatureActivity : BaseActivity() {

    private val viewModel: SignatureViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(SignatureViewModel::class.java)
    }

    private var manifestID: String? = ""
    private var scanDataList: DeliveryOfdParcelList? = DeliveryOfdParcelList()

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signature)

        getManifestIDIntent()
        initActionbar()
        observeData()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.clContainer, SignatureFragment.newInstance(viewModel))
                    .commit()
        }
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

    private fun getManifestIDIntent() {
        manifestID = intent.getStringExtra(Const.PARAMS_MANIFEST_ID)
        viewModel.setManifestID(manifestID.toString())

        scanDataList = intent.getSerializableExtra(PARAMS_MANIFEST_SENT_LIST) as? DeliveryOfdParcelList
        viewModel.setScanDataList(scanDataList!!)
    }

    private fun initActionbar() {
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    private fun observeData() {
        viewModel.trackingNo.observe(this, Observer { it ->
            if (it != null) {
                it.getContentIfNotHandled()?.let {
                    // Only proceed if the event has never been handled
                    supportActionBar!!.title = it
                }
            }
        })
    }
}
