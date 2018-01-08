package org.trv.alex.unshorturl;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity implements CustomDialog.ButtonActionListener {

    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mViewPager = (ViewPager) findViewById(R.id.view_pager);

        setupViewPager();

        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mTabLayout.setupWithViewPager(mViewPager);

    }

    /**
     * Sets up ViewPager on the main screen and adds fragment for each tab
     */
    private void setupViewPager() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new MainFragment(), getString(R.string.main));
        viewPagerAdapter.addFragment(new HistoryFragment(), getString(R.string.history));
        mViewPager.setAdapter(viewPagerAdapter);
    }

    public ViewPager getViewPager() {
        return mViewPager;
    }

    @Override
    public void onPositive() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.view_pager);
        if (fragment instanceof HistoryFragment) {
            HistoryBaseLab.get(getApplicationContext()).deleteAllItems();
            ((HistoryFragment) fragment).updateUI();
            invalidateOptionsMenu();
        }
    }

    @Override
    public void onNegative() {

    }
}
