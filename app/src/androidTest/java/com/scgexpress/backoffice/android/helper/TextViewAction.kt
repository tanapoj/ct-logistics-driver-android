package com.scgexpress.backoffice.android.helper

import android.view.View
import android.widget.TextView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf

class TextViewAction {

    fun setTextInTextView(value: String): ViewAction {
        return onView(value)
    }

    private fun onView(value: String): ViewAction {

        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return allOf(ViewMatchers.isDisplayed(), ViewMatchers.isAssignableFrom(TextView::class.java))
                //
                // To check that the found view is TextView or it's subclass like EditText
                // so it will work for TextView and it's descendants
            }

            override fun perform(uiController: UiController, view: View) {
                (view as TextView).text = value
            }

            override fun getDescription(): String {
                return "replace text"
            }
        }
    }
}