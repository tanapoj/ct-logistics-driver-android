package com.scgexpress.backoffice.android.ui.masterdata

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.scgexpress.backoffice.android.R
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import timber.log.Timber
import javax.inject.Inject


class MasterDataActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var mFragmentInjector: DispatchingAndroidInjector<Fragment>
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return mFragmentInjector
    }

    private val viewModel by lazy {
        val viewModel = ViewModelProviders.of(this, viewModelFactory).get(MasterDataViewModel::class.java)
        viewModel.canonicalPath = filesDir.canonicalPath
        viewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_masterdata)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.clContainer, MasterDataFragment.newInstance(viewModel))
                    .commit()
        }

        Timber.i("-master ${filesDir.canonicalPath}")

        //viewModel.updateMasterData()

    }

}


