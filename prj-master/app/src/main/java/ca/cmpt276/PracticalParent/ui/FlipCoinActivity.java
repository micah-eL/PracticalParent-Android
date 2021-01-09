package ca.cmpt276.PracticalParent.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Random;

import ca.cmpt276.PracticalParent.R;
import ca.cmpt276.PracticalParent.model.ChildManager;
import ca.cmpt276.PracticalParent.model.HistoryLogic;
import ca.cmpt276.PracticalParent.model.HistoryManager;
import ca.cmpt276.PracticalParent.model.PrefConfig;

/**
 * Choose heads/tails to flip a coin.
 * 2 animations: endOnHeads and endOnTails.
 * Cycle through ChildManager and store history of coin flips.
*/
public class FlipCoinActivity extends AppCompatActivity {
    private boolean nobody_turn=false;
    public static final String NOBODY_TURN = "NO_BODY_TURN";


    private static final int ACTIVITY_RESULT_OVERRIDE = 1;
    public static final String CURR_CHILD_INDEX = "CURR_CHILD_INDEX";

    //tmpFloat and RANDOM for deciding heads/tails
    private float tmpFloat;
    public static final Random RANDOM = new Random();

    private ChildManager tmpChildManager;
    private HistoryManager historyManager;
    private String CURR_CHILD = "YOU HAVE NO CHILDREN SAVED";
    public SharedPreferences sharedPref;
    private int index = 0; //index in child manager arraylist
    private boolean childWon;

    private ImageView coin;
    private AnimationDrawable coinFlipAnimation;
    private Button flipCoinHeads;
    private Button flipCoinTails;
    private Button overrideBtn;

