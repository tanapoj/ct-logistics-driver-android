package com.scgexpress.backoffice.android.ui.delivery.ofd.sent

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.ViewModelProviders
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.base.BaseActivity
import com.scgexpress.backoffice.android.common.Const.PARAMS_MANIFEST_ID
import com.scgexpress.backoffice.android.common.Const.PARAMS_TRACKING_ID
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_ofd_create.*

class OfdSentActivity : BaseActivity() {

    private val mFragment by lazy {
        OfdSentFragment.newInstance(viewModel, trackingID!!)
    }

    private lateinit var viewModel: OfdSentViewModel

    private var manifestID: String? = ""
    private var trackingID: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ofd_sent)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(OfdSentViewModel::class.java)

        getIntentManifestID()

        initActionbar()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.clContainer, mFragment)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mFragment.onActivityResult(requestCode, resultCode, data)
    }

    private fun getIntentManifestID() {
        manifestID = intent.getStringExtra(PARAMS_MANIFEST_ID)
        trackingID = if (intent.getStringExtra(PARAMS_TRACKING_ID) != null)
            intent.getStringExtra(PARAMS_TRACKING_ID)
        else ""
        viewModel.setManifestID(manifestID.toString())
    }

    private fun initActionbar() {
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }
}
