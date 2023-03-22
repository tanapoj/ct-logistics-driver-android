package com.scgexpress.backoffice.android.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.base.BaseActivity
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject


class LoginActivity : BaseActivity(), HasSupportFragmentInjector {

    override var allowEmergencyMasterDataUpdateDialog = false

    @Inject
    lateinit var mFragmentInjector: DispatchingAndroidInjector<Fragment>

    private val mFragment by lazy {
        LoginFragment.newInstance()
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return mFragmentInjector
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.clContainer, mFragment)
                    .commit()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mFragment.onActivityResult(requestCode, resultCode, data)
    }

}
