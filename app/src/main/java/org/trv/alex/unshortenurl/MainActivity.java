package org.trv.alex.unshortenurl;

import android.app.Fragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity implements ButtonActionListener {

    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mViewPager = findViewById(R.id.view_pager);

        setupViewPager();

        mTabLayout = findViewById(R.id.tab_layout);
        mTabLayout.setupWithViewPager(mViewPager);

    }

    /**
     * Sets up ViewPager on the main screen and adds fragment for each tab
     */
    private void setupViewPager() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getFragmentManager());
        viewPagerAdapter.addFragment(new MainFragment(), getString(R.string.main));
        viewPagerAdapter.addFragment(new HistoryFragment(), getString(R.string.history));
        mViewPager.setAdapter(viewPagerAdapter);
    }

    public ViewPager getViewPager() {
        return mViewPager;
    }

    @Override
    public void onPositive(Bundle args, DialogType type) {
        Fragment fragment = getFragmentManager().findFragmentById(R.id.view_pager);

        if (type == DialogType.CLEAR_HISTORY_DIALOG && fragment instanceof HistoryFragment) {
            HistoryBaseLab.get(getApplicationContext()).deleteAllItems();
            ((HistoryFragment) fragment).updateUI();
            invalidateOptionsMenu();
        }
        if (type == DialogType.URL_INFO_DIALOG) {
            String url = args.getString(URLInfoDialog.URL_KEY);
            ClipboardManager clipboardManager =
                    (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText(null, url);
            clipboardManager.setPrimaryClip(clipData);
            Snackbar.make(findViewById(R.id.coordinator_layout), R.string.url_copied, Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNegative(Bundle args, DialogType type) {
    }

    @Override
    public void onNeutral(Bundle args, DialogType type) {
        if (type == DialogType.URL_INFO_DIALOG) {
            String url = args.getString(URLInfoDialog.URL_KEY);
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        }
    }
}
