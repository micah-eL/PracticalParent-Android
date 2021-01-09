package ca.cmpt276.PracticalParent.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.TextView;

import ca.cmpt276.PracticalParent.R;

/**
 * Display group name, display developer's name, and citation of the used images and sound
 */
public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        ActionBar ab= getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        TextView infoWindow = (TextView) findViewById(R.id.txtInfo);

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(getString(R.string.group_name));
        stringBuilder.append(getString(R.string.developers));
        stringBuilder.append(getString(R.string.lbl_image_citations));
        stringBuilder.append(getString(R.string.blue_branches_link));
        stringBuilder.append(getString(R.string.branches_link));
        stringBuilder.append(getString(R.string.layer_colourful_link));
        stringBuilder.append(getString(R.string.winter_trees_link));
        stringBuilder.append(getString(R.string.view_link));
        stringBuilder.append(getString(R.string.sunset_link));
        stringBuilder.append(getString(R.string.green_link));
        stringBuilder.append(getString(R.string.lbl_sound_citations));
        stringBuilder.append(getString(R.string.coin_sound_link));
        stringBuilder.append(getString(R.string.inhale_sound_link));
        stringBuilder.append(getString(R.string.exhale_sound_link));
        stringBuilder.append(getString(R.string.sound_link));
        stringBuilder.append(getString(R.string.animation_link));

        infoWindow.setText(stringBuilder.toString());
    }

    public static Intent makeLaunchIntent(Context context) {
        return (new Intent(context, HelpActivity.class));
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