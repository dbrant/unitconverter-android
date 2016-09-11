package com.defianttech.convertme;

/*
 * Copyright (c) 2014-2016 Dmitry Brant
 */
public class SingleUnit {

    private final String name;
    public String getName() { return name; }
    
    private final double multiplier;
    public double getMultiplier() { return multiplier; }
    
    private final double offset;
    public double getOffset() { return offset; }

    private boolean enabled = true;
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public SingleUnit(String unitName, double unitMultiplier, double unitOffset) {
        name = unitName;
        multiplier = unitMultiplier;
        offset = unitOffset;
        this.enabled = enabled;
    }
}
