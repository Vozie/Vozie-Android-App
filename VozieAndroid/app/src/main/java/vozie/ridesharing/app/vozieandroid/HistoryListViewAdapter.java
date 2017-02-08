package vozie.ridesharing.app.vozieandroid;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.example.shane.vozieandroid.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HistoryListViewAdapter extends BaseSwipeAdapter {
    private HistoryActivity mActivity;
    private Context mContext;
    private List<HistoryItem> historyItems;
    private List<Drawable> images;

    public int size;

    public HistoryListViewAdapter(HistoryActivity activity, List<HistoryItem> historyItems) {
        this.mActivity = activity;
        this.mContext = activity.getApplicationContext();
        this.historyItems = historyItems;
        this.size = historyItems.size();
        this.images = new ArrayList<Drawable>();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe_item_layout;
    }

    @Override
    public View generateView(int position, final ViewGroup parent) {
        return LayoutInflater.from(mContext).inflate(R.layout.history_item, parent, false);
    }

    @Override
    public void fillValues(final int position, View convertView) {
        String lat = Double.toString(historyItems.get(position).toLocation.getLatitude());
        String lng = Double.toString(historyItems.get(position).toLocation.getLongitude());
        final String url = "http://maps.google.com/maps/api/staticmap?center=" + lat + "," + lng + "&zoom=15&size=" + "500" + "x" + "200" + "&sensor=false";
        final ImageView imgView = (ImageView) convertView.findViewById(R.id.item_map);

        TextView itemTitleTextview = (TextView) convertView.findViewById(R.id.item_title_textview);
        TextView dateTextview = (TextView) convertView.findViewById(R.id.date_textview);
        TextView distanceTextview = (TextView) convertView.findViewById(R.id.distance_textview);
        ImageView ratingImageview = (ImageView) convertView.findViewById(R.id.star_image);

        ratingImageview.setImageDrawable(getStarsImage(historyItems.get(position).tripRating));

        AssetManager am = mContext.getAssets();
        itemTitleTextview.setTypeface(Typeface.createFromAsset(am,
                String.format(Locale.US, "fonts/%s", "ufonts.com_microsoft-jhenghei.ttf")));
        dateTextview.setTypeface(Typeface.createFromAsset(am,
                String.format(Locale.US, "fonts/%s", "ufonts.com_microsoft-jhenghei.ttf")));
        distanceTextview.setTypeface(Typeface.createFromAsset(am,
                String.format(Locale.US, "fonts/%s", "ufonts.com_microsoft-jhenghei.ttf")));


        String titleText = historyItems.get(position).fromLocation + " to "
                + historyItems.get(position).toLocationString;
        itemTitleTextview.setText(titleText);
        dateTextview.setText(historyItems.get(position).date);
        distanceTextview.setText(historyItems.get(position).distance);

        Thread getMapImagesThread = new Thread(new Runnable() {
            @Override
            public void run() {
                final Bitmap bmp = getBitmapFromUrl(url);
                if (bmp != null) {
                    final Drawable bitmap = new BitmapDrawable(mContext.getResources(), bmp);
                    images.add(bitmap);

                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run(){
                            imgView.setImageBitmap(bmp);
                        }
                    });

                }
            }
        });

        getMapImagesThread.start();
    }

    // To remove
    public Drawable getStarsImage(int stars) {
        switch(stars) {
            case 1:
                return mContext.getResources().getDrawable(R.drawable.one_star);
            case 2:
                return mContext.getResources().getDrawable(R.drawable.two_star);
            case 3:
                return mContext.getResources().getDrawable(R.drawable.three_star);
            case 4:
                return mContext.getResources().getDrawable(R.drawable.four_star);
            case 5:
                return mContext.getResources().getDrawable(R.drawable.five_star);
            default:
                return null;

        }
    }

    public Bitmap getBitmapFromUrl(String urlStr) {
        try {
            URL url = new URL(urlStr);
            URLConnection conn = url.openConnection();
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = httpConn.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();

                return bitmap;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public int getCount() {
        return size;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
