package org.trv.alex.unshortenurl.util;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UtilsTest {

    private List<Integer> mEmptyOfNull;
    private List<Integer> mEmpty;
    private List<Integer> mSingleton;
    private List<Integer> mRegular;

    @Before
    public void setUp() {
        mEmptyOfNull = Utils.unmodifiableList(null);
        mEmpty = Utils.unmodifiableList(new ArrayList<>());
        mSingleton = Utils.unmodifiableList(Arrays.asList(1));
        mRegular = Utils.unmodifiableList(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
    }

    @Test
    public void shouldBeEmptyList() {
        assertTrue(mEmptyOfNull.isEmpty());
        assertTrue(mEmpty.isEmpty());
    }

    @Test
    public void shouldNotBeEmptyList() {
        assertFalse(mSingleton.isEmpty());
        assertFalse(mRegular.isEmpty());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldNotBeModifiedEmptyOfNullList() {
        mEmptyOfNull.add(1);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldNotBeModifiedEmptyList() {
        mEmpty.add(1);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldNotBeModifiedSingletonList() {
        mSingleton.add(1);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldNotBeModifiedRegularList() {
        mRegular.add(1);
    }
}