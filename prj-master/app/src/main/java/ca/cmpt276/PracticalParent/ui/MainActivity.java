package ca.cmpt276.PracticalParent.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.Button;

import java.util.ArrayList;

import ca.cmpt276.PracticalParent.R;
import ca.cmpt276.PracticalParent.model.Child;
import ca.cmpt276.PracticalParent.model.ChildManager;
import ca.cmpt276.PracticalParent.model.PrefConfig;

/**
 * Main activity contains buttons to the 6 main activities (listed in order):
 *      1. Help
 *      2. Task
 *      3. History
 *      4. Flip coin
 *      5. Time out
 *      6. Children
 */
public class MainActivity extends AppCompatActivity {

    private static final String PERMISSION_WRITE_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final int REQUEST_PERMISSION_CODE = 267;
    private static final int CHECK_MIN_SDK = 23;

    ChildManager childManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= CHECK_MIN_SDK) {
            if (checkSelfPermission(PERMISSION_WRITE_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{PERMISSION_WRITE_STORAGE}, REQUEST_PERMISSION_CODE);
            }
        }

        childManager=ChildManager.getInstance();
        childManager.childList = PrefConfig.readListFromPref(this);
        if (childManager.childList == null){
            childManager.childList = new ArrayList<>();
        }
        childManager.indexList = PrefConfig.readChildOrderListFromPref(this);
        if (childManager.indexList == null){
            childManager.indexList = new ArrayList<>();
        }

        if (childManager.indexList.size()!=childManager.childList.size()){
            childManager.indexList = new ArrayList<>();
            for(int i=0;i<childManager.childList.size();i++){
                childManager.indexList.add(new Integer(i));
            }
        }

        for(int i=0;i<childManager.childList.size();i++){
            Child child=childManager.get(i);
            if(child.photo_path==null){
                child.photo_path=Child.EMPTY_PHOTO_PATH;
            }
        }

        initBtns();
    }

    public static Intent makeLaunchIntent(Context context){
        return (new Intent(context, MainActivity.class));
    }

    private void initBtns(){
        Button btnMoveToFlipCoin = findViewById(R.id.btn_flip_coin);
        Button btnMoveToTimeOut = findViewById(R.id.btn_time_out);
        Button btnMoveToChildren = findViewById(R.id.btn_children);
        Button btnMoveToHistory = findViewById(R.id.btn_view_history);
        Button btnMoveToTask = findViewById(R.id.btn_task);
        Button btnMoveToHelp = findViewById(R.id.btn_help);
        Button btnMoveToTakeBreath = findViewById(R.id.btn_take_breath);

        btnMoveToFlipCoin.setOnClickListener(v -> {
            Intent intent = FlipCoinActivity.makeLaunchIntent(MainActivity.this);
            startActivity(intent);
        });

        btnMoveToTimeOut.setOnClickListener(v -> {
            Intent intent = TimeOutActivity.makeLaunchIntent(MainActivity.this);
            startActivity(intent);
        });

        btnMoveToChildren.setOnClickListener(v -> {
            Intent intent = ChildrenActivity.makeLaunchIntent(MainActivity.this);
            startActivity(intent);
        });

        btnMoveToHistory.setOnClickListener(v -> {
            Intent intent = HistoryActivity.makeLaunchIntent(MainActivity.this);
            startActivity(intent);
        });

        btnMoveToTask.setOnClickListener(v -> {
            Intent intent = TaskActivity.makeLaunchIntent(MainActivity.this);
            startActivity(intent);
        });

        btnMoveToHelp.setOnClickListener(v -> {
            Intent intent = HelpActivity.makeLaunchIntent(MainActivity.this);
            startActivity(intent);
        });

        btnMoveToTakeBreath.setOnClickListener(v -> {
            Intent intent = TakeBreathActivity.makeLaunchIntent(MainActivity.this);
            startActivity(intent);
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