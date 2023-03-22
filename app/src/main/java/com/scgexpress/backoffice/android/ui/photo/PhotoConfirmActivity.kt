package com.scgexpress.backoffice.android.ui.photo

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.base.BaseActivity
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_take_photo.*

class PhotoConfirmActivity: BaseActivity(), HasSupportFragmentInjector {

    private lateinit var viewModel: PhotoConfirmViewModel

    private val fragment: PhotoConfirmFragment by lazy {
        PhotoConfirmFragment.newInstance(viewModel)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_take_photo)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(PhotoConfirmViewModel::class.java)
        initActionBar()
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        fragment.onActivityResult(requestCode, resultCode, data)
    }

    private fun initActionBar() {
        setSupportActionBar(toolbar)
    }
}