package com.game.michael.first;

import android.app.Activity;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.widget.PopupMenu;

import java.util.Locale;
import java.util.TreeMap;

abstract class BasicProcedures {

    static String timeToString (int[] p_dateTime) {
        return String.format(Locale.getDefault(), "%06d%02d%02d%02d%02d", p_dateTime[0], p_dateTime[1], p_dateTime[2], p_dateTime[3], p_dateTime[4]
                 );
    }

    static int[] timeFromString (String p_dateTime) {
        int[] v_dateTime = new int[]{0, 0, 0, 0, 0};
        v_dateTime[0] = Integer.parseInt(p_dateTime.substring(0, 6));
        v_dateTime[1] = Integer.parseInt(p_dateTime.substring(6, 8));
        v_dateTime[2] = Integer.parseInt(p_dateTime.substring(8, 10));
        v_dateTime[3] = Integer.parseInt(p_dateTime.substring(10, 12));
        v_dateTime[4] = Integer.parseInt(p_dateTime.substring(12, 14));
        return v_dateTime;
    }

    //Возвращает время в виде строки формата 16:32 (03.09.12345)
    static String formatDate (int[] p_dateTime) {
        return String.format(Locale.getDefault(), "%02d:%02d (%02d.%02d.%d)", p_dateTime[3], p_dateTime[4], p_dateTime[2],
                p_dateTime[1], p_dateTime[0]);
    }

    static String coordinatesToString (int[] p_coordinates) {
        if (p_coordinates.length == 2) {
            return String.valueOf(p_coordinates[0]) + "/" + String.valueOf(p_coordinates[1]);
        } else {return null;}
    }

    static int[] coordinatesFromString (String p_coordinates) {
        int[] v_res = new int[2];
        try {
            v_res[0] = Integer.parseInt(p_coordinates.substring(0, p_coordinates.indexOf("/")));
            v_res[1] = Integer.parseInt(p_coordinates.substring(p_coordinates.indexOf("/") + 1));
            return v_res;
        } catch (Exception e) {
            return null;
        }
    }

    static String getItemNameS (Activity p_act, int p_itemID, String p_language) {
        return p_act.getResources().getStringArray(p_act.getResources().getIdentifier(
                p_language + "_itemsS" + String.valueOf(p_itemID / 100),
                "array",
                p_act.getPackageName()
        ))[p_itemID % 100];
    }
    static String getItemNameP (Activity p_act, int p_itemID, String p_language) {
        return p_act.getResources().getStringArray(p_act.getResources().getIdentifier(
                p_language + "_itemsP" + String.valueOf(p_itemID / 100),
                "array",
                p_act.getPackageName()
        ))[p_itemID % 100];
    }
    static String getItemDesc (Activity p_act, int p_itemID, String p_language) {
        return p_act.getResources().getStringArray(p_act.getResources().getIdentifier(
                p_language + "_itemDesc" + String.valueOf(p_itemID / 100),
                "array",
                p_act.getPackageName()
        ))[p_itemID % 100];
    }

    static String getFactorName (Activity p_act, int p_factorID, String p_language) {
        return p_act.getResources().getStringArray(p_act.getResources().getIdentifier(
                p_language + "_factorsNames",
                "array",
                p_act.getPackageName()
        ))[p_factorID];
    }

    static String getFactorDescr (Activity p_act, int p_factorID, String p_language) {
        return p_act.getResources().getStringArray(p_act.getResources().getIdentifier(
                p_language + "_factorsDescr",
                "array",
                p_act.getPackageName()
        ))[p_factorID];
    }

    static String getInfoMessage (Activity p_act, String p_param, String p_language) {
        try {
            return p_act.getResources().getString(p_act.getResources().getIdentifier(
                    p_language + "_info" + p_param,
                    "string",
                    p_act.getPackageName()
            ));
        } catch (Exception e) {return "Cant get info text!";}
    }

    static String getActionPopUp (Activity p_act, String p_language, int p_actionID) {
        try {
            return p_act.getResources().getStringArray(p_act.getResources().getIdentifier(
                    p_language + "_actionPopUp",
                    "array",
                    p_act.getPackageName()
            ))[p_actionID];
        } catch (Exception e) {return "Cant get info text!";}
    }

