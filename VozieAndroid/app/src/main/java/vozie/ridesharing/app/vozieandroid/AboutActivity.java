package vozie.ridesharing.app.vozieandroid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.shane.vozieandroid.R;


public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.MyAppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Initialize menu bar and navigation drawer
        MenuBarHandler menuBarHandler = new MenuBarHandler(this);
        menuBarHandler.init();

        String aboutText = "Vozie Inc. is a pre-paid ride sharing service HQd out of " +
                "San Jose California located in the heart of the Silicon Valley. " +
                "Vozie began as a garage start up in September of 2015. The idea " +
                "was brought up due to the high surcharges and price increases when " +
                "using ride sharing services during high peak hours. The Vozie team has " +
                "made it our mission to provide an option that will be consistent and not " +
                "gauge people that depend on ride sharing to get around.";

        String aboutText2 ="Vozie has grown " +
                "into a four person team that has worked extensively to provide the best " +
                "solution in the marketplace. Two members of the team are in charge of " +
                "developing the product for the android app and will be making the app " +
                "available for the ios.";

        TextView desText = (TextView) findViewById(R.id.description_text);
        desText.setText(aboutText);
        TextView desText2 = (TextView) findViewById(R.id.description_text2);
        desText2.setText(aboutText2);
    }
}
