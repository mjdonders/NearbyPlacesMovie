package aadcapstone.coursera.org.nearbyplacesmovie.webserviceclient;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Key Protector Test. Since this uses the android.util.Base64 it cannot be done in a normal JUnit..
 * See http://tools.android.com/tech-docs/unit-testing-support
 * Search for: 'We are aware that the default behavior is problematic when using classes like'
 *
 */
@RunWith(AndroidJUnit4.class)
public class KeyProtectorTest {

    /**
     * Intake test, just to see if it works
     * @throws Exception .. not really
     */
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("aadcapstone.coursera.org.nearbyplacesmovie", appContext.getPackageName());
    }

    /**
     * Ensure the protected key gets decrypted on demand
     * @throws Exception it might, in which case the test will fail
     */
    @Test
    public void encryptDefaultData() throws Exception {
        String output = KeyProtector.getProtectedKey();
        assertTrue(output != null);
        assertTrue(output.length() > 0 );
    }

    /**
     * Test that if we encrypt something and decrypt it again, we get the original data back
     * @throws Exception it might, in which case the test will fail
     */
    @Test
    public void decryptEncrypted() throws Exception {

        String data = "dfuhiudfh";
        String encrypted = KeyProtector.encrypt(data);
        assertTrue(encrypted != null);

        String decrypted = KeyProtector.decrypt(encrypted);
        assertTrue(decrypted != null);
        assertTrue(data.compareTo(decrypted) == 0);
    }

    /**
     * The default 'getProtectedKey' just encrypts 'bla', checks if this is true
     * @throws Exception it might, but in this case the test will fail
     */
    @Test
    public void encryptDefaultDataCheckWithEncrypt() throws Exception {

        String encBla = KeyProtector.encrypt("bla");
        String protectedKey = KeyProtector.getProtectedKey();
        assertTrue(encBla.compareTo(protectedKey) == 0);
    }
}
