package beslimir.upravljanjetroskovima;

/**
 * Created by beslimir on 19.01.17..
 */

public class ItemListView {
    //Account
    String account_name, account_value;
    int account_id;

    public ItemListView(int account_id, String account_name, String account_value) {
        this.account_name = account_name;
        this.account_value = account_value;
        this.account_id = account_id;
    }

    public ItemListView(String account_name, String account_value) {
        this.account_name = account_name;
        this.account_value = account_value;
    }

    public String getAccount_name() {
        return account_name;
    }

    public String getAccount_value() {
        return account_value;
    }

    public int getAccount_id() {
        return account_id;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //Charge
    String charge_price, charge_date, charge_note, charge_type;
    int charge_id, charge_account_id, charge_category_id;

    public ItemListView(int charge_category_id, int charge_account_id, int charge_id, String charge_note, String charge_date, String charge_price, String charge_type) {
        this.charge_category_id = charge_category_id;
        this.charge_account_id = charge_account_id;
        this.charge_id = charge_id;
        this.charge_note = charge_note;
        this.charge_date = charge_date;
        this.charge_price = charge_price;
        this.charge_type = charge_type;
    }

    public ItemListView(String charge_price, String charge_type, String charge_date, String charge_note, int charge_account_id, int charge_category_id) {
        this.charge_type = charge_type;
        this.charge_price = charge_price;
        this.charge_date = charge_date;
        this.charge_note = charge_note;
        this.charge_account_id = charge_account_id;
        this.charge_category_id = charge_category_id;
    }

    public ItemListView(int charge_id, int charge_category_id, String charge_date, String charge_price) {
        this.charge_id = charge_id;
        this.charge_category_id = charge_category_id;
        this.charge_date = charge_date;
        this.charge_price = charge_price;
    }

    public ItemListView(int charge_id, int charge_category_id, String category_name, String charge_date, String charge_price) {
        this.charge_id = charge_id;
        this.charge_category_id = charge_category_id;
        this.category_name = category_name;
        this.charge_date = charge_date;
        this.charge_price = charge_price;
    }

    public ItemListView(int charge_id, int charge_category_id, String category_name, String charge_date, String charge_price, String charge_type) {
        this.charge_id = charge_id;
        this.charge_category_id = charge_category_id;
        this.category_name = category_name;
        this.charge_date = charge_date;
        this.charge_price = charge_price;
        this.charge_type = charge_type;
    }

    public String getCharge_price() {
        return charge_price;
    }

    public String getCharge_date() {
        return charge_date;
    }

    public int getCharge_id() {
        return charge_id;
    }

    public String getCharge_note() {
        return charge_note;
    }

    public int getCharge_account_id() {
        return charge_account_id;
    }

    public int getCharge_category_id() {
        return charge_category_id;
    }

    public String getCharge_type() {
        return charge_type;
    }

    public ItemListView(int charge_category_id, String category_name, String charge_price, String charge_type) {
        this.charge_category_id = charge_category_id;
        this.category_name = category_name;
        this.charge_price = charge_price;
        this.charge_type = charge_type;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //Category

    String category_name;
    int category_id;

    public ItemListView(int category_id, String category_name) {
        this.category_id = category_id;
        this.category_name = category_name;
    }

    /*public ItemListView(String category_name) {
        this.category_name = category_name;
    }*/

    public int getCategory_id() {
        return category_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //Navigation drawer

    String title;

    public ItemListView(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
