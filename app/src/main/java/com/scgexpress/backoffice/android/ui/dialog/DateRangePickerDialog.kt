package com.scgexpress.backoffice.android.ui.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AppCompatDialogFragment
import com.archit.calendardaterangepicker.customviews.DateRangeCalendarView
import com.scgexpress.backoffice.android.R
import kotlinx.android.synthetic.main.dialog_date_range_picker.*
import java.util.*

class DateRangePickerDialog : AppCompatDialogFragment() {

    companion object {
        private lateinit var startDate: Calendar
        private lateinit var endDate: Calendar

        fun newInstance(s: Calendar, e: Calendar): DateRangePickerDialog {
            startDate = s
            endDate = e
            val fragment = DateRangePickerDialog()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    private val current: Calendar by lazy {
        Calendar.getInstance()
    }

    private lateinit var mListener: OnDateSelectedListener

    // Container Activity must implement this interface
    interface OnDateSelectedListener {
        fun onDateSelected(startDate: Calendar, endDate: Calendar)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            if (context is Activity) {
                mListener = context as OnDateSelectedListener
            }
        } catch (e: ClassCastException) {
            throw ClassCastException(activity!!.toString() + " must implement OnStatusSelectedListener")
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_date_range_picker, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Perform remaining operations here. No null issues.
        setupView()
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    private fun setupView() {
        btnToday.setOnClickListener {
            startDate = current
            endDate = current
            calendar.setSelectedDateRange(current, current)
        }
        btnOk.setOnClickListener {
            dismiss()
            mListener.onDateSelected(startDate, endDate)
        }
        btnCancel.setOnClickListener {
            dismiss()
        }
        showDatePicker()
    }

    private fun showDatePicker() {
        val now = Calendar.getInstance()
        now.add(Calendar.MONTH, -11)
        val later = now.clone() as Calendar
        later.add(Calendar.MONTH, 12)
        calendar.setVisibleMonthRange(now, later)

        calendar.setCurrentMonth(endDate)
        calendar.setSelectedDateRange(startDate, endDate)

        // DatePicker
        calendar.setCalendarListener(object : DateRangeCalendarView.CalendarListener {
            override fun onFirstDateSelected(s: Calendar?) {
                startDate = s!!
                endDate = s
            }

            override fun onDateRangeSelected(s: Calendar?, e: Calendar?) {
                startDate = s!!
                endDate = e!!
            }
        })
    }
}