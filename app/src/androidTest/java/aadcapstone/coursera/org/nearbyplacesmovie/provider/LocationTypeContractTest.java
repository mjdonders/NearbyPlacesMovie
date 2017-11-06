package aadcapstone.coursera.org.nearbyplacesmovie.provider;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;

/**
 * Location Type Contract test.
 * Since this class uses Android functionality, normal JUnit tests will not work,
 * so using the androidTest.
 *
 * The target class is quite basic, so not much to test..
 */
@RunWith(AndroidJUnit4.class)
public class LocationTypeContractTest {

    /**
     * Some static string tests
     */
    @Test
    public void testStaticStrings() {

        LocationTypeContract testSubject = new LocationTypeContract();
        assertTrue(testSubject.sColumnsToDisplay.length == 2);
        assertTrue(testSubject.sColumnsToDisplay[LocationTypeContract.LocationTypeEntry.COLUMN_LOCATION_TYPE_INDEX].
                        compareTo(LocationTypeContract.LocationTypeEntry.COLUMN_LOCATION_TYPE) == 0);
        assertTrue(testSubject.CONTENT_AUTHORITY_NAME.contains("provider"));

        //the content should contain 'content'  ;-)
        assertTrue(testSubject.BASE_CONTENT_URI.toString().contains("content://"));

        assertTrue(testSubject.CONTENT_ITEMS_TYPE.contains("location_types"));

        assertTrue(testSubject.getContentUri().isAbsolute());
    }

    /**
     * test the URI builder
     */
    @Test
    public void testBuildUri() {
        String uri = LocationTypeContract.LocationTypeEntry.buildUri((long)0).toString();
        assertTrue(uri.contains("content://"));

        String uri2 = LocationTypeContract.LocationTypeEntry.buildUri((long)6).toString();
        assertTrue(uri2.contains("6"));
    }
}
