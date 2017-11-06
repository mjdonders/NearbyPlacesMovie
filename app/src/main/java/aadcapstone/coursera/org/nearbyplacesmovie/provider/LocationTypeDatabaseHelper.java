package aadcapstone.coursera.org.nearbyplacesmovie.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;


/**
 * The LocationType database helper
 */

public class LocationTypeDatabaseHelper extends SQLiteOpenHelper {

    /**
     * Database name
     */
    private static final String DATABASE_NAME = "aadstone_coursera_org_nearbyplaces_db";

    /**
     * Database version number, which is updated with each schema change.
     */
    private static int DATABASE_VERSION = 1;

    /**
     * Constructor - initialize database name and version, but don't
     * actually construct the database (which is done in the
     * onCreate() hook method). It places the database in the
     * application's cache directory, which will be automatically
     * cleaned up by Android if the device runs low on storage space.
     *
     * @param context Any context
     */
    public LocationTypeDatabaseHelper(Context context) {
        super(  context,
                DATABASE_NAME,
                null,
                DATABASE_VERSION);
    }

    /**
     * Hook method called when the database is created.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        //the query to create the table
        final String SQL_CREATE_LOCATION_TYPE_TABLE =
                "CREATE TABLE "+LocationTypeContract.LocationTypeEntry.TABLE_NAME +" ("
                        + LocationTypeContract.LocationTypeEntry._ID + " INTEGER PRIMARY KEY, "
                        + LocationTypeContract.LocationTypeEntry.COLUMN_LOCATION_TYPE + " TEXT NOT NULL "
                        + " );";

        // Create the table.
        db.execSQL(SQL_CREATE_LOCATION_TYPE_TABLE);
    }

    /**
     * Hook method called when the database is upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion,
                          int newVersion) {

        //Let's first check if something has indeed changed..
        if (oldVersion != newVersion) {
            //yes: delete the existing tables.
            db.execSQL("DROP TABLE IF EXISTS "
                    + LocationTypeContract.LocationTypeEntry.TABLE_NAME);

            //and create the new tables.
            onCreate(db);
        }
    }
}
