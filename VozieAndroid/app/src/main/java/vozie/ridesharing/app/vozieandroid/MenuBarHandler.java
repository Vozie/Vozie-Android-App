package vozie.ridesharing.app.vozieandroid;

import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.shane.vozieandroid.R;

/*Title:                            MenuBarHandler
* Description:                      Class to establish listeners with menu view component
*                                   and to initialize navigation drawer.
*
* @param        AppCompatActivity   Reference to calling AppCompatActivity.
*/
public class MenuBarHandler {
    private AppCompatActivity a;
    public Toolbar tb;
    private String[] activities = {"Trip", "Payment Methods", "Trip History", "About Vozie"};
    private Intent appToLaunch;
    private boolean launchApp = false;

    public MenuBarHandler(AppCompatActivity a) {
        this.a = a;
        this.tb = (Toolbar) a.findViewById(R.id.menubar);
    }

    public void init() {
        a.setSupportActionBar(tb);

        ProfilePictureView profilePictureView;
        profilePictureView = (ProfilePictureView) tb.findViewById(R.id.profile_image);
        profilePictureView.setProfileId("100010272871953");

        final ActionBar ab = a.getSupportActionBar();
        ab.setDisplayShowHomeEnabled(false);
        ab.setDisplayHomeAsUpEnabled(false);
        ab.setDisplayShowCustomEnabled(true);
        ab.setDisplayShowTitleEnabled(false);

        final ImageView menuImg = (ImageView) tb.findViewById(R.id.menu_button);
        final DrawerLayout mDrawerLayout = (DrawerLayout) a.findViewById(R.id.drawer_layout);
        final ListView mDrawerList = (ListView) a.findViewById(R.id.drawer_list);

        ArrayAdapter adapter = new ArrayAdapter(a,
                android.R.layout.simple_list_item_1, activities);
        mDrawerList.setAdapter(adapter);
        mDrawerLayout.closeDrawer(GravityCompat.START);

        menuImg.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerOpened(View v) {

            }

            @Override
            public void onDrawerClosed(View v) {
                if (launchApp) {
                    a.startActivity(appToLaunch);
                    a.finish();
                }
                launchApp = false;
            }

            @Override
            public void onDrawerStateChanged(int c) {

            }

            @Override
            public void onDrawerSlide(View v, float f) {

            }
        });

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        appToLaunch = new Intent(a, MapsActivity.class);
                        break;
                    case 1:
                        appToLaunch = new Intent(a, PaymentActivity.class);
                        break;
                    case 2:
                        appToLaunch = new Intent(a, HistoryActivity.class);
                        break;
                    case 3:
                        appToLaunch = new Intent(a, AboutActivity.class);
                        break;
                    default:
                        appToLaunch = new Intent(a, a.getClass());
                }

                launchApp = true;
                mDrawerLayout.closeDrawer(mDrawerList);
            }
        });
    }

    public int getHeight() {
        return tb.getHeight();
    }
}