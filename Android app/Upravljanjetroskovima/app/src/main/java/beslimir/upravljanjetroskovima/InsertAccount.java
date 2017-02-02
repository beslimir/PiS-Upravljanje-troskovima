package beslimir.upravljanjetroskovima;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by beslimir on 19.01.17..
 */

public class InsertAccount extends AppCompatActivity {

    EditText etAccountName, etAccountValue;
    Button bAccountSave, bAccountCancel;
    DBHelper insertDB;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insert_account);

        insertDB = new DBHelper(this);

        etAccountName = (EditText) findViewById(R.id.etAccountName);
        etAccountValue = (EditText) findViewById(R.id.etAccountValue);
        bAccountSave = (Button) findViewById(R.id.bAccountSave);
        bAccountSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNewAccount();
                finish();
                System.exit(0);
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
    }

    public void saveNewAccount(){
        ItemListView insertAccountNow = new ItemListView(etAccountName.getText().toString(),
                etAccountValue.getText().toString()
        );
        insertDB.insertAccount(insertAccountNow);
    }
}
