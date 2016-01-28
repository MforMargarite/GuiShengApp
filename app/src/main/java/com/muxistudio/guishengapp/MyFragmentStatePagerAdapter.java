package com.muxistudio.guishengapp;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;



public class MyFragmentStatePagerAdapter extends FragmentStatePagerAdapter {
    String[] titles;
    MyFragmentStatePagerAdapter(Context context,FragmentManager fm)
    {
        super(fm);
        titles = context.getResources().getStringArray(R.array.tab_name);
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            default:
                return  new NewsFragment();
            case 1:
                return  new OriginalFragment();
            case 2:
                return new InteractFragment();
        }
    }

    @Override
    public int getCount() {
        return titles.length;
    }


    @Override
    public String getPageTitle(int position){
        return titles[position];
    }
}
