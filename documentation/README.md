# Goal
The goal of the app is to easily show youtube movies from nearby places.

# User interface - 
## Welcome screen (screen 1)
The app opens with a welcome screen which has a dropdown box of types of locations.
This dropdown box persistently stores the last select location type.
The welcome screen also has a button to retrieve nearby locations (shown in a new screen).

## Location selection screen (screen 2)
The location selection screen retrieves the current location and requests the Google Places webservice 
nearby places based on the location type selected in the welcome screen and the current location.
The top 5 closest locations are shown in a dropdown box, sorted on closest first.
The location selection screen also has a button, if this is clicked youtube will be opened for this location.

# Class design
Both screens are Activity classes.
The welcome screen uses a SettingService (Android Service created for this app to persistently store the last selected location type).
The welcome screen activity and the related SettingService will communicate via a BroadcastReceiver.
The location selection screen uses a class called GooglePlacesInterface to be able to use the Google Places webservice
in order to retrieve nearby locations based on the selected location type.


# Webservice to use
This is the Google Places webservice via the URL below:
https://maps.googleapis.com/maps/api/place/nearbysearch/json?location={current location here}&rankby=distance&types=food&key={Google API access key}
 
Specification of the webservice is explained in the Google developers section, accessable via the URL:
https://developers.google.com/places/web-service/intro
 
 
# **DEBUGGING INFO**
In the second screen, when getting the location in the emulator, if this does not work
The location needs to be 'submitted' in the emulator settings-screen.

Press the '...' button next to the emulator
In the location 'tab' enter the lattitude and longitude you like.
Click the 'SEND'-button (to feed this info into the emulator)