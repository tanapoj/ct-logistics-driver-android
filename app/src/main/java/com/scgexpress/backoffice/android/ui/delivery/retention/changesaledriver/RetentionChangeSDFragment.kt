package com.scgexpress.backoffice.android.ui.delivery.retention.changesaledriver

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.db.entity.masterdata.TblAuthUsers
import com.scgexpress.backoffice.android.db.entity.masterdata.TblScgBranch
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.fragment_retention_change_sale_driver.*
import javax.inject.Inject

class RetentionChangeSDFragment : Fragment(), HasSupportFragmentInjector {

    companion object {
        private lateinit var viewModel: RetentionChangeSDViewModel

        fun newInstance(vm: RetentionChangeSDViewModel): RetentionChangeSDFragment {
            viewModel = vm
            return RetentionChangeSDFragment()
                .also {
                    it.arguments = Bundle()
                }
        }
    }

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    private lateinit var mContext: Context
    private lateinit var rootView: View

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentInjector
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)

        if (context is RetentionChangeSDActivity) {
            mContext = context
        } else {
            throw IllegalStateException("This fragment must be use in conjunction with " + RetentionChangeSDActivity::class.java.simpleName)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initSpinner()
        observeData()
        initButton()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_retention_change_sale_driver, container, false)
        return rootView
    }

    private fun initButton(){
        btnConfirm.setOnClickListener {
            activity!!.finish()
        }
    }

    private fun observeData(){
        viewModel.loadData()

        viewModel.masterDataBranch.observe(this, Observer {
            if (it == null) {
                return@Observer
            }
            setMasterDataBranchSpinner(it)
        })
    }

    private fun initSpinner(){
        setMasterDataBranchSpinner(emptyList())
        spnBranch.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                parent?.let {
                    if (viewModel.masterDataBranchList.isNotEmpty()) {
                        val branch = viewModel.masterDataBranchList[position]
                        val branchId = branch.branchId
                        branchId?.let { it1 -> getMasterDataSD(it1) }
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setMasterDataBranchSpinner(list: List<TblScgBranch>) {
        viewModel.masterDataBranchList = list

        fun toStrings(sizeData: List<TblScgBranch>) = sizeData.map { item -> item.branchCode }

        spnBranch.adapter = object : ArrayAdapter<String>(
            spnBranch.context, R.layout.support_simple_spinner_dropdown_item, toStrings(viewModel.masterDataBranchList)
        ) {}
    }

    private fun setMasterDataSDSpinner(list: List<TblAuthUsers>) {
        viewModel.masterDataSDList = list

        fun toStrings(sizeData: List<TblAuthUsers>) = sizeData.map { item -> item.personalId +" - "+ item.fname + " " + item.lname }

        spnSD.adapter = object : ArrayAdapter<String>(
            spnSD.context, R.layout.support_simple_spinner_dropdown_item, toStrings(viewModel.masterDataSDList)
        ) {}
    }

    private fun getMasterDataSD(branchId: String) {

        viewModel.getMasterDataSD(branchId)

        viewModel.masterDataSD.observe(this, Observer {
            if  (it == null) {
                return@Observer
            }
            setMasterDataSDSpinner(it)
        })
    }
}