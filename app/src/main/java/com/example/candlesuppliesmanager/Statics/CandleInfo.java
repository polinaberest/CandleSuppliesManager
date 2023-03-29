package com.example.candlesuppliesmanager.Statics;

final public class CandleInfo {
    static final int bigWeight = 3;
    static final int midWeight = 2;
    static final int smallWeight = 1;

    static final int maxParcelWeight = 12;

    static int getBigWeight(int amount)
    {
        return amount*bigWeight;
    }

    static int getMidWeight(int amount)
    {
        return amount*midWeight;
    }

    static int getSmallWeight(int amount)
    {
        return amount*smallWeight;
    }

    static int getParcelsAmount(int wSmall, int wMid, int wBig)
    {
        int totalWeight = wSmall + wMid + wBig;
        return (int)Math.ceil((double)totalWeight/maxParcelWeight);
    }
}
