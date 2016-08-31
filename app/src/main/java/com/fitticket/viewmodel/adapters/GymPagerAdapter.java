package com.fitticket.viewmodel.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.fitticket.model.constants.Apis;
import com.fitticket.viewmodel.fragments.ActivitiesFragment;
import com.fitticket.viewmodel.fragments.ActivitiesFragmentNew;
import com.fitticket.viewmodel.fragments.GymDetailsFragment;

/**
 * Created by Fiticket on 18/02/16.
 */
public class GymPagerAdapter extends FragmentPagerAdapter{


    private final int gymId;

    public GymPagerAdapter(FragmentManager fm,int gymId) {
        super(fm);
        this.gymId=gymId;
    }

    @Override
    public Fragment getItem(int position) {
        if(position==0){
            return GymDetailsFragment.newInstance();
        }else{
            String url= Apis.GET_ACTVITIES_BY_GYM_URL+gymId;
            return ActivitiesFragmentNew.newInstance(ActivitiesFragmentNew.FROM_NEARBY, url);
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "About";
            case 1:
                return "Activities";
        }
        return null;
    }
}
