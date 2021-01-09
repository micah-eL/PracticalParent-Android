package ca.cmpt276.PracticalParent.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import ca.cmpt276.PracticalParent.R;
import ca.cmpt276.PracticalParent.model.ChildManager;
import ca.cmpt276.PracticalParent.model.PrefConfig;
import ca.cmpt276.PracticalParent.model.Task;
import ca.cmpt276.PracticalParent.model.TaskManager;

/**
 * Display the list of task.
 */
public class TaskActivity extends AppCompatActivity {

    private static final int ACTIVITY_RESULT_ADD = 1;
    private static final int ACTIVITY_RESULT_EDIT = 2;


    private TaskManager taskManager;
    private ChildManager childManager;
    private String tempKeyName;
    private SharedPreferences sharedPref;
    private int index;
    private String noChildren;
    private String yourTurn;

    ListView listView;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        ActionBar ab= getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        taskManager = TaskManager.getInstance();
        childManager = ChildManager.getInstance();

        taskManager.taskList = PrefConfig.readTaskListFromPref(this);
        if (taskManager.taskList == null){
            taskManager.taskList = new ArrayList<>();
        }

        childManager.childList = PrefConfig.readListFromPref(this);

        if (childManager.childList == null){
            childManager.childList = new ArrayList<>();
        }

        setupFloatingActionButton();
        populateListView();
        registerClickCallBack();
    }

    public static Intent makeLaunchIntent(Context context){
        return (new Intent(context, TaskActivity.class));
    }

    private void setupFloatingActionButton() {
        FloatingActionButton fabTask = findViewById(R.id.FAB_task);
        fabTask.setOnClickListener(view->{
            Intent i = AddTaskActivity.makeLaunchIntent(TaskActivity.this);
            startActivityForResult(i, ACTIVITY_RESULT_ADD);
        });
    }

    private void populateListView(){
        ArrayAdapter<Task> adapter = new MyListAdapter();
        listView = (ListView) findViewById(R.id.task_list);
        listView.setAdapter(adapter);
    }


    private class MyListAdapter extends ArrayAdapter<Task> {
        public MyListAdapter(){
            super(TaskActivity.this, R.layout.task_layout, taskManager.taskList);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View itemView = convertView;
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.task_layout, parent, false);
            }

            //find what to work with
            Task currentTask = taskManager.get(position);

            tempKeyName = currentTask.getCurrentTaskKey();
            sharedPref = getSharedPreferences("currentChildIndex", 0);
            index = sharedPref.getInt(tempKeyName, 0);

            imageView = (ImageView) itemView.findViewById(R.id.child_photo_tasks);

            if(childManager.getNumChildren() != 0) {
                TextView nextChild = (TextView) itemView.findViewById(R.id.next_child);
                yourTurn = getString(R.string.your_turn, childManager.getInfo(index));
                String path = childManager.get(index).get_photo_path();

                if (childManager.get(index).child_no_pic()) {
                    imageView.setImageResource(R.drawable.default_image);
                }

                else{
                    Bitmap bitmap = BitmapFactory.decodeFile(path);
                    imageView.setImageBitmap(bitmap);
                }

                nextChild.setText(yourTurn);

            } else{
                TextView nextChild = (TextView) itemView.findViewById(R.id.next_child);
                noChildren = getString(R.string.no_children_configured);
                imageView.setImageResource(R.drawable.default_image);
                nextChild.setText(noChildren);
            }

            TextView taskNameText = (TextView) itemView.findViewById(R.id.task_name);
            taskNameText.setText(currentTask.getTaskName());

            return itemView;
        }
    }

    private void registerClickCallBack(){
        ListView listView = (ListView) findViewById(R.id.task_list);
        listView.setOnItemClickListener(
                (parent, view, position, id) -> {
                    Intent intent = SelectedTaskActivity.makeLaunchIntent(TaskActivity.this);
                    intent.putExtra("Position", position);

                    startActivityForResult(intent, ACTIVITY_RESULT_EDIT);
                }
        );

    }

    @Override
    protected void onStart() {
        super.onStart();
        populateListView();
    }

    // Up button
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