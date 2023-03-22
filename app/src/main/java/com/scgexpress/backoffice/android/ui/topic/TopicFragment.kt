package com.scgexpress.backoffice.android.ui.topic

import android.content.Context
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
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.fragment_topic.*
import javax.inject.Inject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class MainFragment : Fragment(), HasSupportFragmentInjector {

    companion object {
        fun newInstance(): MainFragment {
            return MainFragment().also {
                it.arguments = Bundle()
            }
        }
    }

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var rootView: View

    private val viewModel: TopicViewModel by lazy {
        ViewModelProviders.of(activity!!, viewModelFactory).get(TopicViewModel::class.java)
    }

    private val adapter: TopicAdapter by lazy {
        TopicAdapter(viewModel)
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentInjector
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)

        if (context is TopicActivity) {
        } else {
            throw IllegalStateException("This fragment must be use in conjunction with " + TopicActivity::class.java.simpleName)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_topic, container, false)
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
    }

    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView.adapter = adapter
    }

    private fun loadData() {
        viewModel.getTopics()
    }

    private fun observeData() {
        viewModel.data.observe(this, Observer {
            if (it == null) return@Observer
            adapter.data = it
        })

        viewModel.alertMessage.observe(this, Observer { it ->
            it?.getContentIfNotHandled()?.let {
                // Only proceed if the event has never been handled
                Snackbar.make(rootView, it, Snackbar.LENGTH_SHORT).show()
            }
        })
    }
}
