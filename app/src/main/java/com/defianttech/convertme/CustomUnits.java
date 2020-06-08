package com.defianttech.convertme;

import java.util.List;

/*
 * Copyright (c) 2020 Dmitry Brant
 */
public class CustomUnits {
    private List<CustomUnit> units;

    public List<CustomUnit> getUnits() {
        return units;
    }

    public static class CustomUnit {
        private int categoryId;
        private int unitId;
        private double offset;
        private double multiplier;
        private String name;

        public CustomUnit(int categoryId, int unitId, double offset, double multiplier, String name) {
            this.categoryId = categoryId;
            this.unitId = unitId;
            this.offset = offset;
            this.multiplier = multiplier;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public int getCategoryId() {
            return categoryId;
        }

        public int getUnitId() {
            return unitId;
        }

        public double getOffset() {
            return offset;
        }

        public double getMultiplier() {
            return multiplier;
        }
    }
}
