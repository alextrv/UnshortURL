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

public class MainActivity extends AppCompatActivity implements ButtonActionListener {

    public interface Callback {
        void runCallBack();
    }

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewPager = findViewById(R.id.view_pager);

        setupViewPager();

        mTabLayout = findViewById(R.id.tab_layout);

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (tab.getPosition() == 1) {
                    Fragment fragment = getFragmentManager().findFragmentById(R.id.view_pager);
                    if (fragment instanceof Callback) {
                        ((Callback) fragment).runCallBack();
                    }
                }
            }
        });

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

        // Copy URL to clipboard
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
        // Share URL to other apps
        if (type == DialogType.URL_INFO_DIALOG) {
            String url = args.getString(URLInfoDialog.URL_KEY);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, url);
            intent.setType("text/plain");
            startActivity(intent);
        }
    }

    @Override
    public void onNeutral(Bundle args, DialogType type) {
        // Open URL in another app
        if (type == DialogType.URL_INFO_DIALOG) {
            String url = args.getString(URLInfoDialog.URL_KEY);
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        }
    }
}
