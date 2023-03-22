package com.scgexpress.backoffice.android.ui.masterdata.forceupdate

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.preference.LoginPreference
import com.scgexpress.backoffice.android.preferrence.MasterDataPreference
import com.scgexpress.backoffice.android.repository.LoginRepository
import com.scgexpress.backoffice.android.ui.main.MainActivity
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.fragment_masterdata_forceupdate.*
import javax.inject.Inject

class ForceUpdateFragment : Fragment(), HasSupportFragmentInjector {

    companion object {
        fun newInstance(): ForceUpdateFragment = ForceUpdateFragment()
    }

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>
    private lateinit var rootView: View

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentInjector
    }

    @Inject
    lateinit var loginRepository: LoginRepository

    @Inject
    lateinit var loginPreference: LoginPreference

    @Inject
    lateinit var masterDataPreference: MasterDataPreference

    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)

        if (context is ForceUpdateActivity) {
            mContext = context
        } else {
            throw IllegalStateException("This fragment must be use in conjunction with " + ForceUpdateActivity::class.java.simpleName)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_masterdata_forceupdate, container, false)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        btnUpdate.setOnClickListener {
            masterDataPreference.setNotice(MasterDataPreference.POSTPONE_NOW)
            startMainActivity()
        }

        btnPostponeShort.setOnClickListener {
            masterDataPreference.setNotice(MasterDataPreference.POSTPONE_SHORT)
            activity!!.finish()
        }

        btnPostponeLong.setOnClickListener {
            masterDataPreference.setNotice(MasterDataPreference.POSTPONE_LONG)
            activity!!.finish()
        }
    }

    private fun startMainActivity() {
        activity?.apply {
            startActivity(Intent(activity, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            finish()
        }
    }

}