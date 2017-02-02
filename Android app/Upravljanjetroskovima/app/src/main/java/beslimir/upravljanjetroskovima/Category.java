package beslimir.upravljanjetroskovima;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by beslimir on 30.01.17..
 */

public class Category extends AppCompatActivity implements AdapterView.OnItemLongClickListener {

    public DBHelper categoryDB;
    public ListView lvCategory;
    public ArrayAdapter<ItemListView> myAdapter;
    public List<ItemListView> myCategory = new ArrayList<ItemListView>();
    public int[] categoryArrayId;
    public String[] categoryArrayName;
    public int ctgID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        categoryDB = new DBHelper(this);

        lvCategory = (ListView) findViewById(R.id.lvCategory);
        lvCategory.setOnItemLongClickListener(this);
        registerForContextMenu(lvCategory);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Category.this, CategoryInsert.class));
            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        setAdapter();
        myAdapter.clear();
        populateList();
    }

    public void setAdapter(){
        myAdapter = new MyCategoryListAdapter();
        lvCategory.setAdapter(myAdapter);
    }

    public void populateList(){
        Cursor getCategoriesNow = categoryDB.getCategories();
        if(getCategoriesNow.getCount() == 0){
            Log.i("categories", "No categories");
            return;
        }else{
            int i = 0;
            //set the length of the array
            categoryArrayId = new int[getCategoriesNow.getCount()];
            categoryArrayName = new String[getCategoriesNow.getCount()];
            while(getCategoriesNow.moveToNext()){
                categoryArrayId[i] = getCategoriesNow.getInt(getCategoriesNow.getColumnIndex("category_id"));
                categoryArrayName[i] = getCategoriesNow.getString(getCategoriesNow.getColumnIndex("category_name"));
                i++;
            }
        }
        for(int j = 0; j < categoryArrayId.length; j++){
            myCategory.add(new ItemListView(categoryArrayId[j], categoryArrayName[j]));
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        TextView txt_CategoryId = (TextView) parent.getChildAt(position - lvCategory.getFirstVisiblePosition()).findViewById(R.id.tvCategoryId);
        ctgID = Integer.parseInt(txt_CategoryId.getText().toString());

        return false;
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
            Intent i = new Intent(Category.this, CategoryUpdate.class);
            i.putExtra("ctgID", ctgID);
            startActivity(i);
        }else if(item.getItemId() == R.id.delete) {
            Toast.makeText(Category.this,  "Pipo.", Toast.LENGTH_SHORT).show();
            deleteCtg(ctgID);
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent(Category.this, MainActivity.class);
                // set the new task and clear flags
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void deleteCtg(int ctgID){
        Cursor deleteCtgNow = categoryDB.deleteCtg(ctgID);
        if (deleteCtgNow.getCount() == 0) {
            myAdapter.clear();
            populateList();
            Log.i("delete ctg", "Ctg deleted");
        }else{
            Log.i("delete ctg", "Ctg NOT deleted");
        }
    }

    private class MyCategoryListAdapter extends ArrayAdapter<ItemListView> {

        public MyCategoryListAdapter(){
            super(getApplicationContext(), R.layout.item_list_category, myCategory); //class to access all the items; layout to use; items to use
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //make sure we have a view to work with (may have been given null)
            View itemView = convertView;
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.item_list_category, parent, false);
            }
            ItemListView current = myCategory.get(position);

            //Category_id
            TextView tvCategoryId = (TextView) itemView.findViewById(R.id.tvCategoryId);
            tvCategoryId.setText("" + current.getCategory_id());
            //Category_name
            TextView tvCategoryName = (TextView) itemView.findViewById(R.id.tvCategoryName);
            tvCategoryName.setText(current.getCategory_name());

            return itemView;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(Category.this, MainActivity.class));
    }
}
