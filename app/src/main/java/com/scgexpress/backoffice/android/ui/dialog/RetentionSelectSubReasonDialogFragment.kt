package com.scgexpress.backoffice.android.ui.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.common.Const.PARAMS_REASON_CHANGE_DATE
import com.scgexpress.backoffice.android.common.Const.PARAMS_REASON_CHANGE_SD
import com.scgexpress.backoffice.android.common.Const.PARAMS_REASON_NORMAL
import com.scgexpress.backoffice.android.ui.delivery.ofd.retention.OfdRetentionActivity
import com.scgexpress.backoffice.android.ui.delivery.retention.changedatetime.RetentionChangeDateActivity
import com.scgexpress.backoffice.android.ui.delivery.retention.changesaledriver.RetentionChangeSDActivity
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.dialog_sub_reason.*
import javax.inject.Inject

class RetentionSelectSubReasonDialogFragment : AppCompatDialogFragment(),
    HasSupportFragmentInjector {

    companion object {
        fun newInstance(): RetentionSelectSubReasonDialogFragment {
            val fragment = RetentionSelectSubReasonDialogFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var mContext: Context
    private lateinit var rootView: View

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentInjector
    }

    private val adapter by lazy {
        RetentionSelectSubReasonDialogAdapter(viewModel)
    }

    private val viewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory)
            .get(RetentionSelectSubReasonDialogViewModel::class.java)
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)

        try {
            if (context is Activity) {
                mContext = context
            }
        } catch (e: ClassCastException) {
            throw ClassCastException(activity!!.toString() + " must implement OnOptionSelectedListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.dialog_sub_reason, container, false)
        return rootView
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            viewModel.setData(arguments!!.getString("Reason", ""), arguments!!.getString("Id", ""))
            tvReason.text = viewModel.reason.value
        }
        observeData()
        initButton()
        initRecyclerView(recyclerView)
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    private fun initButton() {
        btnOk.setOnClickListener {
            val subReason = viewModel.itemClick.value
            when (subReason!!.type) {
                PARAMS_REASON_CHANGE_DATE -> goToRetentionChangeDateActivity()
                PARAMS_REASON_CHANGE_SD -> goToRetentionChangeSDActivity()
                PARAMS_REASON_NORMAL -> goToRetentionActivity()
            }
            dismiss()
        }
        btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun observeData() {
        viewModel.subReason.observe(this, Observer {
            if (it == null) {
                return@Observer
            }
            adapter.data = it
            adapter.notifyDataSetChanged()
        })
    }

    private fun initRecyclerView(recyclerView: RecyclerView) {
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView.adapter = adapter
    }

    private fun goToRetentionChangeSDActivity() {
        startActivity(Intent(activity, RetentionChangeSDActivity::class.java))
    }

    private fun goToRetentionChangeDateActivity() {
        startActivity(Intent(activity, RetentionChangeDateActivity::class.java))
    }

    private fun goToRetentionActivity() {
        startActivity(Intent(activity, OfdRetentionActivity::class.java))
    }
}