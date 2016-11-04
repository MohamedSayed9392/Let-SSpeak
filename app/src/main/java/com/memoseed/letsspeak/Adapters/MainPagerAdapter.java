package com.memoseed.letsspeak.Adapters;

/**
 * Created by MemoSeed on 16/02/2016.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.memoseed.letsspeak.Fragments.Chats_;
import com.memoseed.letsspeak.Fragments.Friends_;


public class MainPagerAdapter extends FragmentStatePagerAdapter {
    Chats_ tab1;
    Friends_ tab2;

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
        this.tab1 = new Chats_();
        this.tab2 = new Friends_();
    }

    @Override
    public Fragment getItem(int position) {

    switch (position) {
        case 0:
            return tab1;
        case 1:
            return tab2;
        default:
            return null;
    }
}


    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Chats";
            case 1:
                return "Friends";
            default:
                return super.getPageTitle(position);
        }
    }
}