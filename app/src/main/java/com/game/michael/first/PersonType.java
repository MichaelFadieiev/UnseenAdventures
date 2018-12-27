package com.game.michael.first;

import android.app.Activity;
import android.content.Context;
import android.util.ArrayMap;
import android.util.SparseArray;
import android.util.SparseBooleanArray;

import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;

import static com.game.michael.first.GameActivity.flowTime;
import static com.game.michael.first.SourceJSON.*;
import static com.game.michael.first.BasicProcedures.*;


class PersonType {
    static final String placePActions = "actions";
    static final String placePPersons = "persons";
    static final String placePItems = "items";
    static final String placePUActions = "uactions";
    static final String placePUPersons = "upersons";
    static final String placePUItems = "uitems";

    int personID;
    String name;
    String surname;
    //public Hashtable<String, String> relatives; -not yet
    SparseArray<Float> attributes; //SIPAEC
    SparseArray<Float> stats, statsMax; //0_hp, 1_stm, 2_exh, 3_hng, 4_thr
    SparseArray<Float> skills;
    int status;
    char gender;
    TreeMap<String, String> log;
    boolean genom;

    SparseBooleanArray actBasic, actBasicTmp;


    private int weightMax, volumeMax;
    int weightFree, volumeFree;

    ArrayList<ItemType> inventory;
    SparseArray<ItemType> equipped; //0-right_hand, 1-left_hand, 2-head, 3-torso, 4-legs, 5-hands, 6-feet
    //7-armr-head, 8-armr-torso, 9-armr-legs, 10-armr-hands, 11-armr-feet
    int placeID;//position of the person {placeID (if positive), xAxis, yAxis}
    int[] territoryID;
    private int worldID;//идентификатор мира (<0 - сферы, 0 - космос,  >0 - планеты)

    SparseArray<int[]> factors; //-not yet

    private long age;
    //private Float renewDate; -deprecated

    PersonType(int p_worldID,
                      int p_personID,
                      String p_name,
                      String p_surname,
                      SparseArray<Float> p_attributes,
                      SparseArray<Float> p_skills,
                      boolean p_genom,
                      char p_gender) {
        worldID = p_worldID;
        personID = p_personID;
        name = p_name;
        surname = p_surname;
        genom = p_genom;
        age = 28;
        attributes = new SparseArray<>();
        attributes = p_attributes.clone();
        skills = new SparseArray<>();
        skills = p_skills.clone();
        stats = new SparseArray<>();
        actBasic = new SparseBooleanArray(1);
        actBasicTmp = new SparseBooleanArray(1);
        statsMax = new SparseArray<>();
        statsMax.put(0, (400+attributes.get(0)*50+attributes.get(4)*50)); //hpt
        statsMax.put(1, (400+attributes.get(0)*20+attributes.get(2)*10+attributes.get(3)*50+attributes.get(4)*20));//stm
        statsMax.put(2, (float) 1000);//exh
        statsMax.put(3, (float) 1000);//hng
        statsMax.put(4, (float) 1000);//thr
        for (int i=0; i<statsMax.size(); i++) {stats.put(i, statsMax.get(i));}//start with max stats
        //factors = new Hashtable<>();
        inventory = new ArrayList<>(1);
        equipped = new SparseArray<>(7);
        factors = new SparseArray<>(2);
        weightMax = 10000 * Math.round(attributes.get(0));
        weightFree = weightMax;
        volumeMax = 100000;
        volumeFree = volumeMax;
        log = new TreeMap<>();
        territoryID = new int[2];
        if (p_gender == 'M' || p_gender == 'F') {
            gender = p_gender;
        } else {gender = 'M';}
    }

    //Генерация пустого персонажа для последующего заполнения характеристиками.
    public PersonType () {
        attributes = new SparseArray<>();
        stats = new SparseArray<>();
        statsMax = new SparseArray<>();
        skills = new SparseArray<>();
        inventory = new ArrayList<>(1);
        equipped = new SparseArray<>(7);
        factors = new SparseArray<>(2);
        log = new TreeMap<>();
        territoryID = new int[2];
    }

