package vozie.ridesharing.app.vozieandroid;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;

import com.google.android.gms.maps.GoogleMap;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.facebook.FacebookSdk.getApplicationContext;

/**************************************************
* File Name: MapFunctions.java
*
* Description: This class stores functions involved
* in mapping and location for convenience.
**************************************************/
public class MapFunctions {

    public static final int MAX_RESULTS = 50;

    private GoogleMap mMap;
    private Geocoder geoCoder;

    /*Title:                            MapFunctions
    * Description:                      Class constructor function that initializes mMap.
    *                                   and initializes marker variables.
    *
    * @param        map                 Reference to Map object.
    */
    public MapFunctions(GoogleMap map) {
        mMap = map;
        geoCoder = new Geocoder(getApplicationContext(), Locale.getDefault());
    }

    /*Title:               getAddressFromLocation
    * Description:         Acquires the most likely address of a specific Location object.
    *
    * @param       loc     Location object which dictates the lat/long to determine
    *                      the address of.
    */
    public Address getAddressFromLocation (Location loc) {
        List<Address> addresses;

        try {
            addresses = geoCoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
        } catch (IOException e) { return null; }

        if (addresses.size() != 0)
            return addresses.get(0);
        else return null;
    }

    public static Location getLocationFromAddress (Address addr) {
        Location retLoc = new Location("returnLocation");
        retLoc.setLatitude(addr.getLatitude());
        retLoc.setLongitude(addr.getLongitude());

        return retLoc;
    }

    public static String getAddressString(Address addr) {
        String retString = "";

        for (int i = 0; i < addr.getMaxAddressLineIndex(); i++) {
            if (addr.getAddressLine(i) != "USA")
                retString += addr.getAddressLine(i) + " ";
            else break;
        }

        return retString;
    }

    /*Title:                navigateToCoordinates
    * Description:          Opens default navigation software to begin routing to specified
    *                       latitude and longitude.
    *
    * @param        lat     Input latitude of location to route to.
    * @param        lon     Input longitude of location to route to.
    */
    public static void navigateToCoordinates(double lat, double lon) {
        String format = "geo:0,0?q=" + lat + "," + lon + "( Location title)";

        Uri uri = Uri.parse(format);


        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getApplicationContext().startActivity(intent);
    }

    public static List<Address> resultListFromUserInput(String input, Location radialCenter) {
        Geocoder geoCoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {
            double lowerLeftLat = radialCenter.getLatitude()
                    - getRadialDegreeDistance("Lat", radialCenter);
            double lowerLeftLong = radialCenter.getLongitude()
                    - getRadialDegreeDistance("Long", radialCenter);
            double upperRightLat = radialCenter.getLatitude()
                    + getRadialDegreeDistance("Lat", radialCenter);
            double upperRightLong = radialCenter.getLongitude()
                    + getRadialDegreeDistance("Long", radialCenter);

            List<Address> addressList = geoCoder.getFromLocationName(input, MAX_RESULTS, lowerLeftLat,
                    lowerLeftLong, upperRightLat, upperRightLong);

            return addressList;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static double getRadialDegreeDistance(String param, Location radialCenter) {
        switch (param) {
            case "Lat":
                return (1 / 110.54) * 30 * 0.621371;
            case "Long":
                return (1 / (111.320 * Math.cos(radialCenter.getLatitude())))
                        * 30 * 0.621371;
            default:
                return 0;
        }
    }
}
