package com.game.michael.first;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import android.widget.Toast;

import java.util.ArrayList;

import static java.lang.Math.round;

public class ProtagonistCreationActivity extends Activity{

    SparseArray<Float> attributes;
    SparseArray<Float> skills;
    ArrayList<String> skill_names;
    int cnt_skills;
    int cnt_stats;
    int attr_points;

    String[] skill_keys;

    Spinner spnr_proff;
    Spinner spnr_hobby;
    Spinner spnr_inter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.protagonist_creation_layout);

        cnt_skills = getResources().getInteger(R.integer.cnt_skills);
        cnt_stats = getResources().getInteger(R.integer.cnt_stats);

        skill_keys = new String[cnt_skills];
        for (int i=0; i<cnt_skills; i++) {skill_keys[i] = "skl_"+String.valueOf(i);}

        skill_names = new ArrayList<>(cnt_skills);
        skill_names.add(getString(R.string.v2_skl_empty_txt));

        for (String i : skill_keys) {
            skill_names.add(getString(getResources().getIdentifier(i,
                    "string",
                    getPackageName())));
            //skill_names.add(String.valueOf(getResources().getIdentifier("com.game.michael.first:string/"+i, null, null)));
            //skill_names.add(getString(R.string.skl_wpn_blt));
        }

        //toast2 = Toast.makeText(this, "for loop is done", Toast.LENGTH_SHORT);
        //toast2.show();

        //ArrayAdapter<?> adapter1 = ArrayAdapter.createFromResource(this, R.array.skl_keys, android.R.layout.simple_spinner_item);
        //adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        attributes = new SparseArray<>(cnt_stats);
        for (int i = 0; i<cnt_stats;i++) {attributes.put(i, (float)1);}
        attr_points = getResources().getInteger(R.integer.cns_startStatPoints);


    }

    @Override
    protected void onStart() {
        TextView txtV;

        super.onStart();

        skills = new SparseArray<>(4);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, R.layout.spnr_layout,
                R.id.spnr_item, skill_names);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, R.layout.spnr_layout,
                R.id.spnr_item, skill_names);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(this, R.layout.spnr_layout,
                R.id.spnr_item, skill_names);

        spnr_proff = findViewById(R.id.v2_professionSPN);
        spnr_hobby = findViewById(R.id.v2_hobbySPN);
        spnr_inter = findViewById(R.id.v2_interestSPN);

        spnr_proff.setAdapter(adapter1);
        spnr_hobby.setAdapter(adapter2);
        spnr_inter.setAdapter(adapter3);
        //spnr_proff.setPrompt(getString(R.string.v2_skl_proff_txt));
        spnr_proff.setSelection(0);
        /*spnr_proff.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/
        spnr_hobby.setSelection(0);
        spnr_inter.setSelection(0);

        txtV = findViewById(R.id.v2_STR_val_TV);
        txtV.setText(String.valueOf(round(attributes.get(0))));
        txtV = findViewById(R.id.v2_INT_val_TV);
        txtV.setText(String.valueOf(round(attributes.get(1))));
        txtV = findViewById(R.id.v2_PRC_val_TV);
        txtV.setText(String.valueOf(round(attributes.get(2))));
        txtV = findViewById(R.id.v2_AGL_val_TV);
        txtV.setText(String.valueOf(round(attributes.get(3))));
        txtV = findViewById(R.id.v2_END_val_TV);
        txtV.setText(String.valueOf(round(attributes.get(4))));
        txtV = findViewById(R.id.v2_CHR_val_TV);
        txtV.setText(String.valueOf(round(attributes.get(5))));
        txtV = findViewById(R.id.v2_points_value);
        txtV.setText(String.valueOf(attr_points));
    }

    public void goMainMenu (View v) {
        this.onBackPressed();
    }


//Changing attributes when creating protagonist
    public void attr_mod (View v) {
        TextView txtV;
        int attr;
        Boolean mod;
        switch (v.getId()){
            case R.id.v2_STR_down_btn: txtV = findViewById(R.id.v2_STR_val_TV);
                attr = 0;
                mod = false;
                break;
            case R.id.v2_STR_up_btn: txtV = findViewById(R.id.v2_STR_val_TV);
                attr = 0;
                mod = true;
                break;
            case R.id.v2_INT_down_btn: txtV = findViewById(R.id.v2_INT_val_TV);
                attr = 1;
                mod = false;
                break;
            case R.id.v2_INT_up_btn: txtV = findViewById(R.id.v2_INT_val_TV);
                attr = 1;
                mod = true;
                break;
            case R.id.v2_AGL_down_btn: txtV = findViewById(R.id.v2_AGL_val_TV);
                attr = 3;
                mod = false;
                break;
            case R.id.v2_AGL_up_btn: txtV = findViewById(R.id.v2_AGL_val_TV);
                attr = 3;
                mod = true;
                break;
            case R.id.v2_PRC_down_btn: txtV = findViewById(R.id.v2_PRC_val_TV);
                attr = 2;
                mod = false;
                break;
            case R.id.v2_PRC_up_btn: txtV = findViewById(R.id.v2_PRC_val_TV);
                attr = 2;
                mod = true;
                break;
            case R.id.v2_END_down_btn: txtV = findViewById(R.id.v2_END_val_TV);
                attr = 4;
                mod = false;
                break;
            case R.id.v2_END_up_btn: txtV = findViewById(R.id.v2_END_val_TV);
                attr = 4;
                mod = true;
                break;
            case R.id.v2_CHR_down_btn: txtV = findViewById(R.id.v2_CHR_val_TV);
                attr = 5;
                mod = false;
                break;
            case R.id.v2_CHR_up_btn: txtV = findViewById(R.id.v2_CHR_val_TV);
                attr = 5;
                mod = true;
                break;
            default: txtV = findViewById(R.id.v2_points);
                attr = 10;
                mod = false;
                break;
        }
        if (mod) {
            if ((attr_points > 0) && (round(attributes.get(attr)) < 10)) {
                attr_points -= 1;
                attributes.put(attr, attributes.get(attr)+1);
                txtV.setText(String.valueOf(round(attributes.get(attr))));
            }
        } else {
            if (round(attributes.get(attr)) > 1) {
                attr_points += 1;
                attributes.put(attr, attributes.get(attr)-1);
                txtV.setText(String.valueOf(round(attributes.get(attr))));
            }
        }
        txtV = findViewById(R.id.v2_points_value);
        txtV.setText(String.valueOf(attr_points));
    }

    public void startGame (View v) {

        String name, surname;

        //Button btn_return = (Button) findViewById(R.id.btn_0_save);
        //btn_return.setVisibility(View.VISIBLE);
        name = ((EditText) findViewById(R.id.v2_nameET)).getText().toString().trim();
        surname = ((EditText) findViewById(R.id.v2_surnameET)).getText().toString().trim();
        name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
        surname = surname.substring(0, 1).toUpperCase() + surname.substring(1).toLowerCase();
        if (nameIsBad(name)) {
            Toast toast1 = Toast.makeText(this, "Не выеживайся, выбери нормальное имя!", Toast.LENGTH_SHORT);
            toast1.show();}
        else if (nameIsBad(surname)) {
            Toast toast2 = Toast.makeText(this, "Не выеживайся, выбери нормальную фамилию!", Toast.LENGTH_SHORT);
            toast2.show();}
        else {
            Intent startGameIntent = new Intent(this, GameActivity.class);

            startGameIntent.putExtra("type", "new");
            startGameIntent.putExtra("name", name);
            startGameIntent.putExtra("surname", surname);
            float[] temp0 = new float[attributes.size()];
            for (int i=0; i<attributes.size(); i++){
                temp0[i] = attributes.get(i);
            }
            startGameIntent.putExtra("attributes", temp0);

            skills.clear();
            int intTMP = spnr_proff.getSelectedItemPosition();
            if (intTMP > 0) {
                float k = skills.get(intTMP-1, 0f) + 40f;
                skills.put(intTMP-1, k);
            }
            intTMP = spnr_hobby.getSelectedItemPosition();
            if (intTMP > 0) {
                float k = skills.get(intTMP-1, 0f) + 20f;
                skills.put(intTMP-1, k);
            }
            intTMP = spnr_inter.getSelectedItemPosition();
            if (intTMP > 0) {
                float k = skills.get(intTMP-1, 0f) + 10f;
                skills.put(intTMP-1, k);
            }

            int[] temp1 = new int[skills.size()];
            for (int i=0; i<skills.size(); i++) {temp1[i] = skills.keyAt(i);}
            startGameIntent.putExtra("skill_keys", temp1);

            float[] temp = new float[skills.size()];
            for (int i=0; i<skills.size(); i++) {temp[i] = skills.get(temp1[i]);}
            startGameIntent.putExtra("skills", temp);
            startActivity(startGameIntent);
            this.finish();
            }
    }

    public boolean nameIsBad (String p_name) {
        p_name = p_name.trim();
        return (p_name.matches(".*[\\W\\d].*") || p_name.isEmpty());
        //return (p_name.matches("(\\s)+|(\\d)+|\'|\"") || p_name.isEmpty());
        //if (p_name.matches("[^ a-zA-Zа-яА-Я\'\"]") || p_name.isEmpty()) {return false;}
        //else {return true;}
    }

}


/*
// адаптер
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        // заголовок
        spinner.setPrompt("Title");
        // выделяем элемент
        spinner.setSelection(2);
        // устанавливаем обработчик нажатия
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view,
          int position, long id) {
        // показываем позиция нажатого элемента
        Toast.makeText(getBaseContext(), "Position = " + position, Toast.LENGTH_SHORT).show();
      }
      @Override
      public void onNothingSelected(AdapterView<?> arg0) {
      }
    });
 */

/*


No, You can't set id with char, String or anything else except int...because, id is maintained by R.java file which contains only int.

You can use setTag() instead of setId().

Use setTag() as below...

edText.setTag("title");

You can later check it using getTag() edText.getTag().

You can use findViewWithTag in order to find the view with a specific tag.

 */