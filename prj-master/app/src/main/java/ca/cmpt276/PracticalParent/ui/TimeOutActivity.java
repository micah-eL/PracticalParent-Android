package ca.cmpt276.PracticalParent.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.IOException;

import ca.cmpt276.PracticalParent.R;
import ca.cmpt276.PracticalParent.model.CircularProgressView;

/**
 * Represents an activity where user can set a timer for time out
 */
public class TimeOutActivity extends AppCompatActivity {

    private CountDownTimer countDownTimer;
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private TextView timing,time_rate;
    private EditText typeInTime;
    private CircularProgressView pie_view;

    private double time_speed_rate=1;
    private boolean counting = false;
    private double minutesForCount = 1;
    private double minus_prev;
    private double timeLeft;
    private String MINUTES;
    private String ZERO;
    private String EMPTY;
    private String ZERO_SECONDS;
    private String COLON;
    private String START;
    private String RESTART;

    public static Intent makeLaunchIntent(Context context){
        return (new Intent(context, TimeOutActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeout);
        ActionBar ab= getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        MINUTES = getString(R.string.minutes);
        ZERO = getString(R.string.zero);
        EMPTY = getString(R.string.empty);
        ZERO_SECONDS = getString(R.string.zero_seconds);
        COLON = getString(R.string.colon);
        START = getString(R.string.start);
        RESTART = getString(R.string.restart);

        time_rate=findViewById(R.id.timerate);
        pie_view=findViewById(R.id.pieView);
        typeInTime = (EditText) findViewById(R.id.typeInTime);
        inPutWatcher watcher = new inPutWatcher();
        typeInTime.addTextChangedListener(watcher);
        pie_view.setProgress(100);
        permission();
        initiateRadioButton();
        setButton();
        setTimeRate(100);
        
    }

    private void setTimeRate(int i) {
        time_rate.setText("time rate is "+i+" %");
    }

    private void permission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (! Settings.canDrawOverlays(TimeOutActivity.this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent,10);
            }
        }
    }

    private void setButton() {
        Button btn = (Button) findViewById(R.id.timerStart);
        Button btn2 = (Button) findViewById(R.id.pause);
        Button btn3 = (Button) findViewById(R.id.reStart);
        btn.setText(START);
        btn2.setVisibility(View.INVISIBLE);
        btn2.setEnabled(false);
        btn3.setVisibility(View.INVISIBLE);
        btn3.setEnabled(false);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!counting) {
                    btn.setText(RESTART);
                    btn2.setVisibility(View.VISIBLE);
                    btn2.setEnabled(true);
                    counting = true;
                    timeLeft = minutesForCount *60;
                    startTiming();
                }else{
                    setTimeRate(100);
                    time_speed_rate=1;
                    countDownTimer.cancel();
                    timeLeft = minutesForCount *60;
                    startTiming();
                    btn3.setVisibility(View.INVISIBLE);
                    btn3.setEnabled(false);
                    btn2.setVisibility(View.VISIBLE);
                    btn2.setEnabled(true);
                }
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn3.setVisibility(View.VISIBLE);
                btn3.setEnabled(true);
                countDownTimer.cancel();
                btn2.setVisibility(View.INVISIBLE);
                btn2.setEnabled(false);
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTiming();
                btn3.setVisibility(View.INVISIBLE);
                btn3.setEnabled(false);
                btn2.setVisibility(View.VISIBLE);
                btn2.setEnabled(true);
            }
        });
    }


    private void startTiming()  {
        timing = (TextView) findViewById(R.id.timer);
        minus_prev=minutesForCount;
        countDownTimer = new CountDownTimer((long) (timeLeft *1000*time_speed_rate), (long) (1000*time_speed_rate)) {
            @Override
            public void onTick(long minsUntilFinished) {

                timeLeft--;
                double totalTime=minus_prev*60;
                double progress=timeLeft/totalTime*100;
                pie_view.setProgress((int) progress);
                int min = (int) (timeLeft /60);
                int sec = (int) (timeLeft %60);
                String minutesStart,secondsStart;
                if(min >= 10){
                    minutesStart = EMPTY +min;
                }else{
                    minutesStart = ZERO +min;
                }
                if(sec >= 10){
                    secondsStart = EMPTY +sec;
                }else{
                    secondsStart = ZERO +sec;
                }
                timing.setText(minutesStart+ COLON +secondsStart);
            }

            @Override
            public void onFinish() {
                counting = false;
                time_speed_rate=1;
                setTextView();

                setVoice();

                alarmAlert();
                setTimeRate(100);
            }
        };
        countDownTimer.start();

    }

    private void setVoice() {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        try {
            mediaPlayer =new MediaPlayer();
            mediaPlayer.setDataSource(TimeOutActivity.this,notification);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setVolume(0.5f, 0.5f);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        vibrator= (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
        long[] patter = {1000, 1000, 2000, 50};
        vibrator.vibrate(patter, 0);
    }

    private void alarmAlert() {
        AlertDialog.Builder dialog=new AlertDialog.Builder(TimeOutActivity.this);
        dialog.setMessage(getString(R.string.timeout));
        dialog.setPositiveButton("ok",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mediaPlayer.stop();
                mediaPlayer.release();
                setButton();
                vibrator.cancel();
            }
        });
        AlertDialog mDialog=dialog.create();


        WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            lp.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        mDialog.getWindow().setAttributes(lp);
        mDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.time_out_rate,menu);
        return true;
    }


    class inPutWatcher implements TextWatcher{

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            if(typeInTime.length() > 0){
                minutesForCount = Integer.parseInt(typeInTime.getText().toString());
            }if(!counting){
                setTextView();
            }

        }
    }

    private void setTextView() {
        TextView timerView=(TextView) findViewById(R.id.timer);
        pie_view.setProgress(100);
        if(minutesForCount >= 10) {
            timerView.setText((int)(minutesForCount) + ":00");

        }else{
            timerView.setText("0" + (int)(minutesForCount)  + ":00");
        }
    }

    private void initiateRadioButton() {
        RadioGroup choicesForMinutes = (RadioGroup) findViewById(R.id.timeRadio);

        int [] arrayMinutes = getResources().getIntArray(R.array.minutesOptions);


        for(int i = 0;i < arrayMinutes.length; i++){
            final int min = arrayMinutes[i];
            RadioButton btn = new RadioButton(this);
            btn.setText(min + " " + MINUTES);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    minutesForCount = min;
                    if(!counting){
                        setTextView();
                    }
                }
            });
            choicesForMinutes.addView(btn);
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                if(counting) {
                    moveTaskToBack(true);
                }else {
                    finish();
                }
                return true;
            case R.id.per25:
                setTimeSpeed(25);
                break;
            case R.id.per75:
                setTimeSpeed(75);
                break;
            case R.id.per100:
                setTimeSpeed(100);
                break;
            case R.id.per200:
                setTimeSpeed(200);
                break;
            case R.id.per300:
                setTimeSpeed(300);
                break;
            case R.id.per400:
                setTimeSpeed(400);
                break;
            default:
                return super.onOptionsItemSelected(item);

        }
        return true;
    }

    private void setTimeSpeed(int i) {
        setTimeRate(i);
        this.time_speed_rate=(double)(i)/100;
        if(counting){
            countDownTimer.cancel();
            startTiming();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            if(counting) {
                moveTaskToBack(true);
            }else {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}