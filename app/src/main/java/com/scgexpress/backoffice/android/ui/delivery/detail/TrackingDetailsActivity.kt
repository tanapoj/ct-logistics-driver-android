package com.scgexpress.backoffice.android.ui.delivery.detail

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModelProviders
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.base.BaseActivity
import com.scgexpress.backoffice.android.common.Const.PARAMS_PICKUP_TASK_ID
import com.scgexpress.backoffice.android.common.Const.PARAMS_TAG_DIALOG_PHOTO_SELECT
import com.scgexpress.backoffice.android.model.PhotoStored
import com.scgexpress.backoffice.android.model.PhotoTitle
import com.scgexpress.backoffice.android.ui.dialog.PhotoExpandDialogFragment
import com.scgexpress.backoffice.android.ui.dialog.PhotoSelectDialogFragment
import com.scgexpress.backoffice.android.ui.notification.NotificationActivity
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_pickup_details.*

class TrackingDetailsActivity : BaseActivity(),
    PhotoSelectDialogFragment.OnOptionSelectedListener {

    private val viewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(TrackingDetailsViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracking_details)

        getIntentDetails()
        initActionbar()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.clContainer, TrackingDetailsFragment.newInstance(viewModel))
                .commit()
        }
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

    override fun onStatusSelected(photo: PhotoTitle) {
        viewPhoto(photo.photoStored)
    }

    private fun getIntentDetails() {
        (intent.getStringExtra(PARAMS_PICKUP_TASK_ID))?.let {
            viewModel.setTrackingId(it)
        }
    }

    private fun initActionbar() {
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }


    private fun viewPhoto(photoStored: PhotoStored?) {
        if (photoStored == null) return
        val fragment = PhotoExpandDialogFragment.newInstance(photoStored)
        fragment.show(supportFragmentManager, PARAMS_TAG_DIALOG_PHOTO_SELECT)
    }
}
