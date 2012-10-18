package com.adamrocker.abc2012f;

import java.util.ArrayList;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.adamrocker.abc2012f.fragment.TabFragment;
import com.adamrocker.abc2012f.fragment.TabOneFragment;
import com.adamrocker.abc2012f.fragment.TabThreeFragment;
import com.adamrocker.abc2012f.fragment.TabTwoFragment;
import com.adamrocker.abc2012f.R;
import android.R.color;
import android.os.Bundle;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends SherlockFragmentActivity implements ActionBar.TabListener, OnPageChangeListener {
    private final static Class<?>[] PAGE = {TabOneFragment.class, TabTwoFragment.class, TabThreeFragment.class};
    private ViewPager mViewPager;
    private ActionBar mActionBar;
    private SlideFrameLayout mSlidingMenu;
            
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context context = getApplicationContext();
        setContentView(R.layout.activity_main);
        mSlidingMenu = (SlideFrameLayout) LayoutInflater.from(this).inflate(R.layout.behind_menu, null);

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setOnPageChangeListener(this);
        MyPagerAdapter adapter = new MyPagerAdapter(this);
        for (int i = 0; i < PAGE.length; i++) {
            Bundle args = null;
            TabFragment f = (TabFragment) Fragment.instantiate(context, PAGE[i].getName(), args);
            adapter.addFragment(f);
        }
        mViewPager.setAdapter(adapter);
        mActionBar = getSupportActionBar();
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        for (int i = 0; i < PAGE.length; i++) {
            ActionBar.Tab tab = getSupportActionBar().newTab();
            TabFragment f = adapter.getFragment(i);
            tab.setText(f.getTitleId());
            tab.setIcon(f.getIconId());
            tab.setTabListener(this);
            mActionBar.addTab(tab);
        }
    }
    
    @Override
    public void onPostCreate(Bundle save) {
        super.onPostCreate(save);
        mSlidingMenu.setFitsSystemWindows(true);
        ViewGroup decor = (ViewGroup) getWindow().getDecorView();
        ViewGroup above = (ViewGroup) decor.getChildAt(0);//including actionbar
        TypedArray a = getTheme().obtainStyledAttributes(new int[] {android.R.attr.windowBackground});
        int background = a.getResourceId(0, 0);
        above.setBackgroundResource(background);
        decor.removeView(above);
        mSlidingMenu.setAboveView(above);
        decor.addView(mSlidingMenu);
        mActionBar.setDisplayHomeAsUpEnabled(true);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            mSlidingMenu.toggleMenu();
            return true;
        }
        return false;
    }
    
    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageSelected(int position) {
        mActionBar.getTabAt(position).select();
    }

    @Override
    public void onTabSelected(Tab tab,
            android.support.v4.app.FragmentTransaction ft) {
        int index = 0;
        for (index = 0; index < mActionBar.getTabCount(); index++) {
            Tab tmp = mActionBar.getTabAt(index);
            if (tmp == tab) break;
        }
        mViewPager.setCurrentItem(index);
    }

    @Override
    public void onTabUnselected(Tab tab,
            android.support.v4.app.FragmentTransaction ft) {
    }

    @Override
    public void onTabReselected(Tab tab,
            android.support.v4.app.FragmentTransaction ft) {
    }
    
    public static class MyPagerAdapter extends FragmentStatePagerAdapter {
        private final ArrayList<TabFragment> mFragments = new ArrayList<TabFragment>();
        private final Context mContext;

        public MyPagerAdapter(SherlockFragmentActivity activity) {
            super(activity.getSupportFragmentManager());
            mContext = activity;
        }
        
        public void addFragment(TabFragment f) {
            mFragments.add(f);
        }
        
        public TabFragment getFragment(int index) {
            return mFragments.get(index);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public Fragment getItem(int position) {
            Bundle args = null;
            return mFragments.get(position);
        }
    }
}
