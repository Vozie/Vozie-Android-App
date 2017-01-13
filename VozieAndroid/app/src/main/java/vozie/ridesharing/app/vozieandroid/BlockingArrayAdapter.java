package vozie.ridesharing.app.vozieandroid;


import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import java.util.ArrayList;

public class BlockingArrayAdapter
        extends ArrayAdapter<String>
{
    private Filter filter = new BlockingFilter();
    public ArrayList<String> items;

    @Override
    public Filter getFilter() {
        return filter;
    }

    public BlockingArrayAdapter(Context context, int textViewResourceId,
                                ArrayList<String> items) {
        super(context, textViewResourceId, items);
        this.items = items;
    }

    class BlockingFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence arg0) {
            FilterResults result = new FilterResults();
            result.values = items;
            result.count = items.size();
            return result;
        }

        @Override
        protected void publishResults(CharSequence arg0, FilterResults arg1) {
            notifyDataSetChanged();
        }
    }
}
