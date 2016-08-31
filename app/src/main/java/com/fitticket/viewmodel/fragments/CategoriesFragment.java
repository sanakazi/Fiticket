package com.fitticket.viewmodel.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.fitticket.R;
import com.fitticket.model.constants.Apis;
import com.fitticket.model.constants.Constants;
import com.fitticket.model.pojos.CategoryJson;
import com.fitticket.model.pojos.CategoryJsonResponse;
import com.fitticket.model.services.FetchAllActivitiesService;
import com.fitticket.model.services.FetchAllGymsService;
import com.fitticket.model.singleton.MySingleton;
import com.fitticket.model.singleton.PreferencesManager;
import com.fitticket.model.utils.WebServices;
import com.fitticket.viewmodel.custom.ProgressBarCircular;
import com.fitticket.viewmodel.utils.Utilities;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CategoriesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategoriesFragment extends Fragment {
    private static final String TAG = CategoriesFragment.class.getSimpleName();
    private static final long ONE_DAY = 24 * 60 * 60 * 1000; //No of milliseconds in a day
    ProgressBarCircular progressBar;
    SharedPreferences mPrefs;
    TextView favIcon;
    LinearLayout exploreLayout;
    Typeface awesomeFont;
    FragmentInteractionListener mListener;
    private RecyclerView mCatRecyclerView;
    private RecyclerView.Adapter mCategoryAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private AppCompatActivity parentActivity;
    private TextView chevronText, favCountText;


//    private TextView locationText, locationIcon;
    private String addressString;
    private ArrayList<CategoryJson> mCategoryList;

    public CategoriesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CategoriesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CategoriesFragment newInstance(String param1, String param2) {
        CategoriesFragment fragment = new CategoriesFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        mListener = (FragmentInteractionListener) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            //TODO
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ifeel_like, container, false);
        mCatRecyclerView = (RecyclerView) view.findViewById(R.id.category_recycler_view);
        progressBar = (ProgressBarCircular) view.findViewById(R.id.progressBar);
        favCountText = (TextView) view.findViewById(R.id.favorite_count);
        chevronText = (TextView) view.findViewById(R.id.chevron_right);
        favIcon = (TextView) view.findViewById(R.id.fav_icon);
        exploreLayout = (LinearLayout) view.findViewById(R.id.exploreLayout);

        refreshFavCount(mPrefs.getStringSet(Constants.FAVORITE, new HashSet<String>()).size());
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mCatRecyclerView.setHasFixedSize(true);

        exploreLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Set<String> favIds = mPrefs.getStringSet(Constants.FAVORITE, new HashSet<String>());
                if (favIds.size() > 0) {
                    String catIds = getCommaSeperatedFavCatIds(favIds);
                    mListener.onCategoryClicked(catIds);
                } else {
                    Toast.makeText(parentActivity, getResources().getString(R.string.choose_favorite), Toast.LENGTH_SHORT).show();
                }
            }
        });


        return view;
    }

    private String getCommaSeperatedFavCatIds(Set<String> favIds) {
        StringBuffer catIds = new StringBuffer();
        for (String s : favIds) {
            for (CategoryJson categoryJson : mCategoryList) {
                if (s.equals(categoryJson.getId().toString())) {
                    catIds.append(Utilities.getCommaSeperatedCatIds(categoryJson.getCategoryList()));
                    catIds.append(",");
                }
            }
        }
        catIds.deleteCharAt(catIds.length() - 1);
        return catIds.toString();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        parentActivity = (AppCompatActivity) getActivity();

        awesomeFont = Typeface.createFromAsset(parentActivity.getAssets(), "fontawesome-webfont.ttf");
        chevronText.setTypeface(awesomeFont);
        favIcon.setTypeface(awesomeFont);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(parentActivity);
        mCatRecyclerView.setLayoutManager(mLayoutManager);

        //Get Category list from sungleton, if it is empty get new list from server
        mCategoryList = MySingleton.getInstance(parentActivity).getCategoryList();
        if (mCategoryList.isEmpty()) {
            triggerCategoryVolleyRequest();
        } else {
            setCategoryAdapter();
        }
    }

    private void triggerCategoryVolleyRequest() {
        progressBar.setVisibility(View.VISIBLE);
        String cityId = PreferencesManager.getInstance(parentActivity).getSelectedCityId();
        Log.d(TAG, "Get categories Requst: " + Apis.GET_CATEGORIES_GROUP_URL + cityId);
        WebServices.triggerVolleyGetRequest(parentActivity, Apis.GET_CATEGORIES_GROUP_URL + cityId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Get categories response: " + response);
                        Gson gson = new Gson();
                        CategoryJsonResponse jsonResponse = gson.fromJson(response, CategoryJsonResponse.class);
                        if (jsonResponse.getStatusCode().equalsIgnoreCase("0")) {
                            if (jsonResponse.getData() != null && jsonResponse.getData().getCategories() != null
                                    && !jsonResponse.getData().getCategories().isEmpty()) {
                                mCategoryList = addFavorites(jsonResponse.getData().getCategories());
                                MySingleton.getInstance(parentActivity).setCategoryList(mCategoryList);
                                setCategoryAdapter();
                                Long currentTimeInMillis = Calendar.getInstance().getTimeInMillis();
                                if (currentTimeInMillis - PreferencesManager.getInstance(parentActivity).getActivitiesTimeStamp() > ONE_DAY) {
                                    Intent intent = new Intent(parentActivity, FetchAllActivitiesService.class);
                                    parentActivity.startService(intent);
                                }
                                if (currentTimeInMillis - PreferencesManager.getInstance(parentActivity).getGymsTimeStamp() > ONE_DAY) {
                                    Intent intent = new Intent(parentActivity, FetchAllGymsService.class);
                                    parentActivity.startService(intent);
                                }
                            }

                        } else {
                            Log.e(TAG, jsonResponse.getStatusMsg());
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Utilities.handleVolleyError(parentActivity, error);
                        progressBar.setVisibility(View.GONE);
                    }
                });


    }

    private void setCategoryAdapter() {
        mCategoryAdapter = new CategoryAdapter(parentActivity, mCategoryList);
        mCatRecyclerView.setAdapter(mCategoryAdapter);
    }

    //Method adds the favorite status to activities based on previous user preference
    private ArrayList<CategoryJson> addFavorites(ArrayList<CategoryJson> categories) {
        Set<String> favSet = mPrefs.getStringSet(Constants.FAVORITE, new HashSet<String>());
        if (!favSet.isEmpty()) {
            for (CategoryJson category : categories) {
                category.setIsFavorite(favSet.contains(category));
            }
        }
        return categories;
    }

    public void setAddress(String address) {
        this.addressString = address;
//        if(locationText!=null){
//            locationText.setText(address);
//        }

    }

    private void refreshFavCount(int count) {
        if (count > 0) {
            favCountText.setVisibility(View.VISIBLE);
            favCountText.setText("" + count);
            favIcon.setTextColor(getResources().getColor(R.color.explore_yellow));

        } else {
            favCountText.setVisibility(View.GONE);
            favIcon.setTextColor(getResources().getColor(R.color.GrayGoose));

        }

    }


    public interface FragmentInteractionListener {
        void onCategoryClicked(String cateGoryId);
    }

    public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

        private final ArrayList<CategoryJson> mCategoryList;
        Context mContext;
        SharedPreferences mPrefs;
        private ImageLoader mImageLoader;
        private int lastPosition = -1;


        // Provide a suitable constructor (depends on the kind of dataset)
        public CategoryAdapter(Context context, ArrayList<CategoryJson> categoryList) {
            mImageLoader = MySingleton.getInstance(context).getImageLoader();
            mCategoryList = categoryList;
            mContext = context;
            mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.category_listitem, viewGroup, false);
            ViewHolder vh = new ViewHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
            viewHolder.mCategoryTitle.setText(mCategoryList.get(position).getName());
            if ((mCategoryList.get(position).getCount()) > 1) {
                viewHolder.mCategorySubTitle.setText(mCategoryList.get(position).getCount() + " Activities today");
            } else {
                viewHolder.mCategorySubTitle.setText(mCategoryList.get(position).getCount() + " Activity today");
            }
            viewHolder.mCategoryImageview.setImageUrl(mCategoryList.get(position).getCategoryImage(), mImageLoader);
            //Stringset returned from shared pref cannot be modified. So wrapping it.
            final Set<String> favSet = new HashSet<>(mPrefs.getStringSet(Constants.FAVORITE, new HashSet<String>()));
            if (favSet.contains(mCategoryList.get(position).getId())) {
                viewHolder.favoriteIcon.setTextColor(getResources().getColor(R.color.explore_yellow));

            } else {
                viewHolder.favoriteIcon.setTextColor(getResources().getColor(R.color.GrayGoose));
            }

            viewHolder.favoriteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Set<String> favSet = new HashSet<>(mPrefs.getStringSet(Constants.FAVORITE, new HashSet<String>()));
                    if (favSet.contains(mCategoryList.get(position).getId())) {
                        viewHolder.favoriteIcon.setTextColor(getResources().getColor(R.color.GrayGoose));
                        favSet.remove(mCategoryList.get(position).getId());
                        refreshFavCount(favSet.size());
                        mPrefs.edit().putStringSet(Constants.FAVORITE, favSet).commit();
                    } else {
                        viewHolder.favoriteIcon.setTextColor(getResources().getColor(R.color.explore_yellow));
                        favSet.add(mCategoryList.get(position).getId());
                        refreshFavCount(favSet.size());
                        mPrefs.edit().putStringSet(Constants.FAVORITE, favSet).commit();
                    }
                }
            });

            viewHolder.mCategoryImageview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String catIds = Utilities.getCommaSeperatedCatIds(mCategoryList.get(position).getCategoryList());
                    mListener.onCategoryClicked(catIds);
                }
            });
//            setAnimation(viewHolder.categoryLayout,position);
        }

        @Override
        public int getItemCount() {
            return mCategoryList.size();
        }

        /**
         * Here is the key method to apply the animation
         */
        private void setAnimation(View viewToAnimate, int position) {
            // If the bound view wasn't previously displayed on screen, it's animated
            if (position > lastPosition) {
                Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
                viewToAnimate.startAnimation(animation);
                lastPosition = position;
            }
        }

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView mCategoryTitle, favoriteIcon, mCategorySubTitle;
            public NetworkImageView mCategoryImageview;
            FrameLayout categoryLayout;

            public ViewHolder(View v) {
                super(v);
                mCategoryTitle = (TextView) v.findViewById(R.id.titleTextView);
                mCategorySubTitle = (TextView) v.findViewById(R.id.subTitleTextView);
                mCategoryImageview = (NetworkImageView) v.findViewById(R.id.categoryImageView);
                mCategoryImageview.setDefaultImageResId(R.drawable.ic_default);
                favoriteIcon = (TextView) v.findViewById(R.id.fav_icon);
                categoryLayout = (FrameLayout) v.findViewById(R.id.category_list_item);
                favoriteIcon.setTypeface(awesomeFont);
            }

        }
    }


}
