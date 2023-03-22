package com.scgexpress.backoffice.android.ui.delivery.scan

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
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.model.PhotoStored
import com.scgexpress.backoffice.android.model.PhotoTitle
import kotlinx.android.synthetic.main.dialog_photo_select.*

class DeliverySelectDialogFragment : AppCompatDialogFragment() {

    companion object {
        private lateinit var senderPhotos: PhotoStored
        private lateinit var receiverPhotos: PhotoStored

        fun newInstance(): DeliverySelectDialogFragment {
            val fragment = DeliverySelectDialogFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var mListener: OnOptionSelectedListener

    private val adapter by lazy {
        DeliverySelectDialogAdapter(context!!, mListener)
    }


    // Container Activity must implement this interface
    interface OnOptionSelectedListener {
        fun onStatusSelected(photo: PhotoTitle)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            if (context is Activity) {
                mListener = context as OnOptionSelectedListener
            }
        } catch (e: ClassCastException) {
            throw ClassCastException(activity!!.toString() + " must implement OnOptionSelectedListener")
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_photo_select, container, false)
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
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    private fun setupView() {
        initRecyclerView(recyclerView)
        btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun initRecyclerView(recyclerView: RecyclerView) {
        val items = listOf(
            PhotoTitle(context!!.getString(R.string.sender_photo), senderPhotos),
            PhotoTitle(context!!.getString(R.string.receiver_photo), receiverPhotos)
        )
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView.adapter = adapter
        adapter.data = items
    }
}