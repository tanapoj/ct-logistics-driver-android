package com.scgexpress.backoffice.android.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.integration.android.IntentIntegrator
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.common.decodeBase64
import com.scgexpress.backoffice.android.ui.masterdata.MasterDataActivity
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.fragment_login.*
import javax.inject.Inject


class LoginFragment : Fragment(), HasSupportFragmentInjector {

    companion object {
        //        private const val username: String = "15000591"
        //        private const val username: String = "16000260"
        private const val username: String = "15000262"
        private const val password: String = "Initial@1234"
        fun newInstance(): LoginFragment {
            return LoginFragment().also {
                it.arguments = Bundle()
            }
        }
    }

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: LoginViewModel

    private lateinit var rootView: View


    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentInjector
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)

        if (context is LoginActivity) {
        } else {
            throw IllegalStateException("This fragment must be use in conjunction with " + LoginActivity::class.java.simpleName)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(LoginViewModel::class.java)

        if (viewModel.isLogin()) {
            startMenuActivity()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_login, container, false)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mockData()
        initButton()
        observeData()
    }

    private fun mockData() {
        etUser.setText(username)
        etUser.setSelection(etUser.text.length)
        etPassword.setText(password)
    }

    private fun initButton() {
        tvScanQrCode.setOnClickListener { }

        btnLogin.setOnClickListener {
            login(etUser.text.toString(), etPassword.text.toString())
        }

        tvScanQrCode.setOnClickListener {
            IntentIntegrator(activity).apply {
                setPrompt("Scan QR Code")
                setOrientationLocked(false)
            }.run {
                initiateScan()
            }
        }

        etUser.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                val code = etUser.text.toString()
                if (code.toLongOrNull() == null) {
                    etUser.setText(code.decodeBase64())
                    login(code)
                }
                return@OnKeyListener true
            }
            false
        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        IntentIntegrator.parseActivityResult(requestCode, resultCode, data)?.let { it ->
            it.contents?.let {
                etUser.setText(it.decodeBase64())
                login(it)
            }
        }
    }

    private fun observeData() {
        viewModel.isLogin.observe(this, Observer {
            if (it == null) return@Observer
            if (it) startMenuActivity()
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

    private fun login(username: String, password: String) {
        if (username.isNotEmpty() && password.isNotEmpty()) {
            viewModel.login(username, password)
        }
    }

    private fun login(code: String) {
        if (code.isNotEmpty()) {
            viewModel.login(code)
        }
    }

    private fun startMenuActivity() {
//        startActivity(Intent(activity, MenuActivity::class.java))
        startActivity(Intent(activity, MasterDataActivity::class.java))
        activity!!.finish()
    }
}
