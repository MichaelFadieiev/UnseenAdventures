package com.game.michael.first;

import android.util.SparseArray;
import android.util.SparseBooleanArray;

import java.util.ArrayList;

class LocationType {

    public int typeID;
    public int worldID; //идентификатор мира (<0 - сферы, 0 - космос,  >0 - планеты)
    public int size;
    int uniqueID; //идентификатор уникального места (< 0 - не уникально)
    public String name;
    public SparseArray<PersonType> visitorsList;
    public ArrayList<ItemType> lootList;
    SparseBooleanArray permActions;

    public ItemType getLoot (int p_itemID, int p_quantity, int p_freeVolume) {
        ItemType v_item = new ItemType();
        int v_quantity;
        for (int i = 0; i < lootList.size(); i++) {
            ItemType v1_item = lootList.get(i);
            if (v1_item.itemID == p_itemID) {
                //v_item = i;
                v_quantity = p_quantity;
                //if (p_quantity < v_quantity) {v_quantity = p_quantity;} -- в метод предмета!!!
                if (p_freeVolume < (v1_item.volume * v_quantity)) {v_quantity = p_freeVolume / v1_item.volume;}
                v_item = v1_item.takeItem(v_quantity);
                if (v1_item.endDT.size() < 1) {lootList.remove(v1_item);}
                break;
            }
        }
        return v_item;
    }
    public boolean dropLoot (ArrayList<ItemType> p_loot) {
        return lootList.addAll(p_loot);

        //return false;
    }

    public boolean dropLoot (ItemType p_item) {
        return lootList.add(p_item); //нужно добавлять в существующие стаки!
    }

    //naming can be implemented in direct manner!
    public boolean naming(String p_name) {
        name = p_name;
        return true;
    }
}
