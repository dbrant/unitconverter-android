package com.defianttech.convertme;

/**
 * @author Dmitry Brant
 */
public class SingleUnit {

    private final String name;
    public String getName() { return name; }
    
    private final double multiplier;
    public double getMultiplier() { return multiplier; }
    
    private final double offset;
    public double getOffset() { return offset; }

    private boolean enabled;
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public SingleUnit(String unitName, double unitMultiplier, double unitOffset) {
        this(unitName, unitMultiplier, unitOffset, true);
    }

    public SingleUnit(String unitName, double unitMultiplier, double unitOffset, boolean enabled) {
        name = unitName;
        multiplier = unitMultiplier;
        offset = unitOffset;
        this.enabled = enabled;
    }
}
