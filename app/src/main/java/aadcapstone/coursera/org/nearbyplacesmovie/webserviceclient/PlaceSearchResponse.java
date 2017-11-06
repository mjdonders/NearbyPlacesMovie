package aadcapstone.coursera.org.nearbyplacesmovie.webserviceclient;

/**
 * Google Places JSON result class.
 * Used to easily parse the webservice result using GSON.
 *
 * This class is not much more than a 'Java style struct'. No getters/setters used.
 *
 * Notes:
 * All private fields are unused by the program at the moment.
 * Since I didn't find official documentation this is <b>my intrepretation</b>
 */

public class PlaceSearchResponse {
    public String[] html_attributions;
    public String next_page_token;
    public Result[] results;

    public class Result {
        private Geometry geometry;
        private String icon;
        private String id;
        public String name;
        private OpeningHours opening_hours;
        private Photo[] photos;
        private String place_id;
        private String reference;
        private String scope;
        private String[] types;
        private String vicinity;

        public class Geometry {
            public Location location;
            public ViewPort viewport;
        }

        public class ViewPort {
            public Location northeast;
            public Location southwest;
        }

        public class Location {
            private double lat;
            private double lng;
        }

        public class OpeningHours {
            private String open_now;
            private String[] weekday_text;
        }

        public class Photo {
            private String photo_reference;
            private int width;
            private int height;
            private String[] html_attributions;
        }
    }
}
