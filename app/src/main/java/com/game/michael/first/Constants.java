package com.game.michael.first;

final class Constants {
    //territory size = 2km * 2km;
    //time constants
    static final float timeMaxTerritoryWalking = 40f; //max time for finding place or person
    static final float timeMaxPlaceWalking = 0.05f; //max time for finding exit or person
    static final float timeMaxTerritoryRansack = 300f; //max time for finding items on territory
    static final float timeMaxPlaceRansack = 0.3f; //max time for finding items in place per 1 unit of size

    //item constants
    static final float itemVolumeFreeCoefficient = 0.8f; //max free volume of item
    static final int[] itemFillingRanges = {0, 10, 30, 45, 60, 70, 95};
    //0_hp,       1_stm       2_exh,      3_hng,      4_thr
    static final float[] statBasicMod =    {0.000023f,  0.008000f, -0.000231f, -0.000046f, -0.000231f}; //percents restored per minute
    static final float[] statRestMod =     {0.000046f,  0.070000f,  0.000463f, -0.000046f, -0.000231f};
    static final float[] statComfortMod =  {0.000070f,  0.100000f,  0.000463f, -0.000046f, -0.000231f};
    static final float[] statRecreationMod={0.000700f,  0.500000f,  0.004167f,  0.020000f,  0.500000f};


}
