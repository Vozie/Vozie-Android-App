package vozie.ridesharing.app.vozieandroid;

/**************************************************
 * File Name: PaymentItem.java
 *
 * Description: The PaymentItem class encapsulates
 * the data necessary in storing a user's payment
 * information.
 **************************************************/
public class PaymentItem {
    final static int VISA = 0;
    final static int MASTERCARD = 1;
    final static int DISCOVER = 2;
    final static int AMEX = 3;

    public String name, month, year, card, cvv;
    public int type;

    public PaymentItem(String name, String month, String year, String card, String cvv, int type) {
        this.name = name;
        this.month = month;
        this.year = year;
        this.card = card;
        this.cvv = cvv;
        this.type = type;
    }

    /*Title:                getCardFormatted
    * Description:          Gets the card number in a formatted manner
    *                       (card type dependent)
    *
    * @return        String String representing formatted card number.
    */
    public String getCardFormatted() {
        if (type == AMEX) {
            return card.substring(0, 5) + " - " +
                    card.substring(4, 10) + " - " +
                    card.substring(10, 15);
        }
        else {
            return card.substring(0, 4) + " - " +
                    card.substring(4, 8) + " - " +
                    card.substring(8, 12) + " - " +
                    card.substring(12, 16);
        }
    }
}
