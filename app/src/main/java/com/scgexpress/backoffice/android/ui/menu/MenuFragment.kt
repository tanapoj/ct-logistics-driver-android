package com.scgexpress.backoffice.android.ui.menu

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.common.Utils
import com.scgexpress.backoffice.android.ui.delivery.DeliveryMainActivity
import com.scgexpress.backoffice.android.ui.pickup.scan.PickupScanActivity
import com.scgexpress.backoffice.android.ui.pin.PinActivity
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.fragment_menu.*
import javax.inject.Inject


class MenuFragment : Fragment(), HasSupportFragmentInjector {

    companion object {
        fun newInstance(): MenuFragment {
            return MenuFragment().also {
                it.arguments = Bundle()
            }
        }
    }

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: MenuViewModel

    private lateinit var adapter: MenuAdapter

//    private val fileUploader by lazy {
//        FileUploader(activity!!, loginRepository, loginPreference)
//    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentInjector
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)

        if (context is MenuActivity) {
        } else {
            throw IllegalStateException("This fragment must be use in conjunction with " + MenuActivity::class.java.simpleName)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MenuViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initRecyclerView()
        initButton()
        observeData()
    }

    private fun initRecyclerView() {
        adapter = MenuAdapter(viewModel)

        rvMenu.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvMenu.adapter = adapter
    }

    private fun initButton() {
        btnLogout.setOnClickListener {
            if (!viewModel.checkLastClickTime()) return@setOnClickListener
            showDialogLogout()
        }
    }

    private fun observeData() {
        viewModel.menuItems.observe(this, Observer {
            if (it == null) return@Observer
            adapter.data = it
        })
        viewModel.requestMenu()

        viewModel.menuClicked.observe(this, Observer {
            if (it == null) return@Observer
            clickItem(it)
        })
    }

    private fun clickItem(item: MenuModel) {
        if (!viewModel.checkLastClickTime()) return
        when (item.id) {
            R.id.menu_pickup -> {
                startActivity(Intent(activity, PickupScanActivity::class.java))
            }
            R.id.menu_delivery -> {
                startActivity(Intent(activity, DeliveryMainActivity::class.java))
            }
            R.id.menu_sdreport -> {
                //startActivity(Intent(activity, SDReportMainActivity::class.java))
            }
            R.id.menu_line_haul -> {
                // Create the fragment and show it as a dialog.
                /*val lineHaulDialogMenuFragment = LineHaulDialogMenuFragment.newInstance()
                lineHaulDialogMenuFragment.show(fragmentManager, "dialog")*/
            }
            R.id.menu_line -> {
                activity?.let {
                    val intent = Utils.getLineAppIntent(it)
                    startActivity(intent)
                }
            }
        }
    }

    fun showDialogLogout() {
        val mBuilder = AlertDialog.Builder(context!!)

        mBuilder.setTitle("Are you sure you want to logout?")
        mBuilder.setPositiveButton("OK", null)
        mBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }
        val mDialog = mBuilder.create()
        mDialog.setOnShowListener {
            val b = mDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            b.setOnClickListener {
                mDialog.dismiss()
                logout()
            }
        }
        mDialog.show()
    }

    private fun logout() {
        viewModel.logout()
        val intent = Intent(context, PinActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        activity!!.finish()
    }
}