    private TextView TVcurrChild;
    private ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flip_coin);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        imageView = (ImageView) findViewById(R.id.child_photo_coin_flip);

        final MediaPlayer coinFlipSound = MediaPlayer.create(this, R.raw.coinflipsound);
        tmpChildManager = ChildManager.getInstance();
        historyManager = HistoryManager.getInstance();
        tmpChildManager.childList = PrefConfig.readListFromPref(this);
        if (tmpChildManager.childList == null){
            tmpChildManager.childList = new ArrayList<>();
        }

        sharedPref = getSharedPreferences("currChildIndex", 0);
        index = sharedPref.getInt(CURR_CHILD_INDEX, 0);
        nobody_turn = sharedPref.getBoolean(NOBODY_TURN,false);

        TVcurrChild = (TextView) findViewById(R.id.currentChild);
        if(nobody_turn){
            TVcurrChild.setText(getString(R.string.nobody_turn));
            imageView.setImageResource(R.drawable.default_image);

        }
        else {
            if (tmpChildManager.getNumChildren() != 0) {
                CURR_CHILD = tmpChildManager.getInfo(index);
                String path = tmpChildManager.get(index).get_photo_path();

                if(tmpChildManager.get(index).child_no_pic()){
                    imageView.setImageResource(R.drawable.default_image);
                }
                else{
                    Bitmap bitmap = BitmapFactory.decodeFile(path);
                    imageView.setImageBitmap(bitmap);
                }
            }

            TVcurrChild.setText(getString(R.string.current_child_flipCoin, CURR_CHILD));

        }

        coin = (ImageView) findViewById(R.id.coin);

        //pressing button to flip coin - heads selected
        flipCoinHeads = (Button) findViewById(R.id.flipHeadsBtn);
        flipCoinHeads.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //Date and Time: https://www.youtube.com/watch?v=_ZbM6b5SEw0&ab_channel=TechdoctorBD
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd\\MM\\yyyy hh:mm:ss");
                String dateTime = simpleDateFormat.format(calendar.getTime());

                coinFlipSound.start();

                //Call animations
                tmpFloat = RANDOM.nextFloat();
                if (tmpFloat > 0.5f){
                    coin.setTag("tails");
                    endOnTailsAnim();
                }
                else{
                    coin.setTag("heads");
                    endOnHeadsAnim();
                }

                //Save history of coin flips
                if ((String)coin.getTag() == "heads"){
                    childWon = true;
                }
                else{
                    childWon = false;
                }
                if(!nobody_turn) {
                    if (!CURR_CHILD.equals("YOU HAVE NO CHILDREN SAVED")) {
                        historyManager.add(new HistoryLogic(CURR_CHILD, dateTime, coin.getTag().toString(), childWon, tmpChildManager.get(index).get_photo_path()));
                        PrefConfig.writeHistoryListInPref(getApplicationContext(), historyManager.historyInfo);
                    }

                    //Updating and saving current child's index using Shared Preferences
                    if (tmpChildManager.getNumChildren() != 0) {
                        Integer tmp = tmpChildManager.indexList.get(0);
                        tmpChildManager.indexList.remove(0);
                        tmpChildManager.indexList.add(tmp);

                        index = tmpChildManager.getCurrentIndex(0);
                        CURR_CHILD = tmpChildManager.getInfo(index);
                        TVcurrChild.setText(getString(R.string.current_child_flipCoin, CURR_CHILD));

                        String path = tmpChildManager.get(index).get_photo_path();

                        if(tmpChildManager.get(index).child_no_pic()){
                            imageView.setImageResource(R.drawable.default_image);
                        }
                        else{
                            Bitmap bitmap = BitmapFactory.decodeFile(path);
                            imageView.setImageBitmap(bitmap);
                        }
                        sharedPref = getSharedPreferences("currChildIndex", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putInt("CURR_CHILD_INDEX", index);
                        editor.putBoolean(NOBODY_TURN, nobody_turn);
                        editor.commit();
                    }
                }
                else{
                    nobody_turn = false;
                    if (tmpChildManager.getNumChildren() != 0) {
                        CURR_CHILD = tmpChildManager.getInfo(index);
                        String path = tmpChildManager.get(index).get_photo_path();

                        if(tmpChildManager.get(index).child_no_pic()){
                            imageView.setImageResource(R.drawable.default_image);
                        }
                        else{
                            Bitmap bitmap = BitmapFactory.decodeFile(path);
                            imageView.setImageBitmap(bitmap);
                        }
                    }
                    else{
                        imageView.setVisibility(view.INVISIBLE);
                    }


                    TVcurrChild = (TextView) findViewById(R.id.currentChild);
                    TVcurrChild.setText(getString(R.string.current_child_flipCoin, CURR_CHILD));
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean(NOBODY_TURN,nobody_turn);
                    editor.commit();
                }
            }
        });

        //pressing button to flip coin - tails selected
        flipCoinTails = (Button) findViewById(R.id.flipTailsBtn);
        flipCoinTails.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //Date and Time: https://www.youtube.com/watch?v=_ZbM6b5SEw0&ab_channel=TechdoctorBD
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd\\MM\\yyyy hh:mm:ss");
                String dateTime = simpleDateFormat.format(calendar.getTime());

                coinFlipSound.start();

                //Call animations
                tmpFloat = RANDOM.nextFloat();
                if (tmpFloat > 0.5f){
                    coin.setTag("tails");
                    endOnTailsAnim();
                }
                else{
                    coin.setTag("heads");
                    endOnHeadsAnim();
                }

                //Save history of coin flips
                if ((String)coin.getTag() == "tails"){
                    childWon = true;
                }
                else{
                    childWon = false;
                }
                if(!nobody_turn) {
                    if (!CURR_CHILD.equals("YOU HAVE NO CHILDREN SAVED")) {
                        historyManager.add(new HistoryLogic(CURR_CHILD, dateTime, coin.getTag().toString(), childWon, tmpChildManager.get(index).get_photo_path()));
                        PrefConfig.writeHistoryListInPref(getApplicationContext(), historyManager.historyInfo);
                    }

                    //Updating and saving current child's index using Shared Preferences
                    if (tmpChildManager.getNumChildren() != 0) {
                        Integer tmp = tmpChildManager.indexList.get(0);
                        tmpChildManager.indexList.remove(0);
                        tmpChildManager.indexList.add(tmp);

                        index = tmpChildManager.getCurrentIndex(0);
                        CURR_CHILD = tmpChildManager.getInfo(index);
                        TVcurrChild.setText(getString(R.string.current_child_flipCoin, CURR_CHILD));

                        String path = tmpChildManager.get(index).get_photo_path();

                        if(tmpChildManager.get(index).child_no_pic()){
                            imageView.setImageResource(R.drawable.default_image);
                        }
                        else{
                            Bitmap bitmap = BitmapFactory.decodeFile(path);
                            imageView.setImageBitmap(bitmap);
                        }
                        sharedPref = getSharedPreferences("currChildIndex", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putInt("CURR_CHILD_INDEX", index);
                        editor.putBoolean(NOBODY_TURN, nobody_turn);
                        editor.commit();
                    }
                }
                else{
                    nobody_turn = false;
                    if (tmpChildManager.getNumChildren() != 0) {
                        CURR_CHILD = tmpChildManager.getInfo(index);
                        String path = tmpChildManager.get(index).get_photo_path();

                        if(tmpChildManager.get(index).child_no_pic()){
                            imageView.setImageResource(R.drawable.default_image);
                        }
                        else{
                            Bitmap bitmap = BitmapFactory.decodeFile(path);
                            imageView.setImageBitmap(bitmap);
                        }
                    }
                    else{
                        imageView.setVisibility(view.INVISIBLE);
                    }


                    TVcurrChild = (TextView) findViewById(R.id.currentChild);
                    TVcurrChild.setText(getString(R.string.current_child_flipCoin, CURR_CHILD));
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean(NOBODY_TURN,nobody_turn);
                    editor.commit();
                }
            }
        });

        //Override current child's turn
        overrideBtn = (Button) findViewById(R.id.overrideBtn);
        overrideBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = OverrideFlipCoinActivity.makeLaunchIntent(FlipCoinActivity.this);
                startActivityForResult(intent, ACTIVITY_RESULT_OVERRIDE);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        sharedPref = getSharedPreferences("currChildIndex", 0);
        index = sharedPref.getInt(CURR_CHILD_INDEX, 0);
        nobody_turn = sharedPref.getBoolean(NOBODY_TURN,false);

        if(nobody_turn){
            TVcurrChild.setText(getString(R.string.nobody_turn));
            imageView.setImageResource(R.drawable.default_image);
        }
        else {
            if (tmpChildManager.getNumChildren() != 0) {
                CURR_CHILD = tmpChildManager.getInfo(index);
                String path = tmpChildManager.get(index).get_photo_path();

                if(tmpChildManager.get(index).child_no_pic()){
                    imageView.setImageResource(R.drawable.default_image);
                }
                else{
                    Bitmap bitmap = BitmapFactory.decodeFile(path);
                    imageView.setImageBitmap(bitmap);
                }
            }

            TVcurrChild = (TextView) findViewById(R.id.currentChild);
            TVcurrChild.setText(getString(R.string.current_child_flipCoin, CURR_CHILD));
        }
    }

    private void endOnHeadsAnim() {
        coin.setImageResource(R.drawable.tailstoheads);
        coinFlipAnimation = (AnimationDrawable) coin.getDrawable();
        coinFlipAnimation.start();
    }

    private void endOnTailsAnim() {
        coin.setImageResource(R.drawable.headstotails);
        coinFlipAnimation = (AnimationDrawable) coin.getDrawable();
        coinFlipAnimation.start();
    }

    public static Intent makeLaunchIntent(Context context){
        return (new Intent(context, FlipCoinActivity.class));
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