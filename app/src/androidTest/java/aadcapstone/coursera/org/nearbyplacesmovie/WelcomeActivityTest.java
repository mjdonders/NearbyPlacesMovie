package aadcapstone.coursera.org.nearbyplacesmovie;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.espresso.Espresso.onView;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import static org.hamcrest.Matchers.containsString;

/**
 * The test class for our Welcome Activity.
 * These tests are based on Espresso.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class WelcomeActivityTest {

    @Rule
    public ActivityTestRule<WelcomeActivity> mActivityRule =
            new ActivityTestRule(WelcomeActivity.class);

    /**
     * Test if the Spinner is filled with the Content Provider
     */
    @Test
    public void spinnerIsFilledWithContentProviderTest() {

        try {
            //the Spinner should have some text in it.
            //if it contains 'restaurant' the ContentProvider has done it's work
            onView(withId(R.id.location_types))
                    .perform(click());
            onData(allOf(is(instanceOf(String.class)), is("restaurant")))
                    .perform(click());
            onView(withId(R.id.location_types))
                    .check(matches(withSpinnerText(containsString("restaurant"))));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Test if the button if present and enabled
     */
    @Test
    public void buttonPresentAndEnabledTest () {

        onView(withId(R.id.get_nearby_locations)).check(matches(isEnabled()));
    }

    /**
     * Check if we get the next activity when the button is clicked
     */
    @Test
    public void startNextActivityTest () {
        try {
            onView(withId(R.id.get_nearby_locations)).perform(click());
            //onView(withId(R.id.status_text)).check(matches(containsString("")));

            onView(withText("LocationSelectionActivity")).check(matches(isDisplayed()));

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