    static String getLabelGeneral (Activity p_act, String p_param, String p_language) {
        try {
            return p_act.getResources().getString(p_act.getResources().getIdentifier(
                    p_language + "_labelGameGeneral" + p_param,
                    "string",
                    p_act.getPackageName()
            ));
        } catch (Exception e) {return "Cant get info text!";}
    }

    static String getActionMessage (Activity p_act, String p_language, int p_actionID, String p_actionResult) {
        return p_act.getResources().getStringArray(p_act.getResources().getIdentifier(
                p_language + "_actionMessage" + p_actionResult,
                "array",
                p_act.getPackageName()))[p_actionID];
    }

    static String getLocationDescription (Activity p_act, LocationType p_location, String p_language) {
        try {
            String v_result;
            String tmp_paramName = (p_location.isPlace)?((p_location.uniqueID < 0)?"place":"placeU"):"territory";
            int tmp_paramID = (p_location.uniqueID < 0)?p_location.typeID:p_location.uniqueID;
            /*v_result = p_act.getResources().getStringArray(p_act.getResources().getIdentifier(
                    p_language + "_actionMessage" + ((p_location.isPlace)?"0":"1"),
                    "array",
                    p_act.getPackageName()))[tmp_paramID];*/
            //description
            v_result = p_act.getResources().getStringArray(p_act.getResources().getIdentifier(
                    p_language + "_" + tmp_paramName + "Descr",
                    "array",
                    p_act.getPackageName()))[tmp_paramID];
            /*//visitors info
            v_result += (p_location.visitorsList.size() <= 1)
                    ?p_act.getResources().getStringArray(p_act.getResources().getIdentifier(
                    p_language + "_" + tmp_paramName + "Descr",
                    "array",
                    p_act.getPackageName()))[tmp_paramID] + " ";*/
            return v_result;
        } catch (Exception e) {
            System.out.printf("ERROR: Can't get %s description. %s, world %d, coordinates(%d, %d).\n",
                    (p_location.isPlace)?"place":"territory",
                    (p_location.name.length()>0)?p_location.name:("Type "+String.valueOf(p_location.typeID)),
                    p_location.worldID,
                    p_location.coordinates[0],
                    p_location.coordinates[1]);
            return "";
        }
    }

    static boolean checkExpiration (int[] p_checkedDt, int[] p_actualDt) {
        boolean v_res = false;
        return v_res;
    }

    static int[] parseCodeItem (String p_str) {
        TreeMap<Integer, int[]> items = new TreeMap<>();
        int[] item; //0 - ID, 1- UID, 2 - contain, 3 - quantity
        int i=0;
        String tmp;
        String strTMP;
        int sumProbability = 0;
        while (p_str.length()>0) {
            item = new int[4];
            if (p_str.contains("/")) {
                strTMP = p_str.substring(0, p_str.indexOf("/"));
                p_str = p_str.substring(strTMP.length() + 1);
            } else {
                strTMP = p_str;
                p_str = "";
            }
            //System.out.println("Choise X: " + strTMP);
            //System.out.println("Left string: " + p_str);
            //itemID
            item[0] = Integer.valueOf(tmp = strTMP.replaceAll("[/+*%@].*", ""));
            strTMP = strTMP.substring(tmp.length());
            //itemUID
            if (strTMP.contains("+")) {
                item[1] = Integer.valueOf(tmp = strTMP.replaceAll("(^\\+)|([/*%@].*)", ""));
                strTMP = strTMP.substring(tmp.length() + 1);
            } else {
                item[1]= -1;
            }
            //item contain
            if (strTMP.contains("@")) {
                item[2] = Integer.valueOf(tmp = strTMP.replaceAll("(^@)|([/*%].*)", ""));
                strTMP = strTMP.substring(tmp.length() + 1);
            } else {
                item[2]= -1;
            }
            //item quantity and probability
            do {
                int min, max;
                tmp = strTMP.replaceAll("(^\\*)|(\\*.*)", "");
                //System.out.println("TMP" + tmp);
                strTMP = (strTMP.contains("*"))?strTMP.substring(tmp.length()+1):"";
                //System.out.println("strTMP" + strTMP);
                //amount
                min = (tmp.replaceAll("[-%].*", "").length()>0)?Integer.valueOf(tmp.replaceAll("[-%].*", "")):1;
                max = (tmp.contains("-"))?Integer.valueOf(tmp.replaceAll("(.*-)|(%.*)", "")):min;
                //System.out.printf("min= %d; max= %d\n", min, max);
                item[3] = min + (int) Math.floor(Math.random()*(max-min+1));
                //if (item[3] <= 0) item
                //probability
                sumProbability += (tmp.contains("%"))?Integer.valueOf(tmp.replaceAll(".*%", "")):100;
                //System.out.printf("Probability is %d\n", sumProbability);
                if (item[3]>0) {
                    items.put(sumProbability, item.clone());
                } else {
                    int tmpID = item[0];
                    item[0] = -1;
                    items.put(sumProbability, item.clone());
                    item[0] = tmpID;
                }
                i++;
            } while (strTMP.contains("*")||strTMP.length()>0);

        }
        //System.out.printf("**************Max probability is %d\n", sumProbability);
        i = (int) Math.floor(Math.random()*(sumProbability));
        //i = 50;
        //System.out.printf("**************Random value is %d\n", i);
        return items.get(items.higherKey(i));

    }

