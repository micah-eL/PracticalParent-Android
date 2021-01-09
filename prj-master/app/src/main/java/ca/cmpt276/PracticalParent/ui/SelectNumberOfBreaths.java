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
import android.widget.RadioButton;
import android.widget.RadioGroup;

import ca.cmpt276.PracticalParent.R;

/*
** Represents an activity that will allow user to select how many breaths they would like to take
 */

public class SelectNumberOfBreaths extends AppCompatActivity {

    private static final String PREF_NAME = "AppPrefs";
    private static final String BREATH_PREF_NAME = "Number of breaths";

    public static Intent makeLaunchIntent(Context context){
        return (new Intent(context, SelectNumberOfBreaths.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_number_of_breaths);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        createBreathOptions();
    }

    private void createBreathOptions(){
        RadioGroup breathOptionGroup;
        breathOptionGroup = (RadioGroup) findViewById(R.id.radio_group_breaths);

        int[] numberOfBreaths = getResources().getIntArray(R.array.NUMBER_OF_BREATHS);

        //Create the buttons:
        for(int i = 0; i < numberOfBreaths.length; i++){
            final int numberBreaths = numberOfBreaths[i];

            RadioButton button = new RadioButton(this);
            button.setText(""+numberBreaths);

            //Set on-click callbacks
            button.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){

                    saveNumberBreaths(numberBreaths);
                    finish();
                }

            });
            breathOptionGroup.addView(button);

            if(numberBreaths == getOptionsBreath(this)){
                button.setChecked(true);
                saveNumberBreaths(numberBreaths);
            }
        }
    }

    private void saveNumberBreaths(int breaths){
        SharedPreferences prefs = this.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(BREATH_PREF_NAME, breaths);
        editor.apply();
    }

    static public int getOptionsBreath(Context context){
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        int defaultBreaths = context.getResources().getInteger(R.integer.DEFAULT_BREATHS);
        return prefs.getInt(BREATH_PREF_NAME, defaultBreaths);
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