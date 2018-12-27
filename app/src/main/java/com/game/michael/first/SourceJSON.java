package com.game.michael.first;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;

abstract class SourceJSON {
    private static final String srcNames = "names.json";
    private static final String srcItems = "items.json";
    private static final String srcPlaces = "places.json";
    private static final String srcUPlaces = "uplaces.json";
    private static final String srcActions = "actions.json";
    static final String srcTerritories = "territories.json";
    private static final String optFile = "options.bin";

    //Выдает массив случайных имен по полу {M, F} в зависимости от параметра группы имен.
    public static String[] getRandomName (Activity p_act, int p_group) {
        String[] v_names = new String[2];
        try {
            JSONObject jsonObj = new JSONObject(readJSON(p_act, srcNames));
            JSONArray jsonArrayM = jsonObj.getJSONArray("names_M");
            JSONArray jsonArrayF = jsonObj.getJSONArray("names_F");
            v_names[0] = jsonArrayM.getString((int) Math.floor(Math.random()*jsonArrayM.length()));
            v_names[1] = jsonArrayF.getString((int) Math.floor(Math.random()*jsonArrayF.length()));
        } catch (Exception e) {
            v_names[0] = "000";
            v_names[1] = "000";
        }
        return v_names;
    }

    //Выдает массив случайных Фамилий по полу {M, F} в зависимости от параметра группы имен.
    public static String[] getRandomSurname (Activity p_act, int p_group) {
        String[] v_names = new String[2];
        try {
            JSONObject jsonObj = new JSONObject(readJSON(p_act, srcNames));
            JSONArray jsonArrayM = jsonObj.getJSONArray("surnames_M");
            JSONArray jsonArrayF = jsonObj.getJSONArray("surnames_F");
            v_names[0] = jsonArrayM.getString((int) Math.floor(Math.random()*jsonArrayM.length()));
            v_names[1] = jsonArrayF.getString((int) Math.floor(Math.random()*jsonArrayF.length()));
        } catch (Exception e) {
            v_names[0] = "000";
            v_names[1] = "000";
        }
        return v_names;
    }

    //Выдает параметры предмета
    public static String getItemStr(Activity p_act, int p_ID, String p_par) {
        int v_code;
        String v_par = "";
        switch (p_par) {
            case "resources": v_code = 10;
                break;
            case "tools": v_code = 11;
                break;
            case "material": v_code = 12;
                break;
            default: v_code = 30;
                break;
        }
        try {
            JSONObject jsonObj = new JSONObject(readJSON(p_act, srcItems));
            JSONArray jsonArrayItem = jsonObj.getJSONArray(String.valueOf(p_ID));
            v_par = jsonArrayItem.getString(v_code);
        } catch (Exception e) {}
        return v_par;
    }

    public static int getItemInt (Activity p_act, int p_ID, String p_par) {
        int v_code;
        int v_par = -1;
        switch (p_par) {
            case "volume": v_code = 0;
                break;
            case "weight": v_code = 1;
                break;
            case "cost": v_code = 2;
                break;
            case "lifetime": v_code = 3;
                break;
            case "creation time": v_code = 8;
                break;
            case "batch": v_code = 9;
                break;
            default: v_code = 30;
                break;
        }
        try {
            JSONObject jsonObj = new JSONObject(readJSON(p_act, srcItems));
            JSONArray jsonArrayItem = jsonObj.getJSONArray(String.valueOf(p_ID));
            v_par = jsonArrayItem.getInt(v_code);
        } catch (Exception e) {}
        return v_par;
    }

    public static int[] getItemDam (Activity p_act, int p_ID) {
        int[] intTmp = {0, 0, 0, 0, 0};
        try {
            JSONObject jsonObj = new JSONObject(readJSON(p_act, srcItems));
            JSONArray jsonArrayItem = jsonObj.getJSONArray(String.valueOf(p_ID));
            for (int i = 13; i < 18; i++) {
                    intTmp[i - 13] = jsonArrayItem.getInt(i);
            }
        } catch (Exception e) {}
        return intTmp;
    }

