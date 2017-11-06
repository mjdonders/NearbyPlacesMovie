package aadcapstone.coursera.org.nearbyplacesmovie.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ServiceTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.content.LocalBroadcastManager;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * SettingsService test class
 */
@RunWith(AndroidJUnit4.class)   //@RunWith(MockitoJUnitRunner.class)
public class SettingsServiceTest {

    /**
     * From: https://developer.android.com/training/testing/integration-testing/service-testing.html
     *
     * Note: The ServiceTestRule class does not support testing of IntentService objects.
     * If you need to test a IntentService object, you should encapsulate the logic in a
     * separate class and create a corresponding unit test instead.
    //@Rule
    //public final ServiceTestRule mServiceRule = new ServiceTestRule();
     *
     * So, the above statement does not work..
     *
     */

    private Context mContext;

    @Before
    public void setup () {
        mContext = InstrumentationRegistry.getTargetContext();
    }

    /**
     * Intent factory tests - Getter
     */
    @Test
    public void createGetDefaultLocationTypeIntentTest () {

        try {
            Intent result = SettingsService.createGetDefaultLocationTypeIntent(mContext);
            assertTrue(result != null);
            assertEquals(SettingsService.COMMAND_GET_DEFAULT_LOCATION_TYPE, result.getStringExtra(SettingsService.COMMAND));
        } catch (Exception e) {
            e.printStackTrace();
            assertEquals("", e.getMessage());
        }
    }

    /**
     * IntentFilter factory tests
     */
    @Test
    public void createIntentFilterToGetResultTest () {
        IntentFilter intentFilter = SettingsService.createIntentFilterToGetResult();
        assertTrue(intentFilter != null);
    }

    /**
     * Other (internal) Intent factory test
     */
    @Test
    public void createResultDefaultLocationTypeIntentTest () {
        try {
            SettingsService service = new SettingsService();
            Intent result = service.createResultDefaultLocationTypeIntent("6");
            assertTrue(result != null);
            assertEquals(SettingsService.RESULT_GET_DEFAULT_LOCATION_TYPE, result.getStringExtra(SettingsService.COMMAND));
            assertEquals("6", result.getStringExtra(SettingsService.DATA_LOCATION_TYPE));
        } catch (Exception e) {
            e.printStackTrace();
            assertEquals("", e.getMessage());
        }
    }

    /**
     * Intent Factory tests - Setter
     */
    @Test
    public void createSetDefaultLocationTypeIntentTest () {
        try {
            Intent result = SettingsService.createSetDefaultLocationTypeIntent(mContext, "7");
            assertTrue(result != null);
            assertEquals(SettingsService.COMMAND_SET_DEFAULT_LOCATION_TYPE, result.getStringExtra(SettingsService.COMMAND));
            assertEquals("7", result.getStringExtra(SettingsService.DATA_LOCATION_TYPE));
        } catch (Exception e) {
            e.printStackTrace();
            assertEquals("", e.getMessage());
        }

    }

    /**
     * Intent test (non-happy-flow), using an empty Intent
     */
    @Test
    public void onHandleIntent_empty_Test () {
        try {

            SettingsService service = new SettingsService();
            // not a normal scenario, but should be handled just fine
            service.onHandleIntent(new Intent());
        } catch (Exception e) {
            e.printStackTrace();
            assertEquals("", e.getMessage());
        }
    }

    /**
     * Intent test - Setter
     * @throws TimeoutException
     */
    @Test
    public void onHandleIntent_Set_Test () throws TimeoutException {
        try {
            // Try to set the default value, should just work: no exception
            new ContextWrapper(mContext).startService(SettingsService.createSetDefaultLocationTypeIntent(mContext, "restaurant"));
        } catch (Exception e) {
            e.printStackTrace();
            assertEquals("", e.getMessage());
        }
    }

    /**
     * Start the service and request the stored location
     * @throws InterruptedException
     * @throws TimeoutException
     */
    @Test
    public void onHandleIntent_Get_Test () throws InterruptedException, TimeoutException {

        MyReceiver receiver = new MyReceiver();
        try {
            //ask the SettingsService to contact this unitTest
            LocalBroadcastManager.getInstance(mContext).registerReceiver(receiver, SettingsService.createIntentFilterToGetResult());

            new ContextWrapper(mContext).startService(SettingsService.createGetDefaultLocationTypeIntent(mContext));

            //give it some time to handle
            Thread.sleep(5000);

            //if this fails, the above sleep time might not be sufficient..
            //on my system 2000 has never failed, so using 5000 for now
            assertTrue(receiver.storedLocationType.length() > 0);
        } catch (Exception e) {
            e.printStackTrace();
            assertEquals("", e.getMessage());
        } finally {
            LocalBroadcastManager.getInstance(mContext).unregisterReceiver(receiver);
        }
    }

    /**
     * Inner class to support the onHandleIntent_Get_Test testcase.
     * This is where the service will send it's result to
     */
    public class MyReceiver extends BroadcastReceiver {
        protected  String storedLocationType;

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getStringExtra(SettingsService.COMMAND).compareTo(SettingsService.RESULT_GET_DEFAULT_LOCATION_TYPE) == 0) {
                //yes, it's the 'default location type has been retrieved from setting'-Intent
                storedLocationType = intent.getStringExtra(SettingsService.DATA_LOCATION_TYPE);
            }
        }
    }
}
