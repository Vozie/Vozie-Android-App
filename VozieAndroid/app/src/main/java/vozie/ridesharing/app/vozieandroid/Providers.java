package vozie.ridesharing.app.vozieandroid;


import android.content.Context;
import android.location.LocationManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

/**************************************************
 * File Name: Providers.java
 *
 * Description: This class is used to manage and store
 * the status of the various providers necessary in Vozie.
 **************************************************/
public class Providers {
    private Context appContext;
    private LocationManager lm;

    /*Title:            Providers
    * Description:      Constructor
    *
    * @param        c   Application context to get system services.
    */
    public Providers(Context c) {
        appContext = c;
        lm = (LocationManager) appContext.getSystemService(Context.LOCATION_SERVICE);
    }

    /*Title:            isNetworkEnabled
    * Description:      Returns the state of the system network provider
    *
    * @return           Will return true if enabled and false if disabled.
    */
    public boolean isNetworkEnabled() {
        if (appContext != null)
            try {
                lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                return true;
            } catch (Exception e) { }
        return false;
    }

    /*Title:            isGpsEnabled
    * Description:      Returns the state of the system gps provider
    *
    * @return           Will return true if enabled and false if disabled.
    */
    public boolean isGpsEnabled() {
        if (appContext != null)
            try {
                lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                return true;
            } catch (Exception e) { }
        return false;
    }

    /*Title:            isPlayServiceEnabled
    * Description:      Returns the state of the system gps provider
    *
    * @return           Will return true if enabled and false if disabled.
    */
    public boolean isPlayServiceEnabled() {
        if (appContext != null)
            if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(appContext)
                    == ConnectionResult.SUCCESS)
                return true;
        return false;
    }
}
