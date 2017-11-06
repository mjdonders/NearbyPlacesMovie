package aadcapstone.coursera.org.nearbyplacesmovie.provider;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * The LocationType Contract
 */
public final class LocationTypeContract {

    /**
     * Our provider's authority name
     */
    public static final String CONTENT_AUTHORITY_NAME = "aadcapstone.coursera.org.nearbyplacesmovie.provider";

    /**
     * Our provider's base content URI
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY_NAME);


    /**
     * Use
     * vnd.android.cursor.dir for when you expect the Cursor to contain 0 through infinity items
     * or
     * vnd.android.cursor.item for when you expect the Cursor to contain 1 item
     */
    public static final String CONTENT_ITEMS_TYPE =
            "vnd.android.cursor.dir/vnd."
                    + CONTENT_AUTHORITY_NAME
                    + "."   //as specified in https://developer.android.com/guide/topics/providers/content-provider-creating.html
                    + LocationTypeEntry.TABLE_NAME;

    /**
     * The columns in the table we have.
     * Not actually used in this app, but still worth having in the Contract
     */
    public static final String sColumnsToDisplay [] =
            new String[] {
                    LocationTypeContract.LocationTypeEntry._ID,
                    LocationTypeEntry.COLUMN_LOCATION_TYPE
            };

    /**
     * The content URI referencing the (one and only) table
     */
    public static Uri getContentUri () {
        return BASE_CONTENT_URI.buildUpon()
                        .appendPath(LocationTypeEntry.TABLE_NAME).build();
    }

    /**
     * Table entry for the location type table
     */
    public static final class LocationTypeEntry implements BaseColumns {

        /**
         * Table name
         */
        public static final String TABLE_NAME = "location_types";

        /***
         * location type column name
         */
        public static final String COLUMN_LOCATION_TYPE = "location_type";

        /**
         * location type column index (zero based), also see sColumnsToDisplay
         */
        public static final int COLUMN_LOCATION_TYPE_INDEX = 1;

        /**
         * URI builder helper method
         * @param id the id to append
         * @return created Uri
         */
        public static Uri buildUri(Long id) {
            return ContentUris.withAppendedId(getContentUri(), id);
        }

    }
}
