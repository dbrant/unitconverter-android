package com.defianttech.convertme;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (c) 2014-2016 Dmitry Brant
 */
public class UnitCollection {
    private static final String TAG = "UnitCollection";

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

    public UnitCollection(String[] collectionNames, List<SingleUnit> collectionItems) {
        names = collectionNames;
        items = collectionItems;
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

    @NonNull
    public static UnitCollection[] getAllUnits(@NonNull Context context) {
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