    //Процедура рождения персонажа.
    public void birthPPC (String[] p_name, PersonType p_father, PersonType p_mother, int[] p_dateTime) {
        /*Procedure for creation of new NPC, generating name and so on*/
        switch ((int) (Math.random()*2)/2) {
            case 0: gender = 'M';
                name = p_name[0];
                break;
            case 1: gender = 'F';
                name = p_name[1];
                break;
            default: gender = 'M';
                name = p_name[0];
                break;
        }
        switch ((int) (Math.random()*2)/2) {
            case 0: surname = p_mother.surname;
                break;
            case 1: surname = p_father.surname;
                break;
            default: surname = "Dou";
                break;
        }
        age = 0;
        worldID = p_mother.worldID;
        placeID = p_mother.placeID;
        territoryID = p_mother.territoryID.clone();
        //System.arraycopy(p_mother.locationID, 0, locationID, 0, locationID.length);
        this.log.put(BasicProcedures.timeToString(p_dateTime), "Born");
    }

    //Добавление стака предметов в инвентарь - прилетает стак, проверяется на размер, доступный вес,
    //дробится и возвращает, что не влезло.
    boolean itemAdd (ItemType p_item, int p_quantity) {
        ItemType v_item;
        if (!p_item.isLiquid) {
            v_item = p_item.takeItem(Math.min(p_quantity, Math.min(volumeFree / p_item.volume, weightFree / p_item.weight)));
            //int v_volume, v_weight, v_size;
            //v_size = p_item.endDT.size();
            //v_volume = v_size * p_item.volume;
            /*if (v_volume > volumeFree) {
                v_size = volumeFree / p_item.volume;
            }
            v_weight = v_size * p_item.weight;
            if (v_weight > weightFree) {
                v_size = weightFree / p_item.weight;
            }*/
            if (!v_item.endDT.isEmpty()) {
                //boolean v_isThere = false;
                int v_indThere = -1;
                ItemType itemTMP;
                for (int i = 0; i < this.inventory.size(); i++) {
                    itemTMP = this.inventory.get(i);
                    if ((v_item.itemID == itemTMP.itemID) && (v_item.itemUID < 0)
                            && (itemTMP.itemUID < 0) && (!itemTMP.isJar) && (!itemTMP.isContainer)) {
                        //v_isThere = true;
                        v_indThere = i;
                        break;
                    }
                }
                if (v_indThere >= 0) {
                    inventory.get(v_indThere).addItem(v_item.endDT);
                } else {
                    inventory.add(v_item);
                }
                weightFree -= v_item.endDT.size() * v_item.weight;
                volumeFree -= v_item.endDT.size() * v_item.volume;
            }
            return true;
            /*if (p_item.endDT.size() != 0) {
                return p_item;
            } else {
                return null;
            }*/
        } else {
            //ItemType v_item;
            for (int i = 0; i < inventory.size(); i++) {
                v_item = inventory.get(i);
                if (v_item.isJar) {
                    if ((v_item.contain.isEmpty()) || (v_item.contain.get(0).itemID == p_item.itemID)) {
                        int weightTmp = v_item.weight;
                        if (v_item.fillJar(p_item)) {
                            this.weightFree -= weightTmp - v_item.weight;
                        }
                    }
                    if (p_item.endDT.isEmpty()) return true;
                }
            }
        }
        if (p_item.endDT.isEmpty()) {
            return true;
        } else {return false;}
    }

