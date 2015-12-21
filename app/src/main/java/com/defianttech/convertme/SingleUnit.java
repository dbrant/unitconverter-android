package com.defianttech.convertme;

/**
 * @author Dmitry Brant
 */
public class SingleUnit {

    private String name;
    public String getName() { return name; }
    
    private double multiplier;
    public double getMultiplier() { return multiplier; }
    
    private double offset;
    public double getOffset() { return offset; }

    public SingleUnit(String unitName, double unitMultiplier, double unitOffset) {
        name = unitName;
        multiplier = unitMultiplier;
        offset = unitOffset;
    }
}
