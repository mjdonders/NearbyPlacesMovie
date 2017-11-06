package aadcapstone.coursera.org.nearbyplacesmovie.webserviceclient;

import android.util.Base64;

import java.nio.charset.Charset;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Protects my personal Google Places API key
 * Please don't use/log it. Thanks.
 */

public class KeyProtector {

    private final static String confData = "sFSo3IHJYUFm4JB1fpCgmqXuqRdXA2SpTsYdw+/oEf0HLnlXSvYlTNrMVAWEUpsN";
    private final static String key = "bH7hqoHqnuTrnz96";
    private final static String initVector =  "0000000000000000";

    /**
     * General AES based encryption using a fixed key
     */
    protected static String encrypt(String data) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(data.getBytes());
            return new String(Base64.encode(encrypted, Base64.DEFAULT), "UTF-8");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * General AES based decryption using a fixed key
     */
    protected static String decrypt(String data) {
        try {
            if ((data == null) || data.length() == 0)
                return null;

            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] decodedData = Base64.decode(data, Base64.DEFAULT);
            if (decodedData == null)
                return null;

            byte[] original = cipher.doFinal(decodedData);

            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;

    }

    /**
     * Get's my personal API key
     * @return
     */
    public static String getApiKey() {
        return decrypt(confData);
    }

    /**
     * Unused in the actual program
     * @return
     */
    public static String getProtectedKey() {
        String data = "bla";    //originally filled with my Google Places API key
        return encrypt(data);

    }
}
