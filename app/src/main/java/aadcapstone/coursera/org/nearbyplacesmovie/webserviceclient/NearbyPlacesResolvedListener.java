package aadcapstone.coursera.org.nearbyplacesmovie.webserviceclient;

/**
 * Listener interface to indicate the nearby places have been resolved
 */

public interface NearbyPlacesResolvedListener {

    /**
     * The nearby places search is done. Data has been resolved.
     * This method is called to update the caller for this event.
     */
    void nearbyPlacesResolved ();
}