    public static String[] getItemTgs (Activity p_act, int p_ID) {
        String[] v_tags = new String[]{"0", "0", "0", "0"};
        try {
            JSONObject jsonObj = new JSONObject(readJSON(p_act, srcItems));
            JSONArray jsonArrayItem = jsonObj.getJSONArray(String.valueOf(p_ID));
            for (int i = 4; i < 8; i++) {
                v_tags[i - 4] = jsonArrayItem.getString(i);
            }
        } catch (Exception e) {}
        return v_tags;
    }

    //Выдает параметры места
    public static int getPlacePar (Activity p_act, int p_typeID, String p_par) {
        int v_code;
        int v_par = -1;
        switch (p_par) {
            case "size": v_code = 0;
                break;
            default: v_code = 30;
                break;
        }
        try {
            JSONObject jsonObj = new JSONObject(readJSON(p_act, srcPlaces));
            JSONArray jsonArrayPlace = jsonObj.getJSONArray(String.valueOf(p_typeID));
            v_par = jsonArrayPlace.getInt(v_code);
        } catch (Exception e) {}
        return v_par;
    }

    //Выдает параметры места
    static int[] getPlaceArray (Activity p_act, int p_typeID, String p_param) {
        int[] v_par;
        try {
            JSONObject jsonObj = new JSONObject(readJSON(p_act, srcPlaces));
            JSONArray jsonArrayParams = jsonObj.getJSONObject(String.valueOf(p_typeID)).getJSONArray(p_param);
            v_par = new int[jsonArrayParams.length()];
            for (int i=0; i<jsonArrayParams.length(); i++)  v_par[i] = jsonArrayParams.getInt(i);
            return v_par;
        } catch (Exception e) {}
        return null;
    }


    //Выдает параметры уникального места
    static int[] getUPlaceArray (Activity p_act, int p_typeUID, String p_param) {
        int[] v_par;
        try {
            JSONObject jsonObj = new JSONObject(readJSON(p_act, srcUPlaces));
            JSONArray jsonArrayParams = jsonObj.getJSONObject(String.valueOf(p_typeUID)).getJSONArray(p_param);
            v_par = new int[jsonArrayParams.length()];
            for (int i=0; i<jsonArrayParams.length(); i++)  v_par[i] = jsonArrayParams.getInt(i);
            return v_par;
        } catch (Exception e) {}
        return null;
    }

    //Выдает параметры уникального места
    public static int getUPlacePar (Activity p_act, int p_typeID) {return 0;}

    //выдает параметры территории
    static int[] getTerritoryArray (Activity p_act, int p_typeID, String p_param) {
        int[] v_res;
        try {
            JSONObject jsonObj = new JSONObject(readJSON(p_act, srcTerritories));
            JSONArray jsonArrayParams = jsonObj.getJSONObject(String.valueOf(p_typeID)).getJSONArray(p_param);
            v_res = new int[jsonArrayParams.length()];
            for (int i=0; i<jsonArrayParams.length(); i++)  v_res[i] = jsonArrayParams.getInt(i);
            return v_res;
        } catch (Exception e) {
            v_res = new int[]{-1};
            return v_res;
        }
    }




    //Возвращает содержание файла из папки Assets, соответствующего названию во входном параметре.
    static String readJSON (Activity p_act, String p_fileName) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(p_act.getAssets().open(p_fileName)));
            String srcJSON = "";
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                srcJSON += sCurrentLine;
                //System.out.println(sCurrentLine);
            }
            br.close();
            return srcJSON;
        } catch (Exception e) {return null;}
    }

    static boolean writeOption (Context p_con, JSONObject p_json) {
        try {
            FileOutputStream fos = p_con.openFileOutput(optFile, p_con.MODE_PRIVATE);
            fos.write(p_json.toString().getBytes());
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    static JSONObject readOption (Context p_con) {
        try {
            BufferedReader bReader = new BufferedReader(new InputStreamReader(p_con.openFileInput(optFile)));
            String line;
            StringBuffer text = new StringBuffer();
            while ((line = bReader.readLine()) != null) {
                text.append(line + "\n");
            }
            return new JSONObject(new String(text));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /*public String getStringFromAssetFile(Activity activity)
    {
        AssetManager am = activity.getAssets();
        InputStream is = am.open("names.json");
        String s = convertStreamToString(is);
        is.close();
        return s;
    }*/
}
