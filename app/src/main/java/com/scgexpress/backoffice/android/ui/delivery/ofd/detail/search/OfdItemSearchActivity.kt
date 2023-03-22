package com.scgexpress.backoffice.android.ui.delivery.ofd.detail.search

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
import com.scgexpress.backoffice.android.common.Const.PARAMS_MANIFEST_ID
import com.scgexpress.backoffice.android.common.Const.PARAMS_MANIFEST_ITEM_LIST
import com.scgexpress.backoffice.android.model.OfdItemInfoList
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_ofd_item_search.*


class OfdItemSearchActivity : BaseActivity() {

    private val fragment by lazy {
        OfdItemSearchFragment.newInstance(viewModel)
    }

    private val viewModel: OfdItemSearchViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(OfdItemSearchViewModel::class.java)
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
            R.id.menu_scan -> {
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
        val search = menu!!.findItem(R.id.menu_search)
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

    private fun getIntentResult() {
        intent.getStringExtra(PARAMS_MANIFEST_ID)?.let {
            if (it.isNotEmpty()) {
                viewModel.setManifestBarcode(it)
            }
        }

        (intent.getSerializableExtra(PARAMS_MANIFEST_ITEM_LIST) as OfdItemInfoList?)?.let {
            if (it.item.isNotEmpty()) {
                viewModel.setData(it)
            }
        }
    }

    private fun initActionbar() {
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = viewModel.manifestBarcode.value
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
