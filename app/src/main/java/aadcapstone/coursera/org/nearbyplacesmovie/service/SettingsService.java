package aadcapstone.coursera.org.nearbyplacesmovie.service;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;

/**
 * A service implementation able to store and retrieve settings.
 * Specifically: the last stored LocationType.
 *
 * Communication with this service is arranged via Intents, and related factory methods are available
 */

public class SettingsService extends IntentService {

    /**
     * Name of the shared preferences we use internally
     */
    private static final String PREFERENCES_NAME = "SettingsServicePreferences";

    /**
     * Setting name for the default location type
     */
    private static final String DEFAULT_LOCATION_TYPE = "DefaultLocationType";

    /**
     * The Intents extra command type
     */
    public static final String COMMAND = "CMD";

    /**
     * Commands we support: get location type & set locatinon type
     */
    public static final String COMMAND_GET_DEFAULT_LOCATION_TYPE = "CMD_GetLocationType";
    public static final String COMMAND_SET_DEFAULT_LOCATION_TYPE = "CMD_SetLocationType";

    /**
     * The result of the location retrieval, used for Intent filtering by callers and
     * in this class to create the proper Intent
     */
    public static final String RESULT_GET_DEFAULT_LOCATION_TYPE = "RES_GetLocationTypeResult";

    /**
     * Intents extra data: location type
     */
    public static final String DATA_LOCATION_TYPE = "DATA_LocationType";

    /**
     * Default constructor. Just sets a name
     */
    public SettingsService() {
        super("SettingsService");
    }

    /**
     * Factory method in order to create an Intent to initiate the retrieval of the stored Location Type
     * @return
     */
    public static Intent createGetDefaultLocationTypeIntent(Context source) {
        Intent intent = new Intent(source, SettingsService.class);
        intent.putExtra(COMMAND, COMMAND_GET_DEFAULT_LOCATION_TYPE);
        return intent;
    }

    /**
     * Factory method in order to create an IntentFilter in order to filter
     * Intents send for distributing the result of this service
     */
    public static IntentFilter createIntentFilterToGetResult() {
        IntentFilter intentFilter = new IntentFilter(RESULT_GET_DEFAULT_LOCATION_TYPE);
        return intentFilter;
    }

    /**
     * Internally used factory method in order to create an Intent to
     * initiate the retrieval of the stored Location Type
     * @return
     */
    protected Intent createResultDefaultLocationTypeIntent(String locationTypeToReport) {
        Intent intent = new Intent(RESULT_GET_DEFAULT_LOCATION_TYPE);
        intent.putExtra(COMMAND, RESULT_GET_DEFAULT_LOCATION_TYPE);
        intent.putExtra(DATA_LOCATION_TYPE, locationTypeToReport);
        return intent;
    }

    /**
     * Factory method in order to store the Location Type.
     * @param source Activity source
     * @param locationType Location Type to store
     * @return The created Intent, ready for a startService call.
     */
    public static Intent createSetDefaultLocationTypeIntent(Context source, String locationType) {
        Intent intent = new Intent(source, SettingsService.class);
        intent.putExtra(COMMAND, COMMAND_SET_DEFAULT_LOCATION_TYPE);
        intent.putExtra(DATA_LOCATION_TYPE, locationType);
        return intent;
    }

    /**
     * Our 'command interface'
     * @param workIntent
     */
    @Override
    protected void onHandleIntent(Intent workIntent) {
        // Get the command type from the incoming Intent
        String command = workIntent.getStringExtra(COMMAND);

        //and check the command requested
        if (command == null) {
            //ok, not for us I guess.. No command received
        }
        else if (command.compareTo(COMMAND_SET_DEFAULT_LOCATION_TYPE) == 0) {
            //store settings
            String defaultLocationType = workIntent.getStringExtra(DATA_LOCATION_TYPE);
            storeDefaultLocationType(defaultLocationType);

        } else if (command.compareTo(COMMAND_GET_DEFAULT_LOCATION_TYPE) == 0) {
            //retrieve setting
            String defaultLocationType = retrieveDefaultLocationType();
            // and send the result back via a broadcast
            LocalBroadcastManager.getInstance(this)
                    .sendBroadcast(
                            createResultDefaultLocationTypeIntent(defaultLocationType));

        } else {
            //hmm.. shouldn't be reached..
            //an unsupported command is received
        }
    }

    /**
     * Store the setting, using SharedPreferences
     * @param defaultLocationType
     */
    private void storeDefaultLocationType(String defaultLocationType){
        SharedPreferences settings = getSharedPreferences(PREFERENCES_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(DEFAULT_LOCATION_TYPE, defaultLocationType);
        //writes the setting asynchronously to persistent storage
        editor.apply();
    }

    /**
     * Retrieve the setting using SharedPreferences
     */
    private String retrieveDefaultLocationType(){
        SharedPreferences settings = getSharedPreferences(PREFERENCES_NAME, 0);
        return settings.getString(DEFAULT_LOCATION_TYPE, "");
    }

}

