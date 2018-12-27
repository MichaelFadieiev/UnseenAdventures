package com.game.michael.first;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.TextAppearanceSpan;
import android.text.style.TypefaceSpan;
import android.util.ArrayMap;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.TreeMap;

import static com.game.michael.first.Actions.*;
import static com.game.michael.first.BasicProcedures.*;
import static com.game.michael.first.SourceJSON.getUPlaceArray;
import static com.game.michael.first.SourceJSON.readJSON;
import static com.game.michael.first.SourceJSON.readOption;
import static com.game.michael.first.SourceJSON.writeOption;
import static java.lang.Math.round;

public class GameActivity extends Activity {

    GameActivity thisGame;

    String optionLanguage;
    //int[] rng_time = {10, 14, 28, 32, 20, 32};
    static int[] timeConst = {12, 24, 60}; //месяцев в году, часов в дне, минут в часе {12, 24, 60}
    static int[] dayMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31}; //дней в месяцах {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31}

    SparseBooleanArray actSpec, actSpecTmp;

    //лучше использовать другой тип данных!
    public ArrayMap<int[], int[]> timeStack; //stack of planned actions first int[] is
    // planned date+order_of_execution(FIFO), second int[] is a key of
    //action, with further parameters.

    TreeMap<String, int[]> journalQuest; //журнал записей важных событий в игре. Ключ - дата-время,
                                         //значение - массив кодов записи (код события, код сообщения)
    //Журнальная запись:
    /*{
    "dttm" : [0, 0, 0, 0, 0],
    "id" : 0,                   знакомство
    "string" : "",              "Вы познакомились с "
    "tags" : ["", ""]           "Мартин Иден", "Стивен Кинг"
    "place_id" : 0,             1
    "territory_id": [0, 0]      (0, 0)
    }*/


    TreeMap<String, int[]> journalWorld;//журнал записей общих событий в игре. Ключ - дата-время,
                                        //значение - массив кодов записи (код события, код сообщения)

    LinearLayout choicesLL, messagesLL;
    LinearLayout memoMesLL, memoOptLL;

    SparseArray<PersonType> allPersons;
    SparseArray<PlaceType> allPlaces;
    ArrayMap<String, TerritoryType> allTerritories;

    int[] dateTime;//startYear, startMonth, startDay, startHour, startMinute (учесть счет с нуля для месяца и дня!!!)

    ArrayMap<int[], String[]> messages; //Выяснить что то за нах и как устроено!

    PersonType actorP;
    //LocationType locationP;

    //boolean[] actionBlocking;
    //SparseArray<Actions> messages; // textID, colorCode,
    //SparseArray<ArrayMap<String[], Integer[]>> actions; //group/type

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_layout);

        allPersons = new SparseArray<>(1);
        allPlaces = new SparseArray<>(1);
        allTerritories = new ArrayMap<>(1);

        messages = new ArrayMap<>(1);

        timeStack = new ArrayMap<>(10);
        dateTime = new int[5];

        actSpec = new SparseBooleanArray(1);
        actSpecTmp = new SparseBooleanArray(1);

        journalQuest = new TreeMap<>();
        journalWorld = new TreeMap<>();

        switch (getIntent().getStringExtra("type")) {
            case ("new"): gameNew(getIntent().getStringExtra("name"),
                                getIntent().getStringExtra("surname"),
                                getIntent().getIntArrayExtra("skill_keys"),
                                getIntent().getFloatArrayExtra("skills"),
                                getIntent().getFloatArrayExtra("attributes"));
                break;
            case ("load"): gameLoad();
                break;
            default: Toast toast = Toast.makeText(this, "Нет параметра создания/загрузки игры", Toast.LENGTH_SHORT);
                toast.show();
                this.finish();
        }
        loadOption();
        //actionBlocking = new boolean[30];
        //for (int i=0; i<actionBlocking.length; i++) {actionBlocking[i]=false;}
    }

    @Override
    protected void onStart() {
        TextView gameTV, statsTV, inventoryTV, memoryTV;
        LinearLayout gameLL, statsLL, inventoryLL, memoryLL;

        super.onStart();

        gameTV = (TextView) findViewById(R.id.v3_gameTV);
        statsTV = (TextView) findViewById(R.id.v3_statsTV);
        inventoryTV = (TextView) findViewById(R.id.v3_inventoryTV);
        memoryTV = (TextView) findViewById(R.id.v3_memoryTV);

        gameLL = (LinearLayout) findViewById(R.id.v3_tab_game);
        statsLL = (LinearLayout) findViewById(R.id.v3_tab_stats);
        inventoryLL = (LinearLayout) findViewById(R.id.v3_tab_inventory);
        memoryLL = (LinearLayout) findViewById(R.id.v3_tab_memory);

        gameLL.setVisibility(View.VISIBLE);
        statsLL.setVisibility(View.GONE);
        inventoryLL.setVisibility(View.GONE);
        memoryLL.setVisibility(View.GONE);

        gameTV.setVisibility(View.GONE);
        statsTV.setVisibility(View.VISIBLE);
        inventoryTV.setVisibility(View.VISIBLE);
        memoryTV.setVisibility(View.VISIBLE);

        messagesLL = (LinearLayout) findViewById(R.id.v3_messagesLL);
        choicesLL = (LinearLayout) findViewById(R.id.v3_choicesLL);
        memoMesLL = (LinearLayout) findViewById(R.id.v3_memoMesLL);
        memoOptLL = (LinearLayout) findViewById(R.id.v3_memoOptLL);

        if (actorP.stats.get(0) > 0) {renewChoices();}
        loadOption();
        turn(0, new int[]{0, 0}, false);
        thisGame = this;
    }

    public void gameNew (String p_name, String p_surname, int[] p_skillKeys, float[] p_skills, float[] p_attributes) {
        //receive parameters from creation activity
        SparseArray<Float> attributesTemp = new SparseArray<>();
        SparseArray<Float> skillsTemp = new SparseArray<>();

        for (int i=0; i<p_skillKeys.length; i++) {
            skillsTemp.put(p_skillKeys[i], p_skills[i]);
        }
        for (int i=0; i<p_attributes.length; i++) {
            attributesTemp.put(i, p_attributes[i]);
        }


        int[] coordinatesP = getResources().getIntArray(R.array.startPosition);
        int territoryTypeID = getResources().getInteger(R.integer.startTerritoryTypeID);
        int placeTypeID = getResources().getInteger(R.integer.startPlaceTypeID);
        TerritoryType territoryTMP = new TerritoryType(this, 1, coordinatesP, territoryTypeID);
        territoryTMP.createPlace(this, placeTypeID, 1);
        allTerritories.put(coordinatesToString(coordinatesP), territoryTMP);

        allPlaces.get(1).makeUnique(this, 0);
        //place creation
        //PlaceType placeTMP = new PlaceType(this, 1, coordinatesP, placeTypeID, 1);
        //placeTMP.visitorsList.put(actorP.personID, actorP);
        //locationP = placeTMP;
        //actorP.placeID = placeTMP.placeID;

        //territory creation
        //territoryTMP.placesList.put(placeTMP.placeID, placeTMP);
        //showInfo(String.valueOf(territoryTMP.placesList.indexOfKey(1)));
        //showInfo(String.valueOf(allTerritories.get(allPlaces.get(actorP.locationID[0]).territoryID).placesList.indexOfKey(1)));
        //actorP.territoryID = coordinatesP.clone();
        //System.arraycopy(coordinatesP, 0, actorP.locationID, 1, 2);

        //person creation
        actorP = new PersonType(1, 0,
                p_name,
                p_surname,
                attributesTemp,
                skillsTemp,
                true,
                'M');
        allPersons.put(actorP.personID, actorP);
        try {
            settlePerson(this, actorP, allPlaces.get(1));
        } catch (Exception e) {
            showInfo("Can't place actor in place");
            this.finish();
        }
        //time initialisation
        System.arraycopy(getResources().getIntArray(R.array.startDateTime), 0, dateTime, 0, 5);

        //assign actor reference
        //actorP = allTerritories.get(coordinatesP).placesList.get(placeTMP.placeID).visitorsList.get(0);

        journalQuest.put(timeToString(dateTime), new int[]{0, 0});
    }

    public void gameLoad () {


    }

    public void catchGenom () {

    }

    public void renewMessages (boolean p_isNew) {
        if (p_isNew) {messagesLL.removeAllViews();}


        TextView v_tempTV; // = (TextView) findViewById(R.id.v3_gameTV);
        for (int i=0; i < messages.size(); i++) {
            switch (messages.keyAt(i)[0]) {
                case 0: v_tempTV = new TextView(getApplicationContext()); //regular text info
                    v_tempTV.setText(messages.valueAt(i)[0]);
                    v_tempTV.setTextAppearance(getApplicationContext(), R.style.v3_messageGrey);
                    messagesLL.addView(v_tempTV);
                    break;
                case 1: v_tempTV = new TextView(getApplicationContext()); //status text info
                    v_tempTV.setText(messages.valueAt(i)[0]);
                    v_tempTV.setTextAppearance(getApplicationContext(), R.style.v3_messageYellow);
                    messagesLL.addView(v_tempTV);
                    break;
                case 999: v_tempTV = new TextView(getApplicationContext()); //death text info
                    v_tempTV.setText(messages.valueAt(i)[0]);
                    v_tempTV.setTextAppearance(getApplicationContext(), R.style.v3_messageRed);
                    messagesLL.addView(v_tempTV);
                    break;
                default: Toast toast = Toast.makeText(this, "Не определен код сообщения", Toast.LENGTH_SHORT);
                    toast.show();
            }
        }
        messages.clear();
    }

    public void renewChoices () {

        choicesLL.removeAllViews();
        if (actorP.stats.get(0) <= 0.1f) {
            int[] v_mesParI = {0};
            String[] v_mesParS = new String[1];
            v_mesParS[0] = BasicProcedures.formatDate(dateTime);
            messages.put(v_mesParI, v_mesParS);
            v_mesParI[0] = 999;
            v_mesParS[0] = "Вы скончались. Ваша история останется " +
                    "неизвестной. Остаётся надеяться, что жизнь была прожита не зря.";
            messages.put(v_mesParI, v_mesParS);
            renewMessages(false);
            //return;
            actSpec.clear();
            //actorP.actBasic.clear(); проверка внутри персонажа
            actSpec.put(3, true);
        } else if (actorP.stats.get(3) <= 0) {
            int[] v_mesParI = {0};
            String[] v_mesParS = new String[1];
            v_mesParS[0] = BasicProcedures.formatDate(dateTime);
            messages.put(v_mesParI, v_mesParS);
            v_mesParI[0] = 999;
            v_mesParS[0] = "Вы скончались от голода. Ваша история останется " +
                    "неизвестной. Остаётся надеяться, что жизнь была прожита не зря.";
            messages.put(v_mesParI, v_mesParS);
            renewMessages(false);
            //return;
            actSpec.clear();
            actorP.actBasic.clear();
            actSpec.put(3, true);
        } else if (actorP.stats.get(4) <= 0) {
            int[] v_mesParI = {0};
            String[] v_mesParS = new String[1];
            v_mesParS[0] = BasicProcedures.formatDate(dateTime);
            messages.put(v_mesParI, v_mesParS);
            v_mesParI[0] = 999;
            v_mesParS[0] = "Вы скончались от жажды. Ваша история останется " +
                    "неизвестной. Остаётся надеяться, что жизнь была прожита не зря.";
            messages.put(v_mesParI, v_mesParS);
            renewMessages(false);
            //return;
            actSpec.clear();
            actorP.actBasic.clear();
            actSpec.put(3, true);
        } else if (actorP.stats.get(1) <= 0) {
            int[] v_mesParI = {0};
            String[] v_mesParS = new String[1];
            v_mesParS[0] = BasicProcedures.formatDate(dateTime);
            messages.put(v_mesParI, v_mesParS);
            v_mesParI[0] = 1;
            v_mesParS[0] = "Вы обессилены!";
            messages.put(v_mesParI, v_mesParS);
            renewMessages(false);
            //старая реализация
            /*TextView tempTV = new TextView(getApplicationContext());
            tempTV.setText("...");
            tempTV.setTextAppearance(getApplicationContext(), R.style.v3_choiceMarine);
            tempTV.setVisibility(View.VISIBLE);
            tempTV.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) { turn(0, new int[]{1});}//параметр времени - заглушка, после тестирования поставить расчеты
            });
            choicesLL.addView(tempTV);*/
            //новая реализация
            actSpecTmp = actSpec.clone();
            actorP.actBasicTmp = actorP.actBasic.clone();
            actSpec.clear();
            actorP.actBasic.clear();
            actorP.actBasic.put(0, true);
            actorP.actBasic.put(1, true);
            //return;
        } else if (actorP.stats.get(2) <= 0) {
            int[] v_mesParI = {0};
            String[] v_mesParS = new String[1];
            v_mesParS[0] = BasicProcedures.formatDate(dateTime);
            messages.put(v_mesParI, v_mesParS);
            v_mesParI[0] = 1;
            v_mesParS[0] = "Вы потеряли сознание!";
            messages.put(v_mesParI, v_mesParS);
            renewMessages(false);
            //старая реализация
            /*TextView tempTV = new TextView(getApplicationContext());
            tempTV.setText("...");
            tempTV.setTextAppearance(getApplicationContext(), R.style.v3_choiceMarine);
            tempTV.setVisibility(View.VISIBLE);
            tempTV.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    turn(0, new int[]{Math.round(300f / actorP.attributes.get(4))});
                }//параметр времени - заглушка, после тестирования поставить расчеты
            });
            choicesLL.addView(tempTV);*/
            //timeStackAdd (); //deprecated!!!
            //timeStackExecute (10, actorP.personID);
            /*v_mesParI[0] = 0;
            v_mesParS[0] = timeString(dateTime);
            messages.put(v_mesParI, v_mesParS);
            v_mesParI[0] = 1;
            v_mesParS[0] = "Вы очнулись!";
            messages.put(v_mesParI, v_mesParS);
            renewMessages(false);*/
            //return;
            //новая реализация
            actSpecTmp = actSpec.clone();
            actorP.actBasicTmp = actorP.actBasic.clone();
            actSpec.clear();
            actorP.actBasic.clear();
            actorP.actBasic.put(1, true);
        }

        TextView tempTV;
        //Генерация списка специальных действий
        for (int i = 0; i < actSpec.size(); i++) {
            if (actSpec.valueAt(i)) {
                tempTV = new TextView(getApplicationContext());
                switch (actSpec.keyAt(i)) {
                    case 1:
                        tempTV.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                turn(-1, new int[]{5}, true);
                            }
                        });
                        break;
                    case 2:
                        tempTV.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                turn(-2, new int[]{15}, true);
                            }
                        });
                        break;
                    case 3:
                        tempTV.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                turn(-3, new int[]{1440}, true);
                            }
                        });
                        break;
                    default:
                        break;
                }
                tempTV.setText(getResources().getStringArray(R.array.ru_actionUName)[actSpec.keyAt(i) - 1]);
                tempTV.setTextAppearance(getApplicationContext(), R.style.v3_messageYellow);
                choicesLL.addView(tempTV);
            }
        }

        //Генерация списка стандартных действий
        for (int i = 0; i < actorP.actBasic.size(); i++) {
            if (actorP.actBasic.valueAt(i)) {
                tempTV = new TextView(getApplicationContext());
                switch (i) {
                    case 0:
                        tempTV.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                turn(0, new int[]{15}, true);
                            }
                        });
                        break;
                    case 1:
                        tempTV.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                turn(1, new int[]{60, 1}, true);
                            }
                        });
                        break;
                    case 2:
                        tempTV.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                turn(2, new int[]{1}, true);
                            }
                        });
                        break;
                    case 3:
                        tempTV.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                turn(3, new int[]{1}, true);
                            }
                        });
                        break;
                    case 4:
                        tempTV.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                action4();
                            }
                        });
                        break;
                }
                tempTV.setText(getResources().getStringArray(R.array.ru_actionName)[actorP.actBasic.keyAt(i)]);
                tempTV.setTextAppearance(getApplicationContext(), R.style.v3_choiceMarine);
                choicesLL.addView(tempTV);
            }
        }
            /*tempTV = new TextView(this);
            tempTV.setText("Встать");
            tempTV.setVisibility(View.VISIBLE);
            tempTV.setTextAppearance(this, R.style.text);
            tempTV.setTextColor(Color.GREEN);
            tempTV.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            tempTV.setBackgroundColor(getResources().getColor(R.color.clr_dark_grey));
            //tempTV.setId(5);
            tempTV.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    tempAction0(15, actorP.personID);
                    actorP.stats.put(0, (float) 0);
                    messages.put(new int[]{0}, new String[]{timeString(dateTime)});
                    messages.put(new int[]{0}, new String[]{"Вы попытались пошевелиться, но непонятный хруст в позвоночнике прервал ваши попытки."});
                    renewMessages(true);
                    renewChoices();
                }
            });
            choicesLL.addView(tempTV);*/
        /**/
    }

    //Переход между вкладками игрового окна.
    public void tabChange (View v) {
        TextView gameTV, statsTV, inventoryTV, memoryTV;
        LinearLayout gameLL, statsLL, inventoryLL, memoryLL;

        gameTV = (TextView) findViewById(R.id.v3_gameTV);
        statsTV = (TextView) findViewById(R.id.v3_statsTV);
        inventoryTV = (TextView) findViewById(R.id.v3_inventoryTV);
        memoryTV = (TextView) findViewById(R.id.v3_memoryTV);

        gameLL = (LinearLayout) findViewById(R.id.v3_tab_game);
        statsLL = (LinearLayout) findViewById(R.id.v3_tab_stats);
        inventoryLL = (LinearLayout) findViewById(R.id.v3_tab_inventory);
        memoryLL = (LinearLayout) findViewById(R.id.v3_tab_memory);

        gameLL.setVisibility(View.GONE);
        statsLL.setVisibility(View.GONE);
        inventoryLL.setVisibility(View.GONE);
        memoryLL.setVisibility(View.GONE);

        gameTV.setVisibility(View.VISIBLE);
        statsTV.setVisibility(View.VISIBLE);
        inventoryTV.setVisibility(View.VISIBLE);
        memoryTV.setVisibility(View.VISIBLE);

        switch(v.getId()) {
            case R.id.v3_gameTV:
                //renewChoices();
                gameLL.setVisibility(View.VISIBLE);
                gameTV.setVisibility(View.GONE);
                break;
            case R.id.v3_statsTV:
                renewStats();
                statsLL.setVisibility(View.VISIBLE);
                statsTV.setVisibility(View.GONE);
                break;
            case R.id.v3_inventoryTV:
                renewInventory();
                inventoryLL.setVisibility(View.VISIBLE);
                inventoryTV.setVisibility(View.GONE);
                break;
            case R.id.v3_memoryTV:
                renewMemo();
                memoryLL.setVisibility(View.VISIBLE);
                memoryTV.setVisibility(View.GONE);
                break;
            default: break;
        }
    }

    //Проверка списка вещей на законченый срок годности и чистка
    /*public ArrayList<Integer> checkBrokenItems (ArrayList<ItemType> p_items) {
        for (ItemType i : p_items) {
            if (i.typeID > getResources().getInteger(R.integer.cns_eternalItemsID)) {
                i.brokeItem(dateTime);
            }
        }
        return null;
    }*/

    //Процедура запуска вкладки характеристик персонажа.
    public void renewStats () {
        LinearLayout skillsLL, factorsLL;
        TextView STR_valTV, INT_valTV, PRC_valTV, AGL_valTV, END_valTV, CHR_valTV;
        TextView hpValueTV, stmValueTV, exhValueTV, hngValueTV, thrValueTV;

        TextView actorNameTV = findViewById(R.id.v3_actorNameTV);
        TextView actorSurnameTV = findViewById(R.id.v3_actorSurnameTV);
        TextView actorGenderTV = findViewById(R.id.v3_actorGenderTV);
        actorNameTV.setText(actorP.name);
        actorSurnameTV.setText(actorP.surname);
        actorGenderTV.setText(getInfoMessage(this, actorP.gender == 'M' ? "GenderMale" : "GenderFemale", optionLanguage));

        STR_valTV = findViewById(R.id.v3_STR_val_TV);
        STR_valTV.setText(String.valueOf(round(actorP.attributes.get(0))));
        INT_valTV = findViewById(R.id.v3_INT_val_TV);
        INT_valTV.setText(String.valueOf(round(actorP.attributes.get(1))));
        PRC_valTV = findViewById(R.id.v3_PRC_val_TV);
        PRC_valTV.setText(String.valueOf(round(actorP.attributes.get(2))));
        AGL_valTV = findViewById(R.id.v3_AGL_val_TV);
        AGL_valTV.setText(String.valueOf(round(actorP.attributes.get(3))));
        END_valTV = findViewById(R.id.v3_END_val_TV);
        END_valTV.setText(String.valueOf(round(actorP.attributes.get(4))));
        CHR_valTV = findViewById(R.id.v3_CHR_val_TV);
        CHR_valTV.setText(String.valueOf(round(actorP.attributes.get(5))));

        //TEMP code:
        hpValueTV = (TextView) findViewById(R.id.v3_hpValueTV);
        hpValueTV.setText(String.valueOf(Math.round(actorP.stats.get(0))));
        stmValueTV = (TextView) findViewById(R.id.v3_stmValueTV);
        stmValueTV.setText(String.valueOf(Math.round(actorP.stats.get(1))));
        exhValueTV = (TextView) findViewById(R.id.v3_exhValueTV);
        exhValueTV.setText(String.valueOf(Math.round(actorP.stats.get(2))));
        hngValueTV = (TextView) findViewById(R.id.v3_hngValueTV);
        hngValueTV.setText(String.valueOf(Math.round(actorP.stats.get(3))));
        thrValueTV = (TextView) findViewById(R.id.v3_thrValueTV);
        thrValueTV.setText(String.valueOf(Math.round(actorP.stats.get(4))));
        //Раскомментировать и переработать в цикл с тэгами после тестирования
        /*hpValueTV = (TextView) findViewById(R.id.v3_hpValueTV);
        int temp1 = round(actorP.stats.get(0)/actorP.statsMax.get(0))*100;
        int[] temp2 = getResources().getIntArray(R.array.rng_hp_num);
        String[] temp3 = getResources().getStringArray(R.array.rng_hp_txt);
        int[] temp4 = {Color.RED, Color.RED, Color.YELLOW, Color.YELLOW, Color.GREEN};
        for (int i=temp2.length; i>0; i--) {
            if (temp1>temp2[i-1]) {
                hpValueTV.setText(temp3[i]);
                hpValueTV.setTextColor(temp4[i]);
                break;
            }
            if (temp1==temp2[0]) {
                hpValueTV.setText(temp3[0]);
                hpValueTV.setTextColor(temp4[0]);
                break;
            }
        }

        stmValueTV = (TextView) findViewById(R.id.v3_stmValueTV);
        temp1 = round(actorP.stats.get(1)/actorP.statsMax.get(1))*100;
        temp2 = getResources().getIntArray(R.array.rng_stm_num);
        temp3 = getResources().getStringArray(R.array.rng_stm_txt);
        for (int i=temp2.length; i>0; i--) {
            if (temp1>temp2[i-1]) {
                stmValueTV.setText(temp3[i]);
                stmValueTV.setTextColor(temp4[i]);
                break;
            }
            if (temp1==temp2[0]) {
                stmValueTV.setText(temp3[0]);
                stmValueTV.setTextColor(temp4[0]);
                break;
            }
        }

        exhValueTV = (TextView) findViewById(R.id.v3_exhValueTV);
        temp1 = round(actorP.stats.get(2)/actorP.statsMax.get(2))*100;
        temp2 = getResources().getIntArray(R.array.rng_exh_num);
        temp3 = getResources().getStringArray(R.array.rng_exh_txt);
        for (int i=temp2.length; i>0; i--) {
            if (temp1>temp2[i-1]) {
                exhValueTV.setText(temp3[i]);
                exhValueTV.setTextColor(temp4[i]);
                break;
            }
            if (temp1==temp2[0]) {
                exhValueTV.setText(temp3[0]);
                exhValueTV.setTextColor(temp4[0]);
                break;
            }

        }

        hngValueTV = (TextView) findViewById(R.id.v3_hngValueTV);
        temp1 = round(actorP.stats.get(3)/actorP.statsMax.get(3))*100;
        temp2 = getResources().getIntArray(R.array.rng_hng_num);
        temp3 = getResources().getStringArray(R.array.rng_hng_txt);
        for (int i=temp2.length; i>0; i--) {
            if (temp1>temp2[i-1]) {
                hngValueTV.setText(temp3[i]);
                hngValueTV.setTextColor(temp4[i]);
                break;
            }
            if (temp1==temp2[0]) {
                hngValueTV.setText(temp3[0]);
                hngValueTV.setTextColor(temp4[0]);
                break;
            }

        }

        thrValueTV = (TextView) findViewById(R.id.v3_thrValueTV);
        temp1 = round(actorP.stats.get(4)/actorP.statsMax.get(4))*100;
        temp2 = getResources().getIntArray(R.array.rng_thr_num);
        temp3 = getResources().getStringArray(R.array.rng_thr_txt);
        for (int i=temp2.length; i>0; i--) {
            if (temp1>temp2[i-1]) {
                thrValueTV.setText(temp3[i]);
                thrValueTV.setTextColor(temp4[i]);
                break;
            }
            if (temp1==temp2[0]) {
                thrValueTV.setText(temp3[0]);
                thrValueTV.setTextColor(temp4[0]);
                break;
            }
        }*/
        /*Раскоментить для отображения уровней навыков вместо величин
        int[] skill_groups = getResources().getIntArray(R.array.rng_skills);
        String[] skill_levels = getResources().getStringArray(R.array.rng_skills_txt);*/

        skillsLL = (LinearLayout) findViewById(R.id.v3_skillsLL);
        skillsLL.removeAllViews();
        LinearLayout skillRowLL;
        TextView textTV1, textTV2;
        for (int j=0; j < actorP.skills.size(); j++) {
            skillRowLL = new LinearLayout(getApplicationContext());
            textTV1 = new TextView(getApplicationContext());

            textTV1.setText(getString(getResources().getIdentifier("skl_" + String.valueOf(actorP.skills.keyAt(j)),
                    "string",
                    getPackageName())));
            textTV2 = new TextView(getApplicationContext());
            /*for (int i=skill_groups.length-1; i >= 0; i--) {
                if (actorP.skills.get(actorP.skills.keyAt(j)) >= skill_groups[i]) {
                    textTV2.setText(skill_levels[i]);
                    break;
                }
            }*/
            textTV2.setText(String.valueOf(actorP.skills.get(actorP.skills.keyAt(j))));//временно, с целью тестирования
            //LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
            textTV1.setTextAppearance(this, R.style.text);
            textTV1.setTextColor(Color.GRAY);
            textTV1.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
            textTV2.setTextAppearance(this, R.style.text);
            textTV2.setTextColor(Color.GREEN);
            textTV2.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));

            skillRowLL.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
            skillRowLL.addView(textTV1);
            skillRowLL.addView(textTV2);
            skillsLL.addView(skillRowLL);
        }
        factorsLL = (LinearLayout) findViewById(R.id.v3_factorsLL);
        factorsLL.removeAllViews();
        TextView factorTV;
        for (int k=0; k < actorP.factors.size(); k++) {
            factorTV = new TextView(getApplicationContext());
            factorTV.setTag(actorP.factors.keyAt(k));
            factorTV.setText(getFactorName(this, actorP.factors.keyAt(k), optionLanguage));
            factorTV.setOnClickListener(new View.OnClickListener() {
                public void onClick (View v) {
                    showInfo(getFactorDescr(thisGame, (int) v.getTag(), optionLanguage));
                }
            });
            factorsLL.addView(factorTV);
        }
    }

    //Процедура запуска вкладки инвентаря.
    private void renewInventory () {
        //загрузка экипированных предметов
        LinearLayout headLL = (LinearLayout) findViewById(R.id.v3_headLL);
        LinearLayout torsoLL = (LinearLayout) findViewById(R.id.v3_torsoLL);
        LinearLayout legsLL = (LinearLayout) findViewById(R.id.v3_legsLL);
        LinearLayout handsLL = (LinearLayout) findViewById(R.id.v3_handsLL);
        LinearLayout feetLL = (LinearLayout) findViewById(R.id.v3_feetLL);
        //TextView rHandTV =
        //TextView lHandTV = (TextView) findViewById(R.id.v3_lHandTV);
        /*if (actorP.equipped.indexOfKey(0) >= 0) {
            ItemType itemTmp = actorP.equipped.get(0);
            rHandTV.setText(itemTmp.itemID);
            if (itemTmp.endDT.size() == 1) {
                rHandTV.setText(getItemNameS(this, itemTmp.itemID, optionLanguage));
            } else if (itemTmp.endDT.size() > 1) {
                rHandTV.setText(getItemNameP(this, itemTmp.itemID, optionLanguage)
                        + " (" + String.valueOf(itemTmp.endDT.size()) + ")");
            } else rHandTV.setText("");
            rHandTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        } else {
            rHandTV.setText(null);
            rHandTV.setOnClickListener(null);
        }
        if (actorP.equipped.indexOfKey(1) >= 0) {
            lHandTV.setText(null);
        } else {
            lHandTV.setText(null);
            lHandTV.setOnClickListener(null);
        }*/
        ((TextView) findViewById(R.id.v3_rHandTV)).setText(null);
        ((TextView) findViewById(R.id.v3_rHandTV)).setOnClickListener(null);
        ((TextView) findViewById(R.id.v3_lHandTV)).setText(null);
        ((TextView) findViewById(R.id.v3_lHandTV)).setOnClickListener(null);
        headLL.removeAllViews();
        torsoLL.removeAllViews();
        legsLL.removeAllViews();
        handsLL.removeAllViews();
        feetLL.removeAllViews();
        for (int i = 0; i < 12; i++) {
            if (actorP.equipped.indexOfKey(i) >= 0) {
                ItemType itemTMP = actorP.equipped.get(i);
                final TextView viewTMP;
                if (i==0) {
                    viewTMP = (TextView) findViewById(R.id.v3_rHandTV);
                } else if (i==1) {
                    viewTMP = (TextView) findViewById(R.id.v3_lHandTV);
                } else {
                    viewTMP = new TextView(getApplicationContext());
                }
                String strTmp = "";
                if (itemTMP.endDT.size() == 1) {
                    strTmp = getItemNameS(this, itemTMP.itemID, optionLanguage);
                    if (itemTMP.isJar) if (!itemTMP.contain.isEmpty()) {
                        strTmp += " ("
                                + getItemNameS(this, itemTMP.contain.get(0).itemID, optionLanguage).toLowerCase()
                                + " " + String.valueOf(itemTMP.contain.get(0).endDT.size() * itemTMP.contain.get(0).volume)
                                + " мл)";
                    }
                } else if (itemTMP.endDT.size() > 1) {
                    strTmp = getItemNameP(this, itemTMP.itemID, optionLanguage)
                            + " (" + String.valueOf(itemTMP.endDT.size()) + ")";
                }
                viewTMP.setText(strTmp);
                viewTMP.setTag(i);
                viewTMP.setTextAppearance(thisGame, R.style.gameItem);
                viewTMP.setOnLongClickListener(new View.OnLongClickListener() {
                    public boolean onLongClick(View v) {
                        ItemType itemTmp = actorP.equipped.get((int) v.getTag());
                        String strTMP = getItemDesc(thisGame, itemTmp.itemID, optionLanguage);
                        if (itemTmp.isJar) {
                            if (itemTmp.contain.isEmpty()) {
                                strTMP += ". " + String.format(getLabelGeneral(thisGame, "Contain", optionLanguage),
                                        getLabelGeneral(thisGame, "Empty", optionLanguage).toLowerCase());
                            } else {
                                strTMP += ". " + String.format(getLabelGeneral(thisGame, "Contain", optionLanguage),
                                        getItemNameS(thisGame, itemTmp.contain.get(0).itemID, optionLanguage).toLowerCase())
                                        + " " + getItemDesc(thisGame, itemTmp.contain.get(0).itemID, optionLanguage);
                            }
                        } else if (itemTmp.isContainer) {
                            if (itemTmp.contain.isEmpty()) {
                                strTMP += ". " + String.format(getLabelGeneral(thisGame, "Contain", optionLanguage),
                                        getLabelGeneral(thisGame, "Empty", optionLanguage).toLowerCase());
                            } else {
                                strTMP += ". " + getLabelGeneral(thisGame, "ContainItems", optionLanguage);
                            }
                        }
                        showInfo(strTMP);
                        return true;
                    }
                });
                viewTMP.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        PopupMenu pUM = new PopupMenu(getApplicationContext(), viewTMP);
                        showItemMenu(pUM, actorP.equipped.get((int) view.getTag()), true);
                        pUM.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                switch (menuItem.getItemId()) {
                                    case R.id.menuGameItemToInventory: {
                                        if (actorP.itemAdd(actorP.equipped.get((int) view.getTag()), actorP.equipped.get((int) view.getTag()).endDT.size())) {
                                            showInfo(getInfoMessage(thisGame, "ItemToInventory", optionLanguage));
                                            if (actorP.equipped.get((int) view.getTag()).endDT.isEmpty()) {actorP.equipped.delete((int) view.getTag());}
                                            renewUActions();
                                            renewInventory();
                                        } else {showInfo(getInfoMessage(thisGame, "ItemFailed", optionLanguage));}
                                        return true;
                                    }
                                    case R.id.menuGameItemUse: {
                                        showInfo(getInfoMessage(thisGame, "NotImplemented", optionLanguage));
                                        return true;
                                    }
                                    case R.id.menuGameItemOpen: {
                                        showInfo(getInfoMessage(thisGame, "NotImplemented", optionLanguage));
                                        return true;
                                    }
                                    case R.id.menuGameItemStats: {
                                        showInfo(getInfoMessage(thisGame, "NotImplemented", optionLanguage));
                                        return true;
                                    }
                                    case R.id.menuGameItemEat: {
                                        actorP.itemConsume(thisGame, actorP.equipped.get((int) view.getTag()).takeItem(1));
                                        if (actorP.equipped.get((int) view.getTag()).endDT.isEmpty()) {actorP.equipped.remove((int) view.getTag());}
                                        showInfo(getInfoMessage(thisGame, "ItemEat", optionLanguage));
                                        actorP.renewActions(thisGame);
                                        renewUActions();
                                        renewInventory();
                                        return true;
                                    }
                                    case R.id.menuGameItemEatAll: {
                                        actorP.itemConsume(thisGame, actorP.equipped.get((int) view.getTag()).takeItem(actorP.equipped.get((int) view.getTag()).endDT.size()));
                                        if (actorP.equipped.get((int) view.getTag()).endDT.isEmpty()) {actorP.equipped.remove((int) view.getTag());}
                                        showInfo(getInfoMessage(thisGame, "ItemEatAll", optionLanguage));
                                        actorP.renewActions(thisGame);
                                        renewUActions();
                                        renewInventory();
                                        return true;
                                    }
                                    case R.id.menuGameItemDrink: {
                                        actorP.itemConsume(thisGame, actorP.equipped.get((int) view.getTag()).contain.get(0).takeItem(1));
                                        if (actorP.equipped.get((int) view.getTag()).contain.get(0).endDT.isEmpty()) {actorP.equipped.get((int) view.getTag()).contain.clear();}
                                        showInfo(getInfoMessage(thisGame, "ItemDrink", optionLanguage));
                                        actorP.renewActions(thisGame);
                                        renewUActions();
                                        renewInventory();
                                        return true;
                                    }
                                    case R.id.menuGameItemDrinkAll: {
                                        actorP.itemConsume(thisGame, actorP.equipped.get((int) view.getTag()).contain.get(0).takeItem(actorP.equipped.get((int) view.getTag()).contain.get(0).endDT.size()));
                                        if (actorP.equipped.get((int) view.getTag()).contain.get(0).endDT.isEmpty()) {actorP.equipped.get((int) view.getTag()).contain.clear();}
                                        showInfo(getInfoMessage(thisGame, "ItemDrinkAll", optionLanguage));
                                        actorP.renewActions(thisGame);
                                        renewUActions();
                                        renewInventory();
                                        return true;
                                    }
                                    case R.id.menuGameItemDrop: {
                                        actorP.itemDrop(thisGame, (int) view.getTag(), actorP.equipped.get((int) view.getTag()).endDT.size(), true);
                                        showInfo(getInfoMessage(thisGame, "ItemDrop", optionLanguage));
                                        actorP.renewActions(thisGame);
                                        renewUActions();
                                        renewInventory();
                                        return true;
                                    }
                                    case R.id.menuGameItemDropAll: {
                                        actorP.itemDrop(thisGame, (int) view.getTag(), actorP.equipped.get((int) view.getTag()).endDT.size(), true);
                                        showInfo(getInfoMessage(thisGame, "ItemDropAll", optionLanguage));
                                        actorP.renewActions(thisGame);
                                        renewUActions();
                                        renewInventory();
                                        return true;
                                    }
                                    default:
                                        break;
                                }
                                return true;
                            }
                        });
                        pUM.show();
                    }
                });
                switch ((i-2)%5) {
                    case 0 : {
                        headLL.addView(viewTMP);
                        break;
                    }
                    case 1 : {
                        torsoLL.addView(viewTMP);
                        break;
                    }
                    case 2 : {
                        legsLL.addView(viewTMP);
                        break;
                    }
                    case 3 : {
                        handsLL.addView(viewTMP);
                        break;
                    }
                    case 4 : {
                        feetLL.addView(viewTMP);
                        break;
                    }
                    default : break;
                }
            }
        }

        //загрузка содержимого инвентаря
        LinearLayout carryLL = (LinearLayout) findViewById(R.id.v3_carryLL);
        carryLL.removeAllViews();
        for (int i=0; i<actorP.inventory.size(); i++) {
            final TextView itemTV = new TextView(getApplicationContext());
            //actorP.inventory.get(i).name = "Золото"; //check for direct modifying of variables;
            String strTmp = "";
            ItemType item_tmp = actorP.inventory.get(i);
            if (item_tmp.endDT.size() == 1) {
                strTmp = getItemNameS(this, item_tmp.itemID, optionLanguage);
                //strTmp += "/" + String.valueOf(item_tmp.endDT.size()) + "/";
                /*strTmp = getResources().getStringArray(getResources().getIdentifier(
                        optionLanguage + "_itemS" + String.valueOf(item_tmp.itemID / 100),
                        "array",
                        getPackageName()))[item_tmp.itemID % 100];*/
                if (item_tmp.isJar) {
                    if (item_tmp.contain.isEmpty()) {
                        strTmp += " (" + getLabelGeneral(this, "Empty", optionLanguage).toLowerCase() + ")";
                    } else {
                        strTmp += " ("
                            + getItemNameS(this, item_tmp.contain.get(0).itemID, optionLanguage).toLowerCase()
                            + " " + String.valueOf(item_tmp.contain.get(0).endDT.size())
                            + "00 мл)";
                    }
                }
                //R.array.ru_itemsS)[actorP.inventory.get(i).itemID];
            } else if (item_tmp.endDT.size() > 1) {
                strTmp = getItemNameP(this, item_tmp.itemID, optionLanguage)
                        + " (" + String.valueOf(item_tmp.endDT.size()) +")";
                /*strTmp = getResources().getStringArray(R.array.ru_itemsP)[actorP.inventory.get(i).itemID] +
                        " (" + String.valueOf(actorP.inventory.get(i).endDT.size()) +")";*/
            };
            itemTV.setTextAppearance(thisGame, R.style.gameItem);
            itemTV.setText(strTmp);
            itemTV.setTag(i);
            itemTV.setOnLongClickListener(new View.OnLongClickListener() {
                public boolean onLongClick(View v) {
                    ItemType itemTmp = actorP.inventory.get((int) v.getTag());
                    String strTMP = getItemDesc(thisGame, itemTmp.itemID, optionLanguage);
                    if (itemTmp.isJar) {
                        if (itemTmp.contain.isEmpty()) {
                            strTMP += " " + String.format(getLabelGeneral(thisGame, "Contain", optionLanguage),
                                    getLabelGeneral(thisGame, "Empty", optionLanguage).toLowerCase());
                        } else {
                            strTMP += " " + String.format(getLabelGeneral(thisGame, "Contain", optionLanguage),
                                    getItemNameS(thisGame, itemTmp.contain.get(0).itemID, optionLanguage).toLowerCase())
                                    + " " + getItemDesc(thisGame, itemTmp.contain.get(0).itemID, optionLanguage);
                        }
                    } else if (itemTmp.isContainer) {
                        if (itemTmp.contain.isEmpty()) {
                            strTMP += " " + String.format(getLabelGeneral(thisGame, "Contain", optionLanguage),
                                    getLabelGeneral(thisGame, "Empty", optionLanguage).toLowerCase());
                        } else {
                            strTMP += " " + getLabelGeneral(thisGame, "ContainItems", optionLanguage);
                        }
                    }
                    showInfo(strTMP);
                    return true;
                }
            });
            itemTV.setOnClickListener(new View.OnClickListener() {
                public void onClick(final View v) {
                    //showInfo(String.valueOf(v.getTag()));
                    /*showInfo(String.valueOf(getResources().getIdentifier(
                            "ru_itemDesc",
                            "array",
                            getPackageName())));*/
                    //context menu
                    PopupMenu pUM = new PopupMenu(getApplicationContext(), itemTV);
                    showItemMenu(pUM, actorP.inventory.get((int) v.getTag()), false);
                    //pUM.inflate(R.menu.itempum);
                    //pUM.getMenu().removeItem(R.id.);
                    pUM.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.menuGameItemUse: {
                                    showInfo(getInfoMessage(thisGame, "NotImplemented", optionLanguage));
                                    return true;
                                }
                                case R.id.menuGameItemUseAll: {
                                    showInfo(getInfoMessage(thisGame, "NotImplemented", optionLanguage));
                                    return true;
                                }
                                case R.id.menuGameItemInHand: {
                                    if (actorP.itemInHand(thisGame, actorP.inventory.get((int) v.getTag()), 1)) {
                                        showInfo(getInfoMessage(thisGame, "ItemInHand", optionLanguage));
                                        if (actorP.inventory.get((int) v.getTag()).endDT.isEmpty()) {actorP.inventory.remove((int) v.getTag());}
                                        renewUActions();
                                        renewInventory();
                                    } else {
                                        showInfo(getInfoMessage(thisGame, "ItemFailed", optionLanguage));
                                    }
                                    return true;
                                }
                                case R.id.menuGameItemEat: {
                                    actorP.itemConsume(thisGame, actorP.inventory.get((int) v.getTag()).takeItem(1));
                                    if (actorP.inventory.get((int) v.getTag()).endDT.isEmpty()) {actorP.inventory.remove((int) v.getTag());}
                                    showInfo(getInfoMessage(thisGame, "ItemEat", optionLanguage));
                                    actorP.renewActions(thisGame);
                                    renewUActions();
                                    renewInventory();
                                    return true;
                                }
                                case R.id.menuGameItemEatAll: {
                                    actorP.itemConsume(thisGame, actorP.inventory.get((int) v.getTag()).takeItem(actorP.inventory.get((int) v.getTag()).endDT.size()));
                                    if (actorP.inventory.get((int) v.getTag()).endDT.isEmpty()) {actorP.inventory.remove((int) v.getTag());}
                                    showInfo(getInfoMessage(thisGame, "ItemEatAll", optionLanguage));
                                    actorP.renewActions(thisGame);
                                    renewUActions();
                                    renewInventory();
                                    return true;
                                }
                                case R.id.menuGameItemOpen: {
                                    showInfo(getInfoMessage(thisGame, "NotImplemented", optionLanguage));
                                    return true;
                                }
                                case R.id.menuGameItemEquip: {
                                    if (actorP.itemEquip(thisGame, actorP.inventory.get((int) v.getTag()))) {
                                        showInfo(getInfoMessage(thisGame, "ItemEquip", optionLanguage));
                                        if (actorP.inventory.get((int) v.getTag()).endDT.isEmpty()) {actorP.inventory.remove((int) v.getTag());}
                                        renewUActions();
                                        renewInventory();
                                    } else {
                                        showInfo(getInfoMessage(thisGame, "ItemFailed", optionLanguage));
                                    }
                                    return true;
                                }
                                case R.id.menuGameItemDrink: {
                                    actorP.itemConsume(thisGame, actorP.inventory.get((int) v.getTag()).contain.get(0).takeItem(1));
                                    if (actorP.inventory.get((int) v.getTag()).contain.get(0).endDT.isEmpty()) {actorP.inventory.get((int) v.getTag()).contain.clear();}
                                    showInfo(getInfoMessage(thisGame, "ItemDrink", optionLanguage));
                                    actorP.renewActions(thisGame);
                                    renewUActions();
                                    renewInventory();
                                    return true;
                                }
                                case R.id.menuGameItemDrinkAll: {
                                    actorP.itemConsume(thisGame, actorP.inventory.get((int) v.getTag()).contain.get(0).takeItem(actorP.inventory.get((int) v.getTag()).contain.get(0).endDT.size()));
                                    if (actorP.inventory.get((int) v.getTag()).contain.get(0).endDT.isEmpty()) {actorP.inventory.get((int) v.getTag()).contain.clear();}
                                    showInfo(getInfoMessage(thisGame, "ItemDrinkAll", optionLanguage));
                                    actorP.renewActions(thisGame);
                                    renewUActions();
                                    renewInventory();
                                    return true;
                                }
                                case R.id.menuGameItemStats: {
                                    showInfo(getInfoMessage(thisGame, "NotImplemented", optionLanguage));
                                    return true;
                                }
                                case R.id.menuGameItemDrop: {
                                    actorP.itemDrop(thisGame, (int) v.getTag(), 1, false);
                                    showInfo(getInfoMessage(thisGame, "ItemDrop", optionLanguage));
                                    actorP.renewActions(thisGame);
                                    renewUActions();
                                    renewInventory();
                                    return true;
                                }
                                case R.id.menuGameItemDropAll: {
                                    actorP.itemDrop(thisGame, (int) v.getTag(), actorP.inventory.size(), false);
                                    showInfo(getInfoMessage(thisGame, "ItemDropAll", optionLanguage));
                                    actorP.renewActions(thisGame);
                                    renewUActions();
                                    renewInventory();
                                    return true;
                                }
                                default: break;
                            }
                            return true;
                        }
                    });
                    pUM.show();
                }
            });
            carryLL.addView(itemTV);
        }
    }

    //Процедура запуска вкладки воспоминаний.
    public void renewMemo () {
        memoMesLL.removeAllViews();
        memoOptLL.removeAllViews();
        //showLog(); //потом убрать, в игре скрыто
        TextView v_tempTV;
        v_tempTV = new TextView(getApplicationContext());
        v_tempTV.setText("Показать лог персонажа");
        v_tempTV.setTextAppearance(getApplicationContext(), R.style.v3_messageYellow);
        v_tempTV.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                memoMesLL.removeAllViews();
                String strTmp;
                for (String keyTmp : actorP.log.keySet()) {
                    TextView v_tempTV = new TextView(getApplicationContext());
                    strTmp = formatDate(timeFromString(keyTmp)) + " - " + actorP.log.get(keyTmp);
                    //strTmp = keyTmp + " - " + actorP.log.get(keyTmp);
                    v_tempTV.setText(strTmp);
                    v_tempTV.setTextAppearance(getApplicationContext(), R.style.v3_messageYellow);
                    memoMesLL.addView(v_tempTV);
                }
            }
        });
        memoOptLL.addView(v_tempTV);
        v_tempTV = new TextView(getApplicationContext());
        v_tempTV.setText("Показать сюжетные события");
        v_tempTV.setTextAppearance(getApplicationContext(), R.style.v3_messageYellow);
        v_tempTV.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                memoMesLL.removeAllViews();
                String strTmp;
                for (String keyTmp : journalQuest.keySet()) {
                    TextView v_tempTV = new TextView(getApplicationContext());
                    strTmp = formatDate(timeFromString(keyTmp)) + " - " + getString(getResources().getIdentifier(
                            optionLanguage + "_journal_q_" + String.valueOf(journalQuest.get(keyTmp)[0]),
                            "string",
                            getPackageName()));
                    //strTmp = keyTmp + " - " + actorP.log.get(keyTmp);
                    v_tempTV.setText(strTmp);
                    v_tempTV.setTextAppearance(getApplicationContext(), R.style.v3_messageYellow);
                    memoMesLL.addView(v_tempTV);
                }
            }
        });
        memoOptLL.addView(v_tempTV);
    }

    //Возвращает дату, которая будет через заданное кол-во минут от входной даты.
    public static int[] flowTime (int[] p_dateTime, int p_minutesDuration) {
        int v_min, v_hrs, v_dys;
        int[] v_DT = new int[5];
        //for (int i =0; i < p_dateTime.length; i++) {v_DT[i] = p_dateTime[i];}
        System.arraycopy(p_dateTime, 0, v_DT, 0, 5);
        v_min = v_DT[4] + p_minutesDuration;
        v_hrs = v_min / timeConst[2];
        v_DT[4] = v_min % timeConst[2];
        if (v_hrs > 0) {
            v_hrs = v_DT[3] + v_hrs;
            v_dys = v_hrs / timeConst[1];
            v_DT[3] = v_hrs % timeConst[1];
            if (v_dys > 0) {
                v_dys += v_DT[2];
                while (v_dys > dayMonth[v_DT[1]-1]) { //условие инкремента месяца
                    v_dys -= dayMonth[v_DT[1]-1];
                    v_DT[1] ++;	//инкремент месяца
                    if (v_DT[1] > timeConst[0]) { //инкремент года
                        v_DT[0]++;
                        v_DT[1] = 1;
                    }
                }
                v_DT[2] = v_dys;
            }
        }
        return v_DT;
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        startActivity(new Intent(this, MenuActivity.class));
    }

    public void goMenu (View v) {
        onBackPressed();
        //this.finish();
    }



    //присваивание статуса действия протагонисту, стак запланированных действий, действие
    // протагониста (через процедуру выбора), проверки, обновляет сообщения, обновляет возможные
    // действия
    public void turn (int p_actionType, int[] p_actionParams, boolean noSkip) {
        actorP.status = p_actionType;
        if (noSkip) {
            timeStackExecute(p_actionParams[0]);
            actionHandler(p_actionType, actorP.personID, p_actionParams);
        }
        actorP.renewFactors(this, dateTime);
        actorP.renewActions(this);
        renewUActions();
        renewMessages(noSkip);
        renewChoices();
    }

    public void actionHandler (int p_actionType, int p_actorID, int[] p_params) {
        switch (p_actionType) {
            case 0: action0(p_actorID, p_params[0]);
                break;
            case 1: action1(p_actorID, p_params[0], p_params[1]);
                break;
            case 2: action2(p_actorID, p_params[0]);
                break;
            case 3: action3();
                break;
            case 4: action4();
                break;
            case 5: action5();
                break;
            case 6: action6();
                break;
            case -1: actionU1();
                break;
            case -2: actionU2(p_actorID);
                break;
            case -3: actionU3(p_actorID);
                break;
            default: break;
        }

    }

    //ждать
    public void action0 (int p_actorID, int p_duration) {
        PersonType v_actor = allPersons.get(p_actorID);

        v_actor.log.put(BasicProcedures.timeToString(dateTime), "@A~0~" + p_duration + logPosition(v_actor.placeID, v_actor.territoryID)
                + logStats(v_actor.attributes, v_actor.stats, v_actor.statsMax)
                + logEquip(v_actor.equipped) + logInv(v_actor.inventory) + logFact(v_actor.factors));

        v_actor.stats.put(0, v_actor.stats.get(0) + (float) p_duration);
        if (v_actor.stats.get(0) > v_actor.statsMax.get(0)) v_actor.stats.put(0, v_actor.statsMax.get(0));
        v_actor.stats.put(1, v_actor.stats.get(1) + (v_actor.statsMax.get(1)*(float) p_duration)/60f);
        if (v_actor.stats.get(1) > v_actor.statsMax.get(1)) v_actor.stats.put(1, v_actor.statsMax.get(1));
        v_actor.stats.put(2, v_actor.stats.get(2) - ((float) p_duration)/3.6f);
        if (v_actor.stats.get(2) < 0f) v_actor.stats.put(2, 0f);
        v_actor.stats.put(3, v_actor.stats.get(3) - ((float) p_duration)/10.08f);
        if (v_actor.stats.get(3) < 0f) v_actor.stats.put(3, 0f);
        v_actor.stats.put(4, v_actor.stats.get(4) - ((float) p_duration)/4.32f);
        if (v_actor.stats.get(4) < 0f) v_actor.stats.put(4, 0f);

        if (p_actorID == actorP.personID) {
            int[] v_mesParI = {0};
            String[] v_mesParS = new String[1];
            //timeStackExecute (p_duration); in turn!!!
            v_mesParS[0] = BasicProcedures.formatDate(dateTime);
            messages.put(v_mesParI, v_mesParS);
            //renewMessages(false); in turn!!!
            showInfo("Вы некоторое время подождали.");
        }
        //checkItems
        //checkFactors
        //check log (>10 - write to file 5 pcs)
        //planning new action v_actor.status = 0; //waiting

        //восстановление блокиратора действий после отдыха (потеря сознания или бессилие)
        if (actSpecTmp.size() > 0) {
            actSpec.clear();
            actSpec = actSpecTmp.clone();
        }
        if (actorP.actBasicTmp.size() > 0) {
            actorP.actBasic.clear();
            actorP.actBasic = actorP.actBasicTmp.clone();
        }
    }

    //отдыхать
    public void action1 (int p_actorID, int p_duration, int p_comfort) {
        PersonType v_actor = allPersons.get(p_actorID);

        v_actor.log.put(timeToString(dateTime), "@A~1~" + p_duration + logPosition(v_actor.placeID, v_actor.territoryID)
                + logStats(v_actor.attributes, v_actor.stats, v_actor.statsMax)
                + logEquip(v_actor.equipped) + logInv(v_actor.inventory) + logFact(v_actor.factors));

        v_actor.stats.put(0, v_actor.stats.get(0) + (float) (p_duration * p_comfort));
        if (v_actor.stats.get(0) > v_actor.statsMax.get(0)) v_actor.stats.put(0, v_actor.statsMax.get(0));
        v_actor.stats.put(1, v_actor.stats.get(1) + (v_actor.statsMax.get(1)*(float) p_duration * p_comfort)/60f);
        if (v_actor.stats.get(1) > v_actor.statsMax.get(1)) v_actor.stats.put(1, v_actor.statsMax.get(1));
        v_actor.stats.put(2, v_actor.stats.get(2) + ((float) p_duration * p_comfort)/0.72f);
        if (v_actor.stats.get(2) > v_actor.statsMax.get(2)) v_actor.stats.put(2, v_actor.statsMax.get(2));
        v_actor.stats.put(3, v_actor.stats.get(3) - ((float) p_duration)/10.08f);
        if (v_actor.stats.get(3) < 0f) v_actor.stats.put(3, 0f);
        v_actor.stats.put(4, v_actor.stats.get(4) - ((float) p_duration)/4.32f);
        if (v_actor.stats.get(4) < 0f) v_actor.stats.put(4, 0f);

        if (p_actorID == actorP.personID) {
            int[] v_mesParI = {0};
            String[] v_mesParS = new String[1];
            //timeStackExecute (p_duration); in turn!!!
            v_mesParS[0] = BasicProcedures.formatDate(dateTime);
            messages.put(v_mesParI, v_mesParS);
            //renewMessages(false); in turn!!!
            showInfo("Вы прилегли на землю и около часа отдыхали.");
        }

        //checkItems
        //checkFactors
        //check log (>10 - write to file 5 pcs)
        //planning new action v_actor.status = 0; //waiting

        //восстановление блокиратора действий после отдыха (потеря сознания или бессилие)
        if (actSpecTmp.size() > 0) {
            actSpec.clear();
            actSpec = actSpecTmp.clone();
        }
        if (actorP.actBasicTmp.size() > 0) {
            actorP.actBasic.clear();
            actorP.actBasic = actorP.actBasicTmp.clone();
        }
    }

    //осмотреться
    public void action2 (int p_actorID, int p_duration) {
        LocationType whereAmI = getPersonLocation(allPersons.get(p_actorID));
        /*if (actorP.placeID > 0) {
            whereAmI = allPlaces.get(actorP.locationID[0]);
        } else {
            whereAmI = null;
            for (int i=0; i<allTerritories.size(); i++) {
                if (Arrays.equals(new int[] {actorP.locationID[1], actorP.locationID[2]}, allTerritories.keyAt(i))) whereAmI = allTerritories.valueAt(i);
            }
        }*/
        if (p_actorID == actorP.personID) {
            showInfo(getResources().getStringArray(R.array.ru_actionInfo)[2]);
            int[] v_mesParI = {0};
            String[] v_mesParS = new String[1];
            v_mesParS[0] = "";
            //timeStackExecute (p_duration); in turn!!!

            if (actorP.placeID > 0) {
                if (whereAmI.name.equals("")) {

                } else {
                    v_mesParS[0] += "Вы находитесь в закрытом пространстве. ";
                    v_mesParS[0] += "Это " + whereAmI.name.toLowerCase() + ". ";
                    v_mesParS[0] += getResources().getStringArray(R.array.ru_placeDescr)[whereAmI.typeID] + " ";
                    if (whereAmI.uniqueID >= 0) v_mesParS[0] += getResources().getStringArray(R.array.ru_placeUDescr)[whereAmI.uniqueID] + " ";
                }
                //v_mesParS[0] += String.format(" Размер этого места около %d квадратных метров.",
                //        whereAmI.size / 10);
            } else {
                v_mesParS[0] += "Вы находитесь на открытом пространстве. ";
                v_mesParS[0] += "Это " + whereAmI.name.toLowerCase() + ". ";
                v_mesParS[0] += getResources().getStringArray(R.array.ru_territoryDescr)[whereAmI.typeID] + " ";
                /*v_mesParS[0] += String.format(" Площадь территории около %d квадратных километров.",
                        (whereAmI.size * 10 /(1000*1000)));*/ //не реализовано, поскольку для территории открытой сложно определить
            }
            if (whereAmI.visitorsList.size() <= 1) {
                v_mesParS[0] += "Кроме Вас здесь больше никого нет. ";
            } else {
                v_mesParS[0] += "Кроме Вас здесь еще есть: ";
                for (int i=0; i<whereAmI.visitorsList.size(); i++) {
                    if (whereAmI.visitorsList.valueAt(i).personID != actorP.personID) {
                        v_mesParS[0] += whereAmI.visitorsList.valueAt(i).name + " "
                                + whereAmI.visitorsList.valueAt(i).surname + ", ";
                    }
                }
                v_mesParS[0] = v_mesParS[0].substring(0, v_mesParS[0].length()-2) + ". ";
            }
            messages.put(v_mesParI, v_mesParS);
        }

    }

    public void action3 () {
        String[] str1 = SourceJSON.getRandomName(this, 0);
        String[] str2 = SourceJSON.getRandomSurname(this, 0);
        showInfo(str1[0] + " " + str2[0] + ", " + str1[1] + " " + str2[1]);
    }

    public void action4 () {
        int[] tmp = {0, 0};
        //showInfo(String.valueOf(allTerritories.size()) + "/" + String.valueOf(allTerritories.size()));
        //showInfo(actorP.name + ": " + String.valueOf(actorP.locationID[0]) + "/" + String.valueOf(actorP.locationID[1]) + "/" + String.valueOf(actorP.locationID[2]));
        //showInfo(String.valueOf(allPlaces.get(actorP.locationID[0]).territoryID[0]) + "/" + String.valueOf(allPlaces.get(actorP.locationID[0]).territoryID[1]));
        //(actorP.locationID[0] != p_place.placeID)
        //showInfo(String.valueOf(Arrays.equals(tmp, allTerritories.keyAt(0))));
        //showInfo(String.valueOf(allTerritories.get(allPlaces.get(actorP.locationID[0]).territoryID).coordinates[0]));
        SparseArray<Float> attributesTemp = new SparseArray<>();
        attributesTemp.put(0, 10f);
        attributesTemp.put(1, 10f);
        attributesTemp.put(2, 10f);
        attributesTemp.put(3, 10f);
        attributesTemp.put(4, 10f);
        attributesTemp.put(5, 10f);
        SparseArray<Float> skillsTemp = new SparseArray<>();
        int v_personID = 0;
        while (allPersons.indexOfKey(v_personID) >= 0 ) v_personID++;
        PersonType v_person = new PersonType(1, v_personID,
                SourceJSON.getRandomName(this, 0)[1],
                SourceJSON.getRandomSurname(this, 0)[1],
                attributesTemp,
                skillsTemp,
                true,
                'F');
        v_person.placeID = actorP.placeID;
        v_person.territoryID = actorP.territoryID.clone();
        allPersons.put(v_person.personID, v_person);
        try {
            Actions.settlePerson(this, v_person, getPersonLocation(v_person));
        } catch (Exception e) {}

    }

    public void action5 () {}

    public void action6 () {}

    //кастовать предмет
    public void actionU1 () {
        //int[] intTmp = new int[]{0,1,2,3,4,200,201,202,203,204,205,300,302,303,304,306,600,602,606};
        int[] intTmp = {200,300,400,401,402,403,404,600,708,713,714,715,716};
        //int[] intTmp = new int[]{600,300,302,304,306,303};
        int i = (int) Math.floor(Math.random() * intTmp.length);
        /*showInfo(String.format(getResources().getStringArray(R.array.ru_actionUInfo)[0],
                getItemNameS(this, intTmp[i], optionLanguage).toLowerCase()));*/
        showInfo(getItemNameS(this, intTmp[i], optionLanguage).toLowerCase());
        int[] v_mesParI = {0};
        String[] v_mesParS = new String[1];
        /*System.out.println(String.valueOf(flowTime(dateTime, SourceJSON.getItemInt(this, intTmp[i], "lifetime"))[0])
                +String.valueOf(flowTime(dateTime, SourceJSON.getItemInt(this, intTmp[i], "lifetime"))[1])
                +String.valueOf(flowTime(dateTime, SourceJSON.getItemInt(this, intTmp[i], "lifetime"))[2])
                +String.valueOf(flowTime(dateTime, SourceJSON.getItemInt(this, intTmp[i], "lifetime"))[3])
                +String.valueOf(flowTime(dateTime, SourceJSON.getItemInt(this, intTmp[i], "lifetime"))[4])
        );*/
        if (actorP.itemAdd(new ItemType(this, intTmp[i], flowTime(dateTime, SourceJSON.getItemInt(this, intTmp[i], "lifetime"))), 1)) {
            /*showInfo("Ладонь объяло ледяным холодом. Во вспышках искр из ничего " +
                    "начало материализоваться нечто, которое уже через мгновение приняло " +
                    "определенную форму. Моргнув, Вы поняли, что это " +
                    getResources().getStringArray(R.array.ru_itemsS)[i].toLowerCase() +
                    ". Предмет Вы положили себе в рюкзак.");*/
            v_mesParS[0] = getResources().getStringArray(R.array.ru_actionUMessage0)[0];
        } else {
            /*showInfo("Ладонь объяло ледяным холодом. Во вспышках искр из ничего " +
                    "начало материализоваться нечто, которое уже через мгновение приняло " +
                    "определенную форму. Моргнув, Вы поняли, что это " +
                    getResources().getStringArray(R.array.ru_itemsS)[i].toLowerCase() +
                    ". В инвентаре больше нет места и Вы выбросили предмет на землю.");*/
            v_mesParS[0] = getResources().getStringArray(R.array.ru_actionUMessage1)[0];
        }
        messages.put(v_mesParI, v_mesParS);
    }

    //вылезти из могилы
    public void actionU2 (int p_actorID) {
        int[] v_mesParI = {0};
        String[] v_mesParS = new String[1];
        PersonType v_actor;
        PlaceType v_place;
        TerritoryType v_territory;
        //вывод инфо
        if (p_actorID == actorP.personID) showInfo(getResources().getStringArray(R.array.ru_actionUInfo)[1]);
        try {
            v_actor = allPersons.get(p_actorID);
            v_place = allPlaces.get(v_actor.placeID);
            //v_territory = null;
            /*for (int i=0; i<allTerritories.size(); i++) {
                if (Arrays.equals(keyAt(i))) v_territory = allTerritories.valueAt(i);
            }*/
            v_territory = allTerritories.get(coordinatesToString(v_place.territoryID));
            //if ((Math.random() * 16) <= v_actor.attributes.get(0) + v_actor.attributes.get(4)) {
            if ((Math.random() * 10) <= 9) {
                v_actor.stats.put(1, v_actor.stats.get(1) - ((5-v_actor.attributes.get(4))*200));
                //переход персонажа из места на территорию
                Actions.quitPlace(v_actor, v_place, v_territory);
                //изменение типа места
                v_place.changeType(this, 1);
                //отключение действия
                //actSpec.put(2, false);
                //генерация сообщения
                v_mesParS[0] = getResources().getStringArray(R.array.ru_actionUMessage0)[1];
            } else {
                //задохнулся
                v_actor.stats.put(0, 0f);
                v_actor.stats.put(1, 0f);
                v_actor.stats.put(2, 0f);
                //генерация сообщения
                v_mesParS[0] = getResources().getStringArray(R.array.ru_actionUMessage1)[1];
            }
            messages.put(v_mesParI, v_mesParS);
        } catch (Exception e) {showInfo("ERROR: actionU2");}

    }

    //подхватить геном
    public void actionU3 (int p_actorID) {
        showInfo(getResources().getStringArray(R.array.ru_actionUInfo)[2]);
        int[] v_mesParI = {0};
        String[] v_mesParS = new String[1];
        //коллекция полов (0-м, 1-ж) по идентификаторам персонажей, у которых есть геном игрока
        SparseIntArray offspringList = new SparseIntArray(1);
        PersonType personTmp;
        for (int i=0; i < allPersons.size(); i++) {
            personTmp = allPersons.valueAt(i);
            if (personTmp.genom && personTmp.personID != actorP.personID) {
                switch(personTmp.gender) {
                    case 'M': offspringList.put(personTmp.personID, 0);
                    case 'F': offspringList.put(personTmp.personID, 1);
                }
            }
        }
        if (offspringList.size() > 0) {
            actorP = allPersons.get(offspringList.keyAt((int) Math.floor(Math.random()*offspringList.size())));
            v_mesParS[0] = String.format(getResources().getStringArray(R.array.ru_actionUMessage0)[2], actorP.name, actorP.surname);
            catchGenom();
        } else {
            v_mesParS[0] = getResources().getStringArray(R.array.ru_actionUMessage1)[2];
        }
        messages.put(v_mesParI, v_mesParS);
    }

    public void timeStackAdd () {}

    //execution for defined amount of time with ID of initiator (for interruptions)
    public int[] timeStackExecute (int p_minutesDuration) {
        int[] v_dateTime = new int[5];
        System.arraycopy(dateTime, 0, v_dateTime, 0, 0);
        dateTime = flowTime(dateTime, p_minutesDuration);
        return v_dateTime;
    }

    /*public String logTime(int[] p_dateTime) {
        return "@T~" + p_dateTime[0] + "~" + p_dateTime[1] + "~" + p_dateTime[2] + "~" + p_dateTime[3] + "~" + p_dateTime[4];
    }*/

    public String logPosition(int p_placeID, int[] p_territoryID) {
        return "@P~" + p_placeID + "~" + p_territoryID[0] + "~" + p_territoryID[1];
    }

    public String logStats (SparseArray<Float> p_attributes, SparseArray<Float> p_stats, SparseArray<Float> p_statsMax) {
        StringBuilder v_logStats = new StringBuilder();
        v_logStats.append("@S");
        for (int i=0; i<6; i++) {
            v_logStats.append("~");
            v_logStats.append(p_attributes.get(i));
        }
        for (int i=0; i<5; i++) {
            v_logStats.append("~");
            v_logStats.append(p_stats.get(i));
            v_logStats.append("|");
            v_logStats.append(p_statsMax.get(i));
        }
        return v_logStats.toString();
    }

    public String logEquip (SparseArray<ItemType> p_equipped) {
        StringBuilder v_logEquip =  new StringBuilder();
        v_logEquip.append("@E");
        for (int i=0; i<7; i++) {
            v_logEquip.append("~");
            if (p_equipped.get(i) != null) {
                v_logEquip.append(p_equipped.get(i).itemID);
            } else {
                v_logEquip.append("-1");
            }
        }
        return v_logEquip.toString();
    }

    public String logInv (ArrayList<ItemType> p_inventory) {
        StringBuilder v_logInv =  new StringBuilder();
        v_logInv.append("@I");
        for (int i=0; i<p_inventory.size(); i++) {
            v_logInv.append("~");
            v_logInv.append(p_inventory.get(i).itemID);
            v_logInv.append("_");
            v_logInv.append(p_inventory.get(i).endDT.size());
        }
        return v_logInv.toString();
    }

    public String logFact (SparseArray<int[]> p_fact) {
        StringBuilder v_logFact =  new StringBuilder();
        v_logFact.append("@F");
        for (int i=0; i<p_fact.size(); i++) {
            v_logFact.append("~");
            v_logFact.append(p_fact.keyAt(i));
        }
        return v_logFact.toString();
    }

    //Процедура вывода всплывающего сообщения. Длительность - 300 + по 50 миллисекунд на символ.
    public void showInfo (String p_text) {
        final int c_tic = 50;
        int v_duration = 300 + p_text.length() * c_tic;
        final Toast toast = Toast.makeText(this, p_text, Toast.LENGTH_SHORT);
        // Set the countdown to display the toast
        CountDownTimer toastCountDown;
        toastCountDown = new CountDownTimer(v_duration, c_tic /*Tick duration*/) {
            public void onTick(long millisUntilFinished) {
                toast.show();
            }
            public void onFinish() {
                toast.cancel();
            }
        };
        toast.setGravity(Gravity.CENTER, 0, 0);
        // Show the toast and starts the countdown
        toast.show();
        toastCountDown.start();
    }

    //Обновление доступных специальных действий
    private void renewUActions() {
        actSpec.clear();
        actSpec.put(1, true); //кастовать разное
        if (actorP.placeID > 0) {
            int v_uID = allPlaces.get(actorP.placeID).uniqueID;
            int v_typeID = allPlaces.get(actorP.placeID).typeID;
            if (v_uID < 0) {
                try {
                    for (int i : getUPlaceArray(this, v_typeID, PersonType.placePUActions))
                        actSpec.put(i, true);
                } catch (Exception e) {
                }
            } else {
                try {
                    for (int i : getUPlaceArray(this, v_uID, PersonType.placePUActions))
                        actSpec.put(i, true);
                } catch (Exception e) {
                }
            }
        }
    }

    LocationType getPersonLocation (PersonType p_person) {
        if (p_person.placeID > 0) {
            return allPlaces.get(p_person.placeID);
        } else {
            return allTerritories.get(coordinatesToString(p_person.territoryID));
        }
    }

    private void loadOption () {
        try{
            optionLanguage = readOption(getApplicationContext()).getString("language");
        } catch (Exception e) {
            try {
                writeOption(getApplicationContext(), new JSONObject(readJSON(this, "option.json")));
                optionLanguage = readOption(getApplicationContext()).getString("language");
            } catch (Exception e1) {
                optionLanguage = "en";
            }
        }
    }

    void showItemMenu (PopupMenu p_pUM, ItemType p_item, boolean isEquipped) {
        SpannableString spannableStr;
        TextAppearanceSpan styleSpan = new TextAppearanceSpan(this, R.style.gameItemPopUp);
        //StyleSpan styleSpan = new StyleSpan(R.style.gameItemPopUp);
        //ForegroundColorSpan colorSpan = new ForegroundColorSpan(getResources().getColor(R.color.clr_white_grey));

        if ((actorP.factors.indexOfKey(0) < 0) && (actorP.factors.indexOfKey(1) < 0)) { //без факторов "без сознания" и "изможден"
            if (false) {
                spannableStr = new SpannableString(getResources().getString(getResources().getIdentifier(
                        "com.game.michael.first:string/" + optionLanguage + "_labelGameItemUse",
                        null,
                        null
                )));
                spannableStr.setSpan(styleSpan, 0, spannableStr.length(), 0);
                //spannableStr.setSpan(colorSpan, 0, spannableStr.length(), 0);
                p_pUM.getMenu().add(1, R.id.menuGameItemUse, 1, spannableStr);
                if (p_item.endDT.size() > 1) {
                    spannableStr = new SpannableString(getResources().getString(getResources().getIdentifier(
                            "com.game.michael.first:string/" + optionLanguage + "_labelGameItemUseAll",
                            null,
                            null
                    )));
                    spannableStr.setSpan(styleSpan, 0, spannableStr.length(), 0);
                    p_pUM.getMenu().add(1, R.id.menuGameItemUseAll, 2, spannableStr);
                }
            }
            if (!isEquipped) {
                spannableStr = new SpannableString(getResources().getString(getResources().getIdentifier(
                        "com.game.michael.first:string/" + optionLanguage + "_labelGameItemInHand",
                        null,
                        null
                )));
                spannableStr.setSpan(styleSpan, 0, spannableStr.length(), 0);
                //spannableStr.setSpan(colorSpan, 0, spannableStr.length(), 0);
                p_pUM.getMenu().add(1, R.id.menuGameItemInHand, 3, spannableStr);
            } else {
                spannableStr = new SpannableString(getResources().getString(getResources().getIdentifier(
                        "com.game.michael.first:string/" + optionLanguage + "_labelGameItemToInventory",
                        null,
                        null
                )));
                spannableStr.setSpan(styleSpan, 0, spannableStr.length(), 0);
                //spannableStr.setSpan(colorSpan, 0, spannableStr.length(), 0);
                p_pUM.getMenu().add(1, R.id.menuGameItemToInventory, 3, spannableStr);
            }
            if (p_item.tags[0].equals("food") || p_item.tags[2].equals("food")) {
                spannableStr = new SpannableString(getResources().getString(getResources().getIdentifier(
                        "com.game.michael.first:string/" + optionLanguage + "_labelGameItemEat",
                        null,
                        null
                )));
                spannableStr.setSpan(styleSpan, 0, spannableStr.length(), 0);
                p_pUM.getMenu().add(1, R.id.menuGameItemEat, 4, spannableStr);
                if (p_item.endDT.size() > 1) {
                    spannableStr = new SpannableString(getResources().getString(getResources().getIdentifier(
                            "com.game.michael.first:string/" + optionLanguage + "_labelGameItemEatAll",
                            null,
                            null
                    )));
                    spannableStr.setSpan(styleSpan, 0, spannableStr.length(), 0);
                    p_pUM.getMenu().add(1, R.id.menuGameItemEatAll, 5, spannableStr);
                }
            }
            if ((p_item.tags[0].equals("jar") || p_item.tags[0].equals("jar")) && (!p_item.contain.isEmpty())) {
                spannableStr = new SpannableString(getResources().getString(getResources().getIdentifier(
                        "com.game.michael.first:string/" + optionLanguage + "_labelGameItemDrink",
                        null,
                        null
                )));
                spannableStr.setSpan(styleSpan, 0, spannableStr.length(), 0);
                p_pUM.getMenu().add(1, R.id.menuGameItemDrink, 6, spannableStr);
                if (p_item.contain.get(0).endDT.size() > 1) {
                    spannableStr = new SpannableString(getResources().getString(getResources().getIdentifier(
                            "com.game.michael.first:string/" + optionLanguage + "_labelGameItemDrinkAll",
                            null,
                            null
                    )));
                    spannableStr.setSpan(styleSpan, 0, spannableStr.length(), 0);
                    p_pUM.getMenu().add(1, R.id.menuGameItemDrinkAll, 7, spannableStr);
                }
            }
            if ((p_item.tags[0].equals("cntr") || p_item.tags[0].equals("cntr")) && (!p_item.contain.isEmpty())) {
                spannableStr = new SpannableString(getResources().getString(getResources().getIdentifier(
                        "com.game.michael.first:string/" + optionLanguage + "_labelGameItemOpen",
                        null,
                        null
                )));
                spannableStr.setSpan(styleSpan, 0, spannableStr.length(), 0);
                p_pUM.getMenu().add(1, R.id.menuGameItemOpen, 6, spannableStr);
            }
            if (((p_item.tags[0].equals("clth") || p_item.tags[0].equals("clth")) || (p_item.tags[0].equals("armr") || p_item.tags[0].equals("armr"))) && (!isEquipped)){
                spannableStr = new SpannableString(getResources().getString(getResources().getIdentifier(
                        "com.game.michael.first:string/" + optionLanguage + "_labelGameItemEquip",
                        null,
                        null
                )));
                spannableStr.setSpan(styleSpan, 0, spannableStr.length(), 0);
                p_pUM.getMenu().add(1, R.id.menuGameItemEquip, 6, spannableStr);
            }
            spannableStr = new SpannableString(getResources().getString(getResources().getIdentifier(
                    "com.game.michael.first:string/" + optionLanguage + "_labelGameItemDrop",
                    null,
                    null
            )));
            spannableStr.setSpan(styleSpan, 0, spannableStr.length(), 0);
            p_pUM.getMenu().add(1, R.id.menuGameItemDrop, 7, spannableStr);
            if (p_item.endDT.size() > 1) {
                spannableStr = new SpannableString(getResources().getString(getResources().getIdentifier(
                        "com.game.michael.first:string/" + optionLanguage + "_labelGameItemDropAll",
                        null,
                        null
                )));
                spannableStr.setSpan(styleSpan, 0, spannableStr.length(), 0);
                p_pUM.getMenu().add(1, R.id.menuGameItemDropAll, 8, spannableStr);
            }
        }
        spannableStr = new SpannableString(getResources().getString(getResources().getIdentifier(
                "com.game.michael.first:string/" + optionLanguage + "_labelGameItemStats",
                null,
                null
        )));
        //spannableStr.setSpan(styleSpan, 0, spannableStr.length(), 0);
        p_pUM.getMenu().add(1, R.id.menuGameItemStats, 9, spannableStr);
    }

}

