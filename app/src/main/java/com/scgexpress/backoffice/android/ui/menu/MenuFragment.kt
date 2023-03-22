package com.scgexpress.backoffice.android.ui.menu

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
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
import com.scgexpress.backoffice.android.ui.delivery.task.DeliveryTaskActivity
import com.scgexpress.backoffice.android.ui.navigation.NavigationMainActivity
import com.scgexpress.backoffice.android.ui.pickup.task.PickupTaskActivity
import com.scgexpress.backoffice.android.ui.pin.PinActivity
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.fragment_menu.*
import timber.log.Timber
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

        viewModel.loadOfflineData()
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
        //TODO: remove this test func
        btnLogout.setOnLongClickListener {
            viewModel._clearPickupData()
            true
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

        viewModel.dialogMessage.observe(this, Observer {
            showDialogMessage(it)
        })

        viewModel.syncState.observe(this, Observer { (state, percent) ->
            Timber.d("syncState: state=$state percent=$percent")
            when (state) {
                MenuViewModel.Companion.SyncState.NONE -> cvLoadingStatus.visibility = View.GONE
                MenuViewModel.Companion.SyncState.HAS_PENDING -> {
                    cvLoadingStatus.visibility = View.VISIBLE
                    pbStatus.isIndeterminate = true
                }
                MenuViewModel.Companion.SyncState.SYNCING -> {
                    cvLoadingStatus.visibility = View.VISIBLE
                    pbStatus.isIndeterminate = false
                    pbStatus.progress = percent
                }
                MenuViewModel.Companion.SyncState.SYNC_DONE -> {
                    cvLoadingStatus.visibility = View.VISIBLE
                    pbStatus.isIndeterminate = true
                    Handler().postDelayed({
                        cvLoadingStatus.visibility = View.GONE
                        showDialogMessage(resources.getString(R.string.logout_sync_pending_done))
                    }, 1_000)
                }
            }
        })

        viewModel.logout.observe(this, Observer {
            if(it == true) logout()
        })
    }

    private fun clickItem(item: MenuModel) {
        if (!viewModel.checkLastClickTime()) return
        when (item.id) {
            R.id.menu_pickup -> {
                startActivity(Intent(activity, PickupTaskActivity::class.java))
            }
            R.id.menu_delivery -> {
                startActivity(Intent(activity, DeliveryTaskActivity::class.java))
            }
            R.id.menu_navigation -> {
                startActivity(Intent(activity, NavigationMainActivity::class.java))
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

    fun showDialogMessage(msg: String) {
        AlertDialog.Builder(context!!).apply {
            setMessage(msg)
            setPositiveButton("Ok") { dialog, _ ->
                dialog.dismiss()
            }
        }.run {
            create()
        }.also {
            it.show()
        }
    }

    fun showDialogLogout() {
        val mBuilder = AlertDialog.Builder(context!!)

        mBuilder.setTitle(resources.getString(R.string.logout_confirm_msg))
        mBuilder.setPositiveButton(resources.getString(R.string.logout_confirm_msg_btn_ok), null)
        mBuilder.setNegativeButton(resources.getString(R.string.logout_confirm_msg_btn_cancel)) { dialog, _ ->
            dialog.cancel()
        }
        val mDialog = mBuilder.create()
        mDialog.setOnShowListener {
            val b = mDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            b.setOnClickListener {
                mDialog.dismiss()
                viewModel.logout()
            }
        }
        mDialog.show()
    }

    private fun logout() {
        val intent = Intent(context, PinActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        activity!!.finish()
    }
}
