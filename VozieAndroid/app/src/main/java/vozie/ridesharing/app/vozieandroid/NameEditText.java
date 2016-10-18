package vozie.ridesharing.app.vozieandroid;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;

import com.braintreepayments.cardform.view.FloatingLabelEditText;

/**************************************************
 * File Name: NameEditText.java
 *
 * Description: NameEditText class manages the initialization
 * and handling of the Name form input on NewPaymentDialogs.
 * The reason this had to be independently developed from the
 * CardForm is due to the CardForm class not having a name flag.
 **************************************************/
public class NameEditText extends FloatingLabelEditText {
    private boolean nameLabelExists = true;

    /*Title:                NameEditText
    * Description:          Class constructor function that sets the initial settings
    *                       of the NameEditText object and its listeners.
    *
    * @param        Context Context of current application
    */
    public NameEditText(Context c) {
        super(c);

        setTextColor(Color.GRAY);
        setHint("Name");
        setText("Name");
        setTypeface(Typeface.SANS_SERIF);
        setGravity(Gravity.CENTER);

        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable e) {
                if (nameLabelExists) {
                    nameLabelExists = false;
                    setText(Character.toString(getText().charAt(0)));
                    setTextColor(Color.BLACK);
                    setSelection(1);
                }
                if (getText().toString().equals("")) {
                    setText("Name");
                    setTextColor(Color.GRAY);
                    nameLabelExists = true;
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int count, int after) {

            }
        });
    }

    @Override
    protected void onSelectionChanged(int start, int end) {
        if (nameLabelExists)
            this.setSelection(0);
    }
}
