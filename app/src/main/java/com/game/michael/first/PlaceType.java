package com.game.michael.first;

import android.app.Activity;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;

import java.util.ArrayList;

import static com.game.michael.first.SourceJSON.getPlaceArray;
import static com.game.michael.first.SourceJSON.getTerritoryArray;
import static com.game.michael.first.SourceJSON.getUPlaceArray;

class PlaceType extends LocationType{
    static final String srcSize = "size";
    static final String srcActions = "actions";
    static final String srcPersons = "persons";
    static final String srcItems = "items";

    public int placeID;
    public int[] territoryID;


    //Конструктор места
    public PlaceType (Activity p_act, int p_worldID, int[] p_territoryID, int p_typeID, int p_placeID) {
        placeID = p_placeID;
        typeID = p_typeID;
        uniqueID = -1;
        worldID = p_worldID;
        territoryID = new int[2];
        permActions = new SparseBooleanArray();
        System.arraycopy(p_territoryID, 0, territoryID, 0, 2);
        size = SourceJSON.getPlacePar(p_act, typeID, "size");
        name = p_act.getResources().getStringArray(R.array.ru_placeNames)[typeID];
        lootList = new ArrayList<>(1);
        visitorsList = new SparseArray<>(1);
    }

    //Смена типа места
    public boolean changeType (Activity p_act, int p_newTypeID) {
        try {
            typeID = p_newTypeID;
        size = SourceJSON.getPlacePar(p_act, typeID, "size");
        name = p_act.getResources().getStringArray(R.array.ru_placeNames)[typeID];
        return true;
        } catch (Exception e) {return false;}
    }

    boolean makeUnique (Activity p_act, int p_uniqueID) {
        this.permActions.clear();
        uniqueID = p_uniqueID;
        name = p_act.getResources().getStringArray(R.array.ru_placeUNames)[uniqueID];
        try {
            for (int i : getUPlaceArray(p_act, this.uniqueID, srcActions)) {
                this.permActions.put(i, true);
            }
        } catch (Exception e) {}
        //fetch the aspects of the unique place
        return true;
    }

    boolean loadParams (Activity p_act) {
        this.permActions.clear();
        try {this.size = getPlaceArray(p_act, this.typeID, srcSize)[0];} catch (Exception e) {this.size = 1;}
        try {
            for (int i : getPlaceArray(p_act, this.typeID, srcActions)) {
                this.permActions.put(i, true);
            }
        } catch (Exception e) {}
        return true;
    }

}
