package com.scgexpress.backoffice.android.ui.delivery.ofd.detail.item.dragged

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.base.BaseActivity
import com.scgexpress.backoffice.android.common.Const.PARAMS_MANIFEST_ID
import com.scgexpress.backoffice.android.common.Const.PARAMS_MANIFEST_ITEM_LIST
import com.scgexpress.backoffice.android.model.OfdItemInfoList
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_ofd_detail_items_draggable.*

class OfdDetailItemsDraggableActivity : BaseActivity() {

    private val viewModel: OfdDetailItemsDraggableViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(OfdDetailItemsDraggableViewModel::class.java)
    }

    private val adapter: OfdDetailItemsDraggableAdapter by lazy {
        OfdDetailItemsDraggableAdapter(viewModel)
    }

    private var manifestID: String? = ""
    private var trackingList: OfdItemInfoList? = OfdItemInfoList()

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ofd_detail_items_draggable)

        getManifestIDIntent()
        initActionbar()
        initRecyclerView()

        //Snackbar.make(findViewById(R.id.container), "TIP: Long press item to initiate Drag & Drop action!", Snackbar.LENGTH_LONG).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.menu_done -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.ofd_detail_draggable_menu, menu)
        return true
    }

    private fun initRecyclerView() {
        // Setup D&D feature and RecyclerView
        val dragMgr = RecyclerViewDragDropManager()

        dragMgr.setInitiateOnMove(false)
        dragMgr.setInitiateOnLongPress(true)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = dragMgr.createWrappedAdapter(adapter)

        adapter.data = trackingList!!.item.toMutableList()
        recyclerView.layoutManager!!.scrollToPosition(trackingList!!.position)

        dragMgr.attachRecyclerView(recyclerView)
    }

    private fun getManifestIDIntent() {
        manifestID = intent.getStringExtra(PARAMS_MANIFEST_ID)
        trackingList = intent.getSerializableExtra(PARAMS_MANIFEST_ITEM_LIST) as OfdItemInfoList?

        if (manifestID != null)
            viewModel.setManifestID(manifestID!!)
    }

    private fun initActionbar() {
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }
}