    static int[] parseCodePerson (String p_str) {
        TreeMap<Integer, int[]> items = new TreeMap<>();
        int[] person; //0 - ID, 1- UID, 2 - quantity
        int i=0;
        String tmp;
        String strTMP;
        int sumProbability = 0;
        while (p_str.length()>0) {
            person = new int[3];
            if (p_str.contains("/")) {
                strTMP = p_str.substring(0, p_str.indexOf("/"));
                p_str = p_str.substring(strTMP.length() + 1);
            } else {
                strTMP = p_str;
                p_str = "";
            }
            //System.out.println("Choise X: " + strTMP);
            //System.out.println("Left string: " + p_str);
            //personID
            person[0] = Integer.valueOf(tmp = strTMP.replaceAll("[/+*%].*", ""));
            strTMP = strTMP.substring(tmp.length());
            //itemUID
            if (strTMP.contains("+")) {
                person[1] = Integer.valueOf(tmp = strTMP.replaceAll("(^\\+)|([/*%].*)", ""));
                strTMP = strTMP.substring(tmp.length() + 1);
            } else {
                person[1]= -1;
            }
            //persons quantity and probability
            do {
                int min, max;
                tmp = strTMP.replaceAll("(^\\*)|(\\*.*)", "");
                //System.out.println("TMP" + tmp);
                strTMP = (strTMP.contains("*"))?strTMP.substring(tmp.length()+1):"";
                //System.out.println("strTMP" + strTMP);
                //amount
                min = (tmp.replaceAll("[-%].*", "").length()>0)?Integer.valueOf(tmp.replaceAll("[-%].*", "")):1;
                max = (tmp.contains("-"))?Integer.valueOf(tmp.replaceAll("(.*-)|(%.*)", "")):min;
                //System.out.printf("min= %d; max= %d\n", min, max);
                person[2] = min + (int) Math.floor(Math.random()*(max-min+1));
                //if (item[3] <= 0) item
                //probability
                sumProbability += (tmp.contains("%"))?Integer.valueOf(tmp.replaceAll(".*%", "")):100;
                //System.out.printf("Probability is %d\n", sumProbability);
                if (person[2]>0) {
                    items.put(sumProbability, person.clone());
                } else {
                    int tmpID = person[0];
                    person[0] = -1;
                    items.put(sumProbability, person.clone());
                    person[0] = tmpID;
                }
                i++;
            } while (strTMP.contains("*")||strTMP.length()>0);

        }
        //System.out.printf("**************Max probability is %d\n", sumProbability);
        i = (int) Math.floor(Math.random()*(sumProbability));
        //i = 50;
        //System.out.printf("**************Random value is %d\n", i);
        return items.get(items.higherKey(i));
    }

}
