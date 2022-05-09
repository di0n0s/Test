package com.example.myapplication

import android.view.View
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matcher
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Test
    fun test() {
        launchActivity<MainActivity>()

        Espresso.onView(ViewMatchers.isRoot()).perform(waitFor(1000))

        Espresso.onView(ViewMatchers.withId(R.id.full_name_text_view))
            .check(ViewAssertions.matches(withText("Janet Weaver")))

        Espresso.onView(ViewMatchers.withId(R.id.email_text_view))
            .check(ViewAssertions.matches(withText("janet.weaver@reqres.in")))


    }

    fun waitFor(delay: Long): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> = ViewMatchers.isRoot()
            override fun getDescription(): String = "wait for $delay milliseconds"
            override fun perform(uiController: UiController, v: View?) {
                uiController.loopMainThreadForAtLeast(delay)
            }
        }
    }
}