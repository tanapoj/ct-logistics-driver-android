package com.scgexpress.backoffice.android.ui.delivery.ofd.create


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.scgexpress.backoffice.android.R
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.fragment_ofd_create.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 *
 */
class OfdCreateFragment : Fragment(), HasSupportFragmentInjector {

    companion object {
        fun newInstance(): OfdCreateFragment {
            return OfdCreateFragment().also {
                it.arguments = Bundle()
            }
        }
    }

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var rootView: View

    private lateinit var viewModel: OfdCreateViewModel

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentInjector
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)

        if (context is OfdCreateActivity) {
        } else {
            throw IllegalStateException("This fragment must be use in conjunction with " + OfdCreateActivity::class.java.simpleName)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(OfdCreateViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_ofd_create, container, false)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initButton()
        observeData()
    }

    private fun observeData() {
        viewModel.user.observe(this, Observer { it ->
            if (it != null) {
                etBranch.setText(it.branchCode)
                etDriver.setText(it.personalId)
            }
        })

        viewModel.data.observe(this, Observer { it ->
            if (it != null) {
                activity!!.onBackPressed()
            }
        })

        viewModel.snackbar.observe(this, Observer { it ->
            if (it != null) {
                it.getContentIfNotHandled()?.let {
                    // Only proceed if the event has never been handled
                    Snackbar.make(rootView, it, Snackbar.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun initButton() {
        btnCreate.setOnClickListener {
            if (etVehicle.text.isNotEmpty()) {
                viewModel.createOfdManifest(etVehicle.text.toString())
            } else
                viewModel.showSnackbar("Please insert vehicle")
        }
    }
}
