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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ca.cmpt276.PracticalParent.R;
import ca.cmpt276.PracticalParent.model.ChildManager;
import ca.cmpt276.PracticalParent.model.PrefConfig;
import ca.cmpt276.PracticalParent.model.Task;
import ca.cmpt276.PracticalParent.model.TaskManager;

/**
 * Edit, Delete a task from the list view.
 */
public class SelectedTaskActivity extends AppCompatActivity {

    private TaskManager taskManager;
    private ChildManager childManager;
    private String tempKeyName;
    private SharedPreferences sharedPref;
    private int index;

    ImageView imageView;
    private TextView selectedTaskName, currentChildName;
    private EditText textTaskName;
    private Button editButton, cancelButton, confirmTurnButton, deleteButton;

    public static Intent makeLaunchIntent(Context context) {
        return (new Intent(context, SelectedTaskActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_task);
        ActionBar ab= getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        taskManager = TaskManager.getInstance();
        childManager = ChildManager.getInstance();

        childManager.childList = PrefConfig.readListFromPref(this);

        if (childManager.childList == null){
            childManager.childList = new ArrayList<>();
        }


        currentChildName = findViewById(R.id.current_child_task);
        selectedTaskName = findViewById(R.id.task_name_title);
        textTaskName = findViewById(R.id.etxt_task_name);
        editButton = findViewById(R.id.edit_task_btn);
        cancelButton = findViewById(R.id.cancel_btn_task);
        confirmTurnButton = findViewById(R.id.confirm_child_btn);
        deleteButton = findViewById(R.id.delete_btn_task);
        imageView = (ImageView) findViewById(R.id.task_edit_image_view);

        Intent intent = getIntent();
        int selectedTaskIndex = intent.getIntExtra("Position", 0);
        String information = taskManager.getInfo(selectedTaskIndex);

        //Get the unique key for each task that will each store what the current index is
        tempKeyName = taskManager.get(selectedTaskIndex).getCurrentTaskKey();
        sharedPref = getSharedPreferences("currentChildIndex", 0);
        index = sharedPref.getInt(tempKeyName, 0);

        //Display task name and the current child name
        String selectedTask = getString(R.string.selected_task_is, information);
        selectedTaskName.setText(selectedTask);

        if(childManager.getNumChildren() != 0) {
            String yourTurn = getString(R.string.your_turn, childManager.getInfo(index));
            currentChildName.setText(yourTurn);

            String path = childManager.get(index).get_photo_path();// task get index

            if (childManager.get(index).child_no_pic()) {
                imageView.setImageResource(R.drawable.default_image);
            }

            else{
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                imageView.setImageBitmap(bitmap);
            }

        } else{
            String noChildren = getString(R.string.no_children_configured);
            currentChildName.setText(noChildren);
            imageView.setImageResource(R.drawable.default_image);
        }

        setupEditButton();
        setupCancelButton();
        setupDeleteButton();
        setupConfirmButton();
    }


    private void setupConfirmButton() {
        confirmTurnButton.setOnClickListener((view)->{

            if (childManager.getNumChildren() == 0){
                Toast.makeText(this, getString(R.string.no_children_saved), Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, getString(R.string.had_turn_task, childManager.getInfo(index)), Toast.LENGTH_SHORT).show();

                if (index == childManager.getNumChildren() - 1){
                    index = 0;
                }
                else{
                    index++;
                }

                sharedPref = getSharedPreferences("currentChildIndex", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(tempKeyName, index);
                editor.commit();
            }

            finish();
        });
    }

    private void setupDeleteButton() {
        deleteButton.setOnClickListener((view)->{

            Context context =  getApplicationContext();

            // Get the intent that started us to find the parameter (extra)
            Intent intent = getIntent();
            int selectedTaskIndex = intent.getIntExtra("Position",-1);

            SharedPreferences sharedPref = getSharedPreferences("currTaskIndex", Context.MODE_PRIVATE);
            int indexTask = sharedPref.getInt("CURR_TASK_INDEX", 0);
            if(indexTask >= selectedTaskIndex && indexTask!=0){
                indexTask--;
            }
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("CURR_TASK_INDEX", indexTask);
            editor.commit();

            Task selectedTask = taskManager.get(selectedTaskIndex);
            String removedTaskName = taskManager.getInfo(selectedTaskIndex);
            // Remove the task from the childrenList
            taskManager.remove(selectedTask);
            PrefConfig.writeTaskListInPref(getApplicationContext(), taskManager.taskList);

            String message = removedTaskName + " " + getString(R.string.deleted_task);
            Toast.makeText(context,message,Toast.LENGTH_SHORT).show();

            finish();
        });
    }

    private void setupCancelButton() {
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context =  getApplicationContext();
                Toast.makeText(context,getString(R.string.canceled_task),Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void setupEditButton() {
        editButton.setOnClickListener((view)->{

            Context context =  getApplicationContext();

            // Get the intent that started us to find the parameter (extra)
            Intent intent = getIntent();
            int selectedTaskIndex = intent.getIntExtra("Position",-1);

            String newTaskName = textTaskName.getText().toString();

            if(newTaskName.length() > 0) {
                taskManager.edit(selectedTaskIndex, newTaskName);
                Toast.makeText(this, getString(R.string.edited_task), Toast.LENGTH_SHORT).show();
            } else{
                Toast.makeText(this, getString(R.string.no_tasks_typed), Toast.LENGTH_SHORT).show();
            }

            PrefConfig.writeTaskListInPref(getApplicationContext(), taskManager.taskList);

            finish();
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