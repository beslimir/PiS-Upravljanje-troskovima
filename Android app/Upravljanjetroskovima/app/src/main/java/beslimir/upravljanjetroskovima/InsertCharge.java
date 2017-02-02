package beslimir.upravljanjetroskovima;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by beslimir on 20.01.17..
 */

public class InsertCharge extends AppCompatActivity implements View.OnClickListener {

    public EditText etChargePrice, etChargeNote;
    public Button bChargeSave, bChargeCancel;
    public TextView tvChargeAccount, tvChargeDate;
    public Spinner sChargeCategory;
    public DBHelper insertChargeDB;
    public String[] arraySpinner;
    public ArrayAdapter<String> sAdapter;
    public String accValue;

    public DatePickerDialog datePickerDialog;
    public SimpleDateFormat dateFormatter;
    public int year, month, day;

    public RadioGroup radioGroup;
    public RadioButton radioButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insert_charge);

        insertChargeDB = new DBHelper(this);

        tvChargeAccount = (TextView) findViewById(R.id.tvChargeAccount);
        tvChargeAccount.setOnClickListener(this);
        tvChargeDate = (TextView) findViewById(R.id.tvChargeDate);
        etChargePrice = (EditText) findViewById(R.id.etChargePrice);
        etChargeNote = (EditText) findViewById(R.id.etChargeNote);
        sChargeCategory = (Spinner) findViewById(R.id.sChargeCategory);
        bChargeSave = (Button) findViewById(R.id.bChargeSave);
        bChargeSave.setOnClickListener(this);
        bChargeCancel = (Button) findViewById(R.id.bChargeCancel);
        bChargeCancel.setOnClickListener(this);
        radioGroup = (RadioGroup) findViewById(R.id.rGroup);

        //time and date picker
        final Calendar cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);
        tvChargeDate.setInputType(InputType.TYPE_NULL);
        tvChargeDate.setOnClickListener(this);
        dateFormatter = new SimpleDateFormat("dd.MM.yyyy.", Locale.GERMAN);
        tvChargeDate.setText(dateFormatter.format(new Date()));

        Calendar newCalendar = Calendar.getInstance();

        datePickerDialog = new DatePickerDialog(InsertCharge.this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                tvChargeDate.setText(dateFormatter.format(newDate.getTime()));
            }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        addSpinnerItems();
        checkAccounts();
    }

    public void addSpinnerItems(){
        Cursor getCategoryNameNow = insertChargeDB.getCategoryName();
        if(getCategoryNameNow.getCount() == 0){
            Log.i("categories", "No categories");
            return;
        }else{
            int i = 0;
            //set the length of the array
            arraySpinner = new String[getCategoryNameNow.getCount()];
            while(getCategoryNameNow.moveToNext()){
                arraySpinner[i] = getCategoryNameNow.getString(getCategoryNameNow.getColumnIndex("category_name"));
                i++;
            }
        }
        /*this.arraySpinner = new String[] {
                "Hrana",
                "Piće",
                "Sport",
                "Zabava",
                "Kupovina",
                "Pokloni",
                "Putovanje",
                "Prijevoz",
                "Smještaj"
        };*/
        sAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arraySpinner);
        sChargeCategory.setAdapter(sAdapter);
        sChargeCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                // TODO Auto-generated method stub
                Log.i("spinnerText", sChargeCategory.getSelectedItem().toString() + " " + position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                Toast.makeText(getBaseContext(), "You have to select a category", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //////////////



    //////////////

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bChargeSave:
                if(!etChargePrice.getText().toString().equals("") && !tvChargeDate.getText().toString().equals("") && !etChargeNote.getText().toString().equals("")) {
                    saveDataToSQLite();
                    finish();
                    System.exit(0);
                }else{
                    Toast.makeText(InsertCharge.this, "Morate popuniti sva polja", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bChargeCancel:
                finish();
                System.exit(0);
                break;
            case R.id.tvChargeAccount:
                openAccountAlertDialog();
                break;
            case R.id.tvChargeDate:
                datePickerDialog.show();
                break;
        }
    }

    public void checkAccounts(){
        Cursor getAccountNameFirstNow = insertChargeDB.getAccountNameFirst();
        if(getAccountNameFirstNow.getCount() == 0){
            tvChargeAccount.setText("Dodajte račun!");
            tvChargeAccount.setClickable(false);
            Log.i("accounts", "No accounts");
        }else{
            getAccountNameFirstNow.moveToFirst();
            tvChargeAccount.setText(getAccountNameFirstNow.getString(getAccountNameFirstNow.getColumnIndex("account_name")));
            Log.i("accounts", "Accounts");
        }
    }

    public void saveDataToSQLite(){
        // get selected radio button from radioGroup
        // find the radio button by returned id
        radioButton = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());

        ItemListView insertChargeNow = new ItemListView(etChargePrice.getText().toString(),
                radioButton.getText().toString(),
                tvChargeDate.getText().toString(),
                etChargeNote.getText().toString(),
                getChargeAccountId(tvChargeAccount.getText().toString()),
                getChargeCategoryId(sChargeCategory.getSelectedItem().toString())
                );
        insertChargeDB.insertCharge(insertChargeNow);
        updateAcc();
    }

    public void updateAcc(){
        Cursor getAccValueNow = insertChargeDB.getAccValue(getChargeAccountId(tvChargeAccount.getText().toString()));
        if(getAccValueNow.getCount() == 0){
            Log.i("account value", "No account");
            return;
        }else{
            getAccValueNow.moveToFirst();
            accValue = getAccValueNow.getString(getAccValueNow.getColumnIndex("account_value"));
            calculateAcc();
        }
    }

    public void calculateAcc(){
        double newValue = 0.0;
        if(radioButton.getText().toString().contains("prihod")) {
            newValue = Double.parseDouble(accValue) + Double.parseDouble(etChargePrice.getText().toString());
        }else{
            newValue = Double.parseDouble(accValue) - Double.parseDouble(etChargePrice.getText().toString());
        }

        Cursor getAppropriateAccNew = insertChargeDB.getAppropriateAcc(newValue, getChargeAccountId(tvChargeAccount.getText().toString()));
        if(getAppropriateAccNew.getCount() == 0){
            Log.i("account value", "No account");
            return;
        }else{
            Log.i("account value", "Account update succedded");
        }
    }

    public void openAccountAlertDialog(){
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(InsertCharge.this);
        builderSingle.setIcon(R.drawable.information);
        builderSingle.setTitle("Izaberite račun:");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(InsertCharge.this, android.R.layout.select_dialog_singlechoice);
        Cursor getAccountNameNow = insertChargeDB.getAccountName();
        if(getAccountNameNow.getCount() == 0){
            Log.i("accounts", "No accounts");
            return;
        }else{
            while(getAccountNameNow.moveToNext()){
                arrayAdapter.add(getAccountNameNow.getString(getAccountNameNow.getColumnIndex("account_name")));
            }
        }

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String strName = arrayAdapter.getItem(which);
                AlertDialog.Builder builderInner = new AlertDialog.Builder(InsertCharge.this);
                builderInner.setMessage(strName);
                builderInner.setTitle("Your Selected Item is");
                builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {
                        dialog.dismiss();
                        tvChargeAccount.setText(strName);
                    }
                });
                builderInner.show();
            }
        });
        builderSingle.show();
    }

    public int getChargeAccountId(String accName){
        Cursor getChargeAccountIdNow = insertChargeDB.getChargeAccountId(accName);
        if(getChargeAccountIdNow.getCount() == 0){
            Log.i("accounts", "No accounts");

            return -1;
        }else{
            getChargeAccountIdNow.moveToFirst();

            return getChargeAccountIdNow.getInt(getChargeAccountIdNow.getColumnIndex("account_id"));
        }
    }

    public int getChargeCategoryId(String ctgName){
        Cursor getChargeCategoryIdNow = insertChargeDB.getChargeCategoryId(ctgName);
        if(getChargeCategoryIdNow.getCount() == 0){
            Log.i("category", "No category");

            return -5;
        }else{
            getChargeCategoryIdNow.moveToLast();

            return getChargeCategoryIdNow.getInt(getChargeCategoryIdNow.getColumnIndex("category_id"));
        }
    }
}
