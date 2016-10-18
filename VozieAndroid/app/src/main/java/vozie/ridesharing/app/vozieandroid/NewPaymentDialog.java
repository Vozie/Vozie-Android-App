package vozie.ridesharing.app.vozieandroid;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.braintreepayments.cardform.OnCardFormSubmitListener;
import com.braintreepayments.cardform.utils.CardType;
import com.braintreepayments.cardform.view.CardForm;
import com.example.shane.vozieandroid.R;

/**************************************************
 * File Name: NewPaymentDialog.java
 *
 * Description: NewPaymentDialog extends the dialog class,
 * and is used for new payment methods to be inputted or
 * for existing payment methods to be edited. It handles
 * the UI necessary for user input, as well as adding/updating
 * the confirmed information.
 **************************************************/
public class NewPaymentDialog extends Dialog {
    private PaymentActivity mActivity;
    private Context mContext;

    private CardForm cardForm;
    private LinearLayout menuLayout;
    private NameEditText nameEditText;
    private EditText menuTitle;
    private Button addPaymentButton;

    private PaymentItem paymentItem;
    private int position;

    /*Title:                            NewPaymentDialog
    * Description:                      Class constructor to initialize dialog for the
    *                                   input of a NEW payment method.
    *
    * @param        PaymentActivity     Reference to calling PaymentActivity
    */
    public NewPaymentDialog(PaymentActivity activity) {
        super (activity, R.style.DialogTheme);
        this.mContext = activity.getApplicationContext();
        this.mActivity = activity;
    }

    /*Title:                            NewPaymentDialog
    * Description:                      Class constructor to initialize dialog for the
    *                                   EDITING of existing payment method.
    *
    * @param        PaymentActivity     Reference to calling PaymentActivity.
    * @param        PaymentItem         Reference to PaymentItem to load for editing.
    * @param        int                 Position of PaymentItem to edit in ListView.
    */
    public NewPaymentDialog(PaymentActivity activity, PaymentItem editItem, int position) {
        super (activity, R.style.DialogTheme);
        this.mContext = activity.getApplicationContext();
        this.mActivity = activity;

        this.paymentItem = editItem;
        this.position = position;
    }

    @Override
    public void onStart() {
        reinitialize();
    }

    /*Title:                            reinitialize
    * Description:                      Initializes everything in NewPaymentDialog again, so that
    *                                   while only one object needs to be created, every time
    *                                   show() is called, the text fields are reset.
    *
    * @param        PaymentActivity     Reference to calling PaymentActivity.
    */
    private void reinitialize() {
        this.setContentView(R.layout.new_payment_dialog_layout);

        cardForm = (CardForm) this.findViewById(R.id.card_form);
        menuLayout = (LinearLayout) this.findViewById(R.id.new_payment_dialog_menu_layout);
        addPaymentButton = (Button) this.findViewById(R.id.new_payment_button);
        menuTitle = (EditText) this.findViewById(R.id.new_payment_dialog_title);
        nameEditText = new NameEditText(mContext);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(50));
        params.setMargins(dpToPx(20), dpToPx(20), dpToPx(20), dpToPx(20));
        nameEditText.setLayoutParams(params);

        if (paymentItem != null) {
            // First set as blank to deal with animation handling.
            nameEditText.setText(" ");

            nameEditText.setText(paymentItem.name);
            menuTitle.setText(mActivity.getResources().getString(R.string.new_payment_dialog_title_edit));
        }

        menuLayout.addView(nameEditText, 1);

        cardForm.setRequiredFields(mActivity, true, true, true, false, "Add Card");
        cardForm.setOnCardFormSubmitListener(new OnCardFormSubmitListener() {
            @Override
            public void onCardFormSubmit() {
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                menuTitle.requestFocus();
            }
        });

        addPaymentButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cardForm.isValid() && !nameEditText.getText().equals("")) {
                    PaymentItem itemToAdd = new PaymentItem(nameEditText.getText().toString(),
                            cardForm.getExpirationMonth(),
                            cardForm.getExpirationYear(),
                            cardForm.getCardNumber(),
                            cardForm.getCvv(),
                            convertCardType(CardType.forCardNumber(cardForm.getCardNumber())));

                    if (paymentItem != null)
                        mActivity.editPaymentItem(position, itemToAdd);
                    else
                        mActivity.addPaymentItem(itemToAdd);

                    NewPaymentDialog.this.dismiss();
                }
            }
        });
    }

    /*Title:                    convertCardType
    * Description:              converts between CardForm's CardType and PaymentItem's
    *                           representation (int) of cardtypes.
    *
    * @param        CardType    CardForm's CardType to be converted.
    * @return       int         integer representation of the CardType to return.
    */
    private int convertCardType(CardType cT) {
        if (cT == CardType.AMEX)
            return PaymentItem.AMEX;
        else if (cT == CardType.DISCOVER)
            return PaymentItem.DISCOVER;
        else if (cT == CardType.MASTERCARD)
            return PaymentItem.MASTERCARD;
        else if (cT == CardType.VISA)
            return PaymentItem.VISA;
        else
            return -1;
    }

    /*Title:                dpToPx
    * Description:          converts dp unit to px unit.
    *
    * @param        int     dp to convert.
    * @return       int     pixels converted from dp.
    */
    private int dpToPx(int dp) {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }
}
