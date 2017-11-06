package aadcapstone.coursera.org.nearbyplacesmovie;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;

import aadcapstone.coursera.org.nearbyplacesmovie.provider.LocationTypeContract;

/**
 * LocationType provider helper class.
 * Contains all the location types supported by the Google Places API.
 * (these location types are never used direct, but only via the LocationTypeProvider)
 */

public class LocationTypeProviderHelper {

    ContentResolver mContentResolver;

    /*
     * all the Google Places location types, excluding the deprecated ones
     */
    private String mLocationTypes[] = new String[]{
        "accounting",         "airport",           "amusement_park",          "aquarium",
        "art_gallery",        "atm",               "bakery",                  "bank",
        "bar",                "beauty_salon",      "bicycle_store",           "book_store",
        "bowling_alley",      "bus_station",       "cafe",                    "campground",
        "car_dealer",         "car_rental",        "car_repair",              "car_wash",
        "casino",             "cemetery",          "church",                  "city_hall",
        "clothing_store",     "convenience_store", "courthouse",              "dentist",
        "department_store",   "doctor",            "electrician",             "electronics_store",
        "embassy",            "fire_station",      "florist",                 "funeral_home",
        "furniture_store",    "gas_station",       "gym",                     "hair_care",
        "hardware_store",     "hindu_temple",      "home_goods_store",        "hospital",
        "insurance_agency",   "jewelry_store",     "laundry",                 "lawyer",
        "library",            "liquor_store",      "local_government_office", "locksmith",
        "lodging",            "meal_delivery",     "meal_takeaway",           "mosque",
        "movie_rental",       "movie_theater",     "moving_company",          "museum",
        "night_club",         "painter",           "park",                    "parking",
        "pet_store",          "pharmacy",          "physiotherapist",         "plumber",
        "police",             "post_office",       "real_estate_agency",      "restaurant",
        "roofing_contractor", "rv_park",           "school",                  "shoe_store",
        "shopping_mall",      "spa",               "stadium",                 "storage",
        "store",              "subway_station",    "synagogue",               "taxi_stand",
        "train_station",      "transit_station",   "travel_agency",           "university",
        "veterinary_care",    "zoo"};

    /**
     * And the deprecated Google Places location types.
     * (not actually used on the app)
     */
    private String mDeprecatedLocationTypes[] = new String[]{
            "establishment (deprecated)",
            "finance (deprecated)",
            "food (deprecated)",
            "general_contractor (deprecated)",
            "grocery_or_supermarket (deprecated)",
            "health (deprecated)",
            "place_of_worship (deprecated)"};



    public LocationTypeProviderHelper(ContentResolver contentResolver) {
        mContentResolver = contentResolver;
    }

    public ContentResolver getLocationTypeProvider() {
        return mContentResolver;
    }


    /**
     * Fills the database, used when the app starts
     */
    public void initialFillDatabase() {
        //first delete all, so we don't get over-populated..
        emptyDatabase();

        bulkInsert(mLocationTypes);
    }

    /**
     * Completely empty the database
     */
    private void emptyDatabase() {
        try {
            mContentResolver.delete(LocationTypeContract.getContentUri(), null, null);
        } catch (android.database.SQLException e) {
            //it might already have been empty (first run): no issue.
        }
    }

    /**
     * Inserts all the location types in the database
     * @param locationTypes all (non-deprecated) locationn types
     * @return the number of newly created rows
     */
    private int bulkInsert(String[] locationTypes) {
        // Use ContentValues to store the values in appropriate
        // columns, so that ContentResolver can process it.  Since
        // more than one rows needs to be inserted, an Array of
        // ContentValues is needed.
        ContentValues[] cvsArray =
                new ContentValues[locationTypes.length];

        // Index counter.
        int i = 0;

        // Insert all the characters into the ContentValues array.
        for (String locationType : locationTypes) {
            ContentValues cvs = new ContentValues();
            cvs.put(LocationTypeContract.LocationTypeEntry.COLUMN_LOCATION_TYPE, locationType);
            cvsArray[i++] = cvs;
        }

        // Insert the array of content at the designated URI.
        return bulkInsert(LocationTypeContract.getContentUri(), cvsArray);
    }

    /**
     * Insert an array of ContentValues into the database
     * @return the number of newly created rows
     */
    private int bulkInsert(Uri uri,
                             ContentValues[] cvsArray) {
        return mContentResolver.bulkInsert(uri, cvsArray);
    }
}
