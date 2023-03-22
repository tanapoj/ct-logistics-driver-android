package com.scgexpress.backoffice.android.ui

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.scgexpress.backoffice.android.R
import com.scgexpress.backoffice.android.helper.RecyclerViewItemCountAssertion
import com.scgexpress.backoffice.android.helper.RecyclerViewMatcher
import com.scgexpress.backoffice.android.helper.TextViewAction
import com.scgexpress.backoffice.android.ui.topic.TopicActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.hamcrest.Matchers.`is` as _is


@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Rule
    @JvmField
    val activityTestRule = ActivityTestRule(TopicActivity::class.java)

    //For Activity that have intent data
    /*val activityTestRule: ActivityTestRule<TopicActivity> =
        object : ActivityTestRule<TopicActivity>(TopicActivity::class.java) {
            override fun getActivityIntent(): Intent {
                val targetContext = InstrumentationRegistry.getInstrumentation().targetContext
                return Intent(targetContext, TopicActivity::class.java).apply {
                    putExtra(Const.PARAMS_TOPIC_TITLE, "mock")
                }
            }
        }*/

    @Before
    fun setUp() {
    }

    @Test
    fun testTypeTextEditText() {
        //onView(withId(R.id.editText)).perform(typeText("Hello World"))
    }

    @Test
    fun testCheckTextDisplay() {
        onView(withText("All Topics")).check(matches(isDisplayed()))
    }

    @Test
    fun testChangeButtonWord() {
        onView(withId(R.id.btnOk)).perform(TextViewAction().setTextInTextView("Cancel"))
        onView(withId(R.id.btnOk)).perform(click())
    }


    @Test
    fun testClickRecyclerViewItem() {
        onView(withRecyclerView(R.id.recyclerView).atPosition(1)).perform(click())
    }

    @Test
    fun testCheckRecyclerViewItem() {
        onView(withRecyclerView(R.id.recyclerView).atPosition(0))
            .check(matches(hasDescendant(withText("All Topics"))))
    }

    @Test
    @Throws(Exception::class)
    fun ensureRecyclerViewIsPresent() {
        onView(withId(R.id.recyclerView)).check(RecyclerViewItemCountAssertion(5))
    }

    private fun withRecyclerView(recyclerViewId: Int): RecyclerViewMatcher {
        return RecyclerViewMatcher(recyclerViewId)
    }
}