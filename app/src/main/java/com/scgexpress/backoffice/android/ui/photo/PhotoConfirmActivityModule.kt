package com.scgexpress.backoffice.android.ui.photo

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class PhotoConfirmActivityModule {
    @ContributesAndroidInjector(modules = [PhotoConfirmFragmentModule::class])
    internal abstract fun bindTakePhotoFragment(): PhotoConfirmFragment
}