package com.scgexpress.backoffice.android.ui.delivery.retention.changesaledriver

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModelProviders
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.base.BaseActivity
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_retention_change_sale_driver.*

class RetentionChangeSDActivity: BaseActivity() {
    private val viewModelRetentionChangeSD: RetentionChangeSDViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(RetentionChangeSDViewModel::class.java)
    }

    private val mFragment by lazy {
        RetentionChangeSDFragment.newInstance(
            viewModelRetentionChangeSD
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_retention_change_sale_driver)

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
            title = "Reason of retention"
        }
    }
}