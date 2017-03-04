package com.defianttech.convertme;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/*
 * Copyright (c) 2014-2016 Dmitry Brant
 */
public class UnitCollection {
    private static final String TAG = "UnitCollection";

    public static final int DEFAULT_CATEGORY = 5; //default to "distance"
    public static final int DEFAULT_FROM_INDEX = 10; //default to "inch"
    public static final int DEFAULT_TO_INDEX = 2; //default to "centimeter"
    public static final double DEFAULT_VALUE = 1.0;

    private static UnitCollection[] INSTANCE;
    private static String[] allCategoryNames;

    @NonNull
    public static UnitCollection[] getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            INSTANCE = getAllUnits(context);
        }
        return INSTANCE;
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
                currentCollection.add(new SingleUnit(lineArr[0], Double.parseDouble(lineArr[1]), Double.parseDouble(lineArr[2])));
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
        return collections.toArray(new UnitCollection[collections.size()]);
    }
}
