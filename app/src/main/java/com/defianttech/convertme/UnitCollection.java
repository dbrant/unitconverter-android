package com.defianttech.convertme;

import android.content.Context;
import androidx.annotation.NonNull;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/*
 * Copyright (c) 2014-2017 Dmitry Brant
 */
public class UnitCollection {
    private static final String TAG = "UnitCollection";
    private static final String CUSTOM_COLLECTION_PREF_NAME = "custom_collection";

    public static final int DEFAULT_CATEGORY = 5; //default to "distance"
    public static final int DEFAULT_FROM_INDEX = 10; //default to "inch"
    public static final int DEFAULT_TO_INDEX = 2; //default to "centimeter"
    public static final double DEFAULT_VALUE = 1.0;
    public static final int CUSTOM_ID_START = 10000;

    private static UnitCollection[] INSTANCE;
    private static String[] allCategoryNames;

    @NonNull
    public static UnitCollection[] getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            resetInstance(context);
        }
        return INSTANCE;
    }

    private static void resetInstance(@NonNull Context context) {
        INSTANCE = getAllUnits(context);
    }

    @NonNull
    public static String[] getAllCategoryNames(@NonNull Context context) {
        if (allCategoryNames == null) {
            UnitCollection[] collections = getInstance(context);
            List<String> unitCategories = new ArrayList<>();
            for (UnitCollection collection : collections) {
                unitCategories.addAll(Arrays.asList(collection.getNames()));
            }
            allCategoryNames = unitCategories.toArray(new String[unitCategories.size()]);
            Arrays.sort(allCategoryNames, new Comparator<String>() {
                @Override
                public int compare(String left, String right) {
                    return left.compareTo(right);
                }
            });
        }
        return allCategoryNames;
    }

    private final String[] names;
    public String[] getNames() {
        return names;
    }

    private final List<SingleUnit> items;
    public List<SingleUnit> getItems() {
        return items;
    }

    public SingleUnit get(int index) {
        return items.get(index);
    }

    public int length() {
        return items.size();
    }

    public static int collectionIndexByName(UnitCollection[] collections, String name) {
        for (int i = 0; i < collections.length; i++) {
            for (String cName : collections[i].getNames()) {
                if (cName.equals(name)) {
                    return i;
                }
            }
        }
        return 0;
    }

    public static double convert(@NonNull Context context, int category, int fromIndex, int toIndex, double value) {
        UnitCollection[] collections = getInstance(context);
        double result = (value - collections[category].get(fromIndex).getOffset())
                / collections[category].get(fromIndex).getMultiplier();
        result *= collections[category].get(toIndex).getMultiplier();
        result += collections[category].get(toIndex).getOffset();
        return result;
    }

    UnitCollection(String[] collectionNames, List<SingleUnit> collectionItems) {
        names = collectionNames;
        items = collectionItems;
    }

    @NonNull
    static UnitCollection[] getAllUnits(@NonNull Context context) {
        List<UnitCollection> collections = new ArrayList<>();
        InputStream inStream = null;
        try {
            Log.d(TAG, "Loading units from assets...");
            inStream = context.getAssets().open("units.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
            List<SingleUnit> currentCollection = new ArrayList<>();
            String line;
            String[] lineArr;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("#") || line.length() == 0) {
                    continue;
                }
                if (line.startsWith("==")) {
                    currentCollection = new ArrayList<>();
                    lineArr = line.replace("==", "").trim().split("\\s*,\\s*");
                    collections.add(new UnitCollection(lineArr, currentCollection));
                    continue;
                }
                lineArr = line.split("\\s*,\\s*");
                currentCollection.add(new SingleUnit(Integer.parseInt(lineArr[0]), lineArr[1], Double.parseDouble(lineArr[2]), Double.parseDouble(lineArr[3])));
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to read unit collection.", e);
        } finally {
            if (inStream != null) {
                try { inStream.close(); }
                catch(Exception e) {
                    //
                }
            }
        }

        // deserialize and append custom units.
        CustomUnits customUnits = getCustomUnits(context);

        for (CustomUnits.CustomUnit unit : customUnits.getUnits()) {
            UnitCollection category = collections.get(unit.getCategoryId());
            SingleUnit baseUnit = null;
            for (SingleUnit u : category.getItems()) {
                if (u.getId() == unit.getBaseUnitId()) {
                    baseUnit = u;
                    break;
                }
            }
            if (baseUnit == null) {
                break;
            }
            category.getItems().add(new SingleUnit(unit.getId(), unit.getName(),
                    baseUnit.getMultiplier() * unit.getMultiplier(), unit.getOffset()));
        }

        return collections.toArray(new UnitCollection[0]);
    }

    @NonNull
    static CustomUnits getCustomUnits(@NonNull Context context) {
        SharedPreferences prefs = ConvertActivity.getPrefs(context);
        String customSerialized = prefs.getString(CUSTOM_COLLECTION_PREF_NAME, "{}");
        CustomUnits customUnits = new Gson().fromJson(customSerialized, CustomUnits.class);
        return customUnits != null ? customUnits : new CustomUnits();
    }

    static void saveCustomUnits(@NonNull Context context, CustomUnits customUnits) {
        SharedPreferences.Editor editor = ConvertActivity.getPrefs(context).edit();
        editor.putString(CUSTOM_COLLECTION_PREF_NAME, new Gson().toJson(customUnits));
        editor.apply();
    }

    static void addCustomUnit(@NonNull Context context, int categoryId, int baseUnitId, double multiplier, String name) {
        CustomUnits customUnits = getCustomUnits(context);
        int maxId = CUSTOM_ID_START;
        for (CustomUnits.CustomUnit u : customUnits.getUnits()) {
            if (u.getId() >= maxId) {
                maxId = u.getId() + 1;
            }
        }
        CustomUnits.CustomUnit unit = new CustomUnits.CustomUnit(maxId, categoryId, baseUnitId, 0.0, multiplier, name);
        List<CustomUnits.CustomUnit> newUnits = new ArrayList<>(customUnits.getUnits());
        newUnits.add(unit);
        customUnits.setUnits(newUnits);
        saveCustomUnits(context, customUnits);
        resetInstance(context);
    }

    static void deleteCustomUnit(@NonNull Context context, CustomUnits.CustomUnit unit) {
        CustomUnits customUnits = getCustomUnits(context);
        List<CustomUnits.CustomUnit> newUnits = new ArrayList<>(customUnits.getUnits());
        int index = -1;
        for (int i = 0; i < newUnits.size(); i++) {
            if (newUnits.get(i).getId() == unit.getId()) {
                index = i;
                break;
            }
        }
        if (index >= 0) {
            newUnits.remove(index);
        }
        customUnits.setUnits(newUnits);
        saveCustomUnits(context, customUnits);
        resetInstance(context);
    }
}
