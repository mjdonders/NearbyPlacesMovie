package aadcapstone.coursera.org.nearbyplacesmovie.webserviceclient;

import android.os.AsyncTask;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Our own Google Places webservice client
 */

public class GooglePlacesClient {

    /**
     * The base URL of the webservice.
     */
    private String mWebServiceBaseURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/";

    /**
     * The protocol to use for data encoding. I prefer JSON over XML.
     */
    private String mDataProtocol = "json";

    private String[] mNearbyPlaces;

    private NearbyPlacesResolvedListener mNotifier;

    public GooglePlacesClient() {
    }

    /**
     * Build an URL like:
     *  [base URL/]"json?location={current location here}&rankby=distance&types=food&key={Google API access key})";
     * @return URL
     */
    protected URL getWebserviceURL(String positionLatitude,
                                 String positionLongitude,
                                 String locationType) throws java.net.MalformedURLException {

        if ((positionLatitude == null) || (positionLatitude.length() < 1))
            throw new MalformedURLException("Latitude is mandatory");
        if ((positionLongitude == null) || (positionLongitude.length() < 1))
            throw new MalformedURLException("Longitude is mandatory");
        if ((locationType == null) || (locationType.length() < 1))
            throw new MalformedURLException("Location type is mandatory");

        StringBuilder completeURL = new StringBuilder();
        completeURL.append(mWebServiceBaseURL);
        completeURL.append(mDataProtocol);
        completeURL.append("?");
        completeURL.append("location=").append(positionLatitude).append(",").append(positionLongitude);
        completeURL.append("&rankby=distance");
        completeURL.append("&types=").append(locationType);
        //My personal Google Location API key, please don't use it yourself..
        completeURL.append("&key=").append(KeyProtector.getApiKey());

        return new URL(completeURL.toString());
    }

    /**
     * Contact the webservice
     * @param webserviceURL URL to contact
     * @return the result
     * @throws IOException
     */
    protected String readUrl(URL webserviceURL) throws IOException {

        BufferedReader in = new BufferedReader(
                new InputStreamReader(webserviceURL.openStream()));

        String inputLine;
        StringBuilder result = new StringBuilder();
        while ((inputLine = in.readLine()) != null)
            result.append(inputLine);
        in.close();

        return result.toString();
    }

    /**
     * Parse the (JSON) webservice result
     * @param result the result of the webservice to parse
     * @return an array with the nearby location names
     */
    protected String[] parseWebserviceResult(String result) {
        Gson gson = new Gson();

        PlaceSearchResponse response = gson.fromJson(result, PlaceSearchResponse.class);

        String nearbyPlaces[] = new String[response.results.length];
        for (int i=0; i<response.results.length; i++) {
            nearbyPlaces[i] = response.results[i].name;
        }

        return nearbyPlaces;
    }

    /**
     * Get/initiate nearby location name retrieval
     * @param longitude longitude of position to check
     * @param latitude latitude of position to check
     * @param locationType the location type to check at this position
     * @param notifier listener interface to be notified when done
     * @throws IOException in case of issues..
     */
    public void getNearbyPlaces(String longitude,
                                String latitude,
                                String locationType,
                                NearbyPlacesResolvedListener notifier) throws IOException{

        mNotifier = notifier;

        // invalidate the last result (if any)
        mNearbyPlaces = null;

        try {
            //create a proper request URL
            URL webserviceURL = getWebserviceURL(latitude, longitude, locationType);

            //use an inner class ASyncTask implementation to contact the webservice asynchronously
            new AsyncWebserviceContact().execute(webserviceURL);
        } catch (Exception e) {
            //this this just for debugging
            throw e;
        }
    }

    /**
     * The ASyncTask used to contact the webservice
     */
    private class AsyncWebserviceContact extends AsyncTask<URL, Void, Void> {

        /**
         * The do-in-background implementation to contact the webservice
         * @param webserviceURL
         * @return
         */
        @Override
        protected Void doInBackground(URL... webserviceURL){
            try {
                //contact the webservice
                String result = readUrl(webserviceURL[0]);

                //parse the result
                mNearbyPlaces = parseWebserviceResult(result);
            } catch (Exception e) {
                //throw e;
                String error = e.getMessage();
            }
            return null;
        }

        /**
         * ASyncTask progress update. Not used.
         * @param v not used.
         */
        @Override
        protected void onProgressUpdate(Void... v) {
            //no progress update required..
        }

        /**
         * Executed when we're done.
         * @param v not used
         */
        @Override
        protected void onPostExecute(Void v) {
            //result is available from the webservice
            //notify the listener
            mNotifier.nearbyPlacesResolved();
        }
    }

    /**
     * Getter for the retrieved nearby locations
     * Will most likely be called in the nearbyPlacesResolved call we've
     * done earlier in the onPostExecute method
     * @return
     */
    public String[] getNearbyPlaces () {
        return mNearbyPlaces;
    }

}
