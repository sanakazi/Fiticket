package com.fitticket.viewmodel.activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager.PageTransformer;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import com.fitticket.R;
import com.fitticket.viewmodel.adapters.ScreenSlidePagerAdapter;
import com.fitticket.viewmodel.custom.AutoScrollViewPager;
import com.fitticket.viewmodel.custom.CirclePageIndicator;


public class TrainningSlidesActivity extends Activity{

	public static final String TAG = TrainningSlidesActivity.class.getSimpleName();
	private Button getStartedBtn;
	private AutoScrollViewPager viewPager;
	private CirclePageIndicator circlePageIndicator;
	private ScreenSlidePagerAdapter mPagerAdapter;
	private TextView tvSignUp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.tranning_slides);
		Log.i(TAG, "TrainningSlidesActivity");
		viewPager = (AutoScrollViewPager) findViewById(R.id.view_pager);
		circlePageIndicator = (CirclePageIndicator)  findViewById(R.id.indicator);
		getStartedBtn =(Button) findViewById(R.id.btnLogin);

		getStartedBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(TrainningSlidesActivity.this, MainActivity.class));
				finish();
			}
		});
		mPagerAdapter = new ScreenSlidePagerAdapter(TrainningSlidesActivity.this);
		viewPager.setAdapter(mPagerAdapter);
		viewPager.setPageTransformer(false, new PageTransformer() {

            @Override
            public void transformPage(View page, float position) {
                // TODO Auto-generated method stub
            }

        });
		circlePageIndicator.setViewPager(viewPager);
		viewPager.setInterval(5000);
		viewPager.startAutoScroll();
	}
	@Override
    protected void onPause() {
        super.onPause();
        // stop auto scroll when onPause
        viewPager.stopAutoScroll();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // start auto scroll when onResume
        viewPager.startAutoScroll();
    }
    
    @Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
//		FlurryAgent.onStartSession(this, Apis.FLURRY_KEY);
//		FlurryAgent.logEvent(TrainningSlidesActivity.class.getSimpleName());
	}
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();		 
//		FlurryAgent.endTimedEvent(TrainningSlidesActivity.class.getSimpleName());
//		FlurryAgent.onEndSession(this);
	}
}
