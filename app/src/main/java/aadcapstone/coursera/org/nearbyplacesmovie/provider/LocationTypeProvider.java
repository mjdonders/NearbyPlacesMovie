package aadcapstone.coursera.org.nearbyplacesmovie.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;


/**
 * LocationType provider
 */

public class LocationTypeProvider extends ContentProvider {

    /**
     * internally used database helper
     */
    private LocationTypeDatabaseHelper mDatabaseHelper;

    /**
     * The context to be using
     */
    private Context mContext;

    /**
     * The URI matcher
     */
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    /**
     * ACRONYM integer for a location type and
     * in this case: it's all about the location type..
     */
    public static final int LOCATION_TYPE = 100;

    /**
     * Return true if successfully started.
     */
    @Override
    public boolean onCreate() {
        mContext = getContext();

        // Select the concrete implementor.
        // Create the DatabaseHelper.
        mDatabaseHelper = new LocationTypeDatabaseHelper(mContext);
        return true;
    }

    /**
     * Helper method to match each URI to the ACRONYM integers
     * constant defined above.
     *
     * @return UriMatcher
     */
    private static UriMatcher buildUriMatcher() {
        // All paths added to the UriMatcher have a corresponding code
        // to return when a match is found.  The code passed into the
        // constructor represents the code to return for the rootURI.
        // It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        // For each type of URI that is added, a corresponding code is
        // created.
        matcher.addURI(LocationTypeContract.CONTENT_AUTHORITY_NAME,
                LocationTypeContract.LocationTypeEntry.TABLE_NAME,
                LOCATION_TYPE);

        return matcher;
    }

    /**
     * Method called to handle type requests from client applications.
     * It returns the MIME type of the data associated with each URI.
     */
    @Override
    public String getType(Uri uri) {
        // Match the id returned by UriMatcher to return appropriate
        // MIME_TYPE.
        switch (sUriMatcher.match(uri)) {
            case LOCATION_TYPE:
                return LocationTypeContract.CONTENT_ITEMS_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: "
                        + uri);
        }
    }

    /**
     * Method called to handle query requests from client applications.
     */
    @Override
    public Cursor query(Uri uri,
                        String[] projection,
                        String selection,
                        String[] selectionArgs,
                        String sortOrder) {
        Cursor cursor;

        // Match the id returned by UriMatcher to query appropriate rows.
        switch (sUriMatcher.match(uri)) {
            case LOCATION_TYPE:
                cursor = queryLocationTypes(uri,
                        projection,
                        selection,
                        selectionArgs,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: "
                        + uri);
        }

        // Register to watch a content URI for changes.
        cursor.setNotificationUri(mContext.getContentResolver(),
                uri);
        return cursor;
    }

    /**
     * Method called to handle query requests from client
     * applications.
     */
    private Cursor queryLocationTypes(Uri uri,
                                   String[] projection,
                                   String selection,
                                   String[] selectionArgs,
                                   String sortOrder) {
        // Expand the selection if necessary.
        selection = addSelectionArgs(selection,
                selectionArgs,
                "OR");
        return mDatabaseHelper.getReadableDatabase().query
                (LocationTypeContract.LocationTypeEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
    }

    /**
     * Return a selection string that concatenates all the
     * @selectionArgs for a given
     * @selection using the given
     * @operation.
     */
    private String addSelectionArgs(String selection,
                                    String [] selectionArgs,
                                    String operation) {
        // Handle the "null" case.
        if (selection == null
                || selectionArgs == null)
            return null;

        String selectionResult = "";

        // Properly add the selection args to the selectionResult.
        for (int i = 0;
             i < selectionArgs.length - 1;
             ++i)
            selectionResult += (selection
                    + " = ? "
                    + operation
                    + " ");

        // Handle the final selection case.
        selectionResult += (selection
                + " = ?");

        return selectionResult;
    }


    /**
     * Method called to handle insert requests from client apps.
     */
    @Override
    public Uri insert(Uri uri,
                      ContentValues cvs) {
        Uri returnUri;

        // Try to match against the path in a url.  It returns the
        // code for the matched node (added using addURI), or -1 if
        // there is no matched node.  If there's a match insert a new
        // row.
        switch (sUriMatcher.match(uri)) {
            case LOCATION_TYPE:
                returnUri = insertLocationTypes(uri, cvs);
                break;
            default:
                //insert currently not supported
                throw new UnsupportedOperationException("Unknown uri: "
                        + uri);
        }

        // Notifies registered observers that a row was inserted.
        mContext.getContentResolver().notifyChange(uri,
                null);
        return returnUri;
    }

    /**
     * Helper method for inserting of location types
     * @param uri
     * @param cvs
     * @return
     */
    private Uri insertLocationTypes(Uri uri, ContentValues cvs) {
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        long id =
                db.insert(LocationTypeContract.LocationTypeEntry.TABLE_NAME,
                        null,
                        cvs);

        // Check if a new row is inserted or not.
        if (id > 0)
            return LocationTypeContract.LocationTypeEntry.buildUri(id);
        else
            throw new android.database.SQLException
                    ("Failed to insert row into " + uri);
    }

    /**
     * Method called to handle update requests from client
     * applications.
     */
    @Override
    public int update(Uri uri,
                      ContentValues cvs,
                      String selection,
                      String[] selectionArgs) {
        int returnCount = 0;

        // Try to match against the path in a url.  It returns the
        // code for the matched node (added using addURI), or -1 if
        // there is no matched node.  If there's a match update rows.
        switch (sUriMatcher.match(uri)) {
            case LOCATION_TYPE:
                //fall through
            default:
                //no update supported
                throw new UnsupportedOperationException();
        }

        /*
        //unreachable as long as we don't actually implement the update
        //still 'keep this code here', for future updates
        if (returnCount > 0)
            // Notifies registered observers that row(s) were
            // updated.
            mContext.getContentResolver().notifyChange(uri,
                    null);
        return returnCount; */
    }

    /**
     * Delete location types from the database
     * @param uri
     * @param selection
     * @param selectionArgs
     * @return
     */
    private Uri deleteLocationTypes(Uri uri,
                                    String selection,
                                    String[] selectionArgs) {
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        long id =
                db.delete(LocationTypeContract.LocationTypeEntry.TABLE_NAME,
                        selection,
                        selectionArgs);

        // Check if a row is deleted or not.
        if (id > 0)
            return LocationTypeContract.LocationTypeEntry.buildUri(id);
        else
            throw new android.database.SQLException
                    ("Failed to delete row into " + uri);
    }


    /**
     * Method called to handle delete requests from client
     * applications.
     */
    @Override
    public int delete(Uri uri,
                      String selection,
                      String[] selectionArgs) {
        int returnCount = 0;

        // Try to match against the path in a url.  It returns the
        // code for the matched node (added using addURI), or -1 if
        // there is no matched node.  If there's a match delete rows.
        switch (sUriMatcher.match(uri)) {
            case LOCATION_TYPE:
                deleteLocationTypes(uri, selection, selectionArgs);
                break;
            default:
                //delete not currently supported
                throw new UnsupportedOperationException();
        }

        if (returnCount > 0)
            // Notifies registered observers that row(s) were deleted.
            mContext.getContentResolver().notifyChange(uri,
                    null);

        return returnCount;
    }
}
