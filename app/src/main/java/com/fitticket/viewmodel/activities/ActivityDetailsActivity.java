package com.fitticket.viewmodel.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.fitticket.R;
import com.fitticket.viewmodel.fragments.ActivityDetailFragment;
import com.fitticket.viewmodel.utils.Utilities;

public class ActivityDetailsActivity extends AppCompatActivity implements Utilities.BookingSuccessfulListener {
    public static final String ACTIVITY_ID = "ACTIVITY_ID";
    ActivityDetailFragment activityDetailFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_details);

        Intent intent = getIntent();
        String action = intent.getAction();
        String data = intent.getDataString();

        Uri datauri = intent.getData();

        if (Intent.ACTION_VIEW.equals(action) && data != null) {
            String productId = data.substring(data.lastIndexOf("/") + 1);
            Log.e("productId", "productId" + productId);
//            Uri contentUri = PetstoreContentProvider.CONTENT_URI.buildUpon()
//                    .appendPath(productId).build();


            if (productId == null) {


            } else {
                launchActivitiesDetailActivity(productId);
            }
        } else {


        }


        activityDetailFragment = ActivityDetailFragment.newInstance(getIntent().getStringExtra(ACTIVITY_ID));
        getSupportFragmentManager().beginTransaction().add(R.id.container, activityDetailFragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_activity_details, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBookingSucessful() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.SHOW_UPCOMING, true);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    private void launchActivitiesDetailActivity(String activityId) {
        Intent i = new Intent(this, ActivityDetailsActivity.class);
        i.putExtra(ActivityDetailsActivity.ACTIVITY_ID, activityId);
        startActivity(i);
        overridePendingTransition(R.anim.anim_up, R.anim.anim_down);
    }
}