/*when added item, in calling method perform GameActivity.flowTime(p_createdDT, 10000); for endDT */

/*
import android.app.Activity;
        import android.os.Bundle;
        import android.view.View;
        import android.view.ViewGroup.LayoutParams;
        import android.widget.LinearLayout;
        import android.widget.TextView;

public class Stackoverflow extends Activity {
    // Called when the activity is first created.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        View linearLayout =  findViewById(R.id.info);
        //LinearLayout layout = (LinearLayout) findViewById(R.id.info);

        TextView valueTV = new TextView(this);
        valueTV.setText("hallo hallo");
        valueTV.setId(5);
        valueTV.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));

        ((LinearLayout) linearLayout).addView(valueTV);
    }
}
*/
/*
button1.setOnClickListener(new OnClickListener ... );
button2.setOnClickListener(new OnClickListener ... );

public void onClick(View v) {
    doAction(1); // 1 for button1, 2 for button2, etc.
}

button1.setTag(1); //use Object as an argument
button2.setTag(2);

listener = new OnClickListener() {
    @Override
    public void onClick(View v) {
        doAction(v.getTag());
    }
};
 */
/*
LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
params.weight = 1.0f;
params.gravity = Gravity.TOP;

button.setLayoutParams(params);
 */

/*FOR REF
getResources().getIntArray(R.array.rng_skills);
 */