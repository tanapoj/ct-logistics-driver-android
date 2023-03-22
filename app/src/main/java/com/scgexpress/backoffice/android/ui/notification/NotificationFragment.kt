package com.scgexpress.backoffice.android.ui.notification

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.common.Const
import com.scgexpress.backoffice.android.ui.pickup.task.PickupTaskActivity
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.fragment_notification.*
import javax.inject.Inject

class NotificationFragment : Fragment(), HasSupportFragmentInjector {

    companion object {
        fun newInstance(): NotificationFragment {
            return NotificationFragment().also {
                it.arguments = Bundle()
            }
        }
    }

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var rootView: View

    private lateinit var viewModel: NotificationViewModel

    private lateinit var adapter: NotificationAdapter

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentInjector
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)

        if (context is NotificationActivity) {
        } else {
            throw IllegalStateException("This fragment must be use in conjunction with " + NotificationActivity::class.java.simpleName)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(NotificationViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_notification, container, false)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initRecyclerView()
        loadNotification()
        observeData()
    }

    private fun initRecyclerView() {
        adapter = NotificationAdapter(viewModel)

        rvNotification.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvNotification.adapter = adapter
    }

    private fun observeData() {
        viewModel.notificationItems.observe(this, Observer {
            if (it == null) return@Observer
            adapter.data = it
        })

        viewModel.snackbar.observe(this, Observer { it ->
            if (it != null) {
                it.getContentIfNotHandled()?.let {
                    // Only proceed if the event has never been handled
                    Snackbar.make(rootView, it, Snackbar.LENGTH_SHORT).show()
                }
            }
        })

        viewModel.goBooking.observe(this, Observer { it ->
            if (it != null) {
                it.getContentIfNotHandled()?.let {
                    activity!!.onBackPressed()
                    goToPickupTaskActivity()
                }
            }
        })
    }

    private fun loadNotification() {
        viewModel.initNotification()
    }

    private fun goToPickupTaskActivity() {
        Intent(activity, PickupTaskActivity::class.java).apply {
            putExtra(Const.PARAMS_PICKUP_TASK_SELECT_TAB, Const.PARAMS_PICKUP_COMPLETED)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }.also {
            startActivity(it)
        }
    }
}
