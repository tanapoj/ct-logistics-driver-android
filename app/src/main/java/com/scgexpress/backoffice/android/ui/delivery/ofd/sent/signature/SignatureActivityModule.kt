package com.scgexpress.backoffice.android.ui.delivery.ofd.sent.signature

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class SignatureActivityModule {
    @ContributesAndroidInjector(modules = [(SignatureFragmentModule::class)])
    internal abstract fun bindSignatureFragment(): SignatureFragment
}