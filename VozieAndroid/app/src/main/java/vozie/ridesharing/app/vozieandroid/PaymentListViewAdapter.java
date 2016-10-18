package vozie.ridesharing.app.vozieandroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.example.shane.vozieandroid.R;

import java.util.List;

public class PaymentListViewAdapter extends BaseSwipeAdapter {
    private PaymentActivity mActivity;
    private Context mContext;
    private List<PaymentItem> PaymentItems;

    public int size;

    public PaymentListViewAdapter(PaymentActivity activity, List<PaymentItem> paymentItems) {
        this.mActivity = activity;
        this.mContext = activity.getApplicationContext();
        this.PaymentItems = paymentItems;
        this.size = paymentItems.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe_item_layout;
    }

    @Override
    public View generateView(int position, final ViewGroup parent) {
        return LayoutInflater.from(mContext).inflate(R.layout.payment_item, parent, false);
    }

    @Override
    public void fillValues(final int position, View convertView) {
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView date = (TextView) convertView.findViewById(R.id.date);
        TextView cardNumber = (TextView) convertView.findViewById(R.id.card_number);
        ImageView cardImage = (ImageView) convertView.findViewById(R.id.card_image);

        // / Initialize edit and delete imageviews
        final ImageView editImg = (ImageView) convertView.findViewById(R.id.edit_image);
        final ImageView deleteImg = (ImageView) convertView.findViewById(R.id.delete_image);

        editImg.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewPaymentDialog dialog = new NewPaymentDialog(mActivity, PaymentItems.get(position), position);
                dialog.show();
            }
        });

        deleteImg.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.removePaymentItem(position);
            }
        });

        name.setText(PaymentItems.get(position).name);
        date.setText(PaymentItems.get(position).month + "/"
                + PaymentItems.get(position).year);
        cardNumber.setText(PaymentItems.get(position).getCardFormatted());

        switch (PaymentItems.get(position).type) {
            case PaymentItem.VISA:
                cardImage.setImageResource(R.drawable.visa);
                break;
            case PaymentItem.MASTERCARD:
                cardImage.setImageResource(R.drawable.mastercard);
                break;
            case PaymentItem.DISCOVER:
                cardImage.setImageResource(R.drawable.discover);
                break;
            case PaymentItem.AMEX:
                cardImage.setImageResource(R.drawable.amex);
                break;
        }
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
