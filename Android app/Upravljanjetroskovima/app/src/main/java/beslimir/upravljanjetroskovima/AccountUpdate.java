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
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by beslimir on 29.01.17..
 */

public class AccountUpdate extends AppCompatActivity {

    public EditText etAccountName, etAccountValue;
    public Button bAccountSave, bAccountCancel;
    public DBHelper insertDB;
    public int accID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insert_account);

        insertDB = new DBHelper(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        accID = i.getIntExtra("accID", 0);
        Log.i("accID", "" + accID);

        etAccountName = (EditText) findViewById(R.id.etAccountName);
        etAccountValue = (EditText) findViewById(R.id.etAccountValue);
        bAccountSave = (Button) findViewById(R.id.bAccountSave);
        bAccountSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                //System.exit(0);
                updateAcc(accID, etAccountName.getText().toString(), etAccountValue.getText().toString());
            }
        });
        bAccountCancel = (Button) findViewById(R.id.bAccountCancel);
        bAccountCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });

        getDataFromSQLite(accID);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent(AccountUpdate.this, MainActivity.class);
                // set the new task and clear flags
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void getDataFromSQLite(int accID){
        Cursor getAccDataNow = insertDB.getAccData(accID);
        if(getAccDataNow.getCount() == 0){
            Log.i("acc error", "acc no data");
            return;
        }else{
            getAccDataNow.moveToNext();
            etAccountName.setText(getAccDataNow.getString(getAccDataNow.getColumnIndex("account_name")));
            etAccountValue.setText(getAccDataNow.getString(getAccDataNow.getColumnIndex("account_value")));
        }
    }

    public void updateAcc(int myID, String myName, String myValue){
        Cursor updateAccNow = insertDB.updateAcc(myID, myName, myValue);
        if(updateAccNow.getCount() == 0){
            Log.i("acc error", "account not updated");
            return;
        }else{
            Log.i("acc updated", "account updated");
        }
    }
}
