package ca.cmpt276.PracticalParent.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import ca.cmpt276.PracticalParent.R;
import ca.cmpt276.PracticalParent.model.Child;
import ca.cmpt276.PracticalParent.model.ChildManager;
import ca.cmpt276.PracticalParent.model.PrefConfig;

/**
 * Display the list of child.
 */
public class ChildrenActivity extends AppCompatActivity {
    private static final int ACTIVITY_RESULT_ADD = 1;
    private static final int ACTIVITY_RESULT_EDIT = 2;

    private static final String PERMISSION_WRITE_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final int REQUEST_PERMISSION_CODE = 267;
    private ChildManager children;
    private BaseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_children);
        ActionBar ab= getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        children = ChildManager.getInstance();

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(PERMISSION_WRITE_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{PERMISSION_WRITE_STORAGE}, REQUEST_PERMISSION_CODE);
            }
        }

        children.childList = PrefConfig.readListFromPref(this);
        if (children.childList == null){
            children.childList = new ArrayList<>();
        }

        setupFloatingActionButton();
        populateListView();
    }

    public static Intent makeLaunchIntent(Context context){
        return (new Intent(context, ChildrenActivity.class));
    }

    private void setupFloatingActionButton() {
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(view->{
            Intent i = ChildrenDataActivity.makeLaunchIntent(ChildrenActivity.this);
            startActivityForResult(i, ACTIVITY_RESULT_ADD);
        });
    }

    private void populateListView() {
        // Create list of children
        ListView listView = findViewById(R.id.listName);

        adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return children.getNumChildren();
            }

            @Override
            public Child getItem(int position) {
                return children.get(position);
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

                Child currentChild = children.get(position);
                //fill view
                ImageView imageView = (ImageView)itemView.findViewById(R.id.childImage);
                String path=currentChild.get_photo_path();

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
                    Intent intent = EditDeleteActivity.makeLaunchIntent(ChildrenActivity.this);
                    intent.putExtra("Position", position);

                    startActivityForResult(intent, ACTIVITY_RESULT_EDIT);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case ACTIVITY_RESULT_ADD:
                adapter.notifyDataSetChanged();
                break;
            case ACTIVITY_RESULT_EDIT:
                adapter.notifyDataSetChanged();
                break;
        }
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