package com.scgexpress.backoffice.android.ui.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.common.Const.PARAMS_FILTER_DISTANCE_CUSTOM
import com.scgexpress.backoffice.android.common.Const.PARAMS_FILTER_DISTANCE_DEFAULT
import com.scgexpress.backoffice.android.common.Const.PARAMS_FILTER_DISTANCE_FAR
import com.scgexpress.backoffice.android.common.Const.PARAMS_FILTER_DISTANCE_NEAR
import com.scgexpress.backoffice.android.common.Const.PARAMS_PICKUP_TASK_ID
import com.scgexpress.backoffice.android.common.Utils
import com.scgexpress.backoffice.android.preferrence.PickupPreference
import com.scgexpress.backoffice.android.ui.delivery.DeliveryMainActivity
import com.scgexpress.backoffice.android.ui.pickup.main.PickupMainActivity
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.fragment_navigation.*
import kotlinx.android.synthetic.main.fragment_navigation.rvItems
import javax.inject.Inject

class NavigationMainFragment : Fragment(), HasSupportFragmentInjector {

    companion object {
        private lateinit var viewModel: NavigationMainViewModel

        fun newInstance(vm: NavigationMainViewModel): NavigationMainFragment {
            viewModel = vm
            return NavigationMainFragment()
                .also {
                    it.arguments = Bundle()
                }
        }
    }

    private val adapter by lazy {
        NavigationMainAdapter(viewModel)
    }

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var pickupPreference: PickupPreference

    private lateinit var mContext: Context
    private lateinit var rootView: View

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentInjector
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
        if (context is NavigationMainActivity) {
            mContext = context
        } else {
            throw IllegalStateException("This fragment must be use in conjunction with " + NavigationMainActivity::class.java.simpleName)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_navigation, container, false)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        observeData()
        initButton()
        initSpinner()
        initRecyclerView()
        search()
    }

    private fun observeData() {
        viewModel.loadData()
        viewModel.getLatLngCurrentLocation()
        viewModel.sortedModeEnable.observe(this, Observer {
            if (it == null) return@Observer
            val buttons = listOf(
                radioFilterNear to PARAMS_FILTER_DISTANCE_NEAR,
                radioFilterFar to PARAMS_FILTER_DISTANCE_FAR,
                radioFilterOrigin to PARAMS_FILTER_DISTANCE_DEFAULT,
                radioFilterCustom to PARAMS_FILTER_DISTANCE_CUSTOM
            )
            for ((button, key) in buttons) {
                button.visibility = if (it[key] == true) View.VISIBLE else View.GONE
            }
        })

        viewModel.displayTaskList.observe(this, Observer {
            if (it == null) return@Observer
            adapter.data = it
            adapter.notifyDataSetChanged()
            rvItems.smoothScrollToPosition(0)
        })

        viewModel.actionPickup.observe(this, Observer { task ->
            Intent(context, PickupMainActivity::class.java).apply {
                pickupPreference.currentScanningTaskIdList = null
                putExtra(PARAMS_PICKUP_TASK_ID, task.id)
            }.also {
                startActivity(it)
                activity!!.finish()
            }
        })

        viewModel.actionDelivery.observe(this, Observer { task ->
            Intent(context, DeliveryMainActivity::class.java).apply {
                pickupPreference.currentScanningTaskIdList = null
                putExtra(PARAMS_PICKUP_TASK_ID, task.id)
            }.also {
                startActivity(it)
                activity!!.finish()
            }
        })

        viewModel.phoneCall.observe(this, Observer {
            if (it == null) return@Observer
            Utils.phoneCall(this.activity!!, it)
        })

        viewModel.location.observe(this, Observer { location ->
            if (location == null) return@Observer
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("google.navigation:q=${location.latitude},${location.longitude}&mode=d")
            ).apply {
                setPackage("com.google.android.apps.maps")
            }.also {
                startActivity(it)
            }
        })
    }

    private fun initSpinner() {
        fun toStrings(sizeData: List<String>) = sizeData.map { item -> item }

        spnTask.adapter = object : ArrayAdapter<String>(
            spnTask.context,
            R.layout.support_simple_spinner_dropdown_item,
            toStrings(viewModel.filterTypeList)
        ) {}

        spnTask.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                parent?.let {
                    if (viewModel.filterTypeList.isNotEmpty()) {
                        resetButton()
                        val type = viewModel.filterTypeList[position]
                        viewModel.setDisplayCategoryTaskList(type)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun initRecyclerView() {
        rvItems.layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        rvItems.adapter = adapter
    }

    private fun search() {
        edtSearch!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.filterByText(edtSearch.text.toString())
                if (edtSearch.text?.length == 0) {
                    radioFilterCustom.background =
                        resources.getDrawable(R.drawable.button_border_green_background)
                } else {
                    resetButton()
                    radioFilterCustom.background =
                        resources.getDrawable(R.drawable.button_green_background_onclick)
                }
            }

        })
    }

    private fun initButton() {
        radioFilterNear.setOnClickListener {
            viewModel.setSortModeBy(PARAMS_FILTER_DISTANCE_NEAR)
        }

        radioFilterFar.setOnClickListener {
            viewModel.setSortModeBy(PARAMS_FILTER_DISTANCE_FAR)
        }

        radioFilterOrigin.setOnClickListener {
            viewModel.setSortModeBy(PARAMS_FILTER_DISTANCE_DEFAULT)
        }
    }

    private fun resetButton() {
        radioFilterFar.isChecked = false
        radioFilterNear.isChecked = false
    }
}