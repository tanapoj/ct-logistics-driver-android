package com.scgexpress.backoffice.android.ui.delivery.ofd.detail.tracking

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModelProviders
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.base.BaseActivity
import com.scgexpress.backoffice.android.common.Const.PARAMS_MANIFEST_ID
import com.scgexpress.backoffice.android.common.Const.PARAMS_TRACKING_ID
import com.scgexpress.backoffice.android.model.BookingRejectStatusModel
import com.scgexpress.backoffice.android.ui.dialog.RejectStatusDialogFragment
import com.scgexpress.backoffice.android.ui.notification.NotificationActivity
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_tracking_details.*

class TrackingDetailsActivity : BaseActivity(), RejectStatusDialogFragment.OnStatusSelectedListener {

    private val viewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(TrackingDetailsViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracking_details)

        getManifestIDIntent()
        initActionbar()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.clContainer, TrackingDetailsFragment.newInstance(viewModel))
                    .commit()
        }
    }

    override fun onStatusSelected(rejectStatus: BookingRejectStatusModel, note: String) {
        viewModel.confirmOfdReStatus(rejectStatus, note)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.menu_notification -> {
                startActivity(Intent(this, NotificationActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    private fun getManifestIDIntent() {
        intent.getStringExtra(PARAMS_MANIFEST_ID)?.let {
            if (it.isNotEmpty()) {
                viewModel.setManifestId(it)
            }
        }

        intent.getStringExtra(PARAMS_TRACKING_ID)?.let {
            if (it.isNotEmpty()) {
                viewModel.setTrackingId(it)
            }
        }
    }


    private fun initActionbar() {
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }
}
