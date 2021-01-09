package ca.cmpt276.PracticalParent.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;

import ca.cmpt276.PracticalParent.R;
import ca.cmpt276.PracticalParent.model.Child;
import ca.cmpt276.PracticalParent.model.ChildManager;

/**
* Represents an activity where the user can override who is flipping the coin
**/
public class OverrideFlipCoinActivity extends AppCompatActivity {

    private ListView listView;
    private BaseAdapter adapter;
    private ChildManager children;
    public SharedPreferences sharedPref;
    private int index = 0; //index in child manager array list
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_override_flip_coin);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        children = ChildManager.getInstance();
        listView = (ListView) findViewById(R.id.childQueue);
        btn = findViewById(R.id.nobodysTurnBtn);
        populateListView();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPref = getSharedPreferences("currChildIndex", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(FlipCoinActivity.NOBODY_TURN,true);
                editor.commit();
                finish();
            }
        });
    }

    public static Intent makeLaunchIntent(Context context){
        return (new Intent(context, OverrideFlipCoinActivity.class));
    }

    private void populateListView() {
        // Create list of children


        adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return children.getNumChildren();
            }

            @Override
            public Child getItem(int position) {
                index = children.getCurrentIndex(position);
                return children.get(index);
            }

            @Override
            public long getItemId(int position) {
                return 0; // not used
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View itemView = convertView;
                if(itemView == null){
                    itemView = getLayoutInflater().inflate(R.layout.children_layout, parent, false);
                }

                index = children.getCurrentIndex(position);
                Child currentChild = children.get(index);

                //fill view
                ImageView imageView = (ImageView)itemView.findViewById(R.id.childImage);
                String path = currentChild.get_photo_path();

                // default image
                if (currentChild.child_no_pic()) {
                    imageView.setImageResource(R.drawable.default_image);
                }

                // select a different image
                else{
                    Bitmap bitmap = BitmapFactory.decodeFile(path);
                    imageView.setImageBitmap(bitmap);
                }

                TextView childNameText = (TextView) itemView.findViewById(R.id.txtChildName);
                childNameText.setText(currentChild.getName());

                return itemView;
            }
        };

        // Configure the list view
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(
                (parent, view, position, id) -> {
                    children.indexList.add(0,children.indexList.remove(position));
                    populateListView();

                    index = children.getCurrentIndex(0);
                    sharedPref = getSharedPreferences("currChildIndex", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt("CURR_CHILD_INDEX", index);
                    editor.putBoolean(FlipCoinActivity.NOBODY_TURN,false);
                    editor.commit();
                }
        );
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}