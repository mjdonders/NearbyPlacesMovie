package aadcapstone.coursera.org.nearbyplacesmovie;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.URI;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

/**
 * The Location Type Provider Helper test class.
 * These tests are based on Mockito.
 */
@RunWith(MockitoJUnitRunner.class)
public class LocationTypeProviderHelperTest {

    @Mock
    private ContentResolver contentResolver;

    private LocationTypeProviderHelper testSubject;

    @Before
    public void setup(){
        testSubject = new LocationTypeProviderHelper(contentResolver);
    }

    /**
     * Ensure our Mock is properly stored & returned
     */
    @Test
    public void fieldTest(){
        assertTrue(testSubject.getLocationTypeProvider() == contentResolver);
    }

    /**
     * Perform an initial fill of the database: happy flow
     */
    @Test
    public void fillDatabaseTest() {
        try {
            when(contentResolver.delete((Uri)notNull(), (String)isNull(), (String[])isNull())).thenReturn(6);
            when(contentResolver.bulkInsert((Uri)notNull(), (ContentValues[])notNull())).thenReturn(6);

            testSubject.initialFillDatabase();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Perform an initial fill of the database: exception in delete
     */
    @Test
    public void fillDatabase_DeleteFails_Test() {
        try {
            when(contentResolver.delete((Uri)notNull(), (String)isNull(), (String[])isNull())).thenThrow(new android.database.SQLException());
            when(contentResolver.bulkInsert((Uri)notNull(), (ContentValues[])notNull())).thenReturn(6);

            //nothing should happen, if the delete fails, this just means: first run
            testSubject.initialFillDatabase();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
