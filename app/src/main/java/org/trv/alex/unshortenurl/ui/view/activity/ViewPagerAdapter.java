package org.trv.alex.unshortenurl.ui.view.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private List<FragmentTitleContainer> mContainerList = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mContainerList.get(position).getFragment();
    }

    @Override
    public int getCount() {
        return mContainerList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mContainerList.get(position).getTitle();
    }

    /**
     * Adds fragment and title to the {@code ViewPagerAdapter}
     *
     * @param fragment Tab's Fragment
     * @param title    Tab's Title
     */
    public void addFragment(Fragment fragment, String title) {
        mContainerList.add(new FragmentTitleContainer(fragment, title));
    }

    /**
     * Class container for fragment and its title. This class has
     * constant fields which can't be changed after object created.
     */
    private static class FragmentTitleContainer {
        private final Fragment mFragment;
        private final String mTitle;

        public FragmentTitleContainer(Fragment fragment, String title) {
            mFragment = fragment;
            mTitle = title;
        }

        public Fragment getFragment() {
            return mFragment;
        }

        public String getTitle() {
            return mTitle;
        }
    }

}
