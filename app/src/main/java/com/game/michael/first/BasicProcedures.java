package com.game.michael.first;

import android.app.Activity;
import android.util.SparseArray;
import android.widget.PopupMenu;

import java.util.Locale;

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

    static String getLabelGeneral (Activity p_act, String p_param, String p_language) {
        try {
            return p_act.getResources().getString(p_act.getResources().getIdentifier(
                    p_language + "_labelGameGeneral" + p_param,
                    "string",
                    p_act.getPackageName()
            ));
        } catch (Exception e) {return "Cant get info text!";}
    }


    static boolean checkExpiration (int[] p_checkedDt, int[] p_actualDt) {
        boolean v_res = false;
        return v_res;
    }



}
