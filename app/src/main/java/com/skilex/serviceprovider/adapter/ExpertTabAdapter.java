package com.skilex.serviceprovider.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.skilex.serviceprovider.fragment.AcceptedFragment;
import com.skilex.serviceprovider.fragment.RejectedFragment;

public class ExpertTabAdapter extends FragmentStatePagerAdapter {

    public ExpertTabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new AcceptedFragment();
            case 1:
                return new RejectedFragment();
        }
        return null;
    }


    @Override
    public int getCount() {
        return 2;
    }
}
