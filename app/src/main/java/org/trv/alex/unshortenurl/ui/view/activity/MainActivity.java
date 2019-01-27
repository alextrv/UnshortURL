package org.trv.alex.unshortenurl.ui.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import org.trv.alex.unshortenurl.R;
import org.trv.alex.unshortenurl.presenter.MainPresenter;
import org.trv.alex.unshortenurl.ui.dialog.URLInfoDialog;
import org.trv.alex.unshortenurl.ui.view.fragment.HistoryFragment;
import org.trv.alex.unshortenurl.ui.view.fragment.MainFragment;

public class MainActivity extends AppCompatActivity {

    public interface TabCallback {
        void onTabReselected();
    }

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;

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
                    Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.view_pager);
                    if (fragment instanceof TabCallback) {
                        ((TabCallback) fragment).onTabReselected();
                    }
                }
            }
        });

        mTabLayout.setupWithViewPager(mViewPager);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        MainPresenter.INSTANCE.attachActivity(this);
        MainPresenter.INSTANCE.newIntent(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        MainPresenter.INSTANCE.attachActivity(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        MainPresenter.INSTANCE.detachActivity();
    }

    /**
     * Sets up ViewPager on the main screen and adds fragment for each tab
     */
    private void setupViewPager() {
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPagerAdapter.addFragment(new MainFragment(), getString(R.string.main_string));
        mViewPagerAdapter.addFragment(new HistoryFragment(), getString(R.string.history_string));
        mViewPager.setAdapter(mViewPagerAdapter);
    }

    public ViewPager getViewPager() {
        return mViewPager;
    }

    public void showInfoDialog(String text) {
        URLInfoDialog dialog = URLInfoDialog.newInstance(text);
        dialog.show(getSupportFragmentManager(), URLInfoDialog.TAG);
    }
}
