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

/**
 * Created by beslimir on 30.01.17..
 */

public class CategoryUpdate extends AppCompatActivity {

    public int ctgID;
    public DBHelper categoryDB;
    public EditText etCtgName;
    public Button bSave, bCancel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_insert);

        categoryDB = new DBHelper(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        ctgID = i.getIntExtra("ctgID", 0);
        Log.i("ctgID", "" + ctgID);

        etCtgName = (EditText) findViewById(R.id.etCtgName);
        bSave = (Button) findViewById(R.id.bSave);
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                //System.exit(0);
                updateAcc(ctgID, etCtgName.getText().toString());
            }
        });
        bCancel = (Button) findViewById(R.id.bCancel);
        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });

        getDataFromSQLite(ctgID);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent(CategoryUpdate.this, MainActivity.class);
                // set the new task and clear flags
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void getDataFromSQLite(int ctgID){
        Cursor getCategoryByIDNow = categoryDB.getCategoryByID(ctgID);
        if(getCategoryByIDNow.getCount() == 0){
            Log.i("ctg error", "ctg no data");
            return;
        }else{
            getCategoryByIDNow.moveToNext();
            etCtgName.setText(getCategoryByIDNow.getString(getCategoryByIDNow.getColumnIndex("category_name")));
        }
    }

    public void updateAcc(int myID, String myName){
        Cursor updateCtgNow = categoryDB.updateCtg(myID, myName);
        if(updateCtgNow.getCount() == 0){
            Log.i("ctg error", "ctg not updated");
            return;
        }else{
            Log.i("ctg updated", "ctg updated");
        }
    }
}
