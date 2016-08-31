package com.fitticket.viewmodel.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.fitticket.R;
import com.fitticket.model.singleton.MySingleton;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class ImageDisplayActivity extends Activity {
       public static final String IMAGEURL = "IMAGEURL";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_image_display);
        final NetworkImageView contentView = (NetworkImageView)findViewById(R.id.fullscreen_image);

        String url= getIntent().getStringExtra(IMAGEURL);
        ImageLoader imageLoader= MySingleton.getInstance(this).getImageLoader();
        contentView.setImageUrl(url,imageLoader);

    }
}
