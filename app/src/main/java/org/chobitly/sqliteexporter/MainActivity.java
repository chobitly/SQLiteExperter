package org.chobitly.sqliteexporter;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_main)
@OptionsMenu(R.menu.menu_export)
public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    ExportFragment mExportFragment;
    HistoryFragment mHistoryFragment;
    CollectionPagerAdapter mCollectionPagerAdapter;

    @ViewById(R.id.pager)
    ViewPager mViewPager;

    @AfterViews
    protected void afterViews() {
        getActionBar().setDisplayOptions(0, ActionBar.DISPLAY_SHOW_HOME);

        mExportFragment = ExportFragment_.builder().build();
        mHistoryFragment = HistoryFragment_.builder().build();

        mCollectionPagerAdapter = new CollectionPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mCollectionPagerAdapter);

        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        for (int i = 0; i < mCollectionPagerAdapter.getCount(); ++i) {
            actionBar.addTab(actionBar.newTab().setText(mCollectionPagerAdapter.getPageTitle(i))
                    .setTabListener(this));
        }


        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                // When swiping between pages, select the
                // corresponding tab.
                getActionBar().setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        // do nothing
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
        // do nothing
    }

    // Since this is an object collection, use a FragmentStatePagerAdapter,
    // and NOT a FragmentPagerAdapter.
    public class CollectionPagerAdapter extends FragmentStatePagerAdapter {
        public CollectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                default:
                case 0:
                    return mExportFragment;
                case 1:
                    return mHistoryFragment;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                default:
                case 0:
                    return getString(R.string.fragment_export);
                case 1:
                    return getString(R.string.fragment_history);
            }
        }
    }

    @OptionsItem(R.id.action_settings)
    protected boolean onActionSetting(MenuItem item) {
        return true;
    }
}
