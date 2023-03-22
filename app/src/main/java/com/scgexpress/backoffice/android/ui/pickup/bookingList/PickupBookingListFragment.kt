package com.scgexpress.backoffice.android.ui.pickup.bookingList

import android.app.AlertDialog
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
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.common.Const
import com.scgexpress.backoffice.android.common.Const.FLAG_PICKUP_BOOKING_LIST_CONTINUE_NEXT_TASK
import com.scgexpress.backoffice.android.common.Const.PARAMS_PICKUP_COMPLETED
import com.scgexpress.backoffice.android.common.Const.PARAMS_PICKUP_SEARCH_SHIPPER_CODE
import com.scgexpress.backoffice.android.common.Const.PARAMS_PICKUP_TASK_ID
import com.scgexpress.backoffice.android.common.Const.PARAMS_PICKUP_TASK_ID_LIST
import com.scgexpress.backoffice.android.common.Const.PARAMS_PICKUP_TASK_SELECT_TAB
import com.scgexpress.backoffice.android.ui.pickup.main.PickupMainActivity
import com.scgexpress.backoffice.android.ui.pickup.task.PickupTaskActivity
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.fragment_pickup_task_list.*
import kotlinx.android.synthetic.main.fragment_topic.btnOk
import kotlinx.android.synthetic.main.fragment_topic.recyclerView
import timber.log.Timber
import javax.inject.Inject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class PickupBookingListFragment : Fragment(), HasSupportFragmentInjector {

    companion object {
        fun newInstance(): PickupBookingListFragment {
            return PickupBookingListFragment().also {
                it.arguments = Bundle()
            }
        }
    }

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var rootView: View

    private val viewModel: PickupBookingListViewModel by lazy {
        ViewModelProviders.of(activity!!, viewModelFactory)
            .get(PickupBookingListViewModel::class.java)
    }

    private val adapter: PickupBookingListAdapter by lazy {
        PickupBookingListAdapter(viewModel)
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentInjector
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)

        if (context is PickupBookingListActivity) {
        } else {
            throw IllegalStateException("This fragment must be use in conjunction with " + PickupBookingListActivity::class.java.simpleName)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_pickup_task_list, container, false)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initRecyclerView()
        initButton()
        loadData()
        observeData()
    }

    private fun initButton() {
        btnOk.setOnClickListener {
            viewModel.saveSelectedBookingId()
            viewModel.startNextTask()
        }
    }

    //    private fun startNextTask() {
    //        val id = viewModelRetentionReason.getNextSelectedBookingId()
    //        if (id == null || id.isBlank()) {
    //            return backToPickupTaskActivity()
    //        }
    //        Timber.d("F.startNextTask taskIds=${viewModelRetentionReason.taskIdsRemembered} nextId=$id ")
    //        startPickupMainActivity(id)
    //    }

    private fun startPickupMainActivity(taskId: String? = null, shipperCode: String? = null) {
        Timber.d("F.startPickupMainActivity go on ~ taskId: $taskId, shipperCode: $shipperCode")
        when {
            taskId.isNullOrBlank() && shipperCode.isNullOrBlank() -> {
                AlertDialog.Builder(activity!!).apply {
                    setMessage("taskId and Shipper code is missing")
                    setPositiveButton("Ok") { _, _ ->

                    }
                }.run {
                    create()
                }.also {
                    it.show()
                }
            }
            else -> {
                Intent(activity, PickupMainActivity::class.java).apply {
                    putExtra(PARAMS_PICKUP_TASK_ID, taskId)
                    putExtra(PARAMS_PICKUP_SEARCH_SHIPPER_CODE, shipperCode)
                }.also {
                    startActivity(it)
                }
            }
        }
    }

    private fun backToPickupTaskActivity() {
        Intent(activity, PickupTaskActivity::class.java).apply {
            putExtra(PARAMS_PICKUP_TASK_SELECT_TAB, PARAMS_PICKUP_COMPLETED)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }.also {
            startActivity(it)
        }
    }

    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView.adapter = adapter
    }

    private fun loadData() {
        activity?.apply {
            viewModel.autoContinueFirstTask = false

            val shipperCode = intent.getStringExtra(PARAMS_PICKUP_SEARCH_SHIPPER_CODE)?.let {
                viewModel.shipperCode = it
                it
            }

            val ids = intent.getStringArrayExtra(PARAMS_PICKUP_TASK_ID_LIST)?.let {
                val list = it.toMutableList()
                viewModel.taskIds = list
                list
            }
            val continueNext =
                intent.getBooleanExtra(FLAG_PICKUP_BOOKING_LIST_CONTINUE_NEXT_TASK, false).let {
                    viewModel.continueNextFlag = it
                    it
                }

            val taskNotExist = intent.getBooleanExtra(Const.PARAMS_PICKUP_TASK_NOT_EXIST, false)

            Timber.d("F.booking.loadData() intent: taskNotExist=$taskNotExist ids=${ids?.toList()}, shipperCode=${viewModel.shipperCode}, continueNext=$continueNext, vm.taskIdsRemembered=${viewModel.taskIdsRemembered}")

            if (!taskNotExist) {
                viewModel.initList()
            }
        }
    }

    private fun observeData() {
        viewModel.bookings.observe(this, Observer {
            if (it == null) return@Observer
            adapter.pickupTasks = it
            adapter.notifyDataSetChanged()
        })

        viewModel.enableSubmitButton.observe(this, Observer {
            btnOk.isEnabled = it == true
        })

        //        viewModelRetentionReason.noBookingSelected.observe(this, Observer {
        //            if (it == true) {
        //                startPickupMainActivity()
        //            }
        //        })

        viewModel.selectedCount.observe(this, Observer {
            if (it > 0) {
                tvBookingCount.visibility = View.VISIBLE
                tvBookingCount.text = "$it"
            } else {
                tvBookingCount.visibility = View.INVISIBLE
            }
        })

        viewModel.actionBackToTaskActivity.observe(this, Observer {
            if (it == true) {
                backToPickupTaskActivity()
            }
        })

        //        viewModelRetentionReason.actionContinueNextTask.observe(this, Observer {
        //            if (it == true) {
        //                startPickupMainActivity()
        //            }
        //        })

        viewModel.actionStartTask.observe(this, Observer { (taskId, shipperCode) ->
            startPickupMainActivity(taskId, shipperCode)
        })

        viewModel.intentExtraIncorrect.observe(this, Observer {
            if (it != true) return@Observer
            AlertDialog.Builder(activity!!).apply {
                setMessage("taskId and Shipper code is missing")
                setPositiveButton("Ok") { _, _ ->

                }
            }.run {
                create()
            }.also {
                it.show()
            }
        })
    }
}
