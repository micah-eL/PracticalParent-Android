package ca.cmpt276.PracticalParent.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import ca.cmpt276.PracticalParent.R;
import ca.cmpt276.PracticalParent.model.ChildManager;
import ca.cmpt276.PracticalParent.model.PrefConfig;
import ca.cmpt276.PracticalParent.model.Task;
import ca.cmpt276.PracticalParent.model.TaskManager;

/**
 * Add and Save a task to the list view.
 */
public class AddTaskActivity extends AppCompatActivity {

    private TaskManager taskManager;
    private ChildManager childManager;

    private String CURR_CHILD = "YOU HAVE NO CHILDREN SAVED";
    public SharedPreferences sharedPref;
    private int index = 0; //index in child manager arraylist

    EditText taskName;
    Button cancelAdd;
    Button saveTask;

    public static Intent makeLaunchIntent(Context context) {
        return (new Intent(context, AddTaskActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task_activity);
        ActionBar ab= getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        taskManager = TaskManager.getInstance();
        childManager =  ChildManager.getInstance();

        childManager.childList = PrefConfig.readListFromPref(this);
        if (childManager.childList == null){
            childManager.childList = new ArrayList<>();
        }


        taskName = (EditText) findViewById(R.id.enter_task_name);
        cancelAdd = (Button) findViewById(R.id.cancel_add_btn);
        saveTask = (Button) findViewById(R.id.saving_task_btn);

        sharedPref = getSharedPreferences("currChildIndex", 0);
        index = sharedPref.getInt("CURR_CHILD_INDEX", 0);

        //Needed if user deletes children at location < index location
        if ((childManager.getNumChildren() == 0) || index >= childManager.getNumChildren()||index<0){
            index = 0;
        }

        if (childManager.getNumChildren() != 0){
            CURR_CHILD = childManager.getInfo(index);
        }

        setupSaveButton();
        setupCancelButton();
    }

    private void setupCancelButton() {
        cancelAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context =  getApplicationContext();
                Toast.makeText(context,getString(R.string.canceled_task),Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void setupSaveButton() {
        saveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(AddTaskActivity.this, getString(R.string.task_add), Toast.LENGTH_SHORT).show();

                // Extract data from the screen
                String enteredTaskName = taskName.getText().toString();

                Task task = new Task(enteredTaskName);
                task.setTaskNameForPref(task.getTaskName());
                taskManager = TaskManager.getInstance();

                // Add the new child to the childrenList
                taskManager.add(task);

                PrefConfig.writeTaskListInPref(getApplicationContext(), taskManager.taskList);

                finish();

            }
        });
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