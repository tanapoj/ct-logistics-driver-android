package com.scgexpress.backoffice.android.ui.pickup.task.search

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProviders
import com.google.zxing.integration.android.IntentIntegrator
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.R.id
import com.scgexpress.backoffice.android.R.layout
import com.scgexpress.backoffice.android.base.BaseActivity
import com.scgexpress.backoffice.android.common.Const.PARAMS_PICKUP_ITEM_LIST
import com.scgexpress.backoffice.android.model.BookingRejectStatusModel
import com.scgexpress.backoffice.android.model.pickup.PickupTaskList
import com.scgexpress.backoffice.android.ui.dialog.RejectStatusDialogFragment
import com.scgexpress.backoffice.android.ui.pickup.task.PickupTaskViewModel
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_ofd_item_search.*


class PickupSearchActivity : BaseActivity(), RejectStatusDialogFragment.OnStatusSelectedListener {

    private val fragment by lazy {
        PickupSearchFragment.newInstance(viewModel)
    }

    private val viewModel: PickupTaskViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(PickupTaskViewModel::class.java)
    }
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_ofd_item_search)

        getIntentResult()
        initActionbar()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(id.clContainer, fragment)
                .commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            id.menu_scan -> {
                IntentIntegrator(this).run {
                    initiateScan()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.ofd_item_search, menu)
        val search = menu!!.findItem(id.menu_search)
        search.expandActionView()
        searchView = search.actionView as SearchView
        search(searchView)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        IntentIntegrator.parseActivityResult(requestCode, resultCode, data)?.let { it ->
            it.contents?.let {
                searchView.setQuery(it, true)
            }
        }
    }

    override fun onStatusSelected(rejectStatus: BookingRejectStatusModel, note: String) {
        viewModel.rejectBooking(rejectStatus, note)
    }

    private fun getIntentResult() {
        (intent.getSerializableExtra(PARAMS_PICKUP_ITEM_LIST) as PickupTaskList?)?.let {
            if (it.items!!.isNotEmpty()) {
                viewModel.setDataSearch(it)
            }
        }
    }

    private fun initActionbar() {
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        //supportActionBar!!.title = viewModelRetentionReason.manifestBarcode.value
    }

    private fun search(searchView: SearchView) {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                viewModel.search(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return true
            }
        })
    }
}
