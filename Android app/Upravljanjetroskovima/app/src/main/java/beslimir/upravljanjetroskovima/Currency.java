package beslimir.upravljanjetroskovima;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * Created by beslimir on 30.01.17..
 */

public class Currency extends AppCompatActivity {

    public RadioGroup radioGroup;
    public RadioButton radioButton;
    public Button bSave, bCancel;
    public DBHelper currDB;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.currency);

        currDB = new DBHelper(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        radioGroup = (RadioGroup) findViewById(R.id.rGroup);
        radioButton = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
        bSave = (Button) findViewById(R.id.bSave);
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNewState();
                finish();
            }
        });
        bCancel = (Button) findViewById(R.id.bCancel);
        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //For first time, insert into SQLite
        insertCurrency();

        getSavedState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent(Currency.this, MainActivity.class);
                // set the new task and clear flags
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void insertCurrency(){
        Cursor getCurrNow = currDB.getCurr();
        if(getCurrNow.getCount() == 0){
            Log.i("curr", "No curr");
            ItemListView insertCurrNow = new ItemListView(
                    "HRK"
            );
            currDB.insertCurrency(insertCurrNow);
        }else{
            getCurrNow.moveToFirst();
            Log.i("curr", "curr already inserted " +
                    getCurrNow.getString(getCurrNow.getColumnIndex("currency_name")) + " " +
                    getCurrNow.getInt(getCurrNow.getColumnIndex("currency_id"))
            );
        }
    }

    public void getSavedState(){
        String rememberTheName;
        Cursor getCurrOneNow = currDB.getCurrOne();
        if(getCurrOneNow.getCount() == 0){
            Log.i("curr", "No curr");
        }else{
            getCurrOneNow.moveToFirst();
            rememberTheName = getCurrOneNow.getString(getCurrOneNow.getColumnIndex("currency_name"));
            Log.i("currEntered", rememberTheName);
            //radioButton = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
            switch(rememberTheName){
                case "HRK":
                    radioGroup.check(R.id.rHRK);
                    break;
                case "BAM":
                    radioGroup.check(R.id.rBAM);
                    break;
                case "EUR":
                    radioGroup.check(R.id.rEUR);
                    break;
                case "GBP":
                    radioGroup.check(R.id.rGBP);
                    break;
                case "YEN":
                    radioGroup.check(R.id.rYEN);
                    break;
                case "CHF":
                    radioGroup.check(R.id.rCHF);
                    break;
                case "USD":
                    radioGroup.check(R.id.rUSD);
                    break;
                case "SEK":
                    radioGroup.check(R.id.rSEK);
                    break;
            }
        }
    }

    public void saveNewState(){
        radioButton = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
        Log.i("currNewState", "curr new " + radioButton.getText().toString());

        Cursor updateCurrNow = currDB.updateCurr(radioButton.getText().toString());
        if(updateCurrNow.getCount() == 0){
            Log.i("curr error", "curr not updated " + radioButton.getText().toString());
        }else{
            Log.i("curr updated", "curr updated");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
