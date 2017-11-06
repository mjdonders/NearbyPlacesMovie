package aadcapstone.coursera.org.nearbyplacesmovie.webserviceclient;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOError;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Our Google Places Client test cases.
 */
@RunWith(MockitoJUnitRunner.class)
public class GooglePlacesClientTest{

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    GooglePlacesClient testSubject;

    @Before
    public void setup() {
        testSubject = new GooglePlacesClient();
    }

    /**
     * The default (just constructed, no actions done) nearbyPlaces String[] should be null
     */
    @Test
    public void getNearbyPlacesdefaultTest () {
        assertNull(testSubject.getNearbyPlaces());
    }

    private boolean webserviceResultReceived = false;

    /**
     * Test
     */
    @Test
    public void getWebserviceUrltest() throws MalformedURLException {
        //getWebserviceURL

        try {
            String url = testSubject.getWebserviceURL("Lat", "Long", "ZEZ").toString();
            assertTrue(url.contains("Lat,Long"));

            assertTrue(url.contains("types=ZEZ"));
        } catch (Exception e) {
            //just to make sure we get a proper report of what went wrong
            assertEquals("", e.getMessage());
        }

        //the next statement will throw an exception, which is good
        //make sure we expect the proper exception type!
        thrown.expect(MalformedURLException.class);
        testSubject.getWebserviceURL(null, "Long", "ZEZ").toString();
    }

    /**
     * Test the webservice interface function
     * @throws IOException
     */
    @Test
    public void readWebserviceResultTest () throws IOException {
        //contact Google, this should (typically ;-) ) work
        String result = testSubject.readUrl(new URL("https://www.google.com"));

        assertTrue(result.length() > 1);
        assertTrue(result.contains("Google"));
    }

    @Test
    public void parseWebserviceResultTest () {
        //parseWebserviceResult
        //from: https://developers.google.com/places/web-service/search
        String testDataFromGoogle = "{\n" +
                "   \"html_attributions\" : [],\n" +
                "   \"results\" : [\n" +
                "      {\n" +
                "         \"geometry\" : {\n" +
                "            \"location\" : {\n" +
                "               \"lat\" : -33.870775,\n" +
                "               \"lng\" : 151.199025\n" +
                "            }\n" +
                "         },\n" +
                "         \"icon\" : \"http://maps.gstatic.com/mapfiles/place_api/icons/travel_agent-71.png\",\n" +
                "         \"id\" : \"21a0b251c9b8392186142c798263e289fe45b4aa\",\n" +
                "         \"name\" : \"Rhythmboat Cruises\",\n" +
                "         \"opening_hours\" : {\n" +
                "            \"open_now\" : true\n" +
                "         },\n" +
                "         \"photos\" : [\n" +
                "            {\n" +
                "               \"height\" : 270,\n" +
                "               \"html_attributions\" : [],\n" +
                "               \"photo_reference\" : \"CnRnAAAAF-LjFR1ZV93eawe1cU_3QNMCNmaGkowY7CnOf-kcNmPhNnPEG9W979jOuJJ1sGr75rhD5hqKzjD8vbMbSsRnq_Ni3ZIGfY6hKWmsOf3qHKJInkm4h55lzvLAXJVc-Rr4kI9O1tmIblblUpg2oqoq8RIQRMQJhFsTr5s9haxQ07EQHxoUO0ICubVFGYfJiMUPor1GnIWb5i8\",\n" +
                "               \"width\" : 519\n" +
                "            }\n" +
                "         ],\n" +
                "         \"place_id\" : \"ChIJyWEHuEmuEmsRm9hTkapTCrk\",\n" +
                "         \"scope\" : \"GOOGLE\",\n" +
                "         \"alt_ids\" : [\n" +
                "            {\n" +
                "               \"place_id\" : \"D9iJyWEHuEmuEmsRm9hTkapTCrk\",\n" +
                "               \"scope\" : \"APP\"\n" +
                "            }\n" +
                "         ],\n" +
                "         \"reference\" : \"CoQBdQAAAFSiijw5-cAV68xdf2O18pKIZ0seJh03u9h9wk_lEdG-cP1dWvp_QGS4SNCBMk_fB06YRsfMrNkINtPez22p5lRIlj5ty_HmcNwcl6GZXbD2RdXsVfLYlQwnZQcnu7ihkjZp_2gk1-fWXql3GQ8-1BEGwgCxG-eaSnIJIBPuIpihEhAY1WYdxPvOWsPnb2-nGb6QGhTipN0lgaLpQTnkcMeAIEvCsSa0Ww\",\n" +
                "         \"types\" : [ \"travel_agency\", \"restaurant\", \"food\", \"establishment\" ],\n" +
                "         \"vicinity\" : \"Pyrmont Bay Wharf Darling Dr, Sydney\"\n" +
                "      }\n" +
                "       ],\n" +
                "   \"status\" : \"OK\"\n" +
                "}";
        try {
            String[] parcedResult = testSubject.parseWebserviceResult(testDataFromGoogle);
            assertEquals(1, parcedResult.length);
            assertEquals("Rhythmboat Cruises", parcedResult[0]);
        } catch (Exception e) {
            assertEquals("", e.getMessage());
        }
    }
}
