package com.defianttech.convertme;

import android.content.Context;
import androidx.test.InstrumentationRegistry;
import androidx.test.filters.SmallTest;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/*
 * Copyright (c) 2014-2017 Dmitry Brant
 */
@SmallTest
public class UnitCollectionTests {

    @Test
    public void testUnitCollections() {
        Context context = InstrumentationRegistry.getContext();

        final int categoryDistance = 5;
        final int unitInch = 10;
        final int unitCm = 2;

        final float fromIn = 42f;
        final float toCm = 106.68f;

        assertThat((float) UnitCollection.convert(context, categoryDistance, unitInch, unitCm, fromIn), is(toCm));

    }
}
