package com.game.michael.first;

import android.app.Activity;
import android.util.SparseArray;
import android.util.SparseBooleanArray;

import java.util.ArrayList;

import static com.game.michael.first.SourceJSON.loadPlace;

class PlaceType extends LocationType{
    static final byte c_PlaceSize = 0;
    static final byte c_PlacePersons = 1;
    static final byte c_PlaceItems = 2;
    static final byte c_PlaceActions = 3;
    static final byte c_PlaceUActions = 4;

    int placeID;
    //int[] coordinates;


    //Конструктор места
    PlaceType (GameActivity p_act,
               int p_worldID,
               int[] p_territoryID,
               int p_typeID,
               /*int p_placeID*/
               int p_UID) {
        try {
            placeID = (p_act.allPlaces.size()>0)?p_act.allPlaces.keyAt(p_act.allPlaces.size() - 1) + 1:0;
            typeID = p_typeID;
            uniqueID = p_UID;
            worldID = p_worldID;
            isPlace = true;
            coordinates = p_territoryID.clone();
            permActions = new SparseBooleanArray();
            lootList = new ArrayList<>(1);
            visitorsList = new SparseArray<>(1);
            loadPlace(p_act, this);
            //System.arraycopy(p_territoryID, 0, coordinates, 0, 2);
            //size = SourceJSON.getPlacePar(p_act, typeID, "size");
            name = (uniqueID < 0)?p_act.getResources().getStringArray(R.array.ru_placeNames)[typeID]:p_act.getResources().getStringArray(R.array.ru_placeUNames)[uniqueID];
        } catch (Exception e) {
            System.out.printf("ERROR: Can't load place %d (unique ID %d) parameters at territory (%d;%d)!", p_typeID, p_UID, p_territoryID[0], p_territoryID[1]);
        }
    }

    //Смена типа места
    boolean changeType (Activity p_act, int p_newTypeID) {
        try {
            typeID = p_newTypeID;
        size = SourceJSON.getPlacePar(p_act, typeID, "size");
        name = p_act.getResources().getStringArray(R.array.ru_placeNames)[typeID];
        return true;
        } catch (Exception e) {return false;}
    }

    boolean makeUnique (GameActivity p_act, int p_uniqueID) {
        this.permActions.clear();
        uniqueID = p_uniqueID;
        name = p_act.getResources().getStringArray(R.array.ru_placeUNames)[uniqueID];
        /*try {
            for (int i : getUPlaceArray(p_act, this.uniqueID, srcActions)) {
                this.permActions.put(i, true);
            }
        } catch (Exception e) {

        }*/
        //fetch the aspects of the unique place
        //load persons
        //load items
        return true;
    }

    /*boolean loadParams (GameActivity p_act) {
        this.permActions.clear();
        try {this.size = getPlaceArray(p_act, this.typeID, srcSize)[0];} catch (Exception e) {this.size = 1;}
        try {
            for (int i : getPlaceArray(p_act, this.typeID, srcActions)) {
                this.permActions.put(i, true);
            }
        } catch (Exception e) {}
        return true;
    }*/

    /*boolean settlePerson (PersonType p_person) {
        return false;
    }*/

}
