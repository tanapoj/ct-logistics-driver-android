package com.scgexpress.backoffice.android

import androidx.arch.core.executor.ArchTaskExecutor
import androidx.arch.core.executor.TaskExecutor
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class AndroidInstantTaskExecutorRule : TestWatcher() {
    override fun starting(description: Description?) {
        super.starting(description)
        ArchTaskExecutor.getInstance().setDelegate(object : TaskExecutor() {
            override fun executeOnDiskIO(runnable: Runnable) {
                runnable.run()
            }

            override fun postToMainThread(runnable: Runnable) {
                runnable.run()
            }

            override fun isMainThread(): Boolean {
                return true
            }
        })
    }

    override fun finished(description: Description?) {
        super.finished(description)
        ArchTaskExecutor.getInstance().setDelegate(null)
    }
}