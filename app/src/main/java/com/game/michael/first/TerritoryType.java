package com.game.michael.first;

import android.app.Activity;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;

import java.util.ArrayList;

import static com.game.michael.first.BasicProcedures.coordinatesToString;
import static com.game.michael.first.SourceJSON.*;

class TerritoryType extends LocationType {
    static final String srcSize = "size";
    static final String srcActions = "actions";
    static final String srcPersons = "persons";
    static final String srcItems = "items";
    static final String srcResources = "resources";

    int[] coordinates;
    SparseArray<PlaceType> placesList;


    TerritoryType (Activity p_act, int p_worldID, int[] p_coordinates, int p_typeID) {
        worldID = p_worldID;
        typeID = p_typeID;
        coordinates = p_coordinates;
        name = p_act.getResources().getStringArray(R.array.ru_territoryName)[typeID];
        permActions = new SparseBooleanArray();
        loadParams(p_act);
        lootList = new ArrayList<>();
        placesList = new SparseArray<>();
        visitorsList = new SparseArray<>();
    }

    boolean createPlace (GameActivity p_act, int p_typeID, int p_placeID) {
        PlaceType placeTMP = new PlaceType(p_act, worldID, coordinates, p_typeID, p_placeID);
        placesList.put(p_placeID, placeTMP);
        p_act.allPlaces.put(placeTMP.placeID, placeTMP);
        return true;
    }

    boolean loadParams (Activity p_act) {
        this.size = getTerritoryArray(p_act, this.typeID, srcSize)[0];
        this.permActions.clear();
        for (int i : getTerritoryArray(p_act, this.typeID, TerritoryType.srcActions)) {
            this.permActions.put(i, true);
        }
        //for (this.lootList.add(itemsFromTheSource))
        return true;
    }
}
