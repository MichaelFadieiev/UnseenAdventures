package com.game.michael.first;

import android.app.Activity;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

import static com.game.michael.first.BasicProcedures.parseCodeItem;
import static com.game.michael.first.BasicProcedures.parseCodePerson;

abstract class SourceJSON {
    private static final String srcNames = "names.json";
    private static final String srcItems = "items.json";
    private static final String srcItemSets = "item_sets.json";
    private static final String srcPersons = "persons.json";
    private static final String srcUPersons = "upersons.json";
    private static final String srcPlaces = "places.json";
    private static final String srcUPlaces = "uplaces.json";
    private static final String srcActions = "actions.json";
    static final String srcTerritories = "territories.json";
    private static final String optFile = "options.bin";

    //Выдает массив случайных имен по полу {M, F} в зависимости от параметра группы имен.
    public static String[] getRandomName (GameActivity p_act, int p_group) {
        String[] v_names = new String[2];
        try {
            JSONObject jsonObj = new JSONObject(readJSON(p_act, p_act.optionLanguage + "_" + srcNames));
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

    static String[] getUniqueNameSurname(GameActivity p_act, int p_UID) {
        String[] v_names = new String[2];
        try {
            JSONObject jsonObj = new JSONObject(readJSON(p_act, p_act.optionLanguage + "_" + srcNames));
            JSONArray jsonArrayName = jsonObj.getJSONArray("names_U");
            JSONArray jsonArraySurname = jsonObj.getJSONArray("surnames_U");
            v_names[0] = jsonArrayName.getString(p_UID);
            v_names[1] = jsonArraySurname.getString(p_UID);
        } catch (Exception e) {
            v_names[0] = "000";
            v_names[1] = "000";
        }
        return v_names;
    }

    //Выдает массив случайных Фамилий по полу {M, F} в зависимости от параметра группы имен.
    public static String[] getRandomSurname (GameActivity p_act, int p_group) {
        String[] v_names = new String[2];
        try {
            JSONObject jsonObj = new JSONObject(readJSON(p_act, p_act.optionLanguage + "_" + srcNames));
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

    static boolean loadItem (GameActivity p_act, ItemType p_item, int p_quantity) {
        try {
            JSONObject jsonObj = new JSONObject(readJSON(p_act, srcItems));
            JSONArray jsonArrayItem = jsonObj.getJSONArray(String.valueOf(p_item.itemID));
            p_item.volume = jsonArrayItem.getInt(0);
            p_item.weight = jsonArrayItem.getInt(1);
            p_item.cost = jsonArrayItem.getInt(2);
            for (int i=0; i<p_quantity; i++) {
                p_item.endDT.add(GameActivity.flowTime(p_act.dateTime, jsonArrayItem.getInt(3)));
            }
            /*case "creation time": v_code = 8;
                break;
            case "batch": v_code = 9;*/
            //tags = getItemTgs(p_act, p_itemID).clone();
            for (int i = 4; i < 8; i++) {
                p_item.tags[i - 4] = jsonArrayItem.getString(i);
            }
            p_item.isLiquid = jsonArrayItem.getString(12).equals("lqd");
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    //Загрузка предсозданного персонажа: 0 - пол, 1 - возраст, 2 - аттрибуты, 3 - навыки, 4 - экипировано, 5 - инвентарь
    static boolean loadPerson (GameActivity p_act, PersonType p_person, int p_typeID, int p_UID) {
        int min, max;
        String[] names;
        String[] surnames;
        JSONObject jsonObj;
        JSONArray jsonArrayPerson;
        try {
            if (p_UID < 0) {
                jsonObj = new JSONObject(readJSON(p_act, srcPersons));
                jsonArrayPerson = jsonObj.getJSONArray(String.valueOf(p_typeID));
                names = getRandomName(p_act, -1);
                surnames = getRandomSurname(p_act, -1);
            } else {
                jsonObj = new JSONObject(readJSON(p_act, srcUPersons));
                jsonArrayPerson = jsonObj.getJSONArray(String.valueOf(p_UID));
                names = getUniqueNameSurname(p_act, p_UID);
                surnames = new String[]{names[0], names[0]};
                names[1] = names[0];
            }
        } catch (Exception e) {
            System.out.println("Trouble while loading person's JSON!");
            return false;
        }
        try {
            String genderSTR = jsonArrayPerson.getString(0);
            p_person.gender = (genderSTR.equals("M"))?'M':((genderSTR.equals("F"))?'F':((Math.floor(Math.random()*2)%2 == 1)?'M':'F'));
            p_person.name = (genderSTR.equals("M"))?names[0]:names[1];
            p_person.surname = (genderSTR.equals("M"))?surnames[0]:surnames[1];
            //age
            String str = jsonArrayPerson.getString(1);
            min = Integer.valueOf(str.substring(0, str.indexOf("-")));
            if (str.contains("-")) {
                max = Integer.valueOf(str.substring(str.indexOf("-"))+1);
                p_person.age = min + (int) (Math.random()*(max - min));
            } else p_person.age = min;
            //attributes
            JSONArray jsonArrayAttr = jsonArrayPerson.getJSONArray(2);
            for (int i=0; i<jsonArrayAttr.length(); i++) {
                str = jsonArrayAttr.getString(i);
                float valueTMP;
                min = Integer.valueOf(str.substring(0, str.indexOf("-")));
                if (str.contains("-") && (str.substring(str.indexOf("-"))+1).length()>0) {
                    max = Integer.valueOf(str.substring(str.indexOf("-"))+1);
                    valueTMP = (float) min + (float) Math.random()*(max - min);
                } else valueTMP = (float) min;
                p_person.attributes.put(i, valueTMP);
            }
            //skills
            JSONArray jsonArraySkills = jsonArrayPerson.getJSONArray(3);
            for (int i=0; i<jsonArraySkills.length(); i++) {
                str = jsonArraySkills.getString(i);
                float valueTMP;
                min = Integer.valueOf(str.substring(0, str.indexOf("-")));
                if (str.contains("-")) {
                    max = Integer.valueOf(str.substring(str.indexOf("-"))+1);
                    valueTMP = min + (float) Math.random()*(max - min);
                } else valueTMP = (float) min;
                p_person.skills.put(i, valueTMP);
            }
            //equipped & in hands
            JSONArray jsonArrayEquip = jsonArrayPerson.getJSONArray(4);
            for (int i=0; i<jsonArrayEquip.length(); i++) {
                str = jsonArrayEquip.getString(i);
                str = (str.contains("_"))?str.substring(0, str.indexOf("_")):str;
                int[] intTMP = parseCodeItem(str);
                //System.out.printf("Equip slot %d: ID #%d, UID #%d, contain %d, quantity is %d.\n", i, intTMP[0], intTMP[1], intTMP[2], intTMP[3]);
                //item initialisation
                if (intTMP[0]>=0) {
                    if (i<2) {
                        p_person.itemInHand(p_act, new ItemType(p_act, intTMP[0], intTMP[1], intTMP[2], intTMP[3]));
                    } else {
                        p_person.itemEquip(p_act, new ItemType(p_act, intTMP[0], intTMP[1], intTMP[2], intTMP[3]));
                    }
                }
            }
            //inventory
            JSONArray jsonArrayInventory = jsonArrayPerson.getJSONArray(5);
            for (int i=0; i<jsonArrayInventory.length(); i++) {
                str = jsonArrayInventory.getString(i);
                int[] intTMP = parseCodeItem(str);
                if (!p_person.itemAdd(new ItemType(p_act, intTMP[0], intTMP[1], intTMP[2], intTMP[3]), intTMP[3]))
                    System.out.printf("Can't load item (%d/%d/%d) into inventory.\n", intTMP[0], intTMP[1], intTMP[2]);
            }
            return true;
        } catch (Exception e) {
            System.out.println("Trouble while initiating person!");
            return false;
        }
    }

    //
    static void loadPlace (GameActivity p_act, PlaceType p_place) throws Exception {
        JSONArray jsonArrayPlace;
        JSONObject jsonObj;
        if (p_place.uniqueID < 0) {
            jsonObj = new JSONObject(readJSON(p_act, srcPlaces));
            jsonArrayPlace = jsonObj.getJSONArray(String.valueOf(p_place.typeID));
            p_place.name = p_act.getResources().getStringArray(R.array.ru_placeNames)[p_place.typeID];
            //for (int i=0; i<jsonArrayPlace.getJSONArray(4).length(); i++) p_place.permActions.put(i, true);
        } else {
            //System.out.println("file loading started");
            jsonObj = new JSONObject(readJSON(p_act, srcUPlaces));
            jsonArrayPlace = jsonObj.getJSONArray(String.valueOf(p_place.uniqueID));
            p_place.name = p_act.getResources().getStringArray(R.array.ru_placeUNames)[p_place.uniqueID];
            //System.out.println("file loaded");
            //unique actions not used to save space
            //JSONArray jsonArrayParams = jsonArrayPlace.getJSONArray(4);
            //v_par = new int[jsonArrayParams.length()];
            //for (int i=0; i<jsonArrayParams.length(); i++)  p_place.permUActions.put(i, true);
        }
        Random r = new Random();
        float f;
        do {
            f = (float) (r.nextGaussian() / 2.0) + 1.0f;
        } while (!(f >= 0.5 && f <= 3.0));
        p_place.size = Math.round(jsonArrayPlace.getInt(PlaceType.c_PlaceSize) * f);
        //basic actions
        int[] v_permActions = getPlaceArray(p_act, p_place.typeID, PlaceType.c_PlaceActions);
        for (int i : v_permActions) p_place.permActions.put(i, true);
        //for (int i=0; i<jsonArrayPlace.getJSONArray(4).length(); i++) p_place.permActions.put(i, true);
        for (int i=0; i<jsonArrayPlace.getJSONArray(PlaceType.c_PlacePersons).length(); i++) {
            int[] v_personParams = parseCodePerson(jsonArrayPlace.getJSONArray(PlaceType.c_PlacePersons).getString(i));
            //0 - typeID, 1 - UID, 2 - quantity
            for (int j=1; j<=v_personParams[2]; j++) Actions.personChangeLocation(p_act, new PersonType(p_act, p_place.worldID, v_personParams[0], v_personParams[1]), p_place, null);
        }
        //System.out.println("set loading started");
        if (jsonArrayPlace.getInt(PlaceType.c_PlaceItems)>=0) loadSet(p_act, p_place.lootList, jsonArrayPlace.getInt(PlaceType.c_PlaceItems));
        //System.out.println("set loading finished");

    }

    static void loadSet (GameActivity p_act, ArrayList<ItemType> p_itemContainer, int p_itemSet) {
        try {
            //System.out.println("set source loaded1");
            JSONObject jsonObj = new JSONObject(readJSON(p_act, srcItemSets));
            //System.out.println("set source loaded1");
            JSONArray jsonSetArray = jsonObj.getJSONArray(String.valueOf(p_itemSet));
            //System.out.println("set source loaded2");
            String v_itemStr;
            //System.out.println("set source loaded3");
            for (int i=0; i<jsonSetArray.length(); i++) {
                v_itemStr = jsonSetArray.getString(i);
                int[] intTMP = parseCodeItem(v_itemStr);
                p_itemContainer.add(new ItemType(p_act, intTMP[0], intTMP[1], intTMP[2], intTMP[3]));
            }

            /*while (v_itemsSet.length() > 0) {
                if (v_itemsSet.contains("_")) {
                    strTMP = v_itemsSet.substring(0, v_itemsSet.indexOf("_"));
                    v_itemsSet = v_itemsSet.substring(strTMP.length() + 1);
                } else {
                    strTMP = v_itemsSet;
                    v_itemsSet = "";
                }
                int[] intTMP = parseCodeItem(strTMP);
                p_itemContainer.add(new ItemType(p_act, intTMP[0], intTMP[1], intTMP[2], intTMP[3]));
                //System.out.printf("Loaded into box: ID #%d, UID #%d, contain %d, quantity is %d.\n", intTMP[0], intTMP[1], intTMP[2], intTMP[3]);
            }*/
        } catch (Exception e) {
            System.out.println("Trouble while loading item box!");
        }
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
    static int[] getPlaceArray (GameActivity p_act, int p_typeID, int p_paramID) {
        int[] v_par;
        try {
            JSONObject jsonObj = new JSONObject(readJSON(p_act, srcPlaces));
            JSONArray jsonArrayParam = jsonObj.getJSONArray(String.valueOf(p_typeID)).getJSONArray(p_paramID);
            v_par = new int[jsonArrayParam.length()];
            for (int i=0; i<jsonArrayParam.length(); i++)  v_par[i] = jsonArrayParam.getInt(i);
            return v_par;
        } catch (Exception e) {
            return new int[0];
        }
    }


    //Выдает параметры уникального места
    static int[] getUPlaceArray (GameActivity p_act, int p_typeUID, int p_paramID) {
        int[] v_par;
        try {
            JSONObject jsonObj = new JSONObject(readJSON(p_act, srcUPlaces));
            JSONArray jsonArrayParam = jsonObj.getJSONArray(String.valueOf(p_typeUID)).getJSONArray(p_paramID);
            v_par = new int[jsonArrayParam.length()];
            for (int i=0; i<jsonArrayParam.length(); i++)  v_par[i] = jsonArrayParam.getInt(i);
            return v_par;
        } catch (Exception e) {
            return new int[0];
        }

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
        InputStream is = am.open("ru_names.json");
        String s = convertStreamToString(is);
        is.close();
        return s;
    }*/
}
