package beslimir.upravljanjetroskovima;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by beslimir on 30.01.17..
 */

public class CategoryInsert extends AppCompatActivity {

    public EditText etCtgName;
    public Button bSave, bCancel;
    public DBHelper categoryDB;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_insert);

        categoryDB = new DBHelper(this);

        etCtgName = (EditText) findViewById(R.id.etCtgName);
        bSave = (Button) findViewById(R.id.bSave);
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                //System.exit(0);
                saveNewCtg();
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
    }

    public void saveNewCtg(){
        ItemListView insertCtgNow = new ItemListView(
                etCtgName.getText().toString()
        );
        categoryDB.insertCategory(insertCtgNow);
    }
}
