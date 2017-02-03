package beslimir.upravljanjetroskovima;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by beslimir on 23.01.17..
 */

public class OverviewPerMonth extends AppCompatActivity {

    public TextView currentMonth, totalPrice, tvCurrency;
    public DBHelper monthDB;
    public Date date;
    public DateFormat dateFormat;
    public ListView lvMonth;
    public List<ItemListView> MyCharge = new ArrayList<ItemListView>();
    public ArrayAdapter<ItemListView> myAdapter;
    public int[] chargeArrayCategoryId;
    public String[] chargeArrayCategoryName, chargeArrayValue, chargeArrayType;
    public double[] progressStatus;
    public ProgressBar myProgressBar;
    public int amount = 0;
    public int counter = 0;
    public String myDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overview);

        monthDB = new DBHelper(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        currentMonth = (TextView) findViewById(R.id.currentMonth);
        tvCurrency = (TextView) findViewById(R.id.tvCurrency);
        currentMonth.setText("ukupan iznos");
        totalPrice = (TextView) findViewById(R.id.totalPrice);
        lvMonth = (ListView) findViewById(R.id.lvMonth);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getDate();
        getTotalCharge();
        getCurrency();
        getTotalChargeByCategory();
        //ListView:
        setAdapter();
        populateListView();
    }

    public void getDate(){
        //DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy.");
        Date date = new Date();
        myDate = dateFormat.format(date);
    }

    public String getSavedState(){
        String rememberTheName;
        Cursor getCurrOneNow = monthDB.getCurrOne();
        if(getCurrOneNow.getCount() == 0){
            Log.i("curr", "No curr");

            return "HRK";
        }else{
            getCurrOneNow.moveToFirst();
            rememberTheName = getCurrOneNow.getString(getCurrOneNow.getColumnIndex("currency_name"));
            Log.i("currEntered", rememberTheName);

            return rememberTheName;
        }
    }

    public void setAdapter(){
        //ArrayAdapter<ItemListView> myAdapter = new MyListAdapter();
        myAdapter = new MyChargeListAdapter();
        lvMonth.setAdapter(myAdapter);
    }

    public void populateListView(){
        Cursor getChargeEntriesByCategoryNow = monthDB.getChargeEntriesByCategoryM("01.02.2017.", "28.02.2017.");
        if(getChargeEntriesByCategoryNow.getCount() == 0){
            Log.i("charge", "No charge");
            return;
        }else{
            //getChargeEntriesNow.moveToPosition(0);
            int i = 0;

            //set the length of the array
            chargeArrayValue = new String[getChargeEntriesByCategoryNow.getCount()];
            chargeArrayCategoryId = new int[getChargeEntriesByCategoryNow.getCount()];
            chargeArrayCategoryName = new String[getChargeEntriesByCategoryNow.getCount()];
            chargeArrayType = new String[getChargeEntriesByCategoryNow.getCount()];
            while(getChargeEntriesByCategoryNow.moveToNext()){
                chargeArrayValue[i] = getChargeEntriesByCategoryNow.getString(0);
                chargeArrayCategoryId[i] = getChargeEntriesByCategoryNow.getInt(getChargeEntriesByCategoryNow.getColumnIndex("charge_category_id"));
                chargeArrayCategoryName[i] = getChargeCategoryName(chargeArrayCategoryId[i]);
                chargeArrayType[i] = getChargeEntriesByCategoryNow.getString(getChargeEntriesByCategoryNow.getColumnIndex("charge_type"));
                i++;
            }
        }
        for(int j = 0; j < chargeArrayValue.length; j++){
            if(!chargeArrayValue[j].equals(0)) {
                MyCharge.add(new ItemListView(chargeArrayCategoryId[j], chargeArrayCategoryName[j], chargeArrayValue[j], chargeArrayType[j]));
                amount += Integer.parseInt(chargeArrayValue[j]);
                counter++;
                Log.i("add", chargeArrayCategoryId[j] + " " + chargeArrayCategoryName[j] + " " + chargeArrayValue[j] + " " + chargeArrayType[j]);
            }
        }
    }

    public class MyChargeListAdapter extends ArrayAdapter<ItemListView> {

        public MyChargeListAdapter(){
            super(getApplicationContext(), R.layout.item_list_for_overview_per_month, MyCharge); //class to access all the items; layout to use; items to use
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //make sure we have a view to work with (may have been given null)
            View itemView = convertView;
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.item_list_for_overview_per_month, parent, false);
            }
            ItemListView current = MyCharge.get(position);

            //Charge_category_id
            TextView tvChargeCategoryId = (TextView) itemView.findViewById(R.id.tvCategoryId);
            tvChargeCategoryId.setText("" + current.getCharge_category_id());
            //Charge_category_name
            TextView tvChargeCategoryName = (TextView) itemView.findViewById(R.id.tvCategory);
            tvChargeCategoryName.setText("" + getChargeCategoryName(Integer.parseInt(tvChargeCategoryId.getText().toString())));
            //Charge_value
            TextView tvChargeValue = (TextView) itemView.findViewById(R.id.tvPrice);
            if(!current.getCharge_type().contains("prihod")) {
                tvChargeValue.setTextColor(getResources().getColor(R.color.colorRed));
                tvChargeValue.setText("" + current.getCharge_price() + " " + getSavedState());
            }else{
                tvChargeValue.setTextColor(getResources().getColor(R.color.colorGreen));
                tvChargeValue.setText("" + current.getCharge_price() + " " + getSavedState());
            }
            myProgressBar = (ProgressBar) itemView.findViewById(R.id.myProgressBar);
            //myProgressBar.setProgress(5);
            myProgressBar.setProgress((int)(Double.parseDouble(current.getCharge_price()) / Double.parseDouble(totalPrice.getText().toString()) * 100));
            myProgressBar.setProgressTintList(ColorStateList.valueOf(Color.BLUE));
            Log.i("progress4", "" + Double.parseDouble(current.getCharge_price()) / Double.parseDouble(totalPrice.getText().toString()) * 100);

            return itemView;
        }
    }

    public String getChargeCategoryName(int id){
        Cursor getChargeCategoryNameNow = monthDB.getChargeCategoryName(id);
        if(getChargeCategoryNameNow.getCount() == 0){
            Log.i("chargeCategory", "No charge category name");

            return "" + -7;
        }else {
            getChargeCategoryNameNow.moveToFirst();

            return "" + getChargeCategoryNameNow.getString(getChargeCategoryNameNow.getColumnIndex("category_name"));
        }
    }

    public void getTotalCharge(){
        Cursor getTotalChargeNow = monthDB.getTotalChargeM("01.02.2017", "28.02.2017.");
        Cursor getTotalChargeMinusNow = monthDB.getTotalChargeMinusM("01.02.2017", "28.02.2017.");
        getTotalChargeMinusNow.moveToFirst();
        if(getTotalChargeNow.getCount() == 0){
            totalPrice.setText("0");
            Log.i("charge", "No charge");
        }else{
            getTotalChargeNow.moveToNext();
            totalPrice.setText("" + (Double.parseDouble(getTotalChargeNow.getString(0)) - Double.parseDouble(getTotalChargeMinusNow.getString(0))));
        }
    }

    public void getCurrency(){
        Cursor getCurrOneNow = monthDB.getCurrOne();
        if(getCurrOneNow.getCount() == 0){
            Log.i("curr", "No curr");
        }else{
            getCurrOneNow.moveToFirst();
            tvCurrency.setText(getCurrOneNow.getString(getCurrOneNow.getColumnIndex("currency_name")));
        }
    }

    public void getTotalChargeByCategory(){
        Cursor getTotalChargeByCategoryNow = monthDB.getTotalChargeByCategory();
        if(getTotalChargeByCategoryNow.getCount() == 0){
            totalPrice.setText("0");
            Log.i("charge", "No charge");
        }else{
            while(getTotalChargeByCategoryNow.moveToNext()) {
                Log.i("chargee", "" + getTotalChargeByCategoryNow.getString(0));
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent(OverviewPerMonth.this, MainActivity.class);
                // set the new task and clear flags
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
