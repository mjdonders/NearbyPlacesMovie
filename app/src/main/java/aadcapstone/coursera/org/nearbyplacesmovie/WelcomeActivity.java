package aadcapstone.coursera.org.nearbyplacesmovie;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import android.view.View;
import android.view.View.OnClickListener;

import java.util.ArrayList;
import java.util.List;

import aadcapstone.coursera.org.nearbyplacesmovie.provider.LocationTypeContract;
import aadcapstone.coursera.org.nearbyplacesmovie.service.SettingsService;

import static android.widget.Toast.LENGTH_LONG;

/**
 * The welcome activity (screen 1)
 * This just asks the user to select a location type to search.
 *
 * The user interface is filled using a ContentProvider.
 * The last selected location type is stored/retrieved using a service, which we contact
 * via BroadcastReceiver Intents.
 */
public class WelcomeActivity    extends AppCompatActivity
                                implements LoaderCallbacks<Cursor> {

    /**
     * The one and only button in this activity.
     * It starts the next Activity.
     */
    private Button mGetNearbyLocationsButton;

    /**
     * The other user interface item: a Spinner (drop down selection).
     * It's used to select a location type to search
     */
    private Spinner mLocationTypeSpinner;

    /**
     * Instance of our inner class in order to receive broadcast intents.
     */
    private MySettingsReceiver mMySettingsReceiver;


    /**
     * Activity creation
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        try {
            //init the button
            mGetNearbyLocationsButton = (Button) findViewById(R.id.get_nearby_locations);
            mGetNearbyLocationsButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    getNearbyLoctions();
                }
            });

            //and the other GUI item
            mLocationTypeSpinner = (Spinner) findViewById(R.id.location_types);

            //initialize the database, so we're able to fill the GUI based on this info later on
            LocationTypeProviderHelper locationTypeProviderHelper = new LocationTypeProviderHelper(getContentResolver());
            locationTypeProviderHelper.initialFillDatabase();

            //initiate the population of the Spinnen user interface item via a LoadManager
            getLoaderManager().initLoader(0, null, this);

            //Register this a BroadCastReceiver in order to obtain the
            // stored default Location Type from the SettingsService
            mMySettingsReceiver = new MySettingsReceiver();
            LocalBroadcastManager.getInstance(this).registerReceiver(mMySettingsReceiver, SettingsService.createIntentFilterToGetResult());

            //don't initiate the loading of the default LocationType just yet..
            //this results in a race condition

        } catch (Exception e) {
            temporaryShowText(e.getMessage());
        }
    }

    /**
     * Sets the LocationType Spinner to the proper default selected line
     * @param defaultLocationType default location type to set
     */
    private void setDefaultSelectedLocationType(String defaultLocationType){

        try {
            //only perform this action when there is something to select
            if (!defaultLocationType.isEmpty()) {
                ArrayAdapter myAdap = (ArrayAdapter) mLocationTypeSpinner.getAdapter(); //cast to an ArrayAdapter

                if (myAdap != null) {
                    //get the position of the item to select
                    int spinnerPosition = myAdap.getPosition(defaultLocationType);

                    //set the default according to the determined position
                    mLocationTypeSpinner.setSelection(spinnerPosition);
                }
            }
        } catch (Exception e){
            temporaryShowText(e.getMessage());
        }
    }

    /**
     * Starts the get nearby locations activity (screen 2)
     */
    private void getNearbyLoctions(){

        try {
            //first get the current selected location type
            String selectedLocationType = mLocationTypeSpinner.getSelectedItem().toString();
            //and store it, for easy GUI interaction later on
            storeLastSelected(selectedLocationType);

            //start the second screen: the LocationSelectionActivity
            Intent newActivityStarter = new Intent(this, LocationSelectionActivity.class);
            newActivityStarter.putExtra(LocationSelectionActivity.SELECTED_LOCATION_TYPE, selectedLocationType);
            startActivity(newActivityStarter);
        } catch (Exception e) {
            temporaryShowText(e.getMessage());
        }
    }

    /**
     * Store the last selected location type via an Intent to our SettingsService
     * @param selectedLocationType the selected location type to store
     */
    private void storeLastSelected (String selectedLocationType) {
        //Use a service to store our setting
        //fire-and-forget style using an Intent
        startService(SettingsService.createSetDefaultLocationTypeIntent(this, selectedLocationType));
    }

    /**
     * A Cursor Loader Callback.
     * Create a CursorLoader in order to fill the Spinner based on data from our LocationTypeProvider
     */
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                LocationTypeContract.getContentUri(),
                null,                               //return all columns
                null,                               //return all rows
                null,                           //no selection required
                LocationTypeContract.LocationTypeEntry._ID
        );
    }

    /**
     * A Cursor Loader Callback.
     * The LocationProvider is done loading, place this data in the Spinner.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        try {
            //first get the data
            List<String> locationTypeList = new ArrayList<>();
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                locationTypeList.add(cursor.getString(LocationTypeContract.LocationTypeEntry.COLUMN_LOCATION_TYPE_INDEX));
                cursor.moveToNext();
            }

            //and add this data to the Spinner
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_dropdown_item_1line, locationTypeList);
            mLocationTypeSpinner.setAdapter(dataAdapter);

            //OK, the Spinner is populated.
            // initiate the retrieval of the last stored (=default) Location Type
            startService(SettingsService.createGetDefaultLocationTypeIntent(this));

        } catch (Exception e){
            temporaryShowText(e.getMessage());
        }
    }


    /**
     * A Cursor Loader Callback.
     * Not really used..
     */
    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    /**
     * Temporary show some text. Mainly used for debugging & error handling
     * @param text
     */
    private void temporaryShowText(String text) {
        //use a Toast to do so
        Toast.makeText(getApplicationContext(), text, LENGTH_LONG).show();
    }

    /**
     * Our broadcast receiver, used to retrieve results from the SettingsService
     * and update the GUI based on this.
     */
    public class MySettingsReceiver extends BroadcastReceiver {

        /**
         * The only usefull method: we just received an Intent
         * @param context
         * @param intent
         */
        @Override
        public void onReceive(Context context, Intent intent) {

            //check the Intent type
            if (intent.getStringExtra(SettingsService.COMMAND).compareTo(SettingsService.RESULT_GET_DEFAULT_LOCATION_TYPE) == 0) {
                //yes, it's the 'default location type has been retrieved from setting'-Intent
                String storedLocationType = intent.getStringExtra(SettingsService.DATA_LOCATION_TYPE);
                //set this default in the user interface
                setDefaultSelectedLocationType(storedLocationType);
            }
        }
    }
}
