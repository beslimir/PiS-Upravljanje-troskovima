package beslimir.upravljanjetroskovima;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by beslimir on 28.01.17..
 */

public class ChargeOverview extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    public ListView lvCharge;
    public List<ItemListView> MyCharge = new ArrayList<ItemListView>();
    public ArrayAdapter<ItemListView> myChargeAdapter;
    public DBHelper mainDB;
    public int[] chargeArrayId, chargeArrayCategoryId;
    public String[] chargeArrayCategoryName, chargeArrayDate, chargeArrayValue, chargeArrayType;
    public String chargeID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.charge_overview);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mainDB = new DBHelper(this);

        lvCharge = (ListView) findViewById(R.id.lvCharge);
        lvCharge.setOnItemClickListener(this);
        lvCharge.setOnItemLongClickListener(this);
        registerForContextMenu(lvCharge);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        setChargeAdapter();
        myChargeAdapter.clear();
        populateChargeListView();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView txt_ChargeId = (TextView) parent.getChildAt(position - lvCharge.getFirstVisiblePosition()).findViewById(R.id.tvChargeId);
        String chargeID = txt_ChargeId.getText().toString();
        openAlertDialog(Integer.parseInt(chargeID));
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_charge_overview, menu);
    }

    //Update or remove
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        if(item.getItemId() == R.id.update){
            Intent i = new Intent(ChargeOverview.this, ChargeUpdate.class);
            i.putExtra("chargeID", Integer.parseInt(chargeID));
            startActivity(i);
        }else if(item.getItemId() == R.id.delete) {
            Toast.makeText(getApplicationContext(),  "Pipo.", Toast.LENGTH_SHORT).show();
            deleteEntry(Integer.parseInt(chargeID));
        }
        return super.onContextItemSelected(item);
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        TextView txt_ChargeId = (TextView) parent.getChildAt(position - lvCharge.getFirstVisiblePosition()).findViewById(R.id.tvChargeId);
        chargeID = txt_ChargeId.getText().toString();

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent(ChargeOverview.this, MainActivity.class);
                // set the new task and clear flags
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void deleteEntry(int chargeID){
        Cursor deleteEntryNow = mainDB.deleteEntry(chargeID);
        if (deleteEntryNow.getCount() == 0) {
            myChargeAdapter.clear();
            populateChargeListView();
            Log.i("delete entry", "Entry deleted");
            Toast.makeText(getApplicationContext(), "Entry deleted.", Toast.LENGTH_SHORT).show();
        }else{
            Log.i("delete entry", "Entry NOT deleted");
        }
    }

    public void openAlertDialog(int chargeID){
        //get data
        String chargeDate, chargeNote, chargePrice, chargeType;
        int ctgID;
        Cursor getChargeEntryByIDNow = mainDB.getChargeEntryByID(chargeID);
        if(getChargeEntryByIDNow.getCount() == 0){
            Log.i("charge", "No charge");
            return;
        }else{
            getChargeEntryByIDNow.moveToFirst();
            chargeNote = getChargeEntryByIDNow.getString(getChargeEntryByIDNow.getColumnIndex("charge_note"));
            chargeDate = getChargeEntryByIDNow.getString(getChargeEntryByIDNow.getColumnIndex("charge_date"));
            chargePrice = getChargeEntryByIDNow.getString(getChargeEntryByIDNow.getColumnIndex("charge_price"));
            chargeType = getChargeEntryByIDNow.getString(getChargeEntryByIDNow.getColumnIndex("charge_type"));
            ctgID = getChargeEntryByIDNow.getInt(getChargeEntryByIDNow.getColumnIndex("charge_category_id"));
        }

        //get category name
        String ctgName;
        Cursor getCategoryNameByIDNow = mainDB.getCategoryNameByID(ctgID);
        if(getCategoryNameByIDNow.getCount() == 0){
            Log.i("category", "No category");
            return;
        }else{
            getCategoryNameByIDNow.moveToFirst();
            ctgName = getCategoryNameByIDNow.getString(getCategoryNameByIDNow.getColumnIndex("category_name"));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("" + ctgName);
        builder.setIcon(R.drawable.information);

        // Set up the input
        LinearLayout layout = new LinearLayout(getApplicationContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        final TextView space = new TextView(getApplicationContext());
        final TextView charge_price = new TextView(getApplicationContext());
        final TextView charge_note = new TextView(getApplicationContext());
        final TextView charge_date = new TextView(getApplicationContext());

        //Set some space between title and the inputs
        space.setText("");
        space.setVisibility(View.INVISIBLE);
        layout.addView(space);

        //Price
        if(chargeType.contains("prihod")) {
            charge_price.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorGreen));
        }else{
            charge_price.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorRed));
        }
        charge_price.setText("Iznos: " + chargePrice);
        charge_price.setPadding(50, 10, 50, 10);
        charge_price.setTextSize(18);
        layout.addView(charge_price);

        //Note
        if(chargeType.contains("prihod")) {
            charge_note.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorGreen));
        }else{
            charge_note.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorRed));
        }
        charge_note.setText("" + chargeNote);
        charge_note.setPadding(50, 0, 50, 20);
        charge_note.setTextSize(18);
        layout.addView(charge_note);

        //Date
        if(chargeType.contains("prihod")) {
            charge_date.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorGreen));
        }else{
            charge_date.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorRed));
        }
        charge_date.setText("" + chargeDate);
        charge_date.setGravity(Gravity.RIGHT);
        charge_date.setTextSize(18);
        charge_date.setPadding(50, 10, 50, 20);
        layout.addView(charge_date);

        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void setChargeAdapter(){
        //ArrayAdapter<ItemListView> myAdapter = new MyListAdapter();
        myChargeAdapter = new MyChargeListAdapter();
        lvCharge.setAdapter(myChargeAdapter);
    }

    public void populateChargeListView(){
        Cursor getChargeEntriesFullNow = mainDB.getChargeEntriesFull();
        if(getChargeEntriesFullNow.getCount() == 0){
            Log.i("charge", "No charge");
            return;
        }else{
            //getChargeEntriesNow.moveToFirst();
            int i = 0;

            //set the length of the array
            chargeArrayId = new int[getChargeEntriesFullNow.getCount()];
            chargeArrayCategoryId = new int[getChargeEntriesFullNow.getCount()];
            chargeArrayCategoryName = new String[getChargeEntriesFullNow.getCount()];
            chargeArrayDate = new String[getChargeEntriesFullNow.getCount()];
            chargeArrayValue = new String[getChargeEntriesFullNow.getCount()];
            chargeArrayType = new String[getChargeEntriesFullNow.getCount()];
            while(getChargeEntriesFullNow.moveToNext()){
                chargeArrayId[i] = getChargeEntriesFullNow.getInt(getChargeEntriesFullNow.getColumnIndex("charge_id"));
                chargeArrayCategoryId[i] = getChargeEntriesFullNow.getInt(getChargeEntriesFullNow.getColumnIndex("charge_category_id"));
                chargeArrayCategoryName[i] = getChargeCategoryName(chargeArrayCategoryId[i]);
                chargeArrayDate[i] = getChargeEntriesFullNow.getString(getChargeEntriesFullNow.getColumnIndex("charge_date"));
                chargeArrayValue[i] = getChargeEntriesFullNow.getString(getChargeEntriesFullNow.getColumnIndex("charge_price"));
                chargeArrayType[i] = getChargeEntriesFullNow.getString(getChargeEntriesFullNow.getColumnIndex("charge_type"));
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
                tvChargeValue.setText("" + current.getCharge_price() + " KM");
            }else{
                tvChargeValue.setTextColor(getResources().getColor(R.color.colorGreen));
                tvChargeValue.setText("" + current.getCharge_price() + " KM");
            }

            return itemView;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
