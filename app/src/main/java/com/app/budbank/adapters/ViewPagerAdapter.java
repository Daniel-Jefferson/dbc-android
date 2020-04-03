package com.app.budbank.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> listFragment = new ArrayList();

    public void addFragment(Fragment fragment) {
        listFragment.add(fragment);
    }

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }


    @Override
    public Fragment getItem(int position) {
            return listFragment.get(position);
    }

    @Override
    public int getCount() {
        return listFragment.size();
    }
}

