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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.constant.BookingRejectStatus
import com.scgexpress.backoffice.android.model.BookingRejectStatusModel
import kotlinx.android.synthetic.main.dialog_ofd_re_status.*

class RejectStatusDialogFragment : AppCompatDialogFragment() {

    companion object {

        fun newInstance(): RejectStatusDialogFragment {
            val fragment = RejectStatusDialogFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    private val adapter by lazy {
        RejectStatusDialogAdapter()
    }

    var title: String = ""

    private lateinit var mListener: OnStatusSelectedListener

    // Container Activity must implement this interface
    interface OnStatusSelectedListener {
        fun onStatusSelected(rejectStatus: BookingRejectStatusModel, note: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            if (context is Activity) {
                mListener = context as OnStatusSelectedListener
            }
        } catch (e: ClassCastException) {
            throw ClassCastException(activity!!.toString() + " must implement OnStatusSelectedListener")
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_ofd_re_status, container, false)
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
            dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }

    private fun setupView() {
        initRecyclerView(recyclerView)
        btnOk.setOnClickListener {
            if (adapter.selectedPosition > -1) {
                if (adapter.isOther) {
                    if (edtNote.text.toString().isEmpty()) {
                        Snackbar.make(
                            edtNote,
                            getString(R.string.sentence_retention_please_insert_the_reason),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    } else {
                        //confirmOfdRetention(manifestID, input.text.toString())
                        mListener.onStatusSelected(adapter.data[adapter.selectedPosition], edtNote.text.toString())
                        dismiss()
                    }
                } else {
                    //confirmOfdRetention(manifestID, input.text.toString())
                    mListener.onStatusSelected(adapter.data[adapter.selectedPosition], edtNote.text.toString())
                    dismiss()
                }
            } else {
                Snackbar.make(
                    edtNote,
                    getString(R.string.sentence_retention_please_insert_the_reason),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
        btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun initRecyclerView(recyclerView: RecyclerView) {
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView.adapter = adapter
        adapter.data = BookingRejectStatus.list
    }
}