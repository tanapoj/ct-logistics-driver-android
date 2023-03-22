package com.scgexpress.backoffice.android.ui.delivery.ofd.detail.booking

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModelProviders
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.base.BaseActivity
import com.scgexpress.backoffice.android.common.Const.PARAMS_MANIFEST_BOOKING_INFO
import com.scgexpress.backoffice.android.common.Const.REQUEST_CODE_PICKUP
import com.scgexpress.backoffice.android.model.BookingInfo
import com.scgexpress.backoffice.android.model.BookingRejectStatusModel
import com.scgexpress.backoffice.android.ui.dialog.RejectStatusDialogFragment
import com.scgexpress.backoffice.android.ui.notification.NotificationActivity
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_tracking_details.*

class BookingDetailsActivity : BaseActivity(), RejectStatusDialogFragment.OnStatusSelectedListener {

    private val viewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(BookingDetailsViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracking_details)

        getIntentDetails()
        initActionbar()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.clContainer, BookingDetailsFragment.newInstance(viewModel))
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICKUP) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK)
                finish()
            }
        }
    }

    override fun onStatusSelected(rejectStatus: BookingRejectStatusModel, note: String) {
        viewModel.rejectBooking(rejectStatus, note)
    }


    private fun getIntentDetails() {
        (intent.getSerializableExtra(PARAMS_MANIFEST_BOOKING_INFO) as? BookingInfo)?.let {
            if (it.bookingID != "") {
                viewModel.setBookingInfo(it)
            }
        }
    }

    private fun initActionbar() {
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }
}
