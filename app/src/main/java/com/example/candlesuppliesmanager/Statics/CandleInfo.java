package com.example.candlesuppliesmanager.Statics;

final public class CandleInfo {
    static final double bigWeight = 0.4;
    static final double midWeight = 0.3;
    static final double smallWeight = 0.08;

    static final int maxParcelWeight = 12;

    public static double getBigWeight(int amount)
    {
        return amount*bigWeight;
    }

    public static double getMidWeight(int amount)
    {
        return amount*midWeight;
    }

    public static double getSmallWeight(int amount)
    {
        return amount*smallWeight;
    }

    public static int getParcelsAmount(double wSmall, double wMid, double wBig)
    {
        double totalWeight = wSmall + wMid + wBig;
        return (int)Math.ceil(totalWeight/maxParcelWeight);
    }
}
