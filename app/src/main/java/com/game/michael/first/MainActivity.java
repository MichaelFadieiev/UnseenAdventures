package com.game.michael.first;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
    }

    /*@Override
    protected void onDestroy() {
        Process.killProcess(Process.myPid());
        super.onDestroy();
    }*/

    public void goOptions (View v) {
        //Intent act = new Intent(this, OptionActivity.class);
        startActivity(new Intent(this, OptionActivity.class));
    }

    public void goQuit (View v) {
        this.finish();
    }

    public void goNew (View v) {

        //Button btn_return = (Button) findViewById(R.id.btn_0_return);
        //btn_return.setVisibility(View.VISIBLE);
        startActivity(new Intent(this, ProtagonistCreationActivity.class));
    }

    /*public void goBack (View v) {

    }*/

    public void goLoad (View v) {
        Toast toast = Toast.makeText(this, "Нет сохраненных персонажей", Toast.LENGTH_SHORT);
        toast.show();
    }
}
