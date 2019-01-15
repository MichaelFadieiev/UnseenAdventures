package com.game.michael.first;

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

    /*static boolean getPersonFromLocation(GameActivity p_act, PersonType p_person, LocationType p_location) {
        try {
            if (p_person.placeID < 0) {

            }
        } catch (Exception e) {

        }
    }*/
}
