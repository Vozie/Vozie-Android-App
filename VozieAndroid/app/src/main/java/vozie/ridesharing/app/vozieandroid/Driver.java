package vozie.ridesharing.app.vozieandroid;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.example.shane.vozieandroid.R;

import java.util.List;

public class Driver {
    private String firstName, lastName, id, ppmString;
    private List<String> reviews;
    private float ppm;
    private int stars;
    private Context c;
    private Drawable driverImage;

    public Driver (Context c) {
        this.c = c;
    }

    public Driver(String Id, Context c) {
        this.id = Id;
        this.c = c;
    }

    public Driver(String Id, Context c, String firstName, String lastName, float ppm, int stars) {
        this.id = Id;
        this.c = c;
        this.firstName = firstName;
        this.lastName = lastName;
        this.ppm = ppm;
        this.stars = stars;

        this.driverImage = c.getResources().getDrawable(R.drawable.driver_img1);
    }

    public Drawable getStarsImage() {
        switch(stars) {
            case 1:
                return c.getResources().getDrawable(R.drawable.one_star);
            case 2:
                return c.getResources().getDrawable(R.drawable.two_star);
            case 3:
                return c.getResources().getDrawable(R.drawable.three_star);
            case 4:
                return c.getResources().getDrawable(R.drawable.four_star);
            case 5:
                return c.getResources().getDrawable(R.drawable.five_star);
            default:
                return null;

        }
    }

    public String getPpmString() {
        String ppmString = Float.toString(ppm);
        if (ppmString.length() < 4)
            ppmString += "0";

        return "$" + ppmString;
    }

    public Drawable getDriverImage() {
        return driverImage;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getId() {
        return id;
    }
}
