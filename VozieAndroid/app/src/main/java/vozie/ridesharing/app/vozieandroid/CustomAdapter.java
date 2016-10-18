package vozie.ridesharing.app.vozieandroid;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shane.vozieandroid.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CustomAdapter extends BaseAdapter {
    Context context;
    private LayoutInflater inflater = null;
    public List<View> rowViews;
    AssetManager am;
    CircularImageView[] driverImageViews;
    List<Driver> drivers;
    MapsActivity a;

    public CustomAdapter(Context c, List<Driver> drivers, MapsActivity a) {
        context = c;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        am = c.getAssets();
        this.drivers = drivers;
        driverImageViews = new CircularImageView[getCount()];
        rowViews = new ArrayList<>();
        this.a = a;
    }

    public float convertDpToPixel(float dp){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }

    @Override
    public int getCount() {
        return drivers.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder {
        TextView name, ppm;
        CircularImageView img;
        ImageView stars;
        String id;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.driver_list_item, parent, false);

            holder = new Holder();
            holder.name = (TextView) convertView.findViewById(R.id.driver_name);
            holder.ppm = (TextView) convertView.findViewById(R.id.ppm_textview);
            holder.img = (CircularImageView) convertView.findViewById(R.id.driver_image);
            holder.stars = (ImageView) convertView.findViewById(R.id.star_image);
            holder.ppm.setText(drivers.get(position).getPpmString());
            holder.stars.setImageDrawable(drivers.get(position).getStarsImage());
            holder.img.setImageDrawable(drivers.get(position).getDriverImage());
            holder.id = drivers.get(position).getId();
            holder.name.setText(drivers.get(position).getFirstName());
            holder.name.setTypeface(Typeface.createFromAsset(am,
                    String.format(Locale.US, "fonts/%s", "ufonts.com_microsoft-jhenghei.ttf")));

            convertView.setTag(holder);
            rowViews.add(convertView);
        }
        else
            holder = (Holder) convertView.getTag();

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                a.showDriverPreview(holder.id);
            }
        });

        ViewGroup.LayoutParams itemLayoutParams = new ViewGroup.LayoutParams((int) convertDpToPixel(200),
                (int) convertDpToPixel(60));
        convertView.setLayoutParams(itemLayoutParams);
        return convertView;
    }
}