package com.game.michael.first;

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

    static boolean settlePerson (GameActivity p_act, PersonType p_person, LocationType p_location) throws Exception{
        p_location.visitorsList.put(p_person.personID, p_person);
        try {
            p_person.placeID = ((PlaceType) p_location).placeID;
            p_person.territoryID = ((PlaceType) p_location).territoryID.clone();
            p_person.renewActions(p_act);
            //System.arraycopy(((PlaceType) p_location).territoryID, 0, p_person.locationID, 1, 2);
            return true;
        } catch (NullPointerException e) {
            p_person.placeID = 0;
            p_person.territoryID = ((TerritoryType) p_location).coordinates.clone();
            //System.arraycopy(((TerritoryType) p_location).coordinates, 0, p_person.locationID, 1, 2);
            return true;
        }
    }
}
