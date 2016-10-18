package vozie.ridesharing.app.vozieandroid;

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


public class PaymentActivity extends AppCompatActivity {
    private ListView mListView;
    private List<PaymentItem> items;
    private PaymentListViewAdapter mAdapter;
    private NewPaymentDialog newPaymentDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.MyAppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        newPaymentDialog = new NewPaymentDialog(PaymentActivity.this);

        // Initialize menu bar and navigation drawer
        MenuBarHandler menuBarHandler = new MenuBarHandler(this);
        menuBarHandler.init();

        // Initialize list of payment items and set adapter.
        mListView = (ListView) findViewById(R.id.main_list);
        items = new ArrayList<PaymentItem>();
        mAdapter = new PaymentListViewAdapter(this, items);
        mListView.setAdapter(mAdapter);
        mAdapter.setMode(Attributes.Mode.Single);


        // Initialize listview listener
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((SwipeLayout) (mListView.getChildAt(position - mListView.getFirstVisiblePosition()))).open(true);
            }
        });

        // Initialize add new payment button to activate new payment dialog.
        findViewById(R.id.new_payment_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    newPaymentDialog.show();
                }
        });

        addPaymentItem(new PaymentItem("Shane Duffy", "04", "20", "1234512345612345", "0918", PaymentItem.AMEX));
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
    public void addPaymentItem(PaymentItem item) {
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
    public void removePaymentItem(int index) {
        if (index >= 0 && index < mAdapter.size) {
            items.remove(index);
            mAdapter.size--;
            mAdapter.notifyDataSetChanged();
        }
    }

    /*Title:                    editPaymentItem
    * Description:              Edits a PaymentItem to the current ListView and updates adapter.
    *
    * @param        int         Index of item to edit in current ListView.
    * @param        PaymentItem Item to edit in the current ListView.
    */
    public void editPaymentItem(int index, PaymentItem updatedItem) {
        if (index >= 0 && index < mAdapter.size && updatedItem != null) {
            items.set(index, updatedItem);
            mAdapter.notifyDataSetChanged();
        }
    }
}
