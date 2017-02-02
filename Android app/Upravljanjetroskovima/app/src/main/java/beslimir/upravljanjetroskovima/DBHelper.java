package beslimir.upravljanjetroskovima;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by beslimir on 19.01.17..
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Troskovi.db";
    public static final int VERSION = 5;
    //TABLE_1 - Category
    public static final String TABLE_1 = "category";
    public static final String COL_11 = "category_id";
    public static final String COL_12 = "category_name";
    //TABLE_2 - Account
    public static final String TABLE_2 = "account";
    public static final String COL_21 = "account_id";
    public static final String COL_22 = "account_name";
    public static final String COL_23 = "account_value";
    //TABLE_3 - Charge
    public static final String TABLE_3 = "charge";
    public static final String COL_31 = "charge_id";
    public static final String COL_32 = "charge_price";
    public static final String COL_33 = "charge_type";
    public static final String COL_34 = "charge_date";
    public static final String COL_35 = "charge_note";
    public static final String COL_36 = "charge_account_id";
    public static final String COL_37 = "charge_category_id";
    //TABLE_4 - Currency
    public static final String TABLE_4 = "currency";
    public static final String COL_41 = "currency_id";
    public static final String COL_42 = "currency_name";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_1 + " (category_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, category_name TEXT NOT NULL)");
        db.execSQL("CREATE TABLE " + TABLE_2 + " (account_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, account_name TEXT NOT NULL, account_value TEXT NOT NULL)");
        db.execSQL("CREATE TABLE " + TABLE_3 + " (charge_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, charge_price TEXT NOT NULL, charge_type TEXT NOT NULL, charge_date TEXT NOT NULL, charge_note TEXT NOT NULL, charge_account_id INTEGER, charge_category_id INTEGER)");
        db.execSQL("CREATE TABLE " + TABLE_4 + " (currency_id INTEGER PRIMARY KEY NOT NULL, currency_name TEXT NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_1);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_2);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_3);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_4);
        onCreate(db);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //Account

    public void insertAccount(ItemListView account){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_22, account.getAccount_name());
        contentValues.put(COL_23, account.getAccount_value());
        db.insert(TABLE_2, null, contentValues);
        db.close();
    }

    public Cursor getAccounts(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor getAccountsNow = db.rawQuery("SELECT account_id, account_name, account_value FROM " + TABLE_2 + " " +
                " ORDER BY account_name asc", null);
        return getAccountsNow;
    }

    public Cursor getAccountName(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor getAccountNameNow = db.rawQuery("SELECT account_name FROM " + TABLE_2 + " " +
                " ORDER BY account_name asc", null);
        return getAccountNameNow;
    }

    public Cursor deleteAcc(int accountID){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor deleteAccNow = db.rawQuery("DELETE FROM " + TABLE_2 + " WHERE account_id = '" + accountID + "'", null);
        return deleteAccNow;
    }

    public Cursor getAccData(int accID){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor getAccDataNow = db.rawQuery("SELECT account_name, account_value FROM " + TABLE_2 + " " +
                "WHERE account_id = '" + accID + "'", null);
        return getAccDataNow;
    }

    public Cursor updateAcc(int accId, String accName, String accValue){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor updateAccNow = db.rawQuery("UPDATE " + TABLE_2 + " SET account_name = '" + accName + "'," +
                " account_value = '" + accValue + "' WHERE account_id = '" + accId + "'", null);
        return updateAccNow;
    }

    public Cursor getAccEntryByID(int accID){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor getAccEntryByIDNow = db.rawQuery("SELECT account_name, account_value " +
                " FROM " + TABLE_2 + " " +
                " WHERE account_id = '" + accID + "'", null);
        return getAccEntryByIDNow;
    }

    public Cursor getAppropriateAcc(double newValue, int account_id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor getAppropriateAccNow = db.rawQuery("UPDATE " + TABLE_2 +
                " SET account_value = '" + newValue + "'" +
                " WHERE account_id = '" + account_id + "'", null);
        return getAppropriateAccNow;
    }

    public Cursor getAccValue(int account_id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor getAccValueNow = db.rawQuery("SELECT account_value" +
                " FROM " + TABLE_2 +
                " WHERE account_id = '" + account_id + "'", null);
        return getAccValueNow;
    }

    public Cursor getAccountNameFirst(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor getAccountNameFirstNow = db.rawQuery("SELECT account_name FROM " + TABLE_2 +
                " ORDER BY account_name ASC LIMIT 1", null);
        return getAccountNameFirstNow;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //Charge

    public void insertCharge(ItemListView charge){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_32, charge.getCharge_price());
        contentValues.put(COL_33, charge.getCharge_type());
        contentValues.put(COL_34, charge.getCharge_date());
        contentValues.put(COL_35, charge.getCharge_note());
        contentValues.put(COL_36, charge.getCharge_account_id());
        contentValues.put(COL_37, charge.getCharge_category_id());
        db.insert(TABLE_3, null, contentValues);
        db.close();
    }

    public Cursor getChargeAccountId(String accName){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor getChargeAccountIdNow = db.rawQuery("SELECT account_id FROM " + TABLE_2 +
                " WHERE account_name = '" + accName + "'", null);
        return getChargeAccountIdNow;
    }

    public Cursor getChargeCategoryId(String ctgName){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor getChargeCategoryIdNow = db.rawQuery("SELECT category_id FROM " + TABLE_1 +
                " WHERE category_name = '" + ctgName + "'", null);
        return getChargeCategoryIdNow;
    }

    public Cursor getChargeCategoryName(int ctgId){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor getChargeCategoryNameNow = db.rawQuery("SELECT category_name FROM " + TABLE_1 +
                " WHERE category_id = '" + ctgId + "'", null);
        return getChargeCategoryNameNow;
    }

    public Cursor getChargeEntries(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor getChargeEntriesNow = db.rawQuery("SELECT charge_id, charge_price, charge_date, " +
                "charge_account_id, charge_category_id, charge_type FROM " + TABLE_3 + " " +
                " ORDER BY charge_id desc LIMIT 5", null);
        return getChargeEntriesNow;
    }

    public Cursor getChargeEntriesFull(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor getChargeEntriesFullNow = db.rawQuery("SELECT charge_id, charge_price, charge_date, " +
                "charge_account_id, charge_category_id, charge_type FROM " + TABLE_3 + " " +
                " ORDER BY charge_id desc", null);
        return getChargeEntriesFullNow;
    }

    public Cursor getChargeEntryByID(int chargeID){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor getChargeEntryByIDNow = db.rawQuery("SELECT charge_note, charge_date, charge_price, charge_type, charge_category_id " +
                " FROM " + TABLE_3 + " " +
                " WHERE charge_id = '" + chargeID + "'", null);
        return getChargeEntryByIDNow;
    }

    public Cursor updateCharge(String chargeType, String chargeDate, String chargeNote, String chargePrice, int chargeAccountId, int chargeCategoryId, int chargeID){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor updateEntryNow = db.rawQuery("UPDATE " + TABLE_3 + " " +
                " SET charge_type = '" + chargeType + "'" +
                ", charge_date = '" + chargeDate + "'" +
                ", charge_note = '" + chargeNote + "'" +
                ", charge_price = '" + chargePrice + "'" +
                ", charge_account_id = '" + chargeAccountId + "'" +
                ", charge_category_id = '" + chargeCategoryId + "'" +
                " WHERE charge_id = '" + chargeID + "'"
                , null);
        return updateEntryNow;
    }

    public Cursor deleteEntry(int chargeID){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor deleteEntryNow = db.rawQuery("DELETE FROM " + TABLE_3 + " WHERE charge_id = '" + chargeID + "'", null);
        return deleteEntryNow;
    }

    public Cursor getChargeData(int chargeID){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor getChargeDataNow = db.rawQuery("SELECT charge_price, charge_date, charge_note, " +
                " charge_type, charge_account_id, charge_category_id" +
                " FROM " + TABLE_3 + " " +
                " WHERE charge_id = '" + chargeID + "'", null);
        return getChargeDataNow;
    }

    public Cursor getTodayCharge(String myDate){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor getTodayChargeNow = db.rawQuery("SELECT charge_price, charge_type" +
                " FROM " + TABLE_3 + " " +
                " WHERE charge_date = '" + myDate + "'", null);
        return getTodayChargeNow;
    }

    //Overview

    //Day
    public Cursor getTotalChargeD(String myDate){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor getTotalChargeNow = db.rawQuery("SELECT SUM(charge_price)" +
                " FROM " + TABLE_3 +
                " WHERE charge_date = '" + myDate + "'" +
                " AND charge_type = 'prihod'", null);
        return getTotalChargeNow;
    }
    public Cursor getTotalChargeMinusD(String myDate){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor getTotalChargeNow = db.rawQuery("SELECT SUM(charge_price)" +
                " FROM " + TABLE_3 +
                " WHERE charge_type = 'rashod' " +
                " AND charge_date = '" + myDate + "'", null);
        return getTotalChargeNow;
    }
    public Cursor getChargeEntriesByCategoryD(String myDate){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor getChargeEntriesNow = db.rawQuery("SELECT SUM(charge_price), charge_category_id, charge_type " +
                " FROM " + TABLE_3 + " " +
                " WHERE charge_date = '" + myDate + "'" +
                " GROUP BY charge_category_id, charge_type" +
                " ORDER BY charge_price desc", null);
        return getChargeEntriesNow;
    }

    //Month
    public Cursor getTotalChargeM(String myDate1, String myDate2){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor getTotalChargeNow = db.rawQuery("SELECT SUM(charge_price)" +
                " FROM " + TABLE_3 +
                " WHERE charge_type = 'prihod'" +
                " AND (charge_date BETWEEN '" + myDate1 + "' AND '" + myDate2 + "')", null);
        return getTotalChargeNow;
    }
    public Cursor getTotalChargeMinusM(String myDate1, String myDate2){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor getTotalChargeNow = db.rawQuery("SELECT SUM(charge_price)" +
                " FROM " + TABLE_3 +
                " WHERE charge_type = 'rashod'" +
                " AND (charge_date BETWEEN '" + myDate1 + "' AND '" + myDate2 + "')", null);
        return getTotalChargeNow;
    }
    public Cursor getChargeEntriesByCategoryM(String myDate1, String myDate2){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor getChargeEntriesNow = db.rawQuery("SELECT SUM(charge_price), charge_category_id, charge_type " +
                " FROM " + TABLE_3 + " " +
                " WHERE charge_date BETWEEN '" + myDate1 + "' AND '" + myDate2 + "'" +
                " GROUP BY charge_category_id, charge_type" +
                " ORDER BY charge_price desc", null);
        return getChargeEntriesNow;
    }
    /*public Cursor getOverviewPerMonth(String currMonth){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor getOverviewPerMonthNow = db.rawQuery("SELECT charge_price" +
                " FROM " + TABLE_3 + "'", null);
        return getOverviewPerMonthNow;
    }
    public Cursor getTotalMonthCharge(String currMonth){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor getTotalMonthChargeNow = db.rawQuery("SELECT SUM(charge_price)" +
                " FROM " + TABLE_3, null);
        return getTotalMonthChargeNow;
    }
    public Cursor getTotalMonthChargeByCategory(String currMonth){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor getTotalMonthChargeByCategoryNow = db.rawQuery("SELECT SUM(charge_price)" +
                " FROM " + TABLE_3 +
                " GROUP BY charge_category_id", null);
        return getTotalMonthChargeByCategoryNow;
    }*/

    //Total
    public Cursor getOverviewTotal(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor getOverviewTotalNow = db.rawQuery("SELECT charge_price" +
                " FROM " + TABLE_3 + "'", null);
        return getOverviewTotalNow;
    }
    public Cursor getTotalCharge(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor getTotalChargeNow = db.rawQuery("SELECT SUM(charge_price)" +
                " FROM " + TABLE_3 +
                " WHERE charge_type = 'prihod'", null);
        return getTotalChargeNow;
    }
    public Cursor getTotalChargeMinus(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor getTotalChargeNow = db.rawQuery("SELECT SUM(charge_price)" +
                " FROM " + TABLE_3 +
                " WHERE charge_type = 'rashod'", null);
        return getTotalChargeNow;
    }
    public Cursor getTotalChargeByCategory(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor getTotalChargeByCategoryNow = db.rawQuery("SELECT SUM(charge_price)" +
                " FROM " + TABLE_3 +
                " GROUP BY charge_category_id", null);
        return getTotalChargeByCategoryNow;
    }
    public Cursor getChargeEntriesByCategory(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor getChargeEntriesNow = db.rawQuery("SELECT SUM(charge_price), charge_category_id, charge_type " +
                " FROM " + TABLE_3 + " " +
                " GROUP BY charge_category_id, charge_type" +
                " ORDER BY charge_price desc", null);
        return getChargeEntriesNow;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //Category

    public void insertCategoryAutomacitally(ItemListView category){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_11, category.getCategory_id());
        contentValues.put(COL_12, category.getCategory_name());
        db.insert(TABLE_1, null, contentValues);
        db.close();
    }

    public void insertCategory(ItemListView category){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_12, category.getTitle());
        db.insert(TABLE_1, null, contentValues);
        db.close();
    }

    public Cursor getCategories(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor getCategoriesNow = db.rawQuery("SELECT * FROM " + TABLE_1 +
                " ORDER BY category_name ASC", null);
        return getCategoriesNow;
    }

    public Cursor getCategoryByID(int ctgID){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor getCategoriesNow = db.rawQuery("SELECT * FROM " + TABLE_1 +
                " WHERE category_id = '" + ctgID + "'", null);
        return getCategoriesNow;
    }

    public Cursor getCategoryName(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor getCategoryNameNow = db.rawQuery("SELECT category_name FROM " + TABLE_1 +
                " ORDER BY category_name ASC", null);
        return getCategoryNameNow;
    }

    public Cursor getCategoryNameByID(int ctgID){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor getCategoryNameByIDNow = db.rawQuery("SELECT category_name " +
                "FROM " + TABLE_1 + " " +
                " WHERE category_id = '" + ctgID + "'", null);
        return getCategoryNameByIDNow;
    }

    public Cursor deleteCtg(int ctgID){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor deleteCtgNow = db.rawQuery("DELETE FROM " + TABLE_1 + " WHERE category_id = '" + ctgID + "'", null);
        return deleteCtgNow;
    }

    public Cursor updateCtg(int ctgID, String ctgName){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor updateAccNow = db.rawQuery("UPDATE " + TABLE_1 + " SET category_name = '" + ctgName + "'" +
                " WHERE category_id = '" + ctgID + "'", null);
        return updateAccNow;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //Currency

    public void insertCurrency(ItemListView category){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_41, 1);
        contentValues.put(COL_42, category.getTitle());
        db.insert(TABLE_4, null, contentValues);
        db.close();
    }

    public Cursor updateCurr(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor updateCurrNow = db.rawQuery("UPDATE " + TABLE_4 + " SET currency_name = '" + name + "'" +
                " WHERE currency_id = '" + 1 + "'", null);
        return updateCurrNow;
    }

    public Cursor getCurr(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor getCurrNow = db.rawQuery("SELECT * FROM " + TABLE_4, null);
        return getCurrNow;
    }
    public Cursor getCurrOne(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor getCurrNow = db.rawQuery("SELECT currency_name FROM " + TABLE_4, null);
        return getCurrNow;
    }

}
