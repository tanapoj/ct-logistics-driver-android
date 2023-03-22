package com.scgexpress.backoffice.android.ui.delivery.ofd.detail.booking

import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.ViewModelProviders
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.base.BaseActivity
import com.scgexpress.backoffice.android.common.Const.PARAMS_BOOKING_ID
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_booking_items.*

class BookingItemsActivity : BaseActivity() {

    private val fragment by lazy {
        BookingItemsFragment.newInstance(viewModel)
    }

    private val viewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(BookingItemsViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_items)

        getIntentResult()
        initActionbar()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.clContainer, fragment)
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

    private fun getIntentResult() {
        intent.getStringExtra(PARAMS_BOOKING_ID)?.let {
            viewModel.setBookingID(it)
            if (it.isNotEmpty()) toolbar.title = "${getString(R.string.booking_id_with_colon)} $it"
        }
    }

    private fun initActionbar() {
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }
}
