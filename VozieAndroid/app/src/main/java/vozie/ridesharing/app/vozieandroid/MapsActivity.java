package vozie.ridesharing.app.vozieandroid;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shane.vozieandroid.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, LocationListener,
        GoogleApiClient.OnConnectionFailedListener {

    public static boolean mMapIsTouched = false;

    private final int LOCATION_LISTENER_INTERVAL = 10 * 1000;
    private final int LOCATION_LISTENER_FASTEST_INTERVAL = 5 * 1000;
    private final int LOCATION_SMALLEST_DISPLACEMENT = 5;

    private final int CAMERA_ZOOM_LEVEL = 16;
    private final int CAMERA_ZOOM_SPEED = 4 * 1000;

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationManager mLocationManager;
    private LocationRequest mLocationRequest;
    private Providers providers;
    private MapFunctions mapFunctions;

    private Projection projection;
    private EditText locationTextview;
    private LinearLayout markerControl;
    private LinearLayout locationBar;
    private ImageView locationMarker;
    private ImageView searchImage;
    private ListView driversListView;
    private RelativeLayout mapCover;
    private List<Driver> drivers;
    private List<View> driversListItems;
    private ListView resultsListview;
    MenuBarHandler menuBarHandler;
    View searchDivider;
    private BlockingArrayAdapter searchAdapter;

    private Animator revealAnimator;
    private FrameLayout driverPreviewLayout;
    private TextView driverPreviewNameTextview;
    private TextView driverPreviewPpmTextview;
    private ImageView driverPreviewStarsImageview;
    private ImageView driverPreviewImageview;

    private String currentSearchText;
    private Thread searchThread;
    private Location userLocation;
    private ArrayList<String>  searchResults;
    private List<Address> searchResultAddresses;

    private boolean firstRun = true;
    private boolean pickupMode = true;
    private boolean pinDown = true;
    private boolean driverMode = false;
    private boolean driverPreviewMode = false;
    private boolean initialPanDone = false;
    private boolean initialPanStarted = false;

    private int mode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.MyAppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Initialize menu bar and navigation drawer
        menuBarHandler = new MenuBarHandler(this);
        menuBarHandler.init();

        // Initialize providers helper class
        providers = new Providers(this);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        drivers = new ArrayList();

        drivers.add(new Driver("001", getApplicationContext(), "Jackie", "Hoang", 1.25f, 5));
        drivers.add(new Driver("002", getApplicationContext(), "Chris", "Nguyen", 0.99f, 3));
        drivers.add(new Driver("003", getApplicationContext(), "Shane", "Duffy", 0.75f, 3));
        drivers.add(new Driver("004", getApplicationContext(), "James", "Franco", 1.25f, 2));
        drivers.add(new Driver("005", getApplicationContext(), "Barack", "Obama", 1.99f, 1));
        drivers.add(new Driver("006", getApplicationContext(), "Kim Jong", "Un", 1.35f, 5));
        drivers.add(new Driver("007", getApplicationContext(), "Donald", "Trump", 0.87f, 4));
        drivers.add(new Driver("008", getApplicationContext(), "Chris", "Tran", 0.50f, 4));
        drivers.add(new Driver("009", getApplicationContext(), "Tara", "Duffy", 1.90f, 5));
        drivers.add(new Driver("010", getApplicationContext(), "Kevin", "Duffy", 0.99f, 2));
        drivers.add(new Driver("011", getApplicationContext(), "Jackie", "Hoang", 0.95f, 1));

        driversListView = (ListView) findViewById(R.id.drivers_listview);
        final CustomAdapter driverAdapter = new CustomAdapter(getApplicationContext(), drivers, this);
        driversListItems = driverAdapter.rowViews;
        driversListView.setAdapter(driverAdapter);

        searchDivider = findViewById(R.id.search_divider);
        searchDivider.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {

            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                searchDivider.removeOnLayoutChangeListener(this);
                searchDivider.setY(convertDpToPixel(5));
                searchDivider.setX(getScreenWidth());
            }
        });

        AssetManager am = getApplicationContext().getAssets();
        locationTextview = (EditText) findViewById(R.id.location_textview);
        TextView markerControlTextview = (TextView) findViewById(R.id.marker_control_textview);
        markerControl = (LinearLayout) findViewById(R.id.marker_control);
        locationTextview.setTypeface(Typeface.createFromAsset(am,
                String.format(Locale.US, "fonts/%s", "ufonts.com_microsoft-jhenghei.ttf")));
        locationTextview.setCursorVisible(false);
        locationTextview.setFocusable(false);

        locationMarker = (ImageView) findViewById(R.id.location_marker);
        markerControl.setX(getScreenWidth() / 2 - convertDpToPixel(80));
        markerControl.setY(getScreenHeight() / 2 - convertDpToPixel(95));
        markerControlTextview.setTypeface(Typeface.createFromAsset(am,
                String.format(Locale.US, "fonts/%s", "ufonts.com_microsoft-jhenghei.ttf")));

        markerControl.setOnClickListener(new LinearLayout.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterDriverMode();
            }
        });

        locationBar = (LinearLayout) findViewById(R.id.location_input_menu);
        resultsListview = (ListView) findViewById(R.id.results_listview);
        resultsListview.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {

            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                resultsListview.removeOnLayoutChangeListener(this);
                resultsListview.setY(getScreenHeight() - menuBarHandler.getHeight());
            }
        });


        searchImage = (ImageView) findViewById(R.id.search_image);
        searchImage.setClickable(true);
        searchImage.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                activateSearchMode();
            }
        });

        mapCover = (RelativeLayout) findViewById(R.id.map_cover);
        driverPreviewLayout = (FrameLayout) findViewById(R.id.driver_preview_layout);
        driverPreviewLayout.setBackgroundColor(Color.TRANSPARENT);
        driverPreviewNameTextview = (TextView) findViewById(R.id.driver_preview_name_textview);
        driverPreviewNameTextview.setTypeface(Typeface.createFromAsset(am,
                String.format(Locale.US, "fonts/%s", "ufonts.com_microsoft-jhenghei.ttf")));

        driverPreviewStarsImageview = (ImageView) findViewById(R.id.driver_preview_stars_img);
        driverPreviewPpmTextview = (TextView) findViewById(R.id.driver_preview_ppm_textview);
        driverPreviewImageview = (ImageView) findViewById(R.id.driver_preview_circle_img);

        ImageView driverPreviewBack = (ImageView) findViewById(R.id.back_image);
        ImageView driverPreviewWheel = (ImageView) findViewById(R.id.driver_preview_wheel);

        searchResultAddresses = new ArrayList<>();

        driverPreviewWheel.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        driverPreviewBack.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideDriverPreview();
            }
        });

        searchResults = new ArrayList<String>();
        searchAdapter = new BlockingArrayAdapter(MapsActivity.this,
                R.layout.auto_complete_textview_item, searchResults);

        resultsListview.setClickable(true);
        //resultsListview.setItemsCanFocus(false);
        //resultsListview.setFocusableInTouchMode(true);
        //resultsListview.setFocusable(true);
        //resultsListview.setItemsCanFocus(true);

        resultsListview.setAdapter(searchAdapter);
        resultsListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int position, long id) {
                Toast.makeText(getApplicationContext(), "lol", Toast.LENGTH_LONG);
                deactivateSearchMode();
                Location location = MapFunctions.getLocationFromAddress(searchResultAddresses.get(position));

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),
                        location.getLongitude()),CAMERA_ZOOM_LEVEL), CAMERA_ZOOM_SPEED, null);
            }
        });

        locationTextview.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charsequence, int i, int j, int k) {
                /* Sets global currentText and attempts to create adapter before next character
                   is entered. */
                currentSearchText = charsequence.toString();
                final Handler mHandler = new Handler() {
                    public void handleMessage(Message msg) {
                        String[] resultsArray = (String[]) msg.obj;

                        if (resultsArray != null) {
                            searchAdapter.clear();
                            searchAdapter.addAll(resultsArray);
                        }
                        else {
                            searchAdapter.clear();
                        }
                    }
                };

                searchThread = new Thread((new Runnable() {
                    public void run() {
                        List<Address> addresses = null;


                        if (providers.isNetworkEnabled()) {
                            double latitude = userLocation.getLatitude();
                            addresses = MapFunctions.resultListFromUserInput(currentSearchText, userLocation);
                            System.out.println("");
                        }
                        else {
                            // Throw Connection Error
                            System.out.println("This did not connect");
                        }

                        String tText = currentSearchText;
                        searchResultAddresses = addresses;
                        if (addresses != null) {
                            String[] array = new String[addresses.size()];
                            for (int l = 0; l < addresses.size(); l++) {
                                Address indAddress = addresses.get(l);

                                try {
                                    array[l] = indAddress.getAddressLine(0) + " "
                                            + indAddress.getAddressLine(1) + " "
                                            + indAddress.getAddressLine(2);
                                } catch (Exception e) {

                                }
                            }



                            if (tText.equals(currentSearchText)) {
                                Message msg = new Message();
                                msg.obj = array;
                                mHandler.sendMessage(msg);
                                return;
                            }
                        }
                        else {
                            if (tText.equals(currentSearchText)) {
                                Message msg = new Message();
                                msg.obj = null;
                                mHandler.sendMessage(msg);
                                return;
                            }
                        }
                    }
                }));

                if (searchThread.isAlive()) {
                    searchThread.interrupt();
                }

                searchThread.start();
            }

            @Override
            public void beforeTextChanged(CharSequence charsequence, int i, int j, int k) {
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public void activateSearchMode() {
        mode = 1;

        locationTextview.setText("");
        locationTextview.setClickable(true);
        locationTextview.setCursorVisible(true);
        locationTextview.setFocusableInTouchMode(true);
        locationTextview.setInputType(InputType.TYPE_CLASS_TEXT);
        locationTextview.requestFocus();

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }

        Animation raiseBarAnimation = new TranslateAnimation(0, 0, 0, -(getScreenHeight() - convertDpToPixel(60)
                - locationBar.getHeight() - getStatusBarHeight()));
        raiseBarAnimation.setDuration(500);
        raiseBarAnimation.setFillEnabled(true);
        raiseBarAnimation.setFillAfter(true);

        Animation enterSearchDividerAnimation = new TranslateAnimation(0, -(getScreenWidth() - convertDpToPixel(5)), 0, 0);
        enterSearchDividerAnimation.setDuration(500);
        enterSearchDividerAnimation.setFillEnabled(true);
        enterSearchDividerAnimation.setFillAfter(true);

        Animation raiseSearchResultsAnimation = new TranslateAnimation(0, 0, 0, -(getScreenHeight() - convertDpToPixel(60)
                - locationBar.getHeight()));
        raiseSearchResultsAnimation.setDuration(500);
        raiseSearchResultsAnimation.setFillEnabled(true);
        raiseSearchResultsAnimation.setFillAfter(true);

        resultsListview.startAnimation(raiseSearchResultsAnimation);
        locationBar.startAnimation(raiseBarAnimation);
        searchDivider.startAnimation(enterSearchDividerAnimation);
    }

    public void deactivateSearchMode() {
        mode = 0;

        locationTextview.setClickable(false);
        locationTextview.setCursorVisible(false);
        locationTextview.setFocusableInTouchMode(false);

        Animation raiseBarAnimation = new TranslateAnimation(0, 0, -(getScreenHeight() - convertDpToPixel(60)
                - locationBar.getHeight() - getStatusBarHeight()), 0);
        raiseBarAnimation.setDuration(500);
        raiseBarAnimation.setFillEnabled(true);
        raiseBarAnimation.setFillAfter(true);

        Animation enterSearchDividerAnimation = new TranslateAnimation(-(getScreenWidth() - convertDpToPixel(5)), 0, 0, 0);
        enterSearchDividerAnimation.setDuration(500);
        enterSearchDividerAnimation.setFillEnabled(true);
        enterSearchDividerAnimation.setFillAfter(true);

        Animation raiseSearchResultsAnimation = new TranslateAnimation(0, 0, -(getScreenHeight() - convertDpToPixel(60)
                - locationBar.getHeight() - getStatusBarHeight()), 0);
        raiseSearchResultsAnimation.setDuration(500);
        raiseSearchResultsAnimation.setFillEnabled(true);
        raiseSearchResultsAnimation.setFillAfter(true);

        resultsListview.startAnimation(raiseSearchResultsAnimation);
        locationBar.startAnimation(raiseBarAnimation);
        searchDivider.startAnimation(enterSearchDividerAnimation);
    }

    public void lightenMap() {
        Runnable threadRunnable = new Runnable() {
            @Override
            public void run() {
                for (int i = 99; i > 0; i--) {
                    String numToString = Integer.toString(i);

                    if (numToString.length() == 1)
                        numToString = "0" + numToString;

                    final String backgroundString = "#" + numToString + "000000";

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mapCover.setBackgroundColor(Color.parseColor(backgroundString));
                        }
                    });

                    try { Thread.sleep(20); } catch (Exception e) { }
                }
            }
        };

        Thread darkenThread = new Thread(threadRunnable);
        darkenThread.start();
    }

    public void darkenMap() {
        Runnable threadRunnable = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 99; i++) {
                    String numToString = Integer.toString(i);

                    if (numToString.length() == 1)
                        numToString = "0" + numToString;

                    final String backgroundString = "#" + numToString + "000000";

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mapCover.setBackgroundColor(Color.parseColor(backgroundString));
                        }
                    });

                    try { Thread.sleep(5); } catch (Exception e) { }
                }
            }
        };

        Thread darkenThread = new Thread(threadRunnable);
        darkenThread.start();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        try {
            googleMap.setMyLocationEnabled(false);
        } catch (SecurityException e) { }

        mMap = googleMap;

        if (mMap != null) {
            // Initialize mapFunctions object
            mapFunctions = new MapFunctions(mMap);

            // Build and connect to google play services
            buildGoogleApiClient();
            mGoogleApiClient.connect();
        }

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                if (initialPanStarted && !initialPanDone) {
                    initialPanDone = true;
                    locationMarker.setVisibility(View.VISIBLE);

                    Animation initPinAnimation = new TranslateAnimation(0, 0,
                            -(getScreenHeight()/2), 0);
                    initPinAnimation.setDuration(500);
                    initPinAnimation.setFillEnabled(true);
                    initPinAnimation.setFillAfter(true);

                    locationMarker.startAnimation(initPinAnimation);
                    initPinAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            LatLng coords = projection.fromScreenLocation(new Point(getScreenWidth()/2, getScreenHeight()/2));
                            Location locationOfCoords = new Location("coordinateLocation");
                            locationOfCoords.setLatitude(coords.latitude);
                            locationOfCoords.setLongitude(coords.longitude);
                            Address addr = mapFunctions.getAddressFromLocation(locationOfCoords);
                            final String text;
                            if (addr == null)
                                text = "Unknown Address";
                            else
                                text = mapFunctions.getAddressString(addr);

                            revealMarkerControl();
                            locationTextview.setText(text);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }
            }
        });

        projection = mMap.getProjection();
        initMapListener();
    }

    @Override
    public void onBackPressed() {
        if (mode == 1) {
            deactivateSearchMode();
            locationTextview.setText(mapFunctions.getAddressString(mapFunctions.getAddressFromLocation(userLocation)));
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (mLocationManager.getProviders(new Criteria(), true) == null) {
            // Throw Location Error
        }

        if (providers.isGpsEnabled())
            try {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                        mLocationRequest, this);
            } catch (SecurityException e) {
                // Throw Location Error
                Log.e("Location Error -- Chris", String.valueOf(e));
            }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // Throw Service Error
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Throw Service Error
    }

    @Override
    public void onLocationChanged(Location location) {
        if (firstRun) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),
                    location.getLongitude()),CAMERA_ZOOM_LEVEL), CAMERA_ZOOM_SPEED, null);
            initialPanStarted = true;
            firstRun = false;
        }

        userLocation = location;
    }


    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(LOCATION_LISTENER_INTERVAL);
        mLocationRequest.setFastestInterval(LOCATION_LISTENER_FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(LOCATION_SMALLEST_DISPLACEMENT);
    }

    public String getAddressOfMarker() {
        LatLng coords = projection.fromScreenLocation(new Point(getScreenWidth()/2, getScreenHeight()/2));
        Location locationOfCoords = new Location("coordinateLocation");
        locationOfCoords.setLatitude(coords.latitude);
        locationOfCoords.setLongitude(coords.longitude);
        Address addr = mapFunctions.getAddressFromLocation(locationOfCoords);
        final String text;
        if (addr == null)
            text = "Unknown Address";
        else
            text = mapFunctions.getAddressString(addr);

        return text;
    }

    public void initMapListener() {
        Runnable run = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (mMapIsTouched && pickupMode && mode == 0) {
                        Runnable updateProjectionRunnable = new Runnable() {
                            @Override
                            public void run() {
                                projection = mMap.getProjection();
                            }
                        };
                        runOnUiThread(updateProjectionRunnable);

                        final String addr = getAddressOfMarker();

                        Runnable changeTextRunnable = new Runnable() {
                            @Override
                            public void run() {
                                locationTextview.setText(addr);
                                setPinDown(false);
                            }
                        };

                        runOnUiThread(changeTextRunnable);
                    }
                    else if (!mMapIsTouched && pickupMode){
                        Runnable setPinDown = new Runnable() {
                            @Override
                            public void run() {
                                setPinDown(true);
                            }
                        };
                        runOnUiThread(setPinDown);
                    }
                    else if (mMapIsTouched && driverMode) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                enterPickupMode();
                            }
                        });
                    }

                    try { Thread.sleep(10); } catch (Exception e) { }
                }
            }
        };

        Thread markerThread = new Thread(run);
        markerThread.start();
    }

    public void setPinDown(boolean down) {
        Animation anim;

        if (pickupMode) {
            if (down && !pinDown) {
                pinDown = true;
                anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.pin_down_anim);

                anim.setFillEnabled(true);
                anim.setFillAfter(true);
                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        revealMarkerControl();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                locationMarker.startAnimation(anim);
            } else if (!down && pinDown) {
                pinDown = false;
                anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.pin_up_anim);

                anim.setFillEnabled(true);
                anim.setFillAfter(true);
                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        hideMarkerControl();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                locationMarker.startAnimation(anim);
            }
        }
    }

    void revealMarkerControl() {
        final View myView = findViewById(R.id.marker_control);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            // get the center for the clipping circle
            int cx = myView.getMeasuredWidth() / 2;
            int cy = myView.getMeasuredHeight() / 2;

            // get the final radius for the clipping circle
            int finalRadius = Math.max(myView.getWidth(), myView.getHeight()) / 2;

            if (revealAnimator != null)
                revealAnimator.cancel();
            // create the animator for this view (the start radius is zero)
            revealAnimator =
                    ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, finalRadius);

            // make the view visible and start the animation
            myView.setVisibility(View.VISIBLE);
            revealAnimator.start();
        }
        else
            myView.setVisibility(View.VISIBLE);
    }

    void hideMarkerControl() {
        final View myView = findViewById(R.id.marker_control);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            // get the center for the clipping circle
            int cx = myView.getMeasuredWidth() / 2;
            int cy = myView.getMeasuredHeight() / 2;

            // get the initial radius for the clipping circle
            int initialRadius = myView.getWidth() / 2;

            if (revealAnimator != null)
                revealAnimator.cancel();
            // create the animation (the final radius is zero)
            revealAnimator =
                    ViewAnimationUtils.createCircularReveal(myView, cx, cy, initialRadius, 0);

            // make the view invisible when the animation is done
            revealAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    myView.setVisibility(View.INVISIBLE);
                }
            });

            // start the animation
            revealAnimator.start();
        }
        else
            myView.setVisibility(View.INVISIBLE);
    }

    public float convertDpToPixel(float dp){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }

    public int getScreenWidth() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    public int getScreenHeight() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }

    public void showDriversList() {
        driversListView.setVisibility(View.VISIBLE);
        Runnable slideAnimRun = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < driversListItems.size(); i++) {
                    final int index = i;
                    final Animation slideInAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_list_item);

                    slideInAnim.setFillEnabled(true);
                    slideInAnim.setFillAfter(true);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            driversListItems.get(index).setVisibility(View.VISIBLE);
                            driversListItems.get(index).startAnimation(slideInAnim);
                        }
                    });

                    try { Thread.sleep(100); } catch (Exception e) { }
                }
            }
        };

        Thread slideAnimThread = new Thread(slideAnimRun);
        slideAnimThread.start();
    }

    public void hideDriversList() {
        Runnable slideAnimRun = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < driversListItems.size(); i++) {
                    final int index = i;
                    final Animation slideOutAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_list_item);

                    slideOutAnim.setFillEnabled(true);
                    slideOutAnim.setFillAfter(true);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (index == driversListItems.size() - 1) {
                                slideOutAnim.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        driversListView.setVisibility(View.INVISIBLE);
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                            }
                            driversListItems.get(index).setVisibility(View.VISIBLE);
                            driversListItems.get(index).startAnimation(slideOutAnim);
                        }
                    });

                    try { Thread.sleep(100); } catch (Exception e) { }
                }
            }
        };

        Thread slideAnimThread = new Thread(slideAnimRun);
        slideAnimThread.start();
    }
    public void enterPickupMode() {
        if (driverMode) {
            driverMode = false;
            pickupMode = true;

            hideDriversList();
            lightenMap();

            Animation slideOutAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_location_bar);
            slideOutAnim.setFillEnabled(true);
            slideOutAnim.setFillAfter(true);
            slideOutAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    searchImage.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.search,null));
                    String addr = getAddressOfMarker();

                    locationBar.setBackgroundColor(ContextCompat.getColor(getBaseContext(),(R.color.red)));
                    locationTextview.setTextColor(Color.WHITE);
                    locationTextview.setText(addr);

                    Animation slideInAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_location_bar);
                    slideInAnim.setFillEnabled(true);
                    slideInAnim.setFillAfter(true);

                    locationBar.startAnimation(slideInAnim);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            locationBar.startAnimation(slideOutAnim);

            revealMarkerControl();
        }
    }

    public void enterDriverMode() {
        if (pickupMode) {
            showDriversList();
            darkenMap();

            Animation slideOutAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_location_bar);
            slideOutAnim.setFillEnabled(true);
            slideOutAnim.setFillAfter(true);
            slideOutAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    searchImage.setImageDrawable(getResources().getDrawable(R.drawable.sort));
                    searchImage.setOnClickListener(new ImageView.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Search Bar here?
                            //allow user input for an address and search on the map
                        }
                    });
                    locationBar.setBackgroundColor(Color.WHITE);
                    locationTextview.setTextColor(getResources().getColor(R.color.red));
                    locationTextview.setText("Please Select a Driver");

                    Animation slideInAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_location_bar);
                    slideInAnim.setFillEnabled(true);
                    slideInAnim.setFillAfter(true);

                    locationBar.startAnimation(slideInAnim);

                    driverMode = true;
                    pickupMode = false;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            locationBar.startAnimation(slideOutAnim);

            hideMarkerControl();
        }
    }

    public void hideDriverPreview() {
        driverPreviewMode = false;
        driverMode = true;

        Animation slideOutAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_location_bar);
        slideOutAnim.setFillEnabled(true);
        slideOutAnim.setFillAfter(true);
        slideOutAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Animation slideInAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_location_bar);
                slideInAnim.setFillEnabled(true);
                slideInAnim.setFillAfter(true);

                locationBar.startAnimation(slideInAnim);
                showDriversList();
                darkenMap();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        driverPreviewLayout.startAnimation(slideOutAnim);
    }

    public void showDriverPreview(String driverId) {
        driverPreviewMode = true;
        driverMode = false;

        Driver driver = new Driver(getApplicationContext());

        for (int i = 0; i < drivers.size(); i++)
            if (drivers.get(i).getId() == driverId)
                driver = drivers.get(i);

        String tNameString = driver.getFirstName() + " " + driver.getLastName();
        driverPreviewNameTextview.setText(tNameString);
        driverPreviewPpmTextview.setText(driver.getPpmString());
        driverPreviewStarsImageview.setImageDrawable(driver.getStarsImage());

        Animation slideOutAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_location_bar);
        slideOutAnim.setFillEnabled(true);
        slideOutAnim.setFillAfter(true);
        slideOutAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                driverPreviewLayout.setVisibility(View.VISIBLE);

                Animation slideInAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_location_bar);
                slideInAnim.setFillEnabled(true);
                slideInAnim.setFillAfter(true);

                driverPreviewLayout.startAnimation(slideInAnim);
                hideDriversList();
                lightenMap();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        locationBar.startAnimation(slideOutAnim);
    }
}


