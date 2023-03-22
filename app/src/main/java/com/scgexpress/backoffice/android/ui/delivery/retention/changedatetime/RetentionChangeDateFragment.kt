package com.scgexpress.backoffice.android.ui.delivery.retention.changedatetime

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.scgexpress.backoffice.android.R
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.fragment_retention_change_date_time.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class RetentionChangeDateFragment: Fragment(), HasSupportFragmentInjector {

    companion object {
        private lateinit var viewModel: RetentionChangeDateViewModel

        fun newInstance(vm: RetentionChangeDateViewModel): RetentionChangeDateFragment {
            viewModel = vm
            return RetentionChangeDateFragment()
                .also {
                    it.arguments = Bundle()
                }
        }
    }

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    private lateinit var mContext: Context
    private lateinit var rootView: View
    private val cal = Calendar.getInstance()

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentInjector
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
        if (context is RetentionChangeDateActivity) {
            mContext = context
        } else {
            throw IllegalStateException("This fragment must be use in conjunction with " + RetentionChangeDateActivity::class.java.simpleName)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initButton()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_retention_change_date_time, container, false)
        return rootView
    }

    private fun initButton(){
        edtDeliveryDate.setOnClickListener {
            initDatePicker()
        }

        edtDeliveryTime.setOnClickListener {
            initTimePicker()
        }
    }

    private fun updateDateInView() {
        val myFormat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        edtDeliveryDate!!.setText(sdf.format(cal.time))
    }

    private fun initDatePicker(){
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }
        val datePickerDialog = DatePickerDialog(
            activity,
            android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
            dateSetListener,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).apply {
            window!!.setBackgroundDrawableResource(android.R.color.transparent)
            show()
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun initTimePicker(){
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            edtDeliveryTime.setText(SimpleDateFormat("HH:mm").format(cal.time))
        }
        val timePickerDialog = TimePickerDialog(
            activity,
            android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
            timeSetListener,
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            true
        ).apply {
            window!!.setBackgroundDrawableResource(android.R.color.transparent)
            show()
        }
    }
}