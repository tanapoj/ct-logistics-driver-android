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
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.model.PhotoStored
import com.scgexpress.backoffice.android.model.PhotoTitle
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.dialog_photo_select.*
import javax.inject.Inject

class PhotoSelectDialogFragment : AppCompatDialogFragment(),
    HasSupportFragmentInjector {

    companion object {
        private lateinit var senderPhotos: PhotoStored
        private lateinit var receiverPhotos: PhotoStored

        fun newInstance(sender: PhotoStored, receiver: PhotoStored): PhotoSelectDialogFragment {
            senderPhotos = sender
            receiverPhotos = receiver
            val fragment = PhotoSelectDialogFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var mListener: OnOptionSelectedListener

    private val viewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(PhotoDialogViewModel::class.java)
    }

    private val adapter by lazy {
        PhotoSelectDialogAdapter(context!!, mListener, viewModel)
    }

    // Container Activity must implement this interface
    interface OnOptionSelectedListener {
        fun onStatusSelected(photo: PhotoTitle)
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentInjector
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
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