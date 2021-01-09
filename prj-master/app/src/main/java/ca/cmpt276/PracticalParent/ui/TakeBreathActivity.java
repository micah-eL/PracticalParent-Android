package ca.cmpt276.PracticalParent.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import ca.cmpt276.PracticalParent.R;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

/*
** Represents an activity that allows the user to select how many times they would like to inhale and exhale
 */

//Video for animation: https://www.youtube.com/watch?v=_lmRQB58r-U&ab_channel=android-coffee.com
public class TakeBreathActivity extends AppCompatActivity {
   private int numberOfBreathsSaved=0;

    private Button testBtn;
    private TextView testTV;
    private TextView breathsNumberSelected;
    private String state0breaths = "Button state 0: You've taken 0 breaths!";
    private String breatheInToast = "Hold Button and breathe in!";
    private String breatheOutToast = "Button released: Breathe out!";
    private static int MAX_NUM_BREATHS = 3; //default
    private int breathCount;
    private static final int ACTIVITY_RESULT_BREATHS = 1;
    private static final int MAX_INHALE_DURATION = 10000; //in milliseconds

    private ImageView circle;
    private AnimationDrawable breathAnimation;

    MediaPlayer mp;
    int time = 0;

    public static Intent makeLaunchIntent(Context context) {
        return (new Intent(context, TakeBreathActivity.class));
    }

    /**
     * Setting up State methods
     */
    private abstract class State {
        void handleEnter() {}
        void handleExit() {}
        void handleClick() {}
        void handleRelease() {}
    }

    public final State breatheInState = new breatheInState();
    public final State breatheOutState = new breatheOutState();
    private State currentState = new IdleState();

    public void setState(State newState) {
        currentState.handleExit();
        currentState = newState;
        currentState.handleEnter();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_breath);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        circle = (ImageView) findViewById(R.id.circle);
        circle.setImageResource(R.drawable.first);

        breathCount = 0;
        currentState = breatheOutState;

        setupFloatingActionButton();

        refreshScreen();

        testTV = (TextView) findViewById(R.id.takeBreathTestTextView);

