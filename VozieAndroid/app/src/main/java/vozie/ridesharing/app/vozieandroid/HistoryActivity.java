package vozie.ridesharing.app.vozieandroid;


import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.util.Attributes;
import com.example.shane.vozieandroid.R;

import java.util.ArrayList;
import java.util.List;


public class HistoryActivity extends AppCompatActivity {
    private ListView mListView;
    private List<HistoryItem> items;
    private HistoryListViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.MyAppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Initialize menu bar and navigation drawer
        MenuBarHandler menuBarHandler = new MenuBarHandler(this);
        menuBarHandler.init();

        // Initialize list of payment items and set adapter.
        mListView = (ListView) findViewById(R.id.main_list);
        items = new ArrayList<HistoryItem>();
        mAdapter = new HistoryListViewAdapter(this, items);
        mListView.setAdapter(mAdapter);
        mAdapter.setMode(Attributes.Mode.Single);


        // Initialize listview listener
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((SwipeLayout) (mListView.getChildAt(position - mListView.getFirstVisiblePosition()))).open(true);
            }
        });


        /* Test Code
        Location myLocation = new Location("");
        myLocation.setLatitude(48.858235);
        myLocation.setLongitude(2.294571);

        addHistoryItem(new HistoryItem("3002 Ironside Court", "UC San Diego", "400 miles", "$300.00", "10/25/2017", "0000123", myLocation, 4));
        addHistoryItem(new HistoryItem("3002 Ironside Court", "UC San Diego", "400 miles", "$300.00", "10/25/2017", "0000123", myLocation, 4));
        addHistoryItem(new HistoryItem("3002 Ironside Court", "UC San Diego", "400 miles", "$300.00", "10/25/2017", "0000123", myLocation, 4));
        addHistoryItem(new HistoryItem("3002 Ironside Court", "UC San Diego", "400 miles", "$300.00", "10/25/2017", "0000123", myLocation, 4));
        */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*Title:                    addPaymentItem
    * Description:              Adds a PaymentItem to the current ListView and updates adapter.
    *
    * @param        PaymentItem Item to add to the current ListView.
    */
    public void addHistoryItem(HistoryItem item) {
        if (item != null) {
            items.add(item);
            mAdapter.size++;
            mAdapter.notifyDataSetChanged();
        }
    }

    /*Title:                    removePaymentItem
    * Description:              Removes a PaymentItem to the current ListView and updates adapter.
    *
    * @param        PaymentItem Item to remove from the current ListView.
    */
    public void removeHistory(int index) {
        if (index >= 0 && index < mAdapter.size) {
            items.remove(index);
            mAdapter.size--;
            mAdapter.notifyDataSetChanged();
        }
    }
}