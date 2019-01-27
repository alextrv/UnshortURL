package org.trv.alex.unshortenurl.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class UtilsTest {

    private static final Uri sURI = Uri.parse("https://example.com/");

    private Context mContext;
    private String mTestString;

    private Intent mDataIntent;
    private Intent mStringExtraIntent;
    private Intent mEmptyIntent;

    @Before
    public void setUp() {
        mContext = InstrumentationRegistry.getTargetContext();
        mTestString = "Test String";

        mDataIntent = new Intent();
        mDataIntent.setData(sURI);
        mEmptyIntent = new Intent();
        mStringExtraIntent = new Intent();
        mStringExtraIntent.putExtra(Intent.EXTRA_TEXT, sURI.toString());
    }

    @Test
    public void shouldGetTestStringFromClipboard() {
        Utils.setClipboardText(mContext, mTestString);
        assertEquals(mTestString, Utils.getTextFromClipboard(mContext));
    }

    @Test
    public void shouldGetEmptyStringFromClipboard() {
        Utils.setClipboardText(mContext, null);
        assertEquals("", Utils.getTextFromClipboard(mContext));
    }

    @Test
    public void shouldGetDataTextFromIntent() {
        assertEquals(sURI.toString(), Utils.getURLFromIntent(mDataIntent));
    }

    @Test
    public void shouldGetStringExtraFromIntent() {
        assertEquals(sURI.toString(), Utils.getURLFromIntent(mStringExtraIntent));
    }

    @Test
    public void shouldGetNullFromIntent() {
        assertNull(Utils.getURLFromIntent(mEmptyIntent));
    }

}