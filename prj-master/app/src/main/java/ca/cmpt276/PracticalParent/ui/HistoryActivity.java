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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


import ca.cmpt276.PracticalParent.R;
import ca.cmpt276.PracticalParent.model.Child;
import ca.cmpt276.PracticalParent.model.ChildManager;
import ca.cmpt276.PracticalParent.model.HistoryLogic;
import ca.cmpt276.PracticalParent.model.HistoryManager;
import ca.cmpt276.PracticalParent.model.PrefConfig;

//Toggle Switch toggle: https://www.youtube.com/watch?v=mT0ymLOaGhI&ab_channel=AndroidCoding
//Shared Preferences: https://www.youtube.com/watch?v=jcliHGR3CHo&ab_channel=CodinginFlow
/**
 * Represents a class that stores history for both all previous coin flips and previous coin flips for
 * current child
 **/
public class HistoryActivity extends AppCompatActivity {

    private Switch toggleSwitch;
    private ListView listView;

    private HistoryManager historyManager;
    public List<HistoryLogic> tempList = new ArrayList<>();
    private ChildManager childManager;
    private int index = 0;
    private int indexChild = 0;
    private boolean nobody_turn = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ActionBar ab= getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        historyManager = HistoryManager.getInstance();
        childManager =  ChildManager.getInstance();

        childManager.childList = PrefConfig.readListFromPref(this);
        if (childManager.childList == null){
            childManager.childList = new ArrayList<>();
        }

        historyManager.historyInfo = PrefConfig.readHistoryListFromPref(this);
        if (historyManager.historyInfo == null){
            historyManager.historyInfo = new ArrayList<>();
        }

        toggleBetweenChildAndPreviousResults();
    }

    public static Intent makeLaunchIntent(Context context){
        return (new Intent(context, HistoryActivity.class));
    }

    private void populateListView(boolean switchIsOn){

        SharedPreferences sharedPref = getSharedPreferences("currChildIndex", Context.MODE_PRIVATE);
        int indexChild = sharedPref.getInt(FlipCoinActivity.CURR_CHILD_INDEX, index);
        nobody_turn =sharedPref.getBoolean(FlipCoinActivity.NOBODY_TURN,false);
        String CURR_CHILD = "";
        if(childManager.getNumChildren() > 0){
            CURR_CHILD = childManager.getInfo(indexChild);
        }

        if(switchIsOn == false){
            if(childManager.getNumChildren() > 0){
                ArrayAdapter<HistoryLogic> adapter = new MyListAdapter();
                listView = (ListView) findViewById(R.id.history_list);
                listView.setAdapter(adapter);
            } else {
                ArrayAdapter<HistoryLogic> adapter = new MyListAdapter();
                listView = (ListView) findViewById(R.id.history_list);
                listView.setAdapter(adapter);
            }
        }
        else if(switchIsOn && childManager.getNumChildren() > 0){
            boolean hasCurrentChild = false;

            //checking to see if the list already has current child then we don't need to add more
            for(int index = 0; index < tempList.size(); index++){
                if(!tempList.isEmpty()){
                    if(tempList.get(index).getChild().equals(CURR_CHILD)){
                        hasCurrentChild = true;
                    }
                }
            }

            //if we have a different current child, we need to empty current list and get ready to fill
            if(!hasCurrentChild && !tempList.isEmpty()){
                for(int index = 0 ; index < tempList.size(); index++){
                    tempList.remove(index);
                    PrefConfig.writeTempListInPref(getApplicationContext(), tempList);
                }
            }

            //filling up the temp with current child
            if(!hasCurrentChild&&!nobody_turn){
                for(int index = 0; index < historyManager.getNumHistoryInfo(); index++){
                    if(historyManager.get(index).getChild().equals(CURR_CHILD)){
                        tempList.add(new HistoryLogic(CURR_CHILD, (historyManager.get(index).getDateAndTime()),
                                (historyManager.get(index).getResultOfFlip()), (historyManager.get(index).getChildWon()), historyManager.get(index).getPhotoPath()));
                        PrefConfig.writeTempListInPref(getApplicationContext(), tempList);
                    }
                }
            }

            ArrayAdapter<HistoryLogic> adapter = new MyListAdapter2();
            listView = (ListView) findViewById(R.id.history_list);
            listView.setAdapter(adapter);
        } else if(switchIsOn && childManager.getNumChildren() == 0){
            ArrayAdapter<HistoryLogic> adapter = new MyListAdapter2();
            listView = (ListView) findViewById(R.id.history_list);
            listView.setAdapter(adapter);
        }
    }

    private class MyListAdapter extends ArrayAdapter<HistoryLogic>{
        public MyListAdapter(){
            super(HistoryActivity.this, R.layout.history_layout3, historyManager.historyInfo);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            //Make sure we have a view to work with
            View itemView = convertView;
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.history_layout3, parent, false);
            }

            //find what to work with
            HistoryLogic currentHistory = historyManager.get(position);


            //fill view of whether or not the child won the coin toss
            ImageView imageView = (ImageView)itemView.findViewById(R.id.image_of_result);
            if(currentHistory.getChildWon()){
                imageView.setImageResource(R.drawable.check);
            } else {
                imageView.setImageResource(R.drawable.cross);
            }

            //fill view of child's photo
            ImageView childPhoto = (ImageView)itemView.findViewById(R.id.child_photo_history);
            String path = currentHistory.getPhotoPath();
            if(path.equals(Child.EMPTY_PHOTO_PATH)){
                childPhoto.setImageResource(R.drawable.default_image);
            }
            else{
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                childPhoto.setImageBitmap(bitmap);
            }

            TextView historyText = (TextView)itemView.findViewById(R.id.history_line);
            historyText.setText(currentHistory.getDescription());

            return itemView;
        }
    }
    private class MyListAdapter2 extends ArrayAdapter<HistoryLogic>{
        public MyListAdapter2(){
            super(HistoryActivity.this, R.layout.history_layout3, tempList);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            //Make sure we have a view to work with
            View itemView = convertView;
                if(itemView == null){
                    itemView = getLayoutInflater().inflate(R.layout.history_layout3, parent, false);
                }

                //find what to work with
                HistoryLogic currentHistory2 = tempList.get(position);

                //fill view
                ImageView imageView = (ImageView) itemView.findViewById(R.id.image_of_result);
                if (currentHistory2.getChildWon()) {
                    imageView.setImageResource(R.drawable.check);
                } else {
                    imageView.setImageResource(R.drawable.cross);
                }

                //fill view of child's photo
                ImageView childPhoto = (ImageView)itemView.findViewById(R.id.child_photo_history);
                String path = currentHistory2.getPhotoPath();
                if(currentHistory2.child_no_pic()){
                    childPhoto.setImageResource(R.drawable.default_image);
                }
                else{
                    Bitmap bitmap = BitmapFactory.decodeFile(path);
                    childPhoto.setImageBitmap(bitmap);
                }

                TextView historyText = (TextView) itemView.findViewById(R.id.history_line);
                historyText.setText(currentHistory2.getDescription());

            return itemView;
        }
    }

    public void toggleBetweenChildAndPreviousResults() {

        toggleSwitch = findViewById(R.id.toggle_switch);

        if(historyManager.getNumHistoryInfo() > 0){
            populateListView(false);
        }

        toggleSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toggleSwitch.isChecked()) {
                    if(historyManager.getNumHistoryInfo() > 0){
                        populateListView(true);

                    }
                } else {
                    if(historyManager.getNumHistoryInfo() > 0){
                        populateListView(false);
                    }

                }
            }
        });
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