    //забирается часть предметов из инвентаря/экипировки
    ItemType itemTake (int p_itemIndex, int p_quantity, boolean isEquipped) {
        try {
            ItemType v_item;
            ItemType v_res;
            if (isEquipped) {
                v_item = this.equipped.get(p_itemIndex);
                v_res = v_item.takeItem(p_quantity);
                if (v_item.endDT.isEmpty()) {
                    this.equipped.delete(p_itemIndex);
                }
                weightFree += v_res.weight * v_res.endDT.size();
                return v_res;
            } else {
                v_item = this.inventory.get(p_itemIndex);
                v_res = v_item.takeItem(Math.min(v_item.endDT.size(), p_quantity));
                weightFree += v_res.weight * v_res.endDT.size();
                if (weightFree > weightMax) {
                    weightFree = weightMax;
                }
                volumeFree += v_res.volume * v_res.endDT.size();
                if (volumeFree > volumeMax) {
                    volumeFree = volumeMax;
                }
                if (v_item.endDT.isEmpty()) {
                    this.inventory.remove(p_itemIndex);
                }
                return v_res;
            }
        } catch (Exception e) {
            return null;
        }
    }

    boolean itemDrop (GameActivity p_act, int p_itemIndex, int p_quantity, boolean isEquipped) {
        ItemType v_item = this.itemTake(p_itemIndex, p_quantity, isEquipped);
        if (v_item != null) {
            return p_act.getPersonLocation(this).lootList.add(v_item);
        }
        return false;
    }

    boolean itemConsume (GameActivity p_act, ItemType p_item) {
        if (p_item.tags[0].equals("drnk")) {
            this.stats.put(4, this.stats.get(4) + (Integer.parseInt(p_item.tags[1]) * p_item.endDT.size()));
        }
        if (p_item.tags[2].equals("alco")) {
            if (this.factors.indexOfKey(2) >= 0) {
                this.factors.put(2, flowTime(factors.get(2), Integer.parseInt(p_item.tags[3]) * p_item.endDT.size()));
            } else {
                this.factors.put(2, flowTime(p_act.dateTime, Integer.parseInt(p_item.tags[3]) * p_item.endDT.size()));
            }
        }
        if (p_item.tags[0].equals("food")) {
            this.stats.put(3, this.stats.get(3) + (Integer.parseInt(p_item.tags[1]) * p_item.endDT.size()));
        }
        if (p_item.tags[2].equals("food")) {
            this.stats.put(3, this.stats.get(3) + (Integer.parseInt(p_item.tags[3]) * p_item.endDT.size()));
        }
        if (this.stats.get(4) > this.statsMax.get(4)) {
            this.stats.put(4, this.statsMax.get(4));
        }
        if (this.stats.get(3) > this.statsMax.get(3)) {
            this.stats.put(3, this.statsMax.get(3));
        }

        return false;
    }

    boolean itemInHand (GameActivity p_act, ItemType p_item, int p_quantity) {
        if ((p_item.weight <= (this.attributes.get(0) * 5000)) && (p_item.weight <= this.weightFree)) {
            //weight*volume > 10'000'000 for STR <= 5 - 2hand
            //
            //boolean is2Hand = false;
            if ((p_item.volume * p_item.weight) > (this.attributes.get(0) * 2000000)) { //2-handed
                //is2Hand = true;
                if ((this.equipped.indexOfKey(0) < 0) && (this.equipped.indexOfKey(1) < 0)) {
                    ItemType itemTMP = p_item.takeItem(p_quantity);
                    this.equipped.put(0, itemTMP);
                    this.equipped.put(1, itemTMP);
                    this.weightFree -= itemTMP.weight * itemTMP.endDT.size();
                    return true;
                }
            } else if (this.equipped.indexOfKey(0) < 0) {
                ItemType itemTMP = p_item.takeItem(p_quantity);
                this.equipped.put(0, itemTMP);
                this.weightFree -= itemTMP.weight * itemTMP.endDT.size();
                return true;
            } else if (this.equipped.indexOfKey(1) < 0) {
                ItemType itemTMP = p_item.takeItem(p_quantity);
                this.equipped.put(1, itemTMP);
                this.weightFree -= itemTMP.weight * itemTMP.endDT.size();
                return true;
            }
        }
        return false;
    }