        testBtn = (Button) findViewById(R.id.takeBreathTestButton);
        testBtn.setOnTouchListener(new View.OnTouchListener(){

            private long start = 0;
            private long end = 0;
            private long duration = end - start;

            @Override
            public boolean onTouch(View view, MotionEvent event){

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    start = System.currentTimeMillis();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    end = System.currentTimeMillis();
                    duration = end - start;
                }

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: //if button is pressed
                        if (currentState == breatheOutState){
                            breathCount = breathCount + 1;
                            setState((breatheInState));
                        }

                        break;

                    case MotionEvent.ACTION_UP: //if button is released
                         if (currentState == breatheInState && duration >= 3000){
                             setState(breatheOutState);
                         } else if(currentState == breatheInState && duration < 3000){
                             breathCount --;
                             if (breathCount == 0){

                                 String message = getString(R.string.breathe_in_toast);
                                 testTV.setText(message);

                                 breathInAnimFinish();
                             }

                             // give the help toast
                             currentState.handleExit();
                             currentState = breatheOutState;
                         }

                        if (duration < 3000){
                            time = 1; // if we keep the button for less than 3 seconds
                        } else{
                            time = 2; // if we keep the button for more than 3 seconds
                        }


                        break;

                }

                return true;
            }
        });

    }

    private void setupFloatingActionButton(){
        FloatingActionButton fabAdd = findViewById(R.id.fab_breaths);
        fabAdd.setOnClickListener(view->{
            Intent i = SelectNumberOfBreaths.makeLaunchIntent(TakeBreathActivity.this);
            startActivityForResult(i, ACTIVITY_RESULT_BREATHS);
        });
    }

    private void refreshScreen(){
        breathsNumberSelected = (TextView) findViewById(R.id.takeBreathTestTextView);
        numberOfBreathsSaved = SelectNumberOfBreaths.getOptionsBreath(this);
        MAX_NUM_BREATHS = SelectNumberOfBreaths.getOptionsBreath(this);


        breathsNumberSelected.setText("Selected: " + numberOfBreathsSaved +" breaths");
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshScreen();
    }

    /**
     * State pattern states
     */
    private class breatheOutState extends State {
        Handler breatheOutTimerHandler = new Handler();
        Runnable breatheOutTimerRunnable = new Runnable() {
            @Override
            public void run() {
                Toast.makeText(TakeBreathActivity.this, getString(R.string.that_was_10_sec), Toast.LENGTH_SHORT).show();

                if (mp != null) {
                    mp.stop();
                    mp.release();
                    mp = null;
                    breathOutAnimFinish();
                }
            }
        };

        Handler maxBreathsHandler = new Handler();
        Runnable maxBreathsRunnable = new Runnable() {
            @Override
            public void run() {
                if (mp != null) {
                    mp.stop();
                    mp.release();
                    mp = null;
                    breathOutAnimFinish();
                }
            }
        };

        Handler threeSecondButtonChangeHandler = new Handler();
        Runnable threeSecondButtonChangeRunnable = new Runnable() {
            @Override
            public void run() {
                testBtn.setText("IN");
                if(breathCount == MAX_NUM_BREATHS){

                    String GoodJob = getString(R.string.good_job);
                    testBtn.setText(GoodJob);
                    testTV.setText("You've taken " + MAX_NUM_BREATHS + " breaths. Good job!");
                    testBtn.setEnabled(false);
                }
            }
        };

        @Override
        void handleEnter() {
            if (mp != null) {
                mp.stop();
                mp.release();
                mp = null;
            }
            mp = MediaPlayer.create(TakeBreathActivity.this, R.raw.calmsoundforexhale);
            mp.start();
            breathOutAnim();

            String out = getString(R.string.out);
            testBtn.setText(out);

            Toast.makeText(TakeBreathActivity.this, getString(R.string.breathe_out_toast), Toast.LENGTH_SHORT).show();

            testTV.setText("You've taken " + breathCount + " breath(s)!");

            if (breathCount < MAX_NUM_BREATHS){
                threeSecondButtonChangeHandler.postDelayed(threeSecondButtonChangeRunnable, 3000);
                breatheOutTimerHandler.postDelayed(breatheOutTimerRunnable, MAX_INHALE_DURATION);
            }

            if (breathCount == MAX_NUM_BREATHS){ //once user has taken 'MAX_NUM_BREATHS' breaths, remove button
                threeSecondButtonChangeHandler.postDelayed(threeSecondButtonChangeRunnable, 3000);
                maxBreathsHandler.postDelayed(maxBreathsRunnable, MAX_INHALE_DURATION);
            }
        }

        @Override
        void handleExit() {
            breatheOutTimerHandler.removeCallbacks(breatheOutTimerRunnable);
            if (mp != null) {
                mp.stop();
                mp.release();
                mp = null;
            }
        }

        @Override
        void handleClick() {
            setState(breatheInState);
        }

        @Override
        void handleRelease() {
            //
        }
    }

    private class breatheInState extends State {
        Handler breatheInTimerHandler = new Handler();
        Runnable breatheInTimerRunnable = new Runnable() {
            @Override
            public void run() {
                setState(breatheOutState);
            }
        };

        @Override
        void handleEnter() {

            if (mp != null) {
                mp.stop();
                mp.release();
                mp = null;
                breathOutAnimFinish();
            }
            mp = MediaPlayer.create(TakeBreathActivity.this, R.raw.calmsoundforinhale);
            mp.start();
            breathInAnim();

            if (breathCount == 1){
                FloatingActionButton fabAdd = findViewById(R.id.fab_breaths);
                fabAdd.setVisibility(View.GONE);
            }

            testBtn.setText("IN");
            Toast.makeText(TakeBreathActivity.this, getString(R.string.breathe_in_toast), Toast.LENGTH_SHORT).show();

            String breathNum = breathCount + "";

            String breathText = getString(R.string.taking_breath_number, breathNum);
            testTV.setText(breathText);

            breatheInTimerHandler.postDelayed(breatheInTimerRunnable, MAX_INHALE_DURATION);
        }

        @Override
        void handleExit() {
            breatheInTimerHandler.removeCallbacks(breatheInTimerRunnable);
            stopPlaying();
            if (time == 1){
            }
            breathInAnimFinish();
        }

        @Override
        void handleRelease() {
            setState(breatheOutState);
        }

        @Override
        void handleClick() {
            //
        }
    }

    // Use "Null Object" pattern: This class, does nothing! It's like a safe null
    private class IdleState extends State {
    }

    private void stopPlaying() {
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
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

    // Animation
    private void breathInAnim() {
        Animation scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_in);
        circle.setImageResource(R.drawable.green_small);
        circle.startAnimation(scaleAnimation);
    }

    private void breathInAnimFinish() {
        circle.clearAnimation();
        circle.setImageResource(R.drawable.first);
    }

    private void breathOutAnim() {
        Animation scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_out);
        circle.setImageResource(R.drawable.blue_small);
        circle.startAnimation(scaleAnimation);
    }

    private void breathOutAnimFinish() {
        circle.clearAnimation();
        circle.setImageResource(R.drawable.first_blue);
    }

}