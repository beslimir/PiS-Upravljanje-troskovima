package beslimir.upravljanjetroskovima;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by beslimir on 21.01.17..
 */

public class AccountOverview extends AppCompatActivity implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {

    public DBHelper accDB;
    public ListView lvAccount;
    public List<ItemListView> MyAccount = new ArrayList<ItemListView>();
    public ArrayAdapter<ItemListView> myAdapter;
    public int[] accountArrayId;
    public String[] accountArrayName, accountArrayValue;
    public int accountID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_overview);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        accDB = new DBHelper(this);

        lvAccount = (ListView) findViewById(R.id.lvAccount);
        lvAccount.setOnItemClickListener(this);
        lvAccount.setOnItemLongClickListener(this);
        registerForContextMenu(lvAccount);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AccountOverview.this, InsertAccount.class));
            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        setAdapter();
        myAdapter.clear();
        populateListView();
    }

    public void populateListView(){
        Cursor getAccountsNow = accDB.getAccounts();
        if(getAccountsNow.getCount() == 0){
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

    public void setAdapter(){
        //ArrayAdapter<ItemListView> myAdapter = new MyListAdapter();
        myAdapter = new MyListAdapter();
        lvAccount.setAdapter(myAdapter);
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
            Intent i = new Intent(AccountOverview.this, AccountUpdate.class);
            i.putExtra("accID", accountID);
            startActivity(i);
        }else if(item.getItemId() == R.id.delete) {
            Toast.makeText(AccountOverview.this,  "Pipo.", Toast.LENGTH_SHORT).show();
            deleteAcc(accountID);
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView txt_AccountId = (TextView) parent.getChildAt(position - lvAccount.getFirstVisiblePosition()).findViewById(R.id.tv_li_account_id);
        accountID = Integer.parseInt(txt_AccountId.getText().toString());
        openAlertDialog();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        TextView txt_AccountId = (TextView) parent.getChildAt(position - lvAccount.getFirstVisiblePosition()).findViewById(R.id.tv_li_account_id);
        accountID = Integer.parseInt(txt_AccountId.getText().toString());

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent(AccountOverview.this, MainActivity.class);
                // set the new task and clear flags
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void deleteAcc(int accountID){
        Cursor deleteAccNow = accDB.deleteAcc(accountID);
        if (deleteAccNow.getCount() == 0) {
            myAdapter.clear();
            populateListView();
            Log.i("delete entry", "Entry deleted");
        }else{
            Log.i("delete entry", "Entry NOT deleted");
        }
    }

    public void openAlertDialog(){
        String accName, accValue;
        Cursor getAccEntryByIDNow = accDB.getAccEntryByID(accountID);
        if(getAccEntryByIDNow.getCount() == 0){
            Log.i("acc", "No acc");
            return;
        }else{
            getAccEntryByIDNow.moveToFirst();
            accName = getAccEntryByIDNow.getString(getAccEntryByIDNow.getColumnIndex("account_name"));
            accValue = getAccEntryByIDNow.getString(getAccEntryByIDNow.getColumnIndex("account_value"));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("" + accName);
        builder.setIcon(R.drawable.information);

        // Set up the input
        LinearLayout layout = new LinearLayout(getApplicationContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        final TextView space = new TextView(getApplicationContext());
        final TextView account_value = new TextView(getApplicationContext());

        //Set some space between title and the inputs
        space.setText("");
        space.setVisibility(View.INVISIBLE);
        layout.addView(space);

        // Specify the type of input expected
        account_value.setText("Stanje računa: " + accValue + " KM");
        account_value.setPadding(50, 10, 50, 20);
        account_value.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
        account_value.setTextSize(18);
        layout.addView(account_value);

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

    public class MyListAdapter extends ArrayAdapter<ItemListView> {

        public MyListAdapter(){
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
                tv_li_account_value.setText("" + current.getAccount_value() + " KM");
            }else{
                tv_li_account_value.setTextColor(getResources().getColor(R.color.colorRed));
                tv_li_account_value.setText("" + current.getAccount_value() + " KM");
            }

            return itemView;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(AccountOverview.this, MainActivity.class));
    }
}
