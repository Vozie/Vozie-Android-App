package vozie.ridesharing.app.vozieandroid;

import android.location.Location;

/**************************************************
 * File Name: PaymentItem.java
 *
 * Description: The PaymentItem class encapsulates
 * the data necessary in storing a user's payment
 * information.
 **************************************************/
class HistoryItem {
    public String fromLocation, toLocationString, distance, cost, date, driverId;
    public Location toLocation;
    public int tripRating;

    HistoryItem(String fromLocation,
                String toLocationString,
                String distance,
                String cost,
                String date,
                String driverId,
                Location toLocation,
                int tripRating) {
        this.fromLocation = fromLocation;
        this.toLocationString = toLocationString;
        this.distance = distance;
        this.cost = cost;
        this.date = date;
        this.driverId = driverId;
        this.toLocation = toLocation;
        this.tripRating = tripRating;
    }
}
