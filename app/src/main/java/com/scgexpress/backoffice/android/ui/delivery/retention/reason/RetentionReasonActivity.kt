package com.scgexpress.backoffice.android.ui.delivery.retention.reason

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModelProviders
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.base.BaseActivity
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_delivery_retention_reason.*

class RetentionReasonActivity: BaseActivity() {


    private val viewModelRetentionReason: RetentionReasonViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(RetentionReasonViewModel::class.java)
    }

    private val mFragment by lazy {
        RetentionReasonFragment.newInstance(
            viewModelRetentionReason
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delivery_retention_reason)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.clContainer, mFragment)
                .commit()
        }

        initActionBar()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_retention, menu)
        return true
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

    private fun initActionBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Retention Reason"
        }
    }
}