    boolean itemEquip (GameActivity p_act, ItemType p_item) {
        int intTmp = -1;
        int sizeTmp =  p_item.endDT.size();
        if (p_item.tags[0].equals("clth")) {
            intTmp = 0;
        } else if (p_item.tags[0].equals("armr")) {
            intTmp = 5;
        } else {
            return false;
        }
        if (p_item.weight <= this.weightFree) {
            switch (p_item.tags[1]) {
                case "hat" : {
                    if (this.equipped.indexOfKey(2+intTmp) < 0) {
                        this.equipped.put(2 + intTmp, p_item.takeItem(1));
                    }
                    break;
                }
                case "shrt" : {
                    if (this.equipped.indexOfKey(3+intTmp) < 0) {
                        this.equipped.put(3 + intTmp, p_item.takeItem(1));
                    }break;
                }
                case "pnts" : {
                    if (this.equipped.indexOfKey(4+intTmp) < 0) {
                        this.equipped.put(4 + intTmp, p_item.takeItem(1));
                    }break;
                }
                case "glvs" : {
                    if (this.equipped.indexOfKey(5+intTmp) < 0) {
                        this.equipped.put(5 + intTmp, p_item.takeItem(1));
                    }break;
                }
                case "shs" : {
                    if (this.equipped.indexOfKey(6+intTmp) < 0) {
                        this.equipped.put(6 + intTmp, p_item.takeItem(1));
                    }break;
                }
                default : {
                    return false;
                }
            }
        }
        if (sizeTmp - p_item.endDT.size() == 1) {return true;} else {return false;}
    }

    void renewFactors (GameActivity p_act, int[] p_dttm) {
        if (this.factors.indexOfKey(0) >= 0) { //бессилие
            if (this.stats.get(1) > 0) this.factors.delete(0);
        } else {if (this.stats.get(1) <= 0) this.factors.put(0, p_dttm);}
        if (this.factors.indexOfKey(1) >= 0) { //без сознания
            if (this.stats.get(2) > 0) this.factors.delete(1);
        } else {if (this.stats.get(2) <= 0) this.factors.put(1, p_dttm);}
        if (this.factors.indexOfKey(2) >= 0) { //выпил
            double v_last = (Long.parseLong(timeToString(this.factors.get(2))) - Long.parseLong(timeToString(p_dttm)));
            if (v_last > 3000) {
                this.factors.put(1, p_dttm);
            } else if (v_last > 600) { //get drunk
                this.factors.put(3, p_dttm);
            } else if (v_last > 0) { //stop this factor
                this.factors.delete(3);
            } else {
                this.factors.delete(2);
                this.factors.delete(3);
            }
        }
        if (this.factors.indexOfKey(4) >= 0) { //перегружен

        }
        if (this.factors.indexOfKey(5) >= 0) { //обездвижен

        }
    }

    void renewActions (GameActivity p_act) {
        this.actBasic.clear();
        if (this.placeID > 0) {
            this.actBasic = p_act.allPlaces.get(this.placeID).permActions.clone();
        } else {
            this.actBasic = p_act.allTerritories.get(coordinatesToString(this.territoryID)).permActions.clone();
        }
        //добавить проверку по статам, факторам и т.д.

        //смерть персонажа
        if ((this.stats.get(0) <= 0.1f) || (this.stats.get(3) <= 0) || (this.stats.get(4) <= 0)) {
            death(p_act);
        }
        //бессилие - отдых и сон
        if (this.stats.get(1) <= 0) {
            for (int i=0; i<this.actBasic.size(); i++) {
                if (this.actBasic.keyAt(i) > 1) this.actBasic.delete(this.actBasic.keyAt(i));
            }
        }
        //перегружен - никаких действий
        //бессознателен - сон
        if (this.stats.get(2) <= 0) {
            for (int i=0; i<this.actBasic.size(); i++) {
                if (this.actBasic.keyAt(i) != 1) this.actBasic.delete(this.actBasic.keyAt(i));
            }
        }
    }

    void death (Activity p_act) {
        this.actBasic.clear();
        //перевод в (созданние) объект-контейнер "труп" с соответствующим инвентарем
        //убрать из списка всех персонажей
        //убрать из содержащий его локации и добавить вместо него "труп"
    }
}