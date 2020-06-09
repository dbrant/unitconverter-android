package com.defianttech.convertme;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.List;

/*
 * Copyright (c) 2020 Dmitry Brant
 */
public class CustomUnits {
    private int version = 1;
    @Nullable private List<CustomUnit> units;

    public int getVersion() {
        return version;
    }

    @NonNull
    public List<CustomUnit> getUnits() {
        return units != null ? units : Collections.<CustomUnit>emptyList();
    }

    public void setUnits(@NonNull List<CustomUnit> units) {
        this.units = units;
    }

    public static class CustomUnit {
        private int id;
        private int categoryId;
        private int baseUnitId;
        private double offset;
        private double multiplier;
        private String name;

        public CustomUnit(int id, int categoryId, int baseUnitId, double offset, double multiplier, String name) {
            this.id = id;
            this.categoryId = categoryId;
            this.baseUnitId = baseUnitId;
            this.offset = offset;
            this.multiplier = multiplier;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getCategoryId() {
            return categoryId;
        }

        public int getBaseUnitId() {
            return baseUnitId;
        }

        public void setBaseUnitId(int baseUnitId) {
            this.baseUnitId = baseUnitId;
        }

        public double getOffset() {
            return offset;
        }

        public double getMultiplier() {
            return multiplier;
        }

        public void setMultiplier(double multiplier) {
            this.multiplier = multiplier;
        }
    }
}
