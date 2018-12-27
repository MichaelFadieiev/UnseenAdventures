package com.game.michael.first;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.json.JSONObject;

import static com.game.michael.first.SourceJSON.readOption;
import static com.game.michael.first.SourceJSON.writeOption;



import java.util.ArrayList;
import java.util.Collection;

public class OptionActivity extends Activity {

    ArrayList<String> listLang;
    Spinner spinnerLang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.options_layout);
    }

    @Override
    protected void onStart() {
        super.onStart();
        listLang = new ArrayList<>();
        for (String tmp : getResources().getStringArray(R.array.languages)) listLang.add(tmp);
        spinnerLang = (Spinner) findViewById(R.id.v4_optionLanguageS);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, R.layout.spnr_layout,
                R.id.spnr_item, listLang);
        spinnerLang.setAdapter(adapter1);
        spinnerLang.setSelection(1);

    }

    public void goBack (View v) {
        //startActivity(new Intent(this, MainActivity.class));
        this.finish();
        //Activity(new Intent(this, MainActivity.class));
    }

    public void goSaveOptions (View v) {
        //startActivity(new Intent(this, MainActivity.class));
        JSONObject opt = new JSONObject();
        opt = readOption(getApplicationContext());
        try {
            if (((String) spinnerLang.getSelectedItem()).equals("English")) opt.put("language", "en");
            if (((String) spinnerLang.getSelectedItem()).equals("Українська")) opt.put("language", "ua");
            if (((String) spinnerLang.getSelectedItem()).equals("Русский")) opt.put("language", "ru");
            writeOption(getApplicationContext(), opt);
        } catch (Exception e) {}


        this.finish();
        //Activity(new Intent(this, MainActivity.class));
    }
}
