package com.game.michael.first;

import android.util.SparseArray;

import java.util.ArrayList;

abstract class Actions {

    static boolean quitPlace (PersonType p_person,
                              PlaceType p_place,
                              TerritoryType p_territory) {
        if ((p_person.placeID <= 0)
                || (p_person.placeID != p_place.placeID)
                || (p_territory.placesList.indexOfKey(p_place.placeID) < 0)) return false;

        p_place.visitorsList.delete(p_person.personID);
        p_territory.visitorsList.put(p_person.personID, p_person);
        p_person.placeID = 0;
        p_person.territoryID = p_territory.coordinates.clone();
        //System.arraycopy(p_territory.coordinates, 0, p_person.locationID, 1, 2);
        return true;
    }

    static boolean personChangeLocation(GameActivity p_act, PersonType p_person, LocationType p_locationIn, LocationType p_locationOut) /*throws Exception*/{
        int v_placeID_TMP = p_person.placeID;
        int[] v_territoryID_TMP = p_person.territoryID.clone();
        int v_worldID_TMP = p_person.worldID;
        try {
            System.out.println("INFO: "+ p_locationIn.getClass().toString());
            if (p_locationIn.isPlace) {
                p_person.placeID = ((PlaceType) p_locationIn).placeID;
                p_person.territoryID = p_locationIn.coordinates.clone();
                p_person.worldID = p_locationIn.worldID;
                System.out.println("DEBUG: to place.");
            } else {
                p_person.placeID = -1;
                p_person.territoryID = p_locationIn.coordinates.clone();
                p_person.worldID = p_locationIn.worldID;
                System.out.println("DEBUG: to territory.");
            }
            p_person.renewActions(p_act);
            p_locationIn.visitorsList.put(p_person.personID, p_person);
            if ((p_locationOut != null)/*&&(p_locationOut.visitorsList.indexOfKey(p_person.personID))*/) p_locationOut.visitorsList.remove(p_person.personID);
            //System.arraycopy(((PlaceType) p_locationIn).coordinates, 0, p_person.locationID, 1, 2);
            System.out.printf("DEBUG: Person %s moved from %s into %s.\n", p_person.name, (p_locationOut != null)?p_locationOut.name:"nowhere", p_locationIn.name);
            return true;
        } catch (Exception e) {
            System.out.printf("ERROR: Can't put person (%s %s) into (ID %d/ coord %d;%d/ world %d)\n",
                    p_person.name, p_person.surname,
                    (p_locationIn.isPlace)?((PlaceType)p_locationIn).placeID:(-1),
                    p_locationIn.coordinates[0],
                    p_locationIn.coordinates[1],
                    p_locationIn.worldID);
            p_person.placeID = v_placeID_TMP;
            p_person.territoryID = v_territoryID_TMP;
            p_person.worldID = v_worldID_TMP;
            p_locationIn.visitorsList.remove(p_person.personID);
            if (p_locationOut != null) p_locationOut.visitorsList.put(p_person.personID, p_person);
            //p_person.territoryID = ((TerritoryType) p_locationIn).coordinates.clone();
            //System.arraycopy(((TerritoryType) p_locationIn).coordinates, 0, p_person.locationID, 1, 2);
            return false;
        }
    }

    static void statsTimeMode(PersonType p_person, int p_duration, byte p_comfortID) {
        switch (p_comfortID) {
            case 0:
                for (int i=0; i<5; i++) {
                    p_person.stats.put(i, Math.min(p_person.stats.get(i)+(p_person.statsMax.get(i)*p_duration*Constants.statBasicMod[i]), p_person.statsMax.get(i)));
                }
                break;
            case 1:
                for (int i=0; i<5; i++) {
                    p_person.stats.put(i, Math.min(p_person.stats.get(i)+(p_person.statsMax.get(i)*p_duration*Constants.statRestMod[i]), p_person.statsMax.get(i)));
                }
                break;
            case 2:
                for (int i=0; i<5; i++) {
                    p_person.stats.put(i, Math.min(p_person.stats.get(i)+(p_person.statsMax.get(i)*p_duration*Constants.statComfortMod[i]), p_person.statsMax.get(i)));
                }
                break;
            case 3:
                for (int i=0; i<5; i++) {
                    p_person.stats.put(i, Math.min(p_person.stats.get(i)+(p_person.statsMax.get(i)*p_duration*Constants.statRecreationMod[i]), p_person.statsMax.get(i)));
                }
                break;
        }
    }

    static void personLogAction(PersonType p_person, int[] p_dateTime, int p_actionID, int p_duration) {
        p_person.log.put(BasicProcedures.timeToString(p_dateTime), "@A~" + p_actionID + "~" + p_duration + logPosition(p_person.placeID, p_person.territoryID)
                + logStats(p_person.attributes, p_person.stats, p_person.statsMax)
                + logEquip(p_person.equipped) + logInv(p_person.inventory) + logFact(p_person.factors));
    }

    private static String logPosition(int p_placeID, int[] p_territoryID) {
        return "@P~" + p_placeID + "~" + p_territoryID[0] + "~" + p_territoryID[1];
    }

    private static String logStats (SparseArray<Float> p_attributes, SparseArray<Float> p_stats, SparseArray<Float> p_statsMax) {
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

    private static String logEquip (SparseArray<ItemType> p_equipped) {
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

    private static String logInv (ArrayList<ItemType> p_inventory) {
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

    private static String logFact (SparseArray<int[]> p_fact) {
        StringBuilder v_logFact =  new StringBuilder();
        v_logFact.append("@F");
        for (int i=0; i<p_fact.size(); i++) {
            v_logFact.append("~");
            v_logFact.append(p_fact.keyAt(i));
        }
        return v_logFact.toString();
    }


    /*static boolean getPersonFromLocation(GameActivity p_act, PersonType p_person, LocationType p_location) {
        try {
            if (p_person.placeID < 0) {

            }
        } catch (Exception e) {

        }
    }*/
}
