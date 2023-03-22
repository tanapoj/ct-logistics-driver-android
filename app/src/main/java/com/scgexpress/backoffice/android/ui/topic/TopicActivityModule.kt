package com.scgexpress.backoffice.android.ui.topic

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class TopicActivityModule {
    @ContributesAndroidInjector(modules = [(TopicFragmentModule::class)])
    internal abstract fun bindMainFragment(): MainFragment
}