package beslimir.upravljanjetroskovima;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
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
 * Created by beslimir on 29.01.17..
 */

public class ChargeUpdate extends AppCompatActivity implements View.OnClickListener {

    public int chargeID;
    public EditText etChargePrice, etChargeNote;
    public RadioGroup radioGroup;
    public RadioButton radioButton;
    public Button bChargeSave, bChargeCancel;
    public TextView tvChargeAccount, tvChargeDate;
    public Spinner sChargeCategory;
    public DBHelper mainDB;
    public String[] arraySpinner;
    public ArrayAdapter<String> sAdapter;
    public int spinnerID;
    public String accValue;
    public double newTempValue;

    public DatePickerDialog datePickerDialog;
    public SimpleDateFormat dateFormatter;
    public int year, month, day;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insert_charge);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mainDB = new DBHelper(this);

        Intent i = getIntent();
        chargeID = i.getIntExtra("chargeID", 0);
        Log.i("chargeID", "" + chargeID);

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

        datePickerDialog = new DatePickerDialog(ChargeUpdate.this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                tvChargeDate.setText(dateFormatter.format(newDate.getTime()));
            }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        getDataFromSQLite(chargeID);
        addSpinnerItems();
    }

    public void addSpinnerItems(){
        Cursor getCategoryNameNow = mainDB.getCategoryName();
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
        sAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arraySpinner);
        sChargeCategory.setAdapter(sAdapter);
        sChargeCategory.setSelection(spinnerID);
        sChargeCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                // TODO Auto-generated method stub
                Log.i("spinnerText", "" + sChargeCategory.getSelectedItemId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                Toast.makeText(getBaseContext(), "You have to select a category", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent(ChargeUpdate.this, MainActivity.class);
                // set the new task and clear flags
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void getDataFromSQLite(int chargeID){
        String radioButtonText;
        int accID;
        Cursor getChargeDataNow = mainDB.getChargeData(chargeID);
        if(getChargeDataNow.getCount() == 0){
            Log.i("charge error", "charge no data");
            return;
        }else{
            getChargeDataNow.moveToNext();
            etChargePrice.setText(getChargeDataNow.getString(getChargeDataNow.getColumnIndex("charge_price")));
            tvChargeDate.setText(getChargeDataNow.getString(getChargeDataNow.getColumnIndex("charge_date")));
            etChargeNote.setText(getChargeDataNow.getString(getChargeDataNow.getColumnIndex("charge_note")));
            radioButtonText = getChargeDataNow.getString(getChargeDataNow.getColumnIndex("charge_type"));
            radioButton = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
            if(radioButtonText.contains("prihod")) {
                radioGroup.check(R.id.rPrihod);
            }else{
                radioGroup.check(R.id.rRashod);
            }
            accID = getChargeDataNow.getInt(getChargeDataNow.getColumnIndex("charge_account_id"));
            tvChargeAccount.setText(getChargeAccountName(accID));
            spinnerID = getChargeDataNow.getInt(getChargeDataNow.getColumnIndex("charge_category_id"));
        }

        updateAcc();
    }

    public void updateAcc(){
        Cursor getAccValueNow = mainDB.getAccValue(getChargeAccountId(tvChargeAccount.getText().toString()));
        if(getAccValueNow.getCount() == 0){
            Log.i("account value", "No account");
            return;
        }else{
            getAccValueNow.moveToFirst();
            accValue = getAccValueNow.getString(getAccValueNow.getColumnIndex("account_value"));
            Log.i("account", "" + accValue);
            calculateTempAcc(accValue);
        }
    }

    public void calculateTempAcc(String myAccValue){
        radioButton = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
        Log.i("accountRadio", "" + radioButton.getText().toString());
        if(Double.parseDouble(myAccValue) > 0) {
            if(radioButton.getText().toString().contains("prihod")) {
                newTempValue = Double.parseDouble(myAccValue) - Double.parseDouble(etChargePrice.getText().toString());
            }else{
                newTempValue = Double.parseDouble(myAccValue) + Double.parseDouble(etChargePrice.getText().toString());
            }
        }else{
            if(radioButton.getText().toString().contains("prihod")) {
                newTempValue = Double.parseDouble(myAccValue) + Double.parseDouble(etChargePrice.getText().toString());
            }else{
                newTempValue = Double.parseDouble(myAccValue) - Double.parseDouble(etChargePrice.getText().toString());
            }
        }
        Log.i("accountNew", "" + newTempValue);
    }

    public void calculateAcc(double myTempValue){
        Log.i("calculateAcc", "" + radioButton.getText().toString() + ", value: " + myTempValue);
        if(radioButton.getText().toString().contains("prihod")) {
            myTempValue += Double.parseDouble(etChargePrice.getText().toString());
        }else{
            myTempValue -= Double.parseDouble(etChargePrice.getText().toString());
        }

        Cursor getAppropriateAccNew = mainDB.getAppropriateAcc(myTempValue, getChargeAccountId(tvChargeAccount.getText().toString()));
        if(getAppropriateAccNew.getCount() == 0){
            Log.i("account value", "No account");
        }else{
            Log.i("account value", "Account update succedded");
        }
    }

    public String getChargeAccountName(int accID){
        Cursor getAccDataNow = mainDB.getAccData(accID);
        if(getAccDataNow.getCount() == 0){
            Log.i("accounts", "No accounts");

            return "";
        }else{
            getAccDataNow.moveToFirst();

            return getAccDataNow.getString(getAccDataNow.getColumnIndex("account_name"));
        }
    }

    public int getChargeAccountId(String accName){
        Cursor getChargeAccountIdNow = mainDB.getChargeAccountId(accName);
        if(getChargeAccountIdNow.getCount() == 0){
            Log.i("accounts", "No accounts");

            return -1;
        }else{
            getChargeAccountIdNow.moveToFirst();

            return getChargeAccountIdNow.getInt(getChargeAccountIdNow.getColumnIndex("account_id"));
        }
    }

    public int getChargeCategoryId(String ctgName){
        Cursor getChargeCategoryIdNow = mainDB.getChargeCategoryId(ctgName);
        if(getChargeCategoryIdNow.getCount() == 0){
            Log.i("category", "No category");

            return -5;
        }else{
            getChargeCategoryIdNow.moveToLast();

            return getChargeCategoryIdNow.getInt(getChargeCategoryIdNow.getColumnIndex("category_id"));
        }
    }

    public void updateEntry(int chargeID){
        radioButton = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());

        Cursor updateChargeNow = mainDB.updateCharge(
                    radioButton.getText().toString(),
                    tvChargeDate.getText().toString(),
                    etChargeNote.getText().toString(),
                    etChargePrice.getText().toString(),
                    getChargeAccountId(tvChargeAccount.getText().toString()),
                    getChargeCategoryId(sChargeCategory.getSelectedItem().toString()),
                    chargeID
            );
        calculateAcc(newTempValue);
        if(updateChargeNow.getCount() == 0){
            Log.i("acc error", "account not updated");
        }else{
            Log.i("acc updated", "account updated");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bChargeSave:
                if(!etChargePrice.getText().toString().equals("") && !tvChargeDate.getText().toString().equals("") && !etChargeNote.getText().toString().equals("")) {
                    updateEntry(chargeID);
                    finish();
                    System.exit(0);
                }else{
                    Toast.makeText(ChargeUpdate.this, "Morate popuniti sva polja", Toast.LENGTH_SHORT).show();
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

    public void openAccountAlertDialog(){
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(ChargeUpdate.this);
        builderSingle.setIcon(R.drawable.information);
        builderSingle.setTitle("Izaberite račun:");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ChargeUpdate.this, android.R.layout.select_dialog_singlechoice);
        Cursor getAccountNameNow = mainDB.getAccountName();
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
                AlertDialog.Builder builderInner = new AlertDialog.Builder(ChargeUpdate.this);
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
}
