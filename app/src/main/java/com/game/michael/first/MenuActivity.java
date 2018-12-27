package com.game.michael.first;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MenuActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_layout);
    }

    public void goOptions (View v) {
        //Intent act = new Intent(this, OptionActivity.class);
        startActivity(new Intent(this, OptionActivity.class));
    }

    public void goBackToGame (View v) {
        //startActivity(new Intent(this, MainActivity.class));
        this.finish();
        //Activity(new Intent(this, MainActivity.class));
    }

    public void goMainMenu (View v) {
        //startActivity(new Intent(this, MainActivity.class));
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        //Activity(new Intent(this, MainActivity.class));
    }

    public void goSave (View v) {
        Toast toast = Toast.makeText(this, "Ожидает реализации", Toast.LENGTH_SHORT);
        toast.show();
    }
}
/*
public class XYZ extends Activity {
    private long backPressedTime = 0;    // used by onBackPressed()


    @Override
    public void onBackPressed() {        // to prevent irritating accidental logouts
        long t = System.currentTimeMillis();
        if (t - backPressedTime > 2000) {    // 2 secs
            backPressedTime = t;
            Toast.makeText(this, "Press back again to logout",
                                Toast.LENGTH_SHORT).show();
        } else {    // this guy is serious
            // clean up
            super.onBackPressed();       // bye
        }
    }
}
 */