package com.scgexpress.backoffice.android.di

import com.scgexpress.backoffice.android.ui.dialog.*
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentsModule{
    @ContributesAndroidInjector(modules = [(PhotoExpandDialogFragmentModule::class)])
    internal abstract fun bindPhotoExpandDialogFragment(): PhotoExpandDialogFragment

    @ContributesAndroidInjector(modules = [(PhotoSelectDialogFragmentModule::class)])
    internal abstract fun bindPhotoSelectDialogFragment(): PhotoSelectDialogFragment

    @ContributesAndroidInjector(modules = [(RetentionSelectSubReasonDialogFragmentModule::class)])
    internal abstract fun bindRetentionSelectSubResaonDialogFragment(): RetentionSelectSubReasonDialogFragment
}
