package com.muxistudio.guishengapp;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;



public class MyFragmentStatePagerAdapter extends FragmentStatePagerAdapter {
    MyFragmentStatePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                NewsFragment newsFragment = new NewsFragment();
                return newsFragment;
            case 1:
                OriginalFragment originalFragment = new OriginalFragment();
                return originalFragment;
            case 2:
                InteractFragment interactFragment = new InteractFragment();
                return interactFragment;
            default:
                return new NewsFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }


}
