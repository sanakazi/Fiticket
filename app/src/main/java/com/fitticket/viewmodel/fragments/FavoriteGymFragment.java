package com.fitticket.viewmodel.fragments;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.fitticket.R;
import com.fitticket.model.pojos.GymDataJson;
import com.fitticket.model.pojos.GetFavouriteGymResponse;
import com.fitticket.model.singleton.MySingleton;
import com.fitticket.model.singleton.PreferencesManager;
import com.fitticket.viewmodel.adapters.GymAdapter;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FavoriteGymFragment extends Fragment {
    private AppCompatActivity mParentActivity;
    private Location mLocation;
    //Butterknife binding of views
    @Bind(R.id.gyms_listview)
    RecyclerView mGymsListView;
    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;
    @Bind(R.id.nofavLayout)
    RelativeLayout noFavLayout;

    private LinearLayoutManager mLayoutManager;
    ArrayList<GymDataJson> gymList;


    public static FavoriteGymFragment newInstance() {
        FavoriteGymFragment fragment = new FavoriteGymFragment();

        return fragment;
    }

    public FavoriteGymFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gymList=new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_near_by, container, false);
        ButterKnife.bind(this, view);

        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mParentActivity=(AppCompatActivity)getActivity();

        //if user is not logged in disable navigation drawer
        if (PreferencesManager.getInstance(mParentActivity).getUserId()!=0) {
            ArrayList<GymDataJson> allGymList=MySingleton.getInstance(mParentActivity).getmAllGymsList();
            ArrayList<GetFavouriteGymResponse.FavGymIdJson> favGymList=MySingleton.getInstance(mParentActivity).getFavGymList();
            if (!favGymList.isEmpty()&&!allGymList.isEmpty()){
                for (GetFavouriteGymResponse.FavGymIdJson favGym:favGymList)
                {
                    for (GymDataJson gym:allGymList){
                        if (favGym.getId()==gym.getGymId()){
                            gymList.add(gym);
                        }
                    }

                }
                if (!gymList.isEmpty()){
                    // use a linear layout manager
                    mLayoutManager = new LinearLayoutManager(mParentActivity);
                    mGymsListView.setLayoutManager(mLayoutManager);
                    GymAdapter adapter= new GymAdapter(mParentActivity,gymList);
                    mGymsListView.setAdapter(adapter);
                }
            }
            else {
                noFavLayout.setVisibility(View.VISIBLE);
            }
        }
    }
}
