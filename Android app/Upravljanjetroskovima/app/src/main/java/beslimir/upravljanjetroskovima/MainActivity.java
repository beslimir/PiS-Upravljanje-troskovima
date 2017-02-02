package beslimir.upravljanjetroskovima;

import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public TextView tvProfit, tvLoss, tvTotal, tvAccount, tvChargeLast;
    public SimpleDateFormat sdf, sdf2;
    public ListView lvAccount, lvCharge, navListView;
    public List<ItemListView> MyAccount = new ArrayList<ItemListView>();
    public List<ItemListView> MyCharge = new ArrayList<ItemListView>();
    public List<ItemListView> myCategory = new ArrayList<ItemListView>();
    public ArrayAdapter<ItemListView> myAdapter, myChargeAdapter;
    public DBHelper mainDB;
    public int[] accountArrayId, chargeArrayId, chargeArrayCategoryId;
    public String[] accountArrayName, accountArrayValue, chargeArrayCategoryName, chargeArrayDate, chargeArrayValue, chargeArrayType;
    public RelativeLayout rl2, rl3;
    public DrawerLayout drawerLayout;
    public RelativeLayout drawerPane;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    public String dayCro;
    public double chargeToday = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mainDB = new DBHelper(this);

        //navigationDrawer
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerPane = (RelativeLayout) findViewById(R.id.drawer_pane);
        navListView = (ListView) findViewById(R.id.navListView);
        drawerPane.setOnClickListener(this);

        tvAccount = (TextView) findViewById(R.id.tvAccount);
        tvChargeLast = (TextView) findViewById(R.id.tvChargeLast);

        tvProfit = (TextView) findViewById(R.id.tvProfit);
        tvLoss = (TextView) findViewById(R.id.tvLoss);
        tvTotal = (TextView) findViewById(R.id.tvTotal);
        rl2 = (RelativeLayout) findViewById(R.id.rl2);
        rl2.setOnClickListener(this);
        rl3 = (RelativeLayout) findViewById(R.id.rl3);
        rl3.setOnClickListener(this);
        lvAccount = (ListView) findViewById(R.id.lvAccount);
        lvAccount.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(MainActivity.this, AccountOverview.class));
            }
        });
        lvCharge = (ListView) findViewById(R.id.lvCharge);
        lvCharge.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(MainActivity.this, ChargeOverview.class));
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, InsertCharge.class));
            }
        });

        //For the first time running, add categories
        addCategory();

        Cursor getChargeEntriesNow = mainDB.getChargeEntries();
        if(getChargeEntriesNow.getCount() == 0){
            tvChargeLast.setText("Nemate unosa");
            Log.i("charge", "No charge");
        }else{
            getChargeEntriesNow.moveToFirst();
            Toast.makeText(MainActivity.this,
                    getChargeEntriesNow.getInt(getChargeEntriesNow.getColumnIndex("charge_id")) + " " +
                    getChargeEntriesNow.getString(getChargeEntriesNow.getColumnIndex("charge_price")) + " " +
                    getChargeEntriesNow.getString(getChargeEntriesNow.getColumnIndex("charge_date")) + " " +
                    getChargeEntriesNow.getInt(getChargeEntriesNow.getColumnIndex("charge_account_id")) + " " +
                    getChargeEntriesNow.getInt(getChargeEntriesNow.getColumnIndex("charge_category_id")) + " " +
                    getChargeEntriesNow.getInt(getChargeEntriesNow.getColumnIndex("charge_type")),
                    Toast.LENGTH_SHORT).show();
        }

        //NavDrawer
        populateNavDrawer();
        populateCategories();

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_opened, R.string.drawer_closed) {
            @Override
            public void onDrawerOpened(View drawerView) {
                // TODO Auto-generated method stub
                invalidateOptionsMenu();
                super.onDrawerOpened(drawerView);
            }
            @Override
            public void onDrawerClosed(View drawerView) {
                // TODO Auto-generated method stub
                invalidateOptionsMenu();
                super.onDrawerClosed(drawerView);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        navListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        startActivity(new Intent(getApplicationContext(), OverviewPerDay.class));
                        break;
                    case 1:
                        startActivity(new Intent(getApplicationContext(), OverviewPerMonth.class));
                        break;
                    case 2:
                        startActivity(new Intent(getApplicationContext(), Overview.class));
                        break;
                }
                drawerLayout.closeDrawer(drawerPane); //close the drawer
            }
        });

        getDateNow();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        setAccountAdapter();
        myAdapter.clear();
        populateAccountListView();
        setChargeAdapter();
        myChargeAdapter.clear();
        populateChargeListView();
        getCalculatedCharge();
        getSavedState();
    }

    public String getSavedState(){
        String rememberTheName;
        Cursor getCurrOneNow = mainDB.getCurrOne();
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

    public void getDateNow(){
        //get day
        SimpleDateFormat dayFormat = new SimpleDateFormat("E");
        Locale.setDefault(new Locale("hr", "HR"));
        Calendar calendar = Calendar.getInstance();
        dayCro = dayFormat.format(calendar.getTime());
        Log.i("date", "" + dayCro.toUpperCase()); //NED
        //get date
        sdf = new SimpleDateFormat("dd.MM.yyyy.");
        sdf2 = new SimpleDateFormat("HH:mm");
        Log.i("date_currentDate", "" + sdf.format(new Date())); //29.01.2017.
        Log.i("date_currentTime", "" + sdf2.format(new Date())); //21:28
    }

    public void getCalculatedCharge(){
        String theType;
        double plus = 0.0;
        double minus = 0.0;
        Cursor getTodayChargeNow = mainDB.getTodayCharge(sdf.format(new Date()));
        if(getTodayChargeNow.getCount() == 0){
            Log.i("charge", "No charge");
            return;
        }else{
            int i = 0;
            while(getTodayChargeNow.moveToNext()){
                theType = getTodayChargeNow.getString(getTodayChargeNow.getColumnIndex("charge_type"));
                if(theType.contains("prihod")) {
                    chargeToday += Double.parseDouble(getTodayChargeNow.getString(getTodayChargeNow.getColumnIndex("charge_price")));
                    plus += Float.parseFloat(getTodayChargeNow.getString(getTodayChargeNow.getColumnIndex("charge_price")));
                }else{
                    chargeToday -= Double.parseDouble(getTodayChargeNow.getString(getTodayChargeNow.getColumnIndex("charge_price")));
                    minus += Float.parseFloat(getTodayChargeNow.getString(getTodayChargeNow.getColumnIndex("charge_price")));
                }
                i++;
            }
        }
        Log.i("charge today", "" + chargeToday);
        Log.i("charge today", "minus: " + minus + ", plus: " + plus);
        tvTotal.setText("" + chargeToday + " " + getSavedState());
        if(chargeToday > 0){
            tvTotal.setTextColor(getResources().getColor(R.color.colorGreen));
        }else{
            tvTotal.setTextColor(getResources().getColor(R.color.colorRed));
        }
        tvProfit.setText("" + plus + " " + getSavedState());
        tvLoss.setText("" + minus + " " + getSavedState());
    }

    //ActionBar icons
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the MyHome/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        super.onOptionsItemSelected(item);
        int id = item.getItemId();

        if(actionBarDrawerToggle.onOptionsItemSelected(item))
            return true;
        else if(id == R.id.category){
            startActivity(new Intent(MainActivity.this, Category.class));
        }else if(id == R.id.currency){
            startActivity(new Intent(MainActivity.this, Currency.class));
        }
        return super.onOptionsItemSelected(item);
    }

    //the actionBar list icon
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    public void populateNavDrawer(){
        myCategory.add(new ItemListView("Dnevni"));
        myCategory.add(new ItemListView("Mjesečni"));
        myCategory.add(new ItemListView("Ukupno"));
    }

    private void populateCategories(){
        ArrayAdapter<ItemListView> myAdapter = new MyListAdapter();
        navListView.setAdapter(myAdapter);
    }

    //private class for NavigationDrawer
    private class MyListAdapter extends ArrayAdapter<ItemListView> {

        public MyListAdapter(){
            super(getApplicationContext(), R.layout.item_list_nav_drawer, myCategory); //class to access all the items; layout to use; items to use
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //make sure we have a view to work with (may have been given null)
            View itemView = convertView;
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.item_list_nav_drawer, parent, false);
            }
            ItemListView current = myCategory.get(position);

            //Title
            TextView title = (TextView) itemView.findViewById(R.id.title);
            title.setText(current.getTitle());

            return itemView;
        }
    }

    public void addCategory(){
        Cursor getCategoriesNow = mainDB.getCategories();
        if(getCategoriesNow.getCount() == 0){
            //Insert categories
            String[] ctg = {
                    "Hrana", "Piće", "Sport", "Zabava", "Kupovina", "Pokloni", "Putovanje", "Prijevoz", "Smještaj"
            };
            int[] id = {
                    0, 1, 2, 3, 4, 5, 6, 7, 8
            };
            for(int i = 0; i < ctg.length; i++) {
                Log.i("categories", ctg[i] + " " + id[i]);
                ItemListView insertCategoryNow = new ItemListView(
                        id[i],
                        ctg[i]
                );
                mainDB.insertCategoryAutomacitally(insertCategoryNow);
            }
        }else{
            Log.i("categories", "Categories already contained!");
        }
    }

    public void setAccountAdapter(){
        //ArrayAdapter<ItemListView> myAdapter = new MyListAdapter();
        myAdapter = new MyAccountListAdapter();
        lvAccount.setAdapter(myAdapter);
    }

    public void populateAccountListView(){
        Cursor getAccountsNow = mainDB.getAccounts();
        if(getAccountsNow.getCount() == 0){
            tvAccount.setText("Kliknite za dodavanje računa");
            Log.i("accounts", "No accounts");
            return;
        }else{
            int i = 0;
            //set the length of the array
            accountArrayId = new int[getAccountsNow.getCount()];
            accountArrayName = new String[getAccountsNow.getCount()];
            accountArrayValue = new String[getAccountsNow.getCount()];
            while(getAccountsNow.moveToNext()){
                accountArrayId[i] = getAccountsNow.getInt(getAccountsNow.getColumnIndex("account_id"));
                accountArrayName[i] = getAccountsNow.getString(getAccountsNow.getColumnIndex("account_name"));
                accountArrayValue[i] = getAccountsNow.getString(getAccountsNow.getColumnIndex("account_value"));
                i++;
            }
        }
        for(int j = 0; j < accountArrayId.length; j++){
            MyAccount.add(new ItemListView(accountArrayId[j], accountArrayName[j], accountArrayValue[j]));
        }
    }

    public class MyAccountListAdapter extends ArrayAdapter<ItemListView> {

        public MyAccountListAdapter(){
            super(getApplicationContext(), R.layout.item_list_account, MyAccount); //class to access all the items; layout to use; items to use
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //make sure we have a view to work with (may have been given null)
            View itemView = convertView;
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.item_list_account, parent, false);
            }
            ItemListView current = MyAccount.get(position);

            //Account_id
            TextView tv_li_account_id = (TextView) itemView.findViewById(R.id.tv_li_account_id);
            tv_li_account_id.setText("" + current.getAccount_id());
            //Account_name
            TextView tv_il_account_name = (TextView) itemView.findViewById(R.id.tv_il_account_name);
            tv_il_account_name.setText("" + current.getAccount_name());
            //Account_value
            TextView tv_li_account_value = (TextView) itemView.findViewById(R.id.tv_li_account_value);
            if(Double.parseDouble(current.getAccount_value()) > 0) {
                tv_li_account_value.setTextColor(getResources().getColor(R.color.colorGreen));
                tv_li_account_value.setText("" + current.getAccount_value() + " " + getSavedState());
            }else{
                tv_li_account_value.setTextColor(getResources().getColor(R.color.colorRed));
                tv_li_account_value.setText("" + current.getAccount_value() + " " + getSavedState());
            }

            return itemView;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void setChargeAdapter(){
        //ArrayAdapter<ItemListView> myAdapter = new MyListAdapter();
        myChargeAdapter = new MyChargeListAdapter();
        lvCharge.setAdapter(myChargeAdapter);
    }

    public void populateChargeListView(){
        Cursor getChargeEntriesNow = mainDB.getChargeEntries();
        if(getChargeEntriesNow.getCount() == 0){
            Log.i("charge", "No charge");
            return;
        }else{
            //getChargeEntriesNow.moveToPosition(0);
            int i = 0;

            //set the length of the array
            chargeArrayId = new int[getChargeEntriesNow.getCount()];
            chargeArrayCategoryId = new int[getChargeEntriesNow.getCount()];
            chargeArrayCategoryName = new String[getChargeEntriesNow.getCount()];
            chargeArrayDate = new String[getChargeEntriesNow.getCount()];
            chargeArrayValue = new String[getChargeEntriesNow.getCount()];
            chargeArrayType = new String[getChargeEntriesNow.getCount()];
            while(getChargeEntriesNow.moveToNext()){
                chargeArrayId[i] = getChargeEntriesNow.getInt(getChargeEntriesNow.getColumnIndex("charge_id"));
                chargeArrayCategoryId[i] = getChargeEntriesNow.getInt(getChargeEntriesNow.getColumnIndex("charge_category_id"));
                chargeArrayCategoryName[i] = getChargeCategoryName(chargeArrayCategoryId[i]);
                chargeArrayDate[i] = getChargeEntriesNow.getString(getChargeEntriesNow.getColumnIndex("charge_date"));
                chargeArrayValue[i] = getChargeEntriesNow.getString(getChargeEntriesNow.getColumnIndex("charge_price"));
                chargeArrayType[i] = getChargeEntriesNow.getString(getChargeEntriesNow.getColumnIndex("charge_type"));
                i++;
            }
        }
        for(int j = 0; j < chargeArrayId.length; j++){
            if(chargeArrayId[j] != 0) {
                MyCharge.add(new ItemListView(chargeArrayId[j], chargeArrayCategoryId[j], chargeArrayCategoryName[j], chargeArrayDate[j], chargeArrayValue[j], chargeArrayType[j]));
                Log.i("add", chargeArrayId[j] + " " + chargeArrayCategoryId[j] + " " + chargeArrayCategoryName[j] + " " + chargeArrayDate[j] + " " + chargeArrayValue[j] + " " + chargeArrayType[j]);
            }
        }
    }

    public class MyChargeListAdapter extends ArrayAdapter<ItemListView> {

        public MyChargeListAdapter(){
            super(getApplicationContext(), R.layout.item_list_charge, MyCharge); //class to access all the items; layout to use; items to use
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //make sure we have a view to work with (may have been given null)
            View itemView = convertView;
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.item_list_charge, parent, false);
            }
            ItemListView current = MyCharge.get(position);

            //Charge_id
            TextView tvChargeId = (TextView) itemView.findViewById(R.id.tvChargeId);
            tvChargeId.setText("" + current.getCharge_id());
            //Charge_category_id
            TextView tvChargeCategoryId = (TextView) itemView.findViewById(R.id.tvChargeCategoryId);
            tvChargeCategoryId.setText("" + current.getCharge_category_id());
            //Charge_category_name
            TextView tvChargeCategoryName = (TextView) itemView.findViewById(R.id.tvChargeCategoryName);
            tvChargeCategoryName.setText("" + getChargeCategoryName(Integer.parseInt(tvChargeCategoryId.getText().toString())));
            //Charge_date
            TextView tvChargeDate = (TextView) itemView.findViewById(R.id.tvChargeDate);
            tvChargeDate.setText("" + current.getCharge_date());
            //Charge_value
            TextView tvChargeValue = (TextView) itemView.findViewById(R.id.tvChargeValue);
            if(!current.getCharge_type().contains("prihod")) {
                tvChargeValue.setTextColor(getResources().getColor(R.color.colorRed));
                tvChargeValue.setText("" + current.getCharge_price() + " " + getSavedState());
            }else{
                tvChargeValue.setTextColor(getResources().getColor(R.color.colorGreen));
                tvChargeValue.setText("" + current.getCharge_price() + " " + getSavedState());
            }

            return itemView;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl2:
                startActivity(new Intent(MainActivity.this, AccountOverview.class));
                break;
            case R.id.rl3:
                startActivity(new Intent(MainActivity.this, ChargeOverview.class));
                break;
            case R.id.drawer_pane:
                startActivity(new Intent(MainActivity.this, Info.class));
                break;
        }
    }

    public String getChargeCategoryName(int id){
        Cursor getChargeCategoryNameNow = mainDB.getChargeCategoryName(id);
        if(getChargeCategoryNameNow.getCount() == 0){
            Log.i("chargeCategory", "No charge category name");

            return "" + -7;
        }else {
            getChargeCategoryNameNow.moveToFirst();

            return "" + getChargeCategoryNameNow.getString(getChargeCategoryNameNow.getColumnIndex("category_name"));
        }
    }

    /*public int getChargeEntries(){
        Cursor getChargeEntriesNow = mainDB.getChargeEntries();
        if(getChargeEntriesNow.getCount() == 0){
            Log.i("charge", "No charge entries");

            return -7;
        }else{
            getChargeEntriesNow.moveToLast();

            Toast.makeText(MainActivity.this, "" +
                    getChargeEntriesNow.getInt(getChargeEntriesNow.getColumnIndex("charge_id")) + " " +
                    getChargeEntriesNow.getString(getChargeEntriesNow.getColumnIndex("charge_price")) + " " +
                    getChargeEntriesNow.getString(getChargeEntriesNow.getColumnIndex("charge_date")) + " " +
                    getChargeEntriesNow.getString(getChargeEntriesNow.getColumnIndex("charge_note")) + " " +
                    getChargeEntriesNow.getInt(getChargeEntriesNow.getColumnIndex("charge_account_id")) + " " +
                    getChargeEntriesNow.getInt(getChargeEntriesNow.getColumnIndex("charge_category_id")),
                    Toast.LENGTH_SHORT).show();
            return 1;
        }
    }*/
